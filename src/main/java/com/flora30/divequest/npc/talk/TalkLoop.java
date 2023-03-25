package com.flora30.divequest.npc.talk;

import com.flora30.diveapi.data.player.NpcData;
import com.flora30.diveapi.plugins.CoreAPI;
import com.flora30.divequest.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TalkLoop extends Talk{
    int time;

    public TalkLoop(int time) {
        this.time = time;
    }

    @Override
    public void doTalk(Player player, NPC npc) {
        if (npc == null){
            return;
        }
        NpcData data = CoreAPI.getPlayerData(player.getUniqueId()).npcData;
        int currentProgress = data.getTalkProgress(npc.getNpcId());

        if (currentProgress >= npc.getTalkLines().size()){
            currentProgress = npc.getTalkLines().size() -1;
        }

        //Bukkit.getLogger().info("progress "+currentProgress+" -> "+(currentProgress - time));

        if (currentProgress - time < 0) {
            data.talkProgressMap.put(npc.getNpcId(),0);
            data.setFirstTalk = true;
        }
        else {
            data.talkProgressMap.put(npc.getNpcId(),currentProgress - time);
        }
    }
}
