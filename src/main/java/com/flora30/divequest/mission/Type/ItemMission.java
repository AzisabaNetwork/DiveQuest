package com.flora30.divequest.mission.Type;


import com.flora30.divequest.mission.Mission;

public class ItemMission extends Mission {

    private final int itemId;

    public ItemMission(int itemId){
        this.itemId = itemId;
    }

    public boolean check(int id){
        return id == itemId;
    }
}
