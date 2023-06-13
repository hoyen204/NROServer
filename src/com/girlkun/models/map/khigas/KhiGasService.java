package com.girlkun.models.map.khigas;

import com.girlkun.models.boss.bdkb.TrungUyXanhLo;
import com.girlkun.models.item.Item;
import com.girlkun.models.map.khigas.KhiGas;
import com.girlkun.models.player.Player;
import com.girlkun.services.InventoryServiceNew;
import com.girlkun.services.Service;
import com.girlkun.utils.Logger;

/**
 *
 * @author BTH
 *
 */
public class KhiGasService {

    private static KhiGasService i;

    private KhiGasService() {

    }

    public static KhiGasService gI() {
        if (i == null) {
            i = new KhiGasService();
        }
        return i;
    }


    public void openKhiGas(Player player, byte level) {
        if (level >= 1 && level <= 110) {
            if (player.clan != null && player.clan.khigas == null) {
                Item item = InventoryServiceNew.gI().findItemBag(player, 611);
                if (item != null && item.quantity > 0) {
                    KhiGas Khigas = null;
                    for (KhiGas kg : KhiGas.KHI_GAS) {
                        if (!kg.isOpened) {
                            Khigas = kg;
                            break;
                        }
                    }
                    if (Khigas != null) {
                        InventoryServiceNew.gI().subQuantityItemsBag(player, item, 1);
                        InventoryServiceNew.gI().sendItemBags(player);
                        Khigas.openKhiGas(player, player.clan, level);
                        try {
                            long totalDame = 0;
                            long totalHp = 0;
                            for (Player play : player.clan.membersInGame) {
                                totalDame += play.nPoint.dame;
                                totalHp += play.nPoint.hpMax;
                            }
                            long dame = (totalHp / 20) * (level);
                            long hp = (totalDame * 4) * (level);
                            if (dame >= 2000000000L) {
                                dame = 2000000000L;
                            }
                            if (hp >= 2000000000L) {
                                hp = 2000000000L;
                            }
                            new TrungUyXanhLo(player.clan.khigas.getMapById(137), player.clan.khigas.level, (int) dame, (int) hp);
                        } catch (Exception e) {
                            Logger.logException(KhiGasService.class, e, "Lỗi init boss");
                        }
                    } else {
                        Service.getInstance().sendThongBao(player, "Bản đồ kho báu đã đầy, vui lòng quay lại sau");
                    }
                } else {
                    Service.getInstance().sendThongBao(player, "Yêu cầu có bản đồ kho báu");
                }
            } else {
                Service.getInstance().sendThongBao(player, "Không thể thực hiện");
            }
        } else {
            Service.getInstance().sendThongBao(player, "Không thể thực hiện");
        }
    }
}
