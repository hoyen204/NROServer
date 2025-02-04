package com.arriety.card;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Dev Duy Peo
 */
public class Card {
    public short id;
    public byte amount;
    public byte maxAmount;
    public byte level;
    public byte used;
    public List<OptionCard> options;

    public Card(){
        id = -1;
        amount = 0;
        maxAmount = 0;
        level = 0;
        used = 0;
        options = new ArrayList<>();
    }
    
    public Card(byte m , List<OptionCard> o){
        maxAmount = m;
        options = o;
    }
    
    public Card(short i ,byte a,byte ma , byte le, List<OptionCard> o){
        id = i;
        amount = a;
        maxAmount = ma;
        level = le;
        options = o;
    }
    
    public Card(short i ,byte a,byte ma , byte le, List<OptionCard> o,byte u){
        id = i;
        amount = a;
        maxAmount = ma;
        level = le;
        options = o;
        used = u;
    }

    @Override
    public String toString() {
        return "{\"id\":\"" + id +
                "\",\"amount\":\"" + amount +
                "\",\"max\":\"" + maxAmount +
                "\",\"option\":" + options +
                ",\"level\":\"" + level +
                "\",\"used\":\"" + used +
                "\"}";
    }


}