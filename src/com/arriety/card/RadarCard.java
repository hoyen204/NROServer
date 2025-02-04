package com.arriety.card;

import java.util.ArrayList;
import java.util.List;

public class RadarCard {
    public short id;
    public short iconId;
    public byte rank;
    public byte max;
    public byte type;
    public short template;
    public short head;
    public short body;
    public short leg;
    public short bag;
    public String name;
    public String info;
    public List<OptionCard> options;
    public short require;
    public short requireLevel;
    public short auraId;

    public RadarCard()
    {
        id = -1;
        iconId = -1;
        rank = 0;
        max = 0;
        type = 0;
        template = 1;
        name = "";
        info = "";
        options = new ArrayList<>();
        require = -1;
        requireLevel = 0;
        auraId = -1;
    }
}