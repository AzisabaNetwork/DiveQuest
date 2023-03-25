package com.flora30.divequest.mission.Type;


import com.flora30.divequest.mission.Mission;

public class StoryMission extends Mission {

    private int npcId = -1;

    public StoryMission(){

    }

    public StoryMission(int npcId){
        this.npcId = npcId;
    }

    public boolean check(int npcId){
        return npcId == this.npcId;
    }

    public int getNpcId() {
        return npcId;
    }
}
