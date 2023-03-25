package com.flora30.divequest.npc.talk;

import com.flora30.diveapi.plugins.ItemAPI;
import com.flora30.diveapi.tools.PlayerItem;
import com.flora30.divequest.npc.NPC;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class TalkGiveItem extends Talk{
    final int itemId;
    final int amount;
    public TalkGiveItem(int itemId, int amount){
        this.itemId = itemId;
        this.amount = amount;
    }

    @Override
    public void doTalk(Player player, NPC npc) {
        ItemStack item = ItemAPI.getItem(itemId);
        item.setAmount(amount);
        PlayerItem.giveItem(player,item);
    }
}
