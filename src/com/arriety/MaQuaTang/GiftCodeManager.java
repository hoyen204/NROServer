/*
 * Beo Sờ tu đi ô
 */
package com.arriety.MaQuaTang;

import com.girlkun.database.GirlkunDB;
import com.girlkun.models.item.Item.ItemOption;
import com.girlkun.models.player.Player;
import com.girlkun.services.NpcService;
import com.girlkun.services.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 * @author Administrator
 */
public class GiftCodeManager {
    public String name;
    public final ArrayList<GiftCode> listGiftCode = new ArrayList<>();


    private static GiftCodeManager instance;

    public static GiftCodeManager gI() {
        if (instance == null) {
            instance = new GiftCodeManager();
        }
        return instance;
    }

    public void init() {
        try (Connection con = GirlkunDB.getConnection();) {

            PreparedStatement ps = con.prepareStatement("SELECT * FROM giftcode");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                GiftCode giftcode = new GiftCode();
                giftcode.id = rs.getInt("id");
                giftcode.code = rs.getString("code");
                giftcode.countLeft = rs.getInt("count_left");
                giftcode.datecreate = rs.getTimestamp("datecreate");
                giftcode.dateexpired = rs.getTimestamp("expired");
                JSONArray jar = (JSONArray) JSONValue.parse(rs.getString("detail"));
                if (jar != null) {
                    for (Object o : jar) {
                        JSONObject jsonObj = (JSONObject) o;
                        giftcode.detail.put(Integer.parseInt(jsonObj.get("id").toString()), Integer.parseInt(jsonObj.get("quantity").toString()));
                        jsonObj.clear();
                    }
                }

                JSONArray option = (JSONArray) JSONValue.parse(rs.getString("itemoption"));
                if (option != null) {
                    for (Object o : option) {
                        JSONObject jsonobject = (JSONObject) o;
                        giftcode.option.add(new ItemOption(Integer.parseInt(jsonobject.get("id").toString()), Integer.parseInt(jsonobject.get("param").toString())));
                        jsonobject.clear();

                    }
                }

                JSONArray listUser = (JSONArray) JSONValue.parse(rs.getString("listUser"));
                if(listUser != null){
                    for (Object o : listUser) {
                        long idPlayer = Long.parseLong(o.toString());
                        giftcode.listIdPlayer.add(idPlayer);
                    }
                }

                listGiftCode.add(giftcode);
            }
            con.close();
        } catch (Exception erorlogException) {
            erorlogException.printStackTrace();
        }
    }

    public void sizeList(Player pl) {
        Service.gI().sendThongBao(pl, "" + GiftCode.class);
    }

    public GiftCode checkUseGiftCode(int idPlayer, String code) {
        for (GiftCode giftCode : listGiftCode) {
            if (giftCode.code.equals(code) && giftCode.countLeft > 0 && !giftCode.isUsedGiftCode(idPlayer)) {
                giftCode.countLeft -= 1;
                giftCode.addPlayerUsed(idPlayer);
                return giftCode;
            }
        }
        return null;
    }

    public void checkInfomationGiftCode(Player p) {
        StringBuilder sb = new StringBuilder();
        for (GiftCode giftCode : listGiftCode) {
            sb.append("Code: ").append(giftCode.code).append(", Số lượng: ").append(giftCode.countLeft).append("\b").append(", Ngày tạo: ")
                    .append(giftCode.datecreate).append("Ngày hết hạn").append(giftCode.dateexpired);
        }

        NpcService.gI().createTutorial(p, 5073, sb.toString());
    }

    public void saveAllGiftCode() {
        for (GiftCode giftCode : listGiftCode) {
            giftCode.saveGiftCode();
        }
    }
}
