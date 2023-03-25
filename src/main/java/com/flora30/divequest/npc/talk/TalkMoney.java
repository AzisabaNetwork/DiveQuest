package com.flora30.divequest.npc.talk;

import com.flora30.diveapi.plugins.CoreAPI;
import com.flora30.divequest.npc.NPC;
import org.bukkit.entity.Player;

public class TalkMoney extends Talk{
    final int amount;
    public TalkMoney(int amount){
        this.amount = amount;
    }

    @Override
    public void doTalk(Player player, NPC npc) {
        CoreAPI.getPlayerData(player.getUniqueId()).money += amount;
    }
}
