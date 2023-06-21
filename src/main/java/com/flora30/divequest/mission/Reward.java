package com.flora30.divequest.mission;


import com.flora30.divelib.data.talk.Talk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Reward {
    private int exp = 0;
    private double blessPoint = 0;
    private int money = 0;
    //ItemID/個数
    private Map<Integer,Integer> itemMap = new HashMap<>();
    private List<Talk> line = new ArrayList<>();

    public List<Talk> getLine() {
        return line;
    }

    public void setLine(List<Talk> line) {
        this.line = line;
    }

    public int getMoney() {
        return money;
    }

    public int getExp() {
        return exp;
    }

    public double getBlessPoint() {
        return blessPoint;
    }

    public Map<Integer, Integer> getItemMap() {
        return itemMap;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public void setBlessPoint(double blessPoint) {
        this.blessPoint = blessPoint;
    }

    public void setItemMap(Map<Integer, Integer> itemMap) {
        this.itemMap = itemMap;
    }

}
