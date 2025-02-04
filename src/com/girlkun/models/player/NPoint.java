package com.girlkun.models.player;

import com.arriety.card.Card;
import com.arriety.card.OptionCard;
import com.girlkun.consts.ConstPlayer;
import com.girlkun.consts.ConstRatio;
import com.girlkun.models.intrinsic.Intrinsic;
import com.girlkun.models.item.Item;
import com.girlkun.models.skill.Skill;
import com.girlkun.server.Manager;
import com.girlkun.services.EffectSkillService;
import com.girlkun.services.ItemService;
import com.girlkun.services.MapService;
import com.girlkun.services.PlayerService;
import com.girlkun.services.Service;
import com.girlkun.services.TaskService;
import com.girlkun.utils.Logger;
import com.girlkun.utils.SkillUtil;
import com.girlkun.utils.Util;

import java.util.ArrayList;
import java.util.List;

public class NPoint {

    public static final byte MAX_LIMIT = 9;

    private Player player;
    public boolean doPhaLe;
    private long lastTimeXDame;

    public boolean xDameSkill2;
    public int xDameValue;

    public NPoint(Player player) {
        this.xDameValue = 0;
        this.xDameSkill2 = false;
        this.player = player;
        this.tlHp = new ArrayList<>();
        this.tlMp = new ArrayList<>();
        this.tlDef = new ArrayList<>();
        this.tlDame = new ArrayList<>();
        this.tlDameAttMob = new ArrayList<>();
        this.dameBeauty = 0;
        this.tlTNSM = new ArrayList<>();
        this.tlTNSMDT = new ArrayList<>();
        this.tlDameCrit = new ArrayList<>();
    }

    public boolean isCrit;
    public boolean isCrit100;

    private Intrinsic intrinsic;
    private int percentDameIntrinsic;
    public int dameAfter;

    /*-----------------------Chỉ số cơ bản------------------------------------*/
    public byte numAttack;
    public short stamina, maxStamina;

    public byte limitPower;
    public long power;
    public long tiemNang;

    public int hp, hpMax, hpg;
    public int mp, mpMax, mpg;
    public int dame, dameg;
    public int def, defg;
    public int crit, critg;
    public byte speed = 8;

    public boolean teleport;

    public boolean khangTDHS;

    /**
     * Chỉ số cộng thêm
     */
    public int hpAdd, mpAdd, dameAdd, defAdd, critAdd, hpHoiAdd, mpHoiAdd;

    /**
     * //+#% sức đánh chí mạng
     */
    public List<Integer> tlDameCrit;

    /**
     * Tỉ lệ hp, mp cộng thêm
     */
    public List<Integer> tlHp, tlMp;

    /**
     * Tỉ lệ giáp cộng thêm
     */
    public List<Integer> tlDef;

    /**
     * Tỉ lệ sức đánh/ sức đánh khi đánh quái
     */
    public List<Integer> tlDame, tlDameAttMob;

    /**
     * Lượng hp, mp hồi mỗi 30s, mp hồi cho người khác
     */
    public int hpHoi, mpHoi, mpHoiCute;

    /**
     * Tỉ lệ hp, mp hồi cộng thêm
     */
    public short tlHpHoi, tlMpHoi;

    /**
     * Tỉ lệ hp, mp hồi bản thân và đồng đội cộng thêm
     */
    public short tlHpHoiBanThanVaDongDoi, tlMpHoiBanThanVaDongDoi;

    /**
     * Tỉ lệ hút hp, mp khi đánh, hp khi đánh quái
     */
    public short tlHutHp, tlHutMp, tlHutHpMob;

    /**
     * Tỉ lệ hút hp, mp xung quanh mỗi 5s
     */
    public short tlHutHpMpXQ;

    /**
     * Tỉ lệ phản sát thương
     */
    public short tlPST;

    /**
     * Tỉ lệ tiềm năng sức mạnh
     */
    public List<Integer> tlTNSM;

    public List<Integer> tlTNSMDT;

    /**
     * Tỉ lệ vàng cộng thêm
     */
    public short tlGold;

    public short tlNeDon;

    public int dameBeauty;


    public short tlSubSD;

    public short tlSubHP;

    public short tlSubKI;

    public int voHieuChuong;

    /*------------------------Effect skin-------------------------------------*/
    public Item trainArmor;
    public boolean wornTrainArmor;
    public boolean wearingTrainArmor;

    public boolean wearingVoHinh;
    public boolean isKhongLanh;

    public short tlHpGiamODo;
    public short test;

    /*-------------------------------------------------------------------------*/

    /**
     * Tính toán mọi chỉ số sau khi có thay đổi
     */
    public void calPoint() {
        if (this.player.pet != null) {
            this.player.pet.nPoint.setPointWhenWearClothes();
        }
        this.setPointWhenWearClothes();
    }

    private void setPointWhenWearClothes() {
        resetPoint();
        if (this.player.rewardBlackBall.timeOutOfDateReward[1] > System.currentTimeMillis()) {
            tlHutMp += RewardBlackBall.R2S_1;
        }
        if (this.player.rewardBlackBall.timeOutOfDateReward[3] > System.currentTimeMillis()) {
            tlDameAttMob.add(RewardBlackBall.R4S_2);
        }
        if (this.player.rewardBlackBall.timeOutOfDateReward[4] > System.currentTimeMillis()) {
            tlPST += RewardBlackBall.R5S_1;
        }
        if (this.player.rewardBlackBall.timeOutOfDateReward[5] > System.currentTimeMillis()) {
            tlPST += RewardBlackBall.R6S_1;
            tlNeDon += RewardBlackBall.R6S_2;
        }
        if (this.player.rewardBlackBall.timeOutOfDateReward[6] > System.currentTimeMillis()) {
            tlHpHoi += RewardBlackBall.R7S_1;
            tlHutHp += RewardBlackBall.R7S_2;
        }

        this.player.setClothes.worldcup = 0;
        for (Item item : this.player.inventory.itemsBody) {
            if (item.isNotNullItem()) {
                switch (item.template.id) {
                    case 966:
                    case 982:
                    case 983:
                    case 883:
                    case 904:
                        player.setClothes.worldcup++;
                }
                if (item.template.id >= 592 && item.template.id <= 594) {
                    teleport = true;
                }
                Card card = player.Cards.stream().filter(r -> r != null && r.used == 1).findFirst().orElse(null);
                if (card != null) {
                    for (OptionCard io : card.options) {
                        if (io.active == card.level || (card.level == -1 && io.active == 0)) {
                            switch (io.id) {
                                case 0: //Tấn công +#
                                    this.dameAdd += io.param;
                                    break;
                                case 2: //HP, KI+#000
                                    this.hpAdd += io.param * 1000;
                                    this.mpAdd += io.param * 1000;
                                    break;
                                case 3:// fake
                                    this.voHieuChuong += io.param;
                                    break;
                                case 5: //+#% sức đánh chí mạng
                                    this.tlDameCrit.add(io.param);
                                    break;
                                case 6: //HP+#
                                    this.hpAdd += io.param;
                                    break;
                                case 7: //KI+#
                                    this.mpAdd += io.param;
                                    break;
                                case 8: //Hút #% HP, KI xung quanh mỗi 5 giây
                                    this.tlHutHpMpXQ += io.param;
                                    break;
                                case 14: //Chí mạng+#%
                                    this.critAdd += io.param;
                                    break;
                                case 19: //Tấn công+#% khi đánh quái
                                    this.tlDameAttMob.add(io.param);
                                    break;
                                case 22: //HP+#K
                                    this.hpAdd += io.param * 1000;
                                    break;
                                case 23: //MP+#K
                                    this.mpAdd += io.param * 1000;
                                    break;
                                case 27: //+# HP/30s
                                    this.hpHoiAdd += io.param;
                                    break;
                                case 28: //+# KI/30s
                                    this.mpHoiAdd += io.param;
                                    break;
                                case 33: //dịch chuyển tức thời
                                    this.teleport = true;
                                    break;
                                case 47: //Giáp+#
                                    this.defAdd += io.param;
                                    break;
                                case 48: //HP/KI+#
                                    this.hpAdd += io.param;
                                    this.mpAdd += io.param;
                                    break;
                                case 49: //Tấn công+#%
                                case 50: //Sức đánh+#%
                                    this.tlDame.add(io.param);
                                    break;
                                case 77: //HP+#%
                                    this.tlHp.add(io.param);
                                    break;
                                case 80: //HP+#%/30s
                                    this.tlHpHoi += io.param;
                                    break;
                                case 81: //MP+#%/30s
                                    this.tlMpHoi += io.param;
                                    break;
                                case 88: //Cộng #% exp khi đánh quái
                                    this.tlTNSM.add(io.param);
                                    break;
                                case 94: //Giáp #%
                                    this.tlDef.add(io.param);
                                    break;
                                case 95: //Biến #% tấn công thành HP
                                    this.tlHutHp += io.param;
                                    break;
                                case 96: //Biến #% tấn công thành MP
                                    this.tlHutMp += io.param;
                                    break;
                                case 97: //Phản #% sát thương
                                    this.tlPST += io.param;
                                    break;
                                case 100: //+#% vàng từ quái
                                    this.tlGold += io.param;
                                    break;
                                case 101: //+#% TN,SM
                                    this.tlTNSM.add(io.param);
                                    break;
                                case 103: //KI +#%
                                    this.tlMp.add(io.param);
                                    break;
                                case 104: //Biến #% tấn công quái thành HP
                                    this.tlHutHpMob += io.param;
                                    break;
                                case 147: //+#% sức đánh
                                    this.tlDame.add(io.param);
                                    break;
                            }
                        }
                    }
                }
                for (Item.ItemOption io : item.itemOptions) {
                    switch (io.optionTemplate.id) {
                        case 0: //Tấn công +#
                            this.dameAdd += io.param;
                            break;
                        case 2: //HP, KI+#000
                            this.hpAdd += io.param * 1000;
                            this.mpAdd += io.param * 1000;
                            break;
                        case 3:// fake
                            this.voHieuChuong += io.param;
                            break;
                        case 5: //+#% sức đánh chí mạng
                            this.tlDameCrit.add(io.param);
                            break;
                        case 6: //HP+#
                            this.hpAdd += io.param;
                            break;
                        case 7: //KI+#
                            this.mpAdd += io.param;
                            break;
                        case 8: //Hút #% HP, KI xung quanh mỗi 5 giây
                            this.tlHutHpMpXQ += io.param;
                            break;
                        case 14: //Chí mạng+#%
                            this.critAdd += io.param;
                            break;
                        case 19: //Tấn công+#% khi đánh quái
                            this.tlDameAttMob.add(io.param);
                            break;
                        case 22: //HP+#K
                            this.hpAdd += io.param * 1000;
                            break;
                        case 23: //MP+#K
                            this.mpAdd += io.param * 1000;
                            break;
                        case 27: //+# HP/30s
                            this.hpHoiAdd += io.param;
                            break;
                        case 28: //+# KI/30s
                            this.mpHoiAdd += io.param;
                            break;
                        case 33: //dịch chuyển tức thời
                            this.teleport = true;
                            break;
                        case 47: //Giáp+#
                            this.defAdd += io.param;
                            break;
                        case 48: //HP/KI+#
                            this.hpAdd += io.param;
                            this.mpAdd += io.param;
                            break;
                        case 49: //Tấn công+#%
                        case 50: //Sức đánh+#%
                            this.tlDame.add(io.param);
                            break;
                        case 77: //HP+#%
                            this.tlHp.add(io.param);
                            break;
                        case 80: //HP+#%/30s
                            this.tlHpHoi += io.param;
                            break;
                        case 81: //MP+#%/30s
                            this.tlMpHoi += io.param;
                            break;
                        case 88: //Cộng #% exp khi đánh quái
                            this.tlTNSM.add(io.param);
                            break;
                        case 94: //Giáp #%
                            this.tlDef.add(io.param);
                            break;
                        case 95: //Biến #% tấn công thành HP
                            this.tlHutHp += io.param;
                            break;
                        case 96: //Biến #% tấn công thành MP
                            this.tlHutMp += io.param;
                            break;
                        case 97: //Phản #% sát thương
                            this.tlPST += io.param;
                            break;
                        case 100: //+#% vàng từ quái
                            this.tlGold += io.param;
                            break;
                        case 101: //+#% TN,SM
                            this.tlTNSM.add(io.param);
                            break;
                        case 103: //KI +#%
                            this.tlMp.add(io.param);
                            break;
                        case 104: //Biến #% tấn công quái thành HP
                            this.tlHutHpMob += io.param;
                            break;
                        case 105: //Vô hình khi không đánh quái và boss
                            this.wearingVoHinh = true;
                            break;
                        case 106: //Không ảnh hưởng bởi cái lạnh
                            this.isKhongLanh = true;
                            break;
                        case 108: //#% Né đòn
                            this.tlNeDon += io.param;// đối nghịch
                            break;
                        case 109: //Hôi, giảm #% HP
                            this.tlHpGiamODo += io.param;
                            break;
                        case 110: //Hôi, giảm #% HP
                            this.doPhaLe = true;
                            break;
                        case 116: //Kháng thái dương hạ san
                            this.khangTDHS = true;
                            break;
                        case 117: //Đẹp +#% SĐ cho mình và người xung quanh
                            this.dameBeauty = io.param;
                            break;
                        case 147: //+#% sức đánh
                            this.tlDame.add(io.param);
                            break;
                        case 75: //Giảm 50% sức đánh, HP, KI và +#% SM, TN, vàng từ quái
                            this.tlSubSD += 50;
                            this.tlSubHP += 50;
                            this.tlSubKI += 50;
                            this.tlTNSM.add(io.param);
                            this.tlGold += io.param;
                            break;
                        case 162: //Cute hồi #% KI/s bản thân và xung quanh
                            this.mpHoiCute += io.param;
                            break;
                        case 173: //Phục hồi #% HP và KI cho đồng đội
                            this.tlHpHoiBanThanVaDongDoi += io.param;
                            this.tlMpHoiBanThanVaDongDoi += io.param;
                            break;
                        case 211: //test
                            this.test += io.param;
                            break;
                        case 159: // x dame skill chuong
                            this.xDameSkill2 = true;
                            this.xDameValue = io.param;
                            break;
                        case 160: // x dame skill chuong
                            this.tlTNSMDT.add(io.param);
                            break;
                        case 167:
                            this.xDameValue = io.param;
                            break;
                    }
                }
            }
        }
        if (this.player.isPl() && this.player.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2) {
            for (Item item : this.player.inventory.itemsBag) {
                if (item.isNotNullItem() && item.template.id == 921) {
                    for (Item.ItemOption io : item.itemOptions) {
                        switch (io.optionTemplate.id) {
                            case 14: //Chí mạng+#%
                                this.critAdd += io.param;
                                break;
                            case 50: //Sức đánh+#%
                                this.tlDame.add(io.param);
                                break;
                            case 77: //HP+#%
                                this.tlHp.add(io.param);
                                break;
                            case 80: //HP+#%/30s
                                this.tlHpHoi += io.param;
                                break;
                            case 81: //MP+#%/30s
                                this.tlMpHoi += io.param;
                                break;
                            case 94: //Giáp #%
                                this.tlDef.add(io.param);
                                break;
                            case 103: //KI +#%
                                this.tlMp.add(io.param);
                                break;
                            case 108: //#% Né đòn
                                this.tlNeDon += io.param;
                                break;
                        }
                    }
                    break;
                }
            }
        } else if (this.player.isPl() && this.player.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA3) {
            for (Item item : this.player.inventory.itemsBag) {
                if (item.isNotNullItem() && item.template.id == 2074) {
                    for (Item.ItemOption io : item.itemOptions) {
                        switch (io.optionTemplate.id) {
                            case 14: //Chí mạng+#%
                                this.critAdd += io.param;
                                break;
                            case 50: //Sức đánh+#%
                                this.tlDame.add(io.param);
                                break;
                            case 77: //HP+#%
                                this.tlHp.add(io.param);
                                break;
                            case 80: //HP+#%/30s
                                this.tlHpHoi += io.param;
                                break;
                            case 81: //MP+#%/30s
                                this.tlMpHoi += io.param;
                                break;
                            case 94: //Giáp #%
                                this.tlDef.add(io.param);
                                break;
                            case 103: //KI +#%
                                this.tlMp.add(io.param);
                                break;
                            case 108: //#% Né đòn
                                this.tlNeDon += io.param;
                                break;
                        }
                    }
                    break;
                }
            }
        } else if (this.player.isPl() && this.player.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA4) {
            for (Item item : this.player.inventory.itemsBag) {
                if (item.isNotNullItem() && item.template.id == 2075) {
                    for (Item.ItemOption io : item.itemOptions) {
                        switch (io.optionTemplate.id) {
                            case 14: //Chí mạng+#%
                                this.critAdd += io.param;
                                break;
                            case 50: //Sức đánh+#%
                                this.tlDame.add(io.param);
                                break;
                            case 77: //HP+#%
                                this.tlHp.add(io.param);
                                break;
                            case 80: //HP+#%/30s
                                this.tlHpHoi += io.param;
                                break;
                            case 81: //MP+#%/30s
                                this.tlMpHoi += io.param;
                                break;
                            case 94: //Giáp #%
                                this.tlDef.add(io.param);
                                break;
                            case 103: //KI +#%
                                this.tlMp.add(io.param);
                                break;
                            case 108: //#% Né đòn
                                this.tlNeDon += io.param;
                                break;
                        }
                    }
                    break;
                }
            }
        }

        setDameTrainArmor();
        setBasePoint();
    }

    private void setDameTrainArmor() {
        if (!this.player.isPet && !this.player.isBoss) {
            if (this.player.inventory.itemsBody.size() < 7) {
                return;
            }
            try {
                Item gtl = this.player.inventory.itemsBody.get(6);
                if (gtl.isNotNullItem()) {
                    this.wearingTrainArmor = true;
                    this.wornTrainArmor = true;
                    this.player.inventory.trainArmor = gtl;
                    this.tlSubSD += ItemService.gI().getPercentTrainArmor(gtl);
                } else {
                    if (this.wornTrainArmor) {
                        this.wearingTrainArmor = false;
                        for (Item.ItemOption io : this.player.inventory.trainArmor.itemOptions) {
                            if (io.optionTemplate.id == 9 && io.param > 0) {
                                this.tlDame.add(ItemService.gI().getPercentTrainArmor(this.player.inventory.trainArmor));
                                break;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                Logger.error("Lỗi get giáp tập luyện " + this.player.name + "\n");
            }
        }
    }

    public void setBasePoint() {
        setHpMax();
        setHp();
        setMpMax();
        setMp();
        setDame();
        setDef();
        setCrit();
        setHpHoi();
        setMpHoi();
        setNeDon();
    }

    private void setNeDon() {

    }

    private void setHpHoi() {
        this.hpHoi = this.hpMax / 100;
        this.hpHoi += this.hpHoiAdd;
        this.hpHoi += ((long) this.hpMax * this.tlHpHoi / 100);
        this.hpHoi += ((long) this.hpMax * this.tlHpHoiBanThanVaDongDoi / 100);
    }

    private void setMpHoi() {
        this.mpHoi = this.mpMax / 100;
        this.mpHoi += this.mpHoiAdd;
        this.mpHoi += ((long) this.mpMax * this.tlMpHoi / 100);
        this.mpHoi += ((long) this.mpMax * this.tlMpHoiBanThanVaDongDoi / 100);
    }

    private void setHpMax() {
        this.hpMax = this.hpg;
        this.hpMax += this.hpAdd;
        //đồ
        for (Integer tl : this.tlHp) {
            this.hpMax += ((long) this.hpMax * tl / 100);
        }
        //set nappa
        if (this.player.setClothes.nappa == 5) {
            this.hpMax += ((long) this.hpMax * 80 / 100);
        }
        //set worldcup
        if (this.player.setClothes.worldcup == 2) {
            this.hpMax += ((long) this.hpMax * 10 / 100);
        }
        //ngọc rồng đen 1 sao
        if (this.player.rewardBlackBall.timeOutOfDateReward[0] > System.currentTimeMillis()) {
            this.hpMax += ((long) this.hpMax * RewardBlackBall.R1S_1 / 100);
        }
        //khỉ
        if (this.player.effectSkill.isMonkey) {
            if (!this.player.isPet || ((Pet) this.player).status != Pet.FUSION) {
                int percent = SkillUtil.getPercentHpMonkey(player.effectSkill.levelMonkey);
                this.hpMax += ((long) this.hpMax * percent / 100);
            }
        }
        //pet mabư
        if (this.player.isPet && ((Pet) this.player).typePet == 1
                && ((Pet) this.player).master.fusion.typeFusion != ConstPlayer.NON_FUSION) {
            this.hpMax += ((long) this.hpMax * 20 / 100);
        }
        //pet berus
        if (this.player.isPet && ((Pet) this.player).typePet == 2// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion != ConstPlayer.NON_FUSION) {
            this.hpMax += ((long) this.hpMax * 40 / 100);//chi so hp
        }
        //btc3
        //if (this.player.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA3) {
        //    this.hpMax *= 1.5;
        //}

        //New Pet
        if (this.player.isPet && ((Pet) this.player).master.fusion.typeFusion != ConstPlayer.NON_FUSION && ((Pet) this.player).typePet >= 4) {
            this.hpMax += ((long) this.hpMax * (((Pet) this.player).typePet * 5 + 10) / 100);
        }

//        //pet 4
//        if (this.player.isPet && ((Pet) this.player).typePet == 4// chi so lam sao bac tu cho dj
//                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA) {
//            this.hpMax += ((long) this.hpMax * 30 / 100);//chi so hp
//        }
//        if (this.player.isPet && ((Pet) this.player).typePet == 4// chi so lam sao bac tu cho dj
//                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2) {
//            this.hpMax += ((long) this.hpMax * 30 / 100);//chi so hp
//        }
//        if (this.player.isPet && ((Pet) this.player).typePet == 4// chi so lam sao bac tu cho dj
//                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA3) {
//            this.hpMax += ((long) this.hpMax * 30 / 100);//chi so hp
//        }
//        if (this.player.isPet && ((Pet) this.player).typePet == 4// chi so lam sao bac tu cho dj
//                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA4) {
//            this.hpMax += ((long) this.hpMax * 30 / 100);//chi so hp
//        }
//        //pet 5
//        if (this.player.isPet && ((Pet) this.player).typePet == 5// chi so lam sao bac tu cho dj
//                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA) {
//            this.hpMax += ((long) this.hpMax * 40 / 100);//chi so hp
//        }
//        if (this.player.isPet && ((Pet) this.player).typePet == 5// chi so lam sao bac tu cho dj
//                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2) {
//            this.hpMax += ((long) this.hpMax * 40 / 100);//chi so hp
//        }
//        if (this.player.isPet && ((Pet) this.player).typePet == 5// chi so lam sao bac tu cho dj
//                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA3) {
//            this.hpMax += ((long) this.hpMax * 40 / 100);//chi so hp
//        }
//        if (this.player.isPet && ((Pet) this.player).typePet == 5// chi so lam sao bac tu cho dj
//                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA4) {
//            this.hpMax += ((long) this.hpMax * 40 / 100);//chi so hp
//        }
//        //pet 6
//        if (this.player.isPet && ((Pet) this.player).typePet == 6// chi so lam sao bac tu cho dj
//                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA) {
//            this.hpMax += ((long) this.hpMax * 50 / 100);//chi so hp
//        }
//        if (this.player.isPet && ((Pet) this.player).typePet == 6// chi so lam sao bac tu cho dj
//                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2) {
//            this.hpMax += ((long) this.hpMax * 50 / 100);//chi so hp
//        }
//        if (this.player.isPet && ((Pet) this.player).typePet == 6// chi so lam sao bac tu cho dj
//                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA3) {
//            this.hpMax += ((long) this.hpMax * 50 / 100);//chi so hp
//        }
//        if (this.player.isPet && ((Pet) this.player).typePet == 6// chi so lam sao bac tu cho dj
//                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA4) {
//            this.hpMax += ((long) this.hpMax * 50 / 100);//chi so hp
//        }
//        //pet 7
//        if (this.player.isPet && ((Pet) this.player).typePet == 7// chi so lam sao bac tu cho dj
//                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA) {
//            this.hpMax += ((long) this.hpMax * 60 / 100);//chi so hp
//        }
//        if (this.player.isPet && ((Pet) this.player).typePet == 7// chi so lam sao bac tu cho dj
//                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2) {
//            this.hpMax += ((long) this.hpMax * 60 / 100);//chi so hp
//        }
//        if (this.player.isPet && ((Pet) this.player).typePet == 7// chi so lam sao bac tu cho dj
//                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA3) {
//            this.hpMax += ((long) this.hpMax * 60 / 100);//chi so hp
//        }
//        if (this.player.isPet && ((Pet) this.player).typePet == 7// chi so lam sao bac tu cho dj
//                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA4) {
//            this.hpMax += ((long) this.hpMax * 60 / 100);//chi so hp
//        }
//        //pet 8
//        if (this.player.isPet && ((Pet) this.player).typePet == 8// chi so lam sao bac tu cho dj
//                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA) {
//            this.hpMax += ((long) this.hpMax * 70 / 100);//chi so hp
//        }
//        if (this.player.isPet && ((Pet) this.player).typePet == 8// chi so lam sao bac tu cho dj
//                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2) {
//            this.hpMax += ((long) this.hpMax * 70 / 100);//chi so hp
//        }
//        if (this.player.isPet && ((Pet) this.player).typePet == 8// chi so lam sao bac tu cho dj
//                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA3) {
//            this.hpMax += ((long) this.hpMax * 70 / 100);//chi so hp
//        }
//        if (this.player.isPet && ((Pet) this.player).typePet == 8// chi so lam sao bac tu cho dj
//                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA4) {
//            this.hpMax += ((long) this.hpMax * 70 / 100);//chi so hp
//        }
        //phù
        if (this.player.zone != null && MapService.gI().isMapBlackBallWar(this.player.zone.map.mapId)) {
            this.hpMax *= this.player.effectSkin.xHPKI;
        }
        //+hp đệ
        if (this.player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
            this.hpMax += this.player.pet.nPoint.hpMax;
        }
        //huýt sáo
        if (!this.player.isPet || ((Pet) this.player).status != Pet.FUSION || this.player.type != 1) {
            if (this.player.effectSkill.tiLeHPHuytSao != 0) {
                this.hpMax += ((long) this.hpMax * this.player.effectSkill.tiLeHPHuytSao / 100L);

            }
        }
        //bổ huyết
        if (this.player.itemTime != null && this.player.itemTime.isUseBoHuyet) {
            this.hpMax *= 2;
        }// item sieu cap
        if (this.player.itemTime != null && this.player.itemTime.isUseBoHuyetSC) {
            this.hpMax *= 2.2;
        }
        if (this.player.zone != null && MapService.gI().isMapCold(this.player.zone.map)
                && !this.isKhongLanh) {
            this.hpMax /= 2;
        }
        //mèo mun
        if (this.player.effectFlagBag.useMeoMun) {
            this.hpMax += ((long) this.hpMax * 15 / 100);
        }
        if (this.player.itemTime != null && this.player.itemTime.istrbhp) {
            this.hpMax += (this.mpMax * 30 / 100);
        }
        this.hpMax -= ((long) this.hpMax * tlSubHP / 100);
    }

    // (hp sư phụ + hp đệ tử ) + 15%
    // (hp sư phụ + 15% +hp đệ tử)
    private void setHp() {
        if (this.hp > this.hpMax) {
            this.hp = this.hpMax;
        }
    }

    private void setMpMax() {
        this.mpMax = this.mpg;
        this.mpMax += this.mpAdd;
        //đồ
        for (Integer tl : this.tlMp) {
            this.mpMax += (this.mpMax * tl / 100);
        }
//        if (this.player.setClothes.picolo == 5) {
//            this.mpMax *= 3;
//        }
        //ngọc rồng đen 3 sao
        if (this.player.rewardBlackBall.timeOutOfDateReward[2] > System.currentTimeMillis()) {
            this.mpMax += (this.mpMax * RewardBlackBall.R3S_1 / 100);
        }
        //set worldcup
        if (this.player.setClothes.worldcup == 2) {
            this.mpMax += ((long) this.mpMax * 10 / 100);
        }
        //pet mabư
        if (this.player.isPet && ((Pet) this.player).typePet == 1
                && ((Pet) this.player).master.fusion.typeFusion != ConstPlayer.NON_FUSION) {
            this.mpMax += ((long) this.mpMax * 20 / 100);
        }
        //pet br
        if (this.player.isPet && ((Pet) this.player).typePet == 2
                && ((Pet) this.player).master.fusion.typeFusion != ConstPlayer.NON_FUSION) {
            this.mpMax += ((long) this.mpMax * 40 / 100);//MP berus
        }

        //New Pet
        if (this.player.isPet && ((Pet) this.player).master.fusion.typeFusion != ConstPlayer.NON_FUSION && ((Pet) this.player).typePet >= 4) {
            this.mpMax += ((long) this.mpMax * (((Pet) this.player).typePet * 5 + 10) / 100);
        }

//        //pet 4
//        if (this.player.isPet && ((Pet) this.player).typePet == 4// chi so lam sao bac tu cho dj
//                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA) {
//            this.mpMax += ((long) this.mpMax * 30 / 100);//chi so hp
//        }
//        if (this.player.isPet && ((Pet) this.player).typePet == 4// chi so lam sao bac tu cho dj
//                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2) {
//            this.mpMax += ((long) this.mpMax * 30 / 100);//chi so hp
//        }
//        if (this.player.isPet && ((Pet) this.player).typePet == 4// chi so lam sao bac tu cho dj
//                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA3) {
//            this.mpMax += ((long) this.mpMax * 30 / 100);//chi so hp
//        }
//        if (this.player.isPet && ((Pet) this.player).typePet == 4// chi so lam sao bac tu cho dj
//                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA4) {
//            this.mpMax += ((long) this.mpMax * 30 / 100);//chi so hp
//        }
//        //pet 5
//        if (this.player.isPet && ((Pet) this.player).typePet == 5// chi so lam sao bac tu cho dj
//                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA) {
//            this.mpMax += ((long) this.mpMax * 40 / 100);//chi so hp
//        }
//        if (this.player.isPet && ((Pet) this.player).typePet == 5// chi so lam sao bac tu cho dj
//                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2) {
//            this.mpMax += ((long) this.mpMax * 40 / 100);//chi so hp
//        }
//        if (this.player.isPet && ((Pet) this.player).typePet == 5// chi so lam sao bac tu cho dj
//                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA3) {
//            this.mpMax += ((long) this.mpMax * 40 / 100);//chi so hp
//        }
//        if (this.player.isPet && ((Pet) this.player).typePet == 5// chi so lam sao bac tu cho dj
//                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA4) {
//            this.mpMax += ((long) this.mpMax * 40 / 100);//chi so hp
//        }
//        //pet 6
//        if (this.player.isPet && ((Pet) this.player).typePet == 6// chi so lam sao bac tu cho dj
//                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA) {
//            this.mpMax += ((long) this.mpMax * 50 / 100);//chi so hp
//        }
//        if (this.player.isPet && ((Pet) this.player).typePet == 6// chi so lam sao bac tu cho dj
//                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2) {
//            this.mpMax += ((long) this.mpMax * 50 / 100);//chi so hp
//        }
//        if (this.player.isPet && ((Pet) this.player).typePet == 6// chi so lam sao bac tu cho dj
//                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA3) {
//            this.mpMax += ((long) this.mpMax * 50 / 100);//chi so hp
//        }
//        if (this.player.isPet && ((Pet) this.player).typePet == 6// chi so lam sao bac tu cho dj
//                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA4) {
//            this.mpMax += ((long) this.mpMax * 50 / 100);//chi so hp
//        }
//        //pet 7
//        if (this.player.isPet && ((Pet) this.player).typePet == 7// chi so lam sao bac tu cho dj
//                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA) {
//            this.mpMax += ((long) this.mpMax * 60 / 100);//chi so hp
//        }
//        if (this.player.isPet && ((Pet) this.player).typePet == 7// chi so lam sao bac tu cho dj
//                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2) {
//            this.mpMax += ((long) this.mpMax * 60 / 100);//chi so hp
//        }
//        if (this.player.isPet && ((Pet) this.player).typePet == 7// chi so lam sao bac tu cho dj
//                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA3) {
//            this.mpMax += ((long) this.mpMax * 60 / 100);//chi so hp
//        }
//        if (this.player.isPet && ((Pet) this.player).typePet == 7// chi so lam sao bac tu cho dj
//                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA4) {
//            this.mpMax += ((long) this.mpMax * 60 / 100);//chi so hp
//        }
//        //pet 8
//        if (this.player.isPet && ((Pet) this.player).typePet == 8// chi so lam sao bac tu cho dj
//                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA) {
//            this.mpMax += ((long) this.mpMax * 70 / 100);//chi so hp
//        }
//        if (this.player.isPet && ((Pet) this.player).typePet == 8// chi so lam sao bac tu cho dj
//                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2) {
//            this.mpMax += ((long) this.mpMax * 70 / 100);//chi so hp
//        }
//        if (this.player.isPet && ((Pet) this.player).typePet == 8// chi so lam sao bac tu cho dj
//                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA3) {
//            this.mpMax += ((long) this.mpMax * 70 / 100);//chi so hp
//        }
//        if (this.player.isPet && ((Pet) this.player).typePet == 8// chi so lam sao bac tu cho dj
//                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA4) {
//            this.mpMax += ((long) this.mpMax * 70 / 100);//chi so hp
//        }
        //hợp thể
        if (this.player.fusion.typeFusion != 0) {
            this.mpMax += this.player.pet.nPoint.mpMax;
        }
        //bổ khí
        if (this.player.itemTime != null && this.player.itemTime.isUseBoKhi) {
            this.mpMax *= 2;
        }
        if (this.player.itemTime != null && this.player.itemTime.isUseBoKhiSC) {
            this.mpMax *= 2.2;
        }
        //phù
        if (this.player.zone != null && MapService.gI().isMapBlackBallWar(this.player.zone.map.mapId)) {
            this.mpMax *= this.player.effectSkin.xHPKI;
        }
        //xiên cá
        if (this.player.effectFlagBag.useXienCa) {
            this.mpMax += ((long) this.mpMax * 15 / 100);
        }
        if (this.player.itemTime != null && this.player.itemTime.istrbki) {
            this.mpMax += (this.mpMax * 30 / 100);
        }
        this.mpMax -= ((long) this.mpMax * tlSubKI / 100);
    }

    private void setMp() {
        if (this.mp > this.mpMax) {
            this.mp = this.mpMax;
        }
    }

    private void setDame() {
        this.dame = this.dameg;
        this.dame += this.dameAdd;
        int maxDameBeauty = 0;
        if (!this.player.isBoss && !this.player.isNewPet && !this.player.isNewPet1 && this.player.zone != null)
            for (Player plDep : this.player.zone.getNotBosses()) {
                if (Util.getDistance(this.player, plDep) < 200 && plDep.nPoint.dameBeauty > 0) {
                    if(plDep.nPoint.dameBeauty > maxDameBeauty)
                        maxDameBeauty = plDep.nPoint.dameBeauty ;
                }
            }
        if(maxDameBeauty > 0){
            this.dame += (this.dame * maxDameBeauty / 100);
        }
        //đồ
        for (Integer tl : this.tlDame) {
            this.dame += ((long) this.dame * tl / 100);
        }
//        for (Integer tl : this.tlSDDep) {
//            this.dame += ((long) this.dame * tl / 100);
//        }
        //pet mabu
        if (this.player.isPet && ((Pet) this.player).typePet == 1
                && ((Pet) this.player).master.fusion.typeFusion != ConstPlayer.NON_FUSION) {
            this.dame += ((long) this.dame * 20 / 100);
        }
        //pet bill
        if (this.player.isPet && ((Pet) this.player).typePet == 2
                && ((Pet) this.player).master.fusion.typeFusion != ConstPlayer.NON_FUSION) {
            this.dame += ((long) this.dame * 40 / 100);
        }

        //New Pet
        if (this.player.isPet && ((Pet) this.player).master.fusion.typeFusion != ConstPlayer.NON_FUSION && ((Pet) this.player).typePet >= 4) {
            this.dame += ((long) this.dame * (((Pet) this.player).typePet * 5 + 10) / 100);
        }

//        //pet 4
//        if (this.player.isPet && ((Pet) this.player).typePet == 4
//                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA) {
//            this.dame += ((long) this.dame * 30 / 100);
//        }
//        if (this.player.isPet && ((Pet) this.player).typePet == 4
//                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2) {
//            this.dame += ((long) this.dame * 30 / 100);
//        }
//        if (this.player.isPet && ((Pet) this.player).typePet == 4
//                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA3) {
//            this.dame += ((long) this.dame * 30 / 100);
//        }
//        if (this.player.isPet && ((Pet) this.player).typePet == 4
//                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA4) {
//            this.dame += ((long) this.dame * 30 / 100);
//        }
//        //pet 5
//        if (this.player.isPet && ((Pet) this.player).typePet == 5// chi so lam sao bac tu cho dj
//                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA) {
//            this.dame += ((long) this.dame * 40 / 100);//đame
//        }
//        if (this.player.isPet && ((Pet) this.player).typePet == 5// chi so lam sao bac tu cho dj
//                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2) {
//            this.dame += ((long) this.dame * 40 / 100);//đame
//        }
//        if (this.player.isPet && ((Pet) this.player).typePet == 5// chi so lam sao bac tu cho dj
//                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA3) {
//            this.dame += ((long) this.dame * 40 / 100);//đame
//        }
//        if (this.player.isPet && ((Pet) this.player).typePet == 5// chi so lam sao bac tu cho dj
//                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA4) {
//            this.dame += ((long) this.dame * 40 / 100);//đame
//        }
//        //pet 6
//        if (this.player.isPet && ((Pet) this.player).typePet == 6// chi so lam sao bac tu cho dj
//                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA) {
//            this.dame += ((long) this.dame * 50 / 100);//đame
//        }
//        if (this.player.isPet && ((Pet) this.player).typePet == 6// chi so lam sao bac tu cho dj
//                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2) {
//            this.dame += ((long) this.dame * 50 / 100);//cđame
//        }
//        if (this.player.isPet && ((Pet) this.player).typePet == 6// chi so lam sao bac tu cho dj
//                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA3) {
//            this.dame += ((long) this.dame * 50 / 100);//đame
//        }
//        if (this.player.isPet && ((Pet) this.player).typePet == 6// chi so lam sao bac tu cho dj
//                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA4) {
//            this.dame += ((long) this.dame * 50 / 100);//đame
//        }
//        //pet 7
//        if (this.player.isPet && ((Pet) this.player).typePet == 7// chi so lam sao bac tu cho dj
//                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA) {
//            this.dame += ((long) this.dame * 60 / 100);//chi so hp
//        }
//        if (this.player.isPet && ((Pet) this.player).typePet == 7// chi so lam sao bac tu cho dj
//                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2) {
//            this.dame += ((long) this.dame * 60 / 100);//chi so hp
//        }
//        if (this.player.isPet && ((Pet) this.player).typePet == 7// chi so lam sao bac tu cho dj
//                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA3) {
//            this.dame += ((long) this.dame * 60 / 100);//chi so hp
//        }
//        if (this.player.isPet && ((Pet) this.player).typePet == 7// chi so lam sao bac tu cho dj
//                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA4) {
//            this.dame += ((long) this.dame * 60 / 100);//chi so hp
//        }
//        //pet 8
//        if (this.player.isPet && ((Pet) this.player).typePet == 8// chi so lam sao bac tu cho dj
//                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA) {
//            this.dame += ((long) this.dame * 70 / 100);//chi so hp
//        }
//        if (this.player.isPet && ((Pet) this.player).typePet == 8// chi so lam sao bac tu cho dj
//                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2) {
//            this.dame += ((long) this.dame * 70 / 100);//chi so hp
//        }
//        if (this.player.isPet && ((Pet) this.player).typePet == 8// chi so lam sao bac tu cho dj
//                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA3) {
//            this.dame += ((long) this.dame * 70 / 100);//chi so hp
//        }
//        if (this.player.isPet && ((Pet) this.player).typePet == 8// chi so lam sao bac tu cho dj
//                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA4) {
//            this.dame += ((long) this.dame * 70 / 100);//chi so hp
//        }
        //thức ăn
        if (!this.player.isPet && this.player.itemTime.isEatMeal
                || this.player.isPet && ((Pet) this.player).master.itemTime.isEatMeal) {
            this.dame += ((long) this.dame * 10 / 100);
        }
        //hợp thể
        if (this.player.fusion.typeFusion != 0) {
            this.dame += this.player.pet.nPoint.dame;
        }
        //cuồng nộ
        if (this.player.itemTime != null && this.player.itemTime.isUseCuongNo) {
            this.dame *= 2;
        }
        if (this.player.itemTime != null && this.player.itemTime.isUseCuongNoSC) {
            this.dame += this.dame * 2.2;
        }
        //giảm dame
        this.dame -= ((long) this.dame * tlSubSD / 100);
        //map cold
        if (this.player.zone != null && MapService.gI().isMapCold(this.player.zone.map)
                && !this.isKhongLanh) {
            this.dame /= 2;
        }
        //ngọc rồng đen 1 sao
        if (this.player.rewardBlackBall.timeOutOfDateReward[0] > System.currentTimeMillis()) {
            this.dame += ((long) this.dame * RewardBlackBall.R1S_2 / 100);
        }
        //phóng heo
        if (this.player.effectFlagBag.usePhongHeo) {
            this.dame += ((long) this.dame * 15 / 100);
        }
        //set worldcup
        if (this.player.setClothes.worldcup == 2) {
            this.tlDameCrit.add(20);
        }
        //khỉ
        if (this.player.effectSkill.isMonkey) {
            if (!this.player.isPet || (this.player.isPet
                    && ((Pet) this.player).status != Pet.FUSION)) {
                int percent = SkillUtil.getPercentDameMonkey(player.effectSkill.levelMonkey);
                this.dame += ((long) this.dame * percent / 100);
            }
        }
        if (this.player.itemTime != null && this.player.itemTime.istrbsd) {
            this.dame += (this.dame * 30 / 100);
        }
    }

    private void setDef() {
        this.def = this.defg * 4;
        this.def += this.defAdd;
        //đồ
        for (Integer tl : this.tlDef) {
            this.def += (this.def * tl / 100);
        }
        //ngọc rồng đen 2 sao
        if (this.player.rewardBlackBall.timeOutOfDateReward[1] > System.currentTimeMillis()) {
            this.def += ((long) this.def * RewardBlackBall.R2S_2 / 100);
        }
    }

    private void setCrit() {
        this.crit = this.critg;
        this.crit += this.critAdd;
        //ngọc rồng đen 3 sao
        if (this.player.rewardBlackBall.timeOutOfDateReward[2] > System.currentTimeMillis()) {
            this.crit += RewardBlackBall.R3S_2;
        }
        //biến khỉ
        if (this.player.effectSkill.isMonkey) {
            this.crit = 110;
        }
    }

    private void resetPoint() {
        this.xDameValue = 0;
        this.xDameSkill2 = false;
        this.voHieuChuong = 0;
        this.hpAdd = 0;
        this.mpAdd = 0;
        this.dameAdd = 0;
        this.defAdd = 0;
        this.critAdd = 0;
        this.tlHp.clear();
        this.tlMp.clear();
        this.tlDef.clear();
        this.tlDame.clear();
        this.tlDameCrit.clear();
        this.tlDameAttMob.clear();
        this.tlHpHoiBanThanVaDongDoi = 0;
        this.tlMpHoiBanThanVaDongDoi = 0;
        this.hpHoi = 0;
        this.mpHoi = 0;
        this.mpHoiCute = 0;
        this.tlHpHoi = 0;
        this.tlMpHoi = 0;
        this.tlHutHp = 0;
        this.tlHutMp = 0;
        this.tlHutHpMob = 0;
        this.tlHutHpMpXQ = 0;
        this.tlPST = 0;
        this.tlTNSM.clear();
        this.tlTNSMDT.clear();
        this.tlDameAttMob.clear();
        this.tlGold = 0;
        this.tlNeDon = 0;
        this.dameBeauty = 0;
        this.tlSubSD = 0;
        this.tlHpGiamODo = 0;
        this.test = 0;
        this.teleport = false;
        this.doPhaLe = false;
        this.wearingVoHinh = false;
        this.isKhongLanh = false;
        this.khangTDHS = false;
    }

    public void addHp(int hp) {
        this.hp += hp;
        if (this.hp > this.hpMax) {
            this.hp = this.hpMax;
        }
    }

    public void addMp(int mp) {
        this.mp += mp;
        if (this.mp > this.mpMax) {
            this.mp = this.mpMax;
        }
    }

    public void setHp(long hp) {
        if (hp > this.hpMax) {
            this.hp = this.hpMax;
        } else {
            this.hp = (int) hp;
        }
    }

    public void setMp(long mp) {
        if (mp > this.mpMax) {
            this.mp = this.mpMax;
        } else {
            this.mp = (int) mp;
        }
    }

    private void setIsCrit() {
        if (intrinsic != null && intrinsic.id == 25
                && this.getCurrPercentHP() <= intrinsic.param1) {
            isCrit = true;
        } else if (isCrit100) {
            isCrit100 = false;
            isCrit = true;
        } else {
            isCrit = Util.isTrue(this.crit, ConstRatio.PER100);
        }
    }

    public int getDameAttack(boolean isAttackMob) {
        setIsCrit();
        long dameAttack = this.dame;
        intrinsic = this.player.playerIntrinsic.intrinsic;
        percentDameIntrinsic = 0;
        int percentDameSkill = 0;
        int percentXDame = 0;
        Skill skillSelect = player.playerSkill.skillSelect;
        switch (skillSelect.template.id) {
            case Skill.DRAGON:
                if (intrinsic.id == 1) {
                    percentDameIntrinsic = intrinsic.param1;
                }
                percentDameSkill = skillSelect.damage;
                break;
            case Skill.KAMEJOKO:
                if (intrinsic.id == 2) {
                    percentDameIntrinsic = intrinsic.param1;
                }
                percentDameSkill = skillSelect.damage;
                if (this.player.setClothes.songoku == 5) {
                    percentXDame = 100;
                }
                if (xDameSkill2 && Util.canDoWithTime(this.lastTimeXDame, 60000)) {
                    percentXDame += (100 * xDameValue);
                    this.lastTimeXDame = System.currentTimeMillis();
                }
                break;
            case Skill.GALICK:
                if (intrinsic.id == 16) {
                    percentDameIntrinsic = intrinsic.param1;
                }
                percentDameSkill = skillSelect.damage;
                if (this.player.setClothes.kakarot == 5) {
                    percentXDame = 100;
                }
                break;
            case Skill.ANTOMIC:
                if (intrinsic.id == 17) {
                    percentDameIntrinsic = intrinsic.param1;
                }
                percentDameSkill = skillSelect.damage;
                if (xDameSkill2 && Util.canDoWithTime(this.lastTimeXDame, 60000)) {
                    percentXDame += (100 * xDameValue);
                    this.lastTimeXDame = System.currentTimeMillis();
                }
                break;
            case Skill.DEMON:
                if (intrinsic.id == 8) {
                    percentDameIntrinsic = intrinsic.param1;
                }
                percentDameSkill = skillSelect.damage;


                break;
            case Skill.MASENKO:
                if (intrinsic.id == 9) {
                    percentDameIntrinsic = intrinsic.param1;
                }
                percentDameSkill = skillSelect.damage;
                if (xDameSkill2 && Util.canDoWithTime(this.lastTimeXDame, 60000)) {
                    percentXDame += (100 * xDameValue);
                    this.lastTimeXDame = System.currentTimeMillis();
                }
                break;
            case Skill.KAIOKEN:
                if (intrinsic.id == 26) {
                    percentDameIntrinsic = intrinsic.param1;
                }
                percentDameSkill = skillSelect.damage;
                if (this.player.setClothes.kirin == 5) {
                    percentXDame = 100;
                }
                break;
            case Skill.LIEN_HOAN:
                if (intrinsic.id == 13) {
                    percentDameIntrinsic = intrinsic.param1;
                }
                percentDameSkill = skillSelect.damage;
                if (this.player.setClothes.ocTieu == 5) {
                    percentXDame = 50;
                }
                break;
            case Skill.MAKANKOSAPPO:
                percentDameSkill = skillSelect.damage;
                dameAttack = (int) ((long) this.mp * percentDameSkill / 100);
                if (this.player.setClothes.picolo == 5) {
                    percentXDame = 50; //set picolo
                }
                break;
            case Skill.DICH_CHUYEN_TUC_THOI:
                dameAttack *= 2;
                dameAttack = Util.nextInt((int) (dameAttack - (dameAttack * 5 / 100)),
                        (int) (dameAttack + (dameAttack * 5 / 100)));
                return (int) dameAttack;

            case Skill.QUA_CAU_KENH_KHI:
                int dame = this.dame * 40;
                if (this.player.setClothes.kirin == 5) {
                    dame *= 2;
                }
                dame = dame + (Util.nextInt(-5, 5) * dame / 100);
                return dame;
        }
        if (intrinsic.id == 18 && this.player.effectSkill.isMonkey) {
            percentDameIntrinsic = intrinsic.param1;
        }
        if (percentDameSkill != 0) {
            dameAttack = dameAttack * percentDameSkill / 100;
        }
        dameAttack += (dameAttack * percentDameIntrinsic / 100);
        dameAttack += (dameAttack * dameAfter / 100);

        if (isAttackMob) {
            for (Integer tl : this.tlDameAttMob) {
                dameAttack += (dameAttack * tl / 100);
            }
        }
        dameAfter = 0;
        if (this.player.isPet && ((Pet) this.player).master.charms.tdDeTu > System.currentTimeMillis()) {
            dameAttack *= 2;
        }
        if (isCrit) {
            dameAttack *= 2;
            for (Integer tl : this.tlDameCrit) {
                dameAttack += (dameAttack * tl / 100);
            }
        }
        dameAttack += ((long) dameAttack * percentXDame / 100);
        dameAttack = Util.nextInt((int) (dameAttack - (dameAttack * 8 / 100)), (int) (dameAttack + (dameAttack * 8 / 100)));
        if (player.isPl()) {
            if (player.inventory.haveOption(player.inventory.itemsBody, 5, 159)) {
                if (Util.canDoWithTime(player.lastTimeUseOption, 60000) && (player.playerSkill.skillSelect.skillId == Skill.KAMEJOKO || player.playerSkill.skillSelect.skillId == Skill.ANTOMIC || player.playerSkill.skillSelect.skillId == Skill.MASENKO)) {
                    dameAttack *= player.inventory.getParam(player.inventory.itemsBody.get(5), 159);
                    player.lastTimeUseOption = System.currentTimeMillis();
                }
            }
        }
        //check activation set
        return Math.toIntExact(dameAttack > Integer.MAX_VALUE ? Integer.MAX_VALUE - 1 : dameAttack);
    }

    public int getCurrPercentHP() {
        if (this.hpMax == 0) {
            return 100;
        }
        return (int) ((long) this.hp * 100 / this.hpMax);
    }

    public int getCurrPercentMP() {
        return (int) ((long) this.mp * 100 / this.mpMax);
    }

    public void setFullHpMp() {
        this.hp = this.hpMax;
        this.mp = this.mpMax;

    }

    public void subHP(int sub) {
        this.hp -= sub;
        if (this.hp < 0) {
            this.hp = 0;
        }
    }

    public void subMP(int sub) {
        this.mp -= sub;
        if (this.mp < 0) {
            this.mp = 0;
        }
    }

    public long calSucManhTiemNang(long tiemNang) {
        if (power < getPowerLimit()) {
            for (Integer tl : this.tlTNSM) {
                tiemNang += ((long) tiemNang * (long) tl / 100L);
            }
            if (this.player.cFlag != 0) {
                if (this.player.cFlag == 8) {
                    tiemNang += (tiemNang * 10L / 100L);
                } else {
                    tiemNang += (tiemNang * 5L / 100L);
                }
            }
            long tn = tiemNang;
            if (this.player.charms.tdTriTue > System.currentTimeMillis()) {
                tiemNang += tn;
            }
            if (this.player.charms.tdTriTue3 > System.currentTimeMillis()) {
                tiemNang += tn * 2L;
            }
            if (this.player.charms.tdTriTue4 > System.currentTimeMillis()) {
                tiemNang += tn * 3L;
            }
            if (this.intrinsic != null && this.intrinsic.id == 24) {
                tiemNang += (tiemNang * (long) this.intrinsic.param1 / 100L);
            }
            if (this.power >= 60000000000L) {
                tiemNang -= (tiemNang * 85L / 100L);
            }
            if (this.player.isPet) {
                if (((Pet) this.player).master.charms.tdDeTu > System.currentTimeMillis()) {
                    tiemNang += tn * 2L;
                }
                for (Integer tl : this.tlTNSMDT) {
                    tiemNang += ((long) tiemNang * (long) tl / 100L);
                }
            }
            tiemNang *= Manager.RATE_EXP_SERVER;
            tiemNang = calSubTNSM(tiemNang);
            if (tiemNang <= 0) {
                tiemNang = 1L;
            }
        } else {
            tiemNang = 10L;
        }
        return (tiemNang > Integer.MAX_VALUE ? Integer.MAX_VALUE - 1 : tiemNang);
    }

    public long calSubTNSM(long tiemNang) {
        if ((player.zone.map.mapId == 122 || player.zone.map.mapId == 123 || player.zone.map.mapId == 124)) {
            if (player.nPoint.power < 15_000_000_000L)
                tiemNang *= 30L;
            else tiemNang = 10;
        }
        if (power >= 130_000_000_000L) {
            tiemNang -= (tiemNang * 90L / 100L);
        } else if (power >= 110_000_000_000L) {
            tiemNang -= (tiemNang * 80L / 100L);
        } else if (power >= 100_000_000_000L) {
            tiemNang -= (tiemNang * 70L / 100L);
        } else if (power >= 90_000_000_000L) {
            tiemNang -= (tiemNang * 60L / 100L);
        } else if (power >= 80_000_000_000L) {
            tiemNang -= (tiemNang * 50L / 100L);
        }

        return tiemNang;
    }

    public short getTileHutHp(boolean isMob) {
        if (isMob) {
            return (short) (this.tlHutHp + this.tlHutHpMob);
        } else {
            return this.tlHutHp;
        }
    }

    public short getTiLeHutMp() {
        return this.tlHutMp;
    }

    public int subDameInjureWithDeff(int dame) {
        int def = this.def;
        dame -= def;
        if (this.player.itemTime.isUseGiapXen) {
            dame /= 2;
        }
        if (dame < 0) {
            dame = 1;
        }
        return dame;
    }

    /*------------------------------------------------------------------------*/
    public boolean canOpenPower() {
        return this.power >= getPowerLimit();
    }

    public long getPowerLimit() {
        switch (limitPower) {
            case 0:
                return 17_999_999_999L;
            case 1:
                return 18_999_999_999L;
            case 2:
                return 20_999_999_999L;
            case 3:
                return 24_999_999_999L;
            case 4:
                return 30_999_999_999L;
            case 5:
                return 40_999_999_999L;
            case 6:
                return 60_999_999_999L;
            case 7:
                return 80_999_999_999L;
            case 8:
                return 110_000_000_000L;
            case 9:
                return 150_000_000_000L;
            default:
                return 0;
        }
    }

    public long getPowerNextLimit() {
        switch (limitPower + 1) {
            case 0:
                return 17_999_999_999L;
            case 1:
                return 18_999_999_999L;
            case 2:
                return 20_999_999_999L;
            case 3:
                return 24_999_999_999L;
            case 4:
                return 30_999_999_999L;
            case 5:
                return 40_999_999_999L;
            case 6:
                return 60_999_999_999L;
            case 7:
                return 80_999_999_999L;
            case 8:
                return 110_000_000_000L;
            case 9:
                return 150_000_000_000L;
            default:
                return 0;
        }
    }

    public int getHpMpLimit() {
        if (limitPower == 0) {
            return 220000;
        }
        if (limitPower == 1) {
            return 240000;
        }
        if (limitPower == 2) {
            return 300000;
        }
        if (limitPower == 3) {
            return 350000;
        }
        if (limitPower == 4) {
            return 400000;
        }
        if (limitPower == 5) {
            return 450000;
        }
        if (limitPower == 6) {
            return 500000;
        }
        if (limitPower == 7) {
            return 550000;
        }
        if (limitPower == 8) {
            return 580000;
        }
        if (limitPower == 9) {
            return 620000;
        }
        return 0;
    }

    public int getDameLimit() {
        if (limitPower == 0) {
            return 11000;
        }
        if (limitPower == 1) {
            return 12000;
        }
        if (limitPower == 2) {
            return 15000;
        }
        if (limitPower == 3) {
            return 18000;
        }
        if (limitPower == 4) {
            return 20000;
        }
        if (limitPower == 5) {
            return 22000;
        }
        if (limitPower == 6) {
            return 25000;
        }
        if (limitPower == 7) {
            return 28000;
        }
        if (limitPower == 8) {
            return 31000;
        }
        if (limitPower == 9) {
            return 33000;
        }
        return 0;
    }

    public short getDefLimit() {
        if (limitPower == 0) {
            return 550;
        }
        if (limitPower == 1) {
            return 600;
        }
        if (limitPower == 2) {
            return 700;
        }
        if (limitPower == 3) {
            return 800;
        }
        if (limitPower == 4) {
            return 1000;
        }
        if (limitPower == 5) {
            return 1200;
        }
        if (limitPower == 6) {
            return 1400;
        }
        if (limitPower == 7) {
            return 1600;
        }
        if (limitPower == 8) {
            return 1700;
        }
        if (limitPower == 9) {
            return 1800;
        }
        return 0;
    }

    public byte getCritLimit() {
        if (limitPower == 0) {
            return 5;
        }
        if (limitPower == 1) {
            return 6;
        }
        if (limitPower == 2) {
            return 7;
        }
        if (limitPower == 3) {
            return 8;
        }
        if (limitPower == 4) {
            return 9;
        }
        if (limitPower == 5) {
            return 10;
        }
        if (limitPower == 6) {
            return 10;
        }
        if (limitPower == 7) {
            return 10;
        }
        if (limitPower == 8) {
            return 10;
        }
        if (limitPower == 9) {
            return 10;
        }
        return 0;
    }

    //**************************************************************************
    //POWER - TIEM NANG
    public void powerUp(long power) {
        this.power += power;
        TaskService.gI().checkDoneTaskPower(player, this.power);
    }

    public void tiemNangUp(long tiemNang) {
        this.tiemNang += tiemNang;
    }

    public void increasePoint(byte type, short point) {
        if (point <= 0 || point > 100) {
            return;
        }
        long tiemNangUse = 0;
        if (type == 0) {
            int pointHp = point * 20;
            tiemNangUse = point * (2L * (this.hpg + 1000) + pointHp - 20) / 2;
            if ((this.hpg + pointHp) <= getHpMpLimit()) {
                if (doUseTiemNang(tiemNangUse)) {
                    hpg += pointHp;
                }
            } else {
                Service.gI().sendThongBaoOK(player, "Vui lòng mở giới hạn sức mạnh");
                return;
            }
        }
        if (type == 1) {
            int pointMp = point * 20;
            tiemNangUse = point * (2L * (this.mpg + 1000) + pointMp - 20) / 2;
            if ((this.mpg + pointMp) <= getHpMpLimit()) {
                if (doUseTiemNang(tiemNangUse)) {
                    mpg += pointMp;
                }
            } else {
                Service.gI().sendThongBaoOK(player, "Vui lòng mở giới hạn sức mạnh");
                return;
            }
        }
        if (type == 2) {
            tiemNangUse = point * (2L * this.dameg + point - 1) / 2 * 100;
            if ((this.dameg + point) <= getDameLimit()) {
                if (doUseTiemNang(tiemNangUse)) {
                    dameg += point;
                }
            } else {
                Service.gI().sendThongBaoOK(player, "Vui lòng mở giới hạn sức mạnh");
                return;
            }
        }
        if (type == 3) {
            tiemNangUse = ((long) point * (2L * (this.defg + 5L) + (long) (point - 1)) / 2L * 100000L);
            if ((this.defg + point) <= getDefLimit()) {
                if (doUseTiemNang(tiemNangUse)) {
                    defg += point;
                }
            } else {
                Service.gI().sendThongBaoOK(player, "Vui lòng mở giới hạn sức mạnh");
                return;
            }
        }
        if (type == 4) {
            tiemNangUse = 50000000L;
            for (int i = 0; i < this.critg; i++) {
                tiemNangUse *= 5L;
            }
            if ((this.critg + point) <= getCritLimit()) {
                if (doUseTiemNang(tiemNangUse)) {
                    critg += point;
                }
            } else {
                Service.gI().sendThongBaoOK(player, "Vui lòng mở giới hạn sức mạnh");
                return;
            }
        }
        Service.gI().point(player);
    }

    private boolean doUseTiemNang(long tiemNang) {
        if (this.tiemNang < tiemNang) {
            Service.gI().sendThongBaoOK(player, "Bạn không đủ tiềm năng");
            return false;
        }
        if (this.tiemNang - tiemNang >= 0) {
            this.tiemNang -= tiemNang;
            TaskService.gI().checkDoneTaskUseTiemNang(player);
            return true;
        }
        return false;
    }

    //--------------------------------------------------------------------------
    private long lastTimeHoiPhuc;
    private long lastTimeHoiStamina;

    public void update() {
        if (player != null && player.effectSkill != null) {
            if (player.effectSkill.isCharging && player.effectSkill.countCharging < 10) {
                int tiLeHoiPhuc = SkillUtil.getPercentCharge(player.playerSkill.skillSelect.point);
                if (!player.isDie() && !player.effectSkill.isHaveEffectSkill() && (hp < hpMax || mp < mpMax)) {
                    PlayerService.gI().hoiPhuc(player, hpMax / 100 * tiLeHoiPhuc,
                            mpMax / 100 * tiLeHoiPhuc);
                    if (player.effectSkill.countCharging % 3 == 0) {
                        Service.gI().chat(player, "Phục hồi năng lượng " + getCurrPercentHP() + "%");
                    }
                } else {
                    EffectSkillService.gI().stopCharge(player);
                }
                if (++player.effectSkill.countCharging >= 10) {
                    EffectSkillService.gI().stopCharge(player);
                }
            }
            if (Util.canDoWithTime(lastTimeHoiPhuc, 30000)) {
                PlayerService.gI().hoiPhuc(this.player, hpHoi, mpHoi);
                this.lastTimeHoiPhuc = System.currentTimeMillis();
            }
            if (Util.canDoWithTime(lastTimeHoiStamina, 60000) && this.stamina < this.maxStamina) {
                this.stamina++;
                this.lastTimeHoiStamina = System.currentTimeMillis();
                if (!this.player.isBoss && !this.player.isPet) {
                    PlayerService.gI().sendCurrentStamina(this.player);
                }
            }
            setBasePoint();
            Service.gI().point(player);
        }
        //hồi phục 30s
        //hồi phục thể lực
    }

    public void dispose() {
        this.intrinsic = null;
        this.player = null;
        this.tlHp = null;
        this.tlMp = null;
        this.tlDef = null;
        this.tlDame = null;
        this.tlDameAttMob = null;
        this.dameBeauty = 0;
        this.tlTNSM = null;
        this.tlTNSMDT = null;
        this.xDameValue = 0;
        this.xDameSkill2 = false;
    }
}