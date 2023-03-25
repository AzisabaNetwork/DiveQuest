package com.flora30.divequest.npc.talk;

import com.flora30.diveapi.data.player.NpcData;
import com.flora30.diveapi.plugins.CoreAPI;
import com.flora30.divequest.npc.NPC;
import org.bukkit.entity.Player;

public class CheckOtherProgress extends Check{
    private final int npcId;
    private final int progress;
    public CheckOtherProgress(String failed, int npcId, int progress) {
        super(failed);
        this.npcId = npcId;
        this.progress = progress;
    }

    @Override
    public boolean doCheck(Player player, NPC npc) {
        NpcData data = CoreAPI.getPlayerData(player.getUniqueId()).npcData;
        if(data.getTalkProgress(npcId) >= progress){
            return true;
        }
        else{
            falsedText(player,npc);
            return false;
        }
    }
}
