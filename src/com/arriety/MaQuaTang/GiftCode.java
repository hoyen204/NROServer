/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arriety.MaQuaTang;

import com.girlkun.database.GirlkunDB;
import com.girlkun.models.item.Item.ItemOption;
import org.json.simple.JSONArray;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * @author Administrator
 */
public class GiftCode {
    int id;
    String code;
    int countLeft;
    public HashMap<Integer, Integer> detail = new HashMap<>();
    public ArrayList<Long> listIdPlayer = new ArrayList<>();
    public ArrayList<ItemOption> option = new ArrayList<>();
    Timestamp datecreate;
    Timestamp dateexpired;

    public boolean isUsedGiftCode(long idPlayer) {
        return listIdPlayer.contains(idPlayer);
    }

    public void addPlayerUsed(long idPlayer) {
        listIdPlayer.add(idPlayer);
    }

    public void saveGiftCode() {
        JSONArray jsonArray = new JSONArray();
        for (long idUser : listIdPlayer) {
            jsonArray.add(idUser);
        }
        try {
            GirlkunDB.executeUpdate("UPDATE `giftcode` SET `count_left` = " + countLeft + ", `listUser` = '" + jsonArray.toJSONString() + "' WHERE `id` = " + id + ";");
        } catch (Exception e) {
            e.getStackTrace();
        }

    }

    public boolean timeCode() {
        return this.datecreate.getTime() > this.dateexpired.getTime() ? true : false;
    }
}
