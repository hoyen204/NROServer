package com.girlkun.models.mob;

import com.girlkun.consts.ConstMap;
import com.girlkun.consts.ConstMob;
import com.girlkun.consts.ConstTask;
import com.girlkun.models.item.Item;
import com.girlkun.models.map.ItemMap;

import java.util.List;

import com.girlkun.models.map.Zone;
import com.girlkun.models.player.Location;
import com.girlkun.models.player.Pet;
import com.girlkun.models.player.Player;
import com.girlkun.models.reward.ItemMobReward;
import com.girlkun.models.reward.MobReward;
import com.girlkun.network.io.Message;
import com.girlkun.server.Maintenance;
import com.girlkun.server.Manager;
import com.girlkun.services.*;
import com.girlkun.utils.Util;

import java.util.ArrayList;

public class Mob {

    public int id;
    public Zone zone;
    public int tempId;
    public String name;
    public byte level;

    public MobPoint point;
    public MobEffectSkill effectSkill;
    public Location location;
    public long lastDamePlayerId = -1;

    public byte pDame;
    public int pTiemNang;
    private long maxTiemNang;

    public long lastTimeDie;
    public int lvMob = 0;
    public int status = 5;

    public Mob(Mob mob) {
        this.point = new MobPoint(this);
        this.effectSkill = new MobEffectSkill(this);
        this.location = new Location();
        this.id = mob.id;
        this.tempId = mob.tempId;
        this.level = mob.level;
        this.point.setHpFull(mob.point.getHpFull());
        this.point.sethp(this.point.getHpFull());
        this.location.x = mob.location.x;
        this.location.y = mob.location.y;
        this.pDame = mob.pDame;
        this.pTiemNang = mob.pTiemNang;
        this.setTiemNang();
    }

    public Mob() {
        this.point = new MobPoint(this);
        this.effectSkill = new MobEffectSkill(this);
        this.location = new Location();
    }

    public static void initMobBanDoKhoBau(Mob mob, byte level) {
        mob.point.dame = level * 3250 * mob.level * 4;
        mob.point.maxHp = level * 12472 * mob.level * 2 + level * 7263 * mob.tempId;
    }

    public static void initMobKG(Mob mob, byte level) {
        mob.point.dame = level * 3250 * mob.level * 6;
        mob.point.maxHp = level * 12472 * mob.level * 3 + level * 7263 * mob.tempId;
    }

    public static void hoiSinhMob(Mob mob) {
        mob.point.hp = mob.point.maxHp;
        mob.setTiemNang();
        Message msg;
        try {
            msg = new Message(-13);
            msg.writer().writeByte(mob.id);
            msg.writer().writeByte(mob.tempId);
            msg.writer().writeByte(0); //level mob
            msg.writer().writeInt((mob.point.hp));
            Service.getInstance().sendMessAllPlayerInMap(mob.zone, msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void setTiemNang() {
        this.maxTiemNang = (long) this.point.getHpFull() * ((long) this.pTiemNang + (long) Util.nextInt(-2, 2)) / 100L;
    }

    private long lastTimeAttackPlayer;

    public boolean isDie() {
        return this.point.gethp() <= 0;
    }

    public synchronized int injured(Player plAtt, int damage, boolean dieWhenHpFull) {
        attackPlayer();
        if (!this.isDie()) {
            if (damage >= this.point.hp) {
                damage = this.point.hp;
            }
            if (!dieWhenHpFull) {
                if ((this.point.hp == this.point.maxHp && damage >= this.point.hp) && this.lvMob == 0) {
                    damage = this.point.hp - 1;
                }
                if (this.tempId == 0 && damage > 10) {
                    damage = 10;
                }
            }
            if (this.lvMob != 0) {
                if (damage > this.point.maxHp) {
                    damage = this.point.maxHp;
                }
            }
            this.point.hp -= damage;
            if (this.isDie()) {
                this.status = 0;
                this.sendMobDieAffterAttacked(plAtt, damage);
                TaskService.gI().checkDoneTaskKillMob(plAtt, this);
                TaskService.gI().checkDoneSideTaskKillMob(plAtt, this);
                this.lastTimeDie = System.currentTimeMillis();
            } else {
                this.sendMobStillAliveAffterAttacked(damage, plAtt != null ? plAtt.nPoint.isCrit : false);
            }
            if (plAtt != null) {
                this.lastDamePlayerId = plAtt.id;
                Service.gI().addSMTN(plAtt, (byte) 2, getTiemNangForPlayer(plAtt, damage), true);
            }
            return damage;
        }
        return 0;
    }

    public long getTiemNangForPlayer(Player pl, long dame) {
        int levelPlayer = Service.gI().getCurrLevel(pl);
        int n = levelPlayer - this.level;
        long pDameHit = dame * 100L / point.getHpFull();
        long tiemNang = pDameHit * maxTiemNang / 100L;
        if (tiemNang <= 0) {
            tiemNang = 1;
        }
        if (n >= 0) {
            for (int i = 0; i < n; i++) {
                long sub = tiemNang * 10 / 100;
                if (sub <= 0) {
                    sub = 1;
                }
                tiemNang -= sub;
            }
        } else {
            for (int i = 0; i < -n; i++) {
                long add = tiemNang * 10 / 100;
                if (add <= 0) {
                    add = 1;
                }
                tiemNang += add;
            }
        }
        if (tiemNang <= 0) {
            tiemNang = 1;
        }
        tiemNang = pl.nPoint.calSucManhTiemNang(tiemNang);

        return tiemNang;
    }

    int tick = 0;

    public void update() {

        if (this.tempId == 71) {
            try {
                Message msg = new Message(102);
                msg.writer().writeByte(5);
                msg.writer().writeShort(this.zone.getPlayers().get(0).location.x);
                Service.gI().sendMessAllPlayerInMap(zone, msg);
                msg.cleanup();
            } catch (Exception e) {
            }
        }

        if (this.isDie() && !Maintenance.isRuning) {
            switch (zone.map.type) {
                case ConstMap.MAP_DOANH_TRAI:
                    break;
                case ConstMap.MAP_BAN_DO_KHO_BAU:
                    break;
                default:
                    if (Util.canDoWithTime(lastTimeDie, 5000)) {
                        this.hoiSinh();
                        this.sendMobHoiSinh();
                    }
            }
        }
        effectSkill.update();
        if (lvMob == 0)
            attackPlayer();
    }

    private void attackPlayer() {
        if (!isDie() && !effectSkill.isHaveEffectSkill() && !(tempId == 0) && (Util.canDoWithTime(lastTimeAttackPlayer, Util.nextInt(1500, 3000))) || lvMob != 0) {
            Player pl = getPlayerCanAttack();
            if (pl != null) {
                this.mobAttackPlayer(pl);
            }
            this.lastTimeAttackPlayer = System.currentTimeMillis();
        }
    }

    private Player getPlayerCanAttack() {
        int distance = 100;
        if (lastDamePlayerId > 0) {
            Player p = this.zone.getPlayerInMap(lastDamePlayerId);
            if (p == null) {
                lastDamePlayerId = -1;
                return null;
            }
            if (p.canBeAttack()) {
                int dis = Util.getDistance(p, this);
                if (dis <= distance * 3) {
                    return p;
                }
            }
        }


        Player plAttack = null;
        try {
            List<Player> players = this.zone.getNotBosses();
            for (Player pl : players) {
                if (!pl.isDie() && pl.canBeAttack()) {
                    int dis = Util.getDistance(pl, this);
                    if (dis <= distance) {
                        plAttack = pl;
                        distance = dis;
                    }
                }
            }
        } catch (Exception e) {
e.printStackTrace();
        }
        return plAttack;
    }

    //**************************************************************************
    private void mobAttackPlayer(Player player) {
        int dameMob = this.point.getDameAttack();
        if (player.charms.tdDaTrau > System.currentTimeMillis()) {
            dameMob /= 2;
        }
        int dame = player.injured(null, lvMob != 0 ? player.nPoint.hpMax / 10 : dameMob, false, true, lvMob != 0);
        this.sendMobAttackMe(player, dame);
        this.sendMobAttackPlayer(player);
    }

    private void sendMobAttackMe(Player player, int dame) {
        if (!player.isPet && !player.isNewPet) {
            Message msg;
            try {
                msg = new Message(-11);
                msg.writer().writeByte(this.id);
                msg.writer().writeInt(dame); //dame
                player.sendMessage(msg);
                msg.cleanup();
            } catch (Exception e) {
            }
        }
    }

    private void sendMobAttackPlayer(Player player) {
        Message msg;
        try {
            msg = new Message(-10);
            msg.writer().writeByte(this.id);
            msg.writer().writeInt((int) player.id);
            msg.writer().writeInt(player.nPoint.hp);
            Service.gI().sendMessAnotherNotMeInMap(player, msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void hoiSinh() {
        this.status = 5;
        this.lvMob = 0;
        this.point.hp = this.point.maxHp;
        if (this.lastDamePlayerId > 0)
            this.lastDamePlayerId = -1;
        if (!this.zone.isSuperMobInZone() && Util.isTrue(30, 100) && this.point.maxHp >= 3000)
            this.lvMob = 1;
        this.setTiemNang();
    }

    private void sendMobHoiSinh() {
        Message msg;
        try {
            msg = new Message(-13);
            msg.writer().writeByte(this.id);
            msg.writer().writeByte(this.tempId);
            msg.writer().writeByte(this.lvMob);
            msg.writer().writeInt(this.point.hp += (this.point.hp * (9 * lvMob)));
            Service.gI().sendMessAllPlayerInMap(this.zone, msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    //**************************************************************************
    private void sendMobDieAffterAttacked(Player plKill, int dameHit) {
        Message msg;
        try {
            msg = new Message(-12);
            msg.writer().writeByte(this.id);
            msg.writer().writeInt(dameHit);
            msg.writer().writeBoolean(plKill.nPoint.isCrit); // crit
            List<ItemMap> items = mobReward(plKill, this.dropItemTask(plKill), msg);
            Service.gI().sendMessAllPlayerInMap(this.zone, msg);
            msg.cleanup();
            hutItem(plKill, items);
        } catch (Exception e) {
        }
    }

    private void hutItem(Player player, List<ItemMap> items) {
        if (!player.isPet && !player.isNewPet) {
            if (player.charms.tdThuHut > System.currentTimeMillis()) {
                for (ItemMap item : items) {
                    if (item.itemTemplate.id != 590) {
                        ItemMapService.gI().pickItem(player, item.itemMapId, true);
                    }
                }
            }
        } else {
            if (((Pet) player).master.charms.tdThuHut > System.currentTimeMillis()) {
                for (ItemMap item : items) {
                    if (item.itemTemplate.id != 590) {
                        ItemMapService.gI().pickItem(((Pet) player).master, item.itemMapId, true);
                    }
                }
            }
        }
    }

    private List<ItemMap> mobReward(Player player, ItemMap itemTask, Message msg) {
//        nplayer
        List<ItemMap> itemReward = new ArrayList<>();
        try {
//            if ((!player.isPet && player.getSession().isActived() && player.setClothes.setDHD()) || (player.isPet && ((Pet) player).master.getSession().isActived() && player.setClothes.setDHD())) {
//                if (Util.isTrue(25, 100)) {
//                    byte random = 1;
//                    if (Util.isTrue(5, 100)) {
//                        random = 2;
//                    }
//                    Item i = Manager.RUBY_REWARDS.get(Util.nextInt(0, Manager.RUBY_REWARDS.size() - 1));
//                    i.quantity = random;
//                    InventoryServiceNew.gI().addItemBag(player, i);
//                    InventoryServiceNew.gI().sendItemBags(player);
//                    Service.gI().sendThongBao(player, "Bạn vừa nhận được " + random + " hồng ngọc");
//                }
//
//            }
            if ((!player.isPet && player.getSession().isActived() && player.setClothes.setGod() && (this.zone.map.mapId > 104 && this.zone.map.mapId < 111 || this.zone.map.mapId == 159))) {
                if (Util.isTrue(10, 100)) {
                    itemReward.add(new ItemMap(zone, Manager.thucan[(Util.nextInt(0, 4))], 1, this.location.x + Util.nextInt(-10, 10), this.location.y, player.id));
                }
            }
            if ((!player.isPet && player.getSession().isActived() && player.setClothes.setDHD() && (this.zone.map.mapId > 159 && this.zone.map.mapId < 164 || this.zone.map.mapId == 155))) {
                if (Util.isTrue(15, 100)) {
                    itemReward.add(new ItemMap(zone, Manager.manhts[(Util.nextInt(Manager.manhts.length))], 1, this.location.x + Util.nextInt(-10, 10), this.location.y, player.id));
                }
            }

            itemReward = this.getItemMobReward(player, this.location.x + Util.nextInt(-10, 10),
                    this.zone.map.yPhysicInTop(this.location.x, this.location.y));
            if (itemTask != null) {
                itemReward.add(itemTask);
            }
            msg.writer().writeByte(itemReward.size()); //sl item roi
            for (ItemMap itemMap : itemReward) {
                msg.writer().writeShort(itemMap.itemMapId);// itemmapid
                msg.writer().writeShort(itemMap.itemTemplate.id); // id item
                msg.writer().writeShort(itemMap.x); // xend item
                msg.writer().writeShort(itemMap.y); // yend item
                msg.writer().writeInt((int) itemMap.playerId); // id nhan nat
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return itemReward;
    }

    public List<ItemMap> getItemMobReward(Player player, int x, int yEnd) {
        List<ItemMap> list = new ArrayList<>();
        MobReward mobReward = Manager.MOB_REWARDS.get(this.tempId);
        if (mobReward == null) {
            return list;
        }
        List<ItemMobReward> items = mobReward.getItemReward();
        List<ItemMobReward> golds = mobReward.getGoldReward();
        if (!items.isEmpty()) {
            ItemMobReward item = items.get(Util.nextInt(0, items.size() - 1));
            ItemMap itemMap = item.getItemMap(zone, player, x, yEnd);
            if (itemMap != null) {
                list.add(itemMap);
            }
        }
        if (!golds.isEmpty() && Util.isTrue(10, 100)) {
            ItemMobReward gold = golds.get(Util.nextInt(0, golds.size() - 1));
            ItemMap itemMap = gold.getItemMap(zone, player, x, yEnd);
            if (itemMap != null) {
                list.add(itemMap);
            }
        }
        if (player.itemTime.isUseMayDo && Util.isTrue(10, 100) && this.tempId > 57 && this.tempId < 66) {
            list.add(new ItemMap(zone, 380, 1, x, player.location.y, player.id));
        }// vat phẩm rơi khi user maaáy dò
        if (player.itemTime.isUseMayDo2 && Util.isTrue(7, 100) && this.tempId > 1 && this.tempId < 81) {
            list.add(new ItemMap(zone, 2036, 1, x, player.location.y, player.id));// cai nay sua sau nha
        }

//        if (player.isPet && player.getSession().actived && Util.isTrue(15, 100)) {
//            list.add(new ItemMap(zone, 610, 1, x, player.location.y, player.id));
//        }
        return list;
    }

    private ItemMap dropItemTask(Player player) {
        ItemMap itemMap = null;
        switch (this.tempId) {
            case ConstMob.KHUNG_LONG:
            case ConstMob.LON_LOI:
            case ConstMob.QUY_DAT:
                if (TaskService.gI().getIdTask(player) == ConstTask.TASK_2_0 && Util.isTrue(60,100)) {
                    itemMap = new ItemMap(this.zone, 73, 1, this.location.x, this.location.y, player.id);
                }
                break;
        }
        if (itemMap != null) {
            return itemMap;
        }
        return null;
    }

    private void sendMobStillAliveAffterAttacked(int dameHit, boolean crit) {
        Message msg;
        try {
            msg = new Message(-9);
            msg.writer().writeByte(this.id);
            msg.writer().writeInt(this.point.gethp());
            msg.writer().writeInt(dameHit);
            msg.writer().writeBoolean(crit); // chí mạng
            msg.writer().writeInt(-1);
            Service.gI().sendMessAllPlayerInMap(this.zone, msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }
}
