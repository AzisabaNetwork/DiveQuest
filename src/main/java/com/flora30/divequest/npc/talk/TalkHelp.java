package com.flora30.divequest.npc.talk;

import com.flora30.diveapi.plugins.CoreAPI;
import com.flora30.divequest.npc.NPC;
import org.bukkit.entity.Player;

public class TalkHelp extends Talk{
    final int id;
    public TalkHelp(int id){
        this.id = id;
    }

    @Override
    public void doTalk(Player player, NPC npc) {
        CoreAPI.addHelp(player,id);
    }
}