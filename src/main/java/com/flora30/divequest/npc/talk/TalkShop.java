package com.flora30.divequest.npc.talk;

import com.flora30.diveapi.plugins.ItemAPI;
import com.flora30.divequest.npc.NPC;
import org.bukkit.entity.Player;

public class TalkShop extends Talk {
    private final int id;

    public TalkShop(int id) {
        this.id = id;
    }

    @Override
    public void doTalk(Player player, NPC npc) {
        player.openInventory(ItemAPI.getShopGUI(id));
    }
}
