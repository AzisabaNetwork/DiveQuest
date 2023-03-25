package com.flora30.divequest.npc.talk;

import com.flora30.divequest.npc.NPC;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class TalkText extends Talk{
    private final String text;
    public TalkText(String text){
        this.text = ChatColor.translateAlternateColorCodes('&',text);
    }

    @Override
    public void doTalk(Player player, NPC npc) {
        player.sendMessage(text);
        player.playSound(player.getLocation(), Sound.ENTITY_PUFFER_FISH_BLOW_UP,1,1);
    }
}
