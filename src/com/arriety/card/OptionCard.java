package com.arriety.card;

public class OptionCard {
    public int id; 
    public int param;
    public byte active;
    
    public OptionCard(int i , int p , byte a){
        id = i;
        param = p;
        active = a;
    }

    @Override
    public String toString() {
        return "{\"id\":\"" + id +
                "\",\"active\":\"" + active +
                "\",\"param\":\"" + param +
                "\"}";
    }

}