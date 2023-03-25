package com.flora30.divequest.npc;

import com.flora30.divequest.npc.talk.Talk;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TalkLine {
    //会話タイムライン
    private final List<Talk> timeline = new ArrayList<>();

    public void addTalk(Talk talk){
        timeline.add(talk);
    }

    public List<Talk> getTimeline() {
        return timeline;
    }
}
