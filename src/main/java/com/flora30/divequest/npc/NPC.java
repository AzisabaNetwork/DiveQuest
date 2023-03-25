package com.flora30.divequest.npc;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class NPC {
    private final int npcId;
    private final String name;

    public NPC(int id, String name){
        npcId = id;
        this.name = name;
    }
    //会話回数に応じて分岐
    private final List<TalkLine> talkLines = new ArrayList<>();

    public int getNpcId() {
        return npcId;
    }

    public String getName() {
        return name;
    }

    public void addLine(TalkLine line){
        talkLines.add(line);
    }

    public TalkLine getLine(int i){
        return talkLines.get(i);
    }

    public int getLastProgress(){
        return talkLines.size();
    }

    public List<TalkLine> getTalkLines() {
        return talkLines;
    }
}
