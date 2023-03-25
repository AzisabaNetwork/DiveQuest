package com.flora30.divequest.npc.talk;

import com.flora30.divequest.npc.NPC;
import org.bukkit.entity.Player;

public class Check extends Talk{
    private final TalkText anotherText;
    private final TalkLoop loop = new TalkLoop(1);
    public Check(String falsed){
        anotherText = new TalkText(falsed);
    }

    public boolean doCheck(Player player, NPC npc){
        return false;
    }
    public void falsedText(Player player, NPC npc){
        anotherText.doTalk(player,npc);
        loop.doTalk(player,npc);
    }
}
