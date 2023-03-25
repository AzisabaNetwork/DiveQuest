package com.flora30.divequest.npc.talk;

import com.flora30.diveapi.plugins.ItemAPI;
import com.flora30.divequest.npc.NPC;
import org.bukkit.entity.Player;

public class TalkWhistle extends Talk{
    @Override
    public void doTalk(Player player, NPC npc) {
        ItemAPI.openWhistleGUI(player);
    }
}
