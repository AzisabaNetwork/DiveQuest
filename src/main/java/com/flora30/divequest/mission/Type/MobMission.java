package com.flora30.divequest.mission.Type;


import com.flora30.divequest.mission.Mission;

public class MobMission extends Mission {

    private final String mobName;

    public MobMission(String name){
        mobName = name;
    }

    public boolean check(String name){
        return name.equals(mobName);
    }
}
