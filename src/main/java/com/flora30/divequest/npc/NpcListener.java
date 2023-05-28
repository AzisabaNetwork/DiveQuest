package com.flora30.divequest.npc;

import com.flora30.divelib.data.player.PlayerData;
import com.flora30.divelib.data.player.PlayerDataObject;
import com.flora30.divequest.DiveQuest;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCCreateEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public class NpcListener {

    public static void onCommand(Player player, String command, String command2){
        switch (command){
            case "reset":
                Player target = Bukkit.getPlayer(command2);
                if (target == null){
                    player.sendMessage("プレイヤーが見つかりません");
                    return;
                }
                PlayerData data = PlayerDataObject.INSTANCE.getPlayerDataMap().get(target.getUniqueId());
                if (data == null){
                    player.sendMessage("プレイヤーデータが見つかりません");
                    return;
                }

                Map<Integer,Integer> map = data.getNpcData().getTalkProgressMap();
                map.clear();
                player.sendMessage(command2 + "の会話データを削除しました");
        }
    }

    public static void onTalk(NPCRightClickEvent e){
        int npcId =e.getNPC().getId();
        if(NpcMain.hasNPC(npcId)){
            NpcMain.talk(e.getClicker(),npcId);
        }
    }

    public static void onCreate(NPCCreateEvent e){
        NPC npc = e.getNPC();
        if (DiveQuest.firstLoaded && !NpcMain.hasNPC(npc.getId())){
            //新規登録
            NpcMain.newNPC(npc.getId(),npc.getName());
        }
    }

    public static void onTickParticle(Player player){
        List<Entity> entities = player.getNearbyEntities(25,25,25);

        for(Entity entity : entities){
            //npcであるとき
            if(CitizensAPI.getNPCRegistry().isNPC(entity)){
                //パーティクル表示
                Location loc = entity.getLocation().add(0,1,0);
                player.spawnParticle(Particle.END_ROD,loc,10,0.3,1,0.3,0);
            }
        }
    }
}
