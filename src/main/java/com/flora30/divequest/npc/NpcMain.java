package com.flora30.divequest.npc;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.flora30.diveapi.DiveAPI;
import com.flora30.diveapi.data.player.NpcData;
import com.flora30.diveapi.plugins.CoreAPI;
import com.flora30.divequest.npc.talk.Check;
import com.flora30.divequest.npc.talk.Talk;
import com.flora30.divequest.npc.talk.TalkDelay;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class NpcMain {
    private static final NpcConfig NPC_CONFIG = new NpcConfig();
    //NpcID | NPC
    private static final Map<Integer,NPC> npcMap = new HashMap<>();

    public static void talk(Player player, int npcId){
        NpcData data = CoreAPI.getPlayerData(player.getUniqueId()).npcData;
        //会話中を除外
        //Bukkit.getLogger().info("会話中 = "+data.isTalking);
        if (data.isTalking){
            return;
        }
        //NPCが会話を持っていない場合
        NPC npc = getNPC(npcId);
        if (npc.getTalkLines().size() == 0){
            Bukkit.getLogger().info("NPC["+npcId+"]は会話を持っていません");
            return;
        }

        data.isTalking = true;

        //会話の進捗を取り出す
        int talked = data.getTalkProgress(npcId);
        //npcのもつ会話の数を超えないように調整
        if (talked >= npc.getTalkLines().size()){
            talked = npc.getTalkLines().size() -1;
        }
        //Bukkit.getLogger().info("会話判定 - NpcId:"+npcId+" 進捗:"+talked);

        //進捗に合わせた会話を取り出す
        TalkLine talkLine = npc.getLine(talked);

        //delayTime：遅らせるtick数
        int delay = data.talkDelay;
        int delayTime = 0;
        //会話を行う処理をサーバーに投げる
        for (Talk talk : talkLine.getTimeline()){
            if(talk instanceof TalkDelay){
                delayTime += delay;
                continue;
            }
            if(talk instanceof Check){
                Check check = (Check) talk;
                if(!check.doCheck(player,npc)){
                    //checkが上手くいかなかったとき=checkのタイムラインを実行する
                    DiveAPI.plugin.delayedTask(delayTime,() -> {
                        check.falsedText(player,npc);
                    });
                    break;
                }
            }
            DiveAPI.plugin.delayedTask(delayTime,() -> {
                talk.doTalk(player,npc);
            });
        }

        //会話を終了するときの処理
        DiveAPI.plugin.delayedTask(delayTime + 1,() -> {
            int finalTalked = data.getTalkProgress(npcId); // loopを反映する
            //Bukkit.getLogger().info("progress = "+finalTalked+", setFirst = "+ data.setFirstTalk);

            if (data.setFirstTalk) {
                data.setFirstTalk = false;
            }
            else {
                data.talkProgressMap.put(npcId, finalTalked +1);
            }

            //会話を終了する
            data.isTalking = false;

            // 他プレイヤーチャットの再表示
            List<PacketContainer> chatStacks = CoreAPI.getPlayerData(player.getUniqueId()).chatStackList;
            int size = chatStacks.size();
            int chatCount = 0;
            for (; chatCount < size; chatCount++) {
                PacketContainer container = chatStacks.get(chatCount);
                    DiveAPI.plugin.delayedTask(chatCount * 2,() -> {
                        try {
                            ProtocolLibrary.getProtocolManager().sendServerPacket(player,container);
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    });
            }
            DiveAPI.plugin.delayedTask(chatCount * 2 + 2, chatStacks::clear);
        });
    }

    //登録する時：初回右クリック
    public static void newNPC(int id, String name){
        NPC npc = new NPC(id,name);
        npcMap.put(id,npc);
        NPC_CONFIG.firstSave(id);
        Bukkit.getLogger().info("NPC : "+name+"を登録しました");
    }

    public static NPC getNPC(int id){
        return npcMap.get(id);
    }

    public static boolean hasNPC(int id){
        return npcMap.containsKey(id);
    }

    public static void setNPC(int id, NPC npc){
        npcMap.put(id,npc);
    }
}
