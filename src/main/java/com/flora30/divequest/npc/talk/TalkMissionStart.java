package com.flora30.divequest.npc.talk;

import com.flora30.divequest.mission.MissionMain;
import com.flora30.divequest.npc.NPC;
import org.bukkit.entity.Player;

public class TalkMissionStart extends Talk{
    private final String missionType;
    private final int missionId;
    public TalkMissionStart(String type, int id){
        missionType = type;
        missionId = id;
    }

    @Override
    public void doTalk(Player player, NPC npc) {
        MissionMain.startMission(missionType,missionId,player);
    }
}
