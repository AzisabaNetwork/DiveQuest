package com.flora30.divequest.mission;

import com.flora30.diveapi.DiveAPI;
import com.flora30.diveapi.data.PlayerData;
import com.flora30.diveapi.data.player.NpcData;
import com.flora30.diveapi.plugins.CoreAPI;
import com.flora30.diveapi.plugins.ItemAPI;
import com.flora30.diveapi.plugins.RegionAPI;
import com.flora30.divequest.mission.Type.ItemMission;
import com.flora30.divequest.mission.Type.MobMission;
import com.flora30.divequest.mission.Type.StoryMission;
import com.flora30.divequest.npc.TalkLine;
import com.flora30.divequest.npc.talk.Check;
import com.flora30.divequest.npc.talk.Talk;
import com.flora30.divequest.npc.talk.TalkDelay;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MissionMain {

    public final static List<StoryMission> storyMissions = new ArrayList<>();
    public final static List<MobMission> mobMissions = new ArrayList<>();
    public final static List<ItemMission> itemMissions = new ArrayList<>();

    public static void onKillMob(Player player, String mobName){
        NpcData data = CoreAPI.getPlayerData(player.getUniqueId()).npcData;
        if (data == null){
            return;
        }

        int id = data.mobMissionId;

        if (id == -1){
            return;
        }
        if (mobMissions.get(id).check(mobName)){
            complete("Mob",id,player);
        }
    }

    public static void onGetItem(Player player, ItemStack item){
        NpcData data = CoreAPI.getPlayerData(player.getUniqueId()).npcData;
        if (data == null){
            return;
        }

        int itemId = ItemAPI.getItemID(item);
        int id = data.itemMissionId;

        if (id == -1){
            return;
        }
        if (itemMissions.get(id).check(itemId)){
            complete("Item",id,player);
        }
    }

    public static void onTalkNPC(Player player, int npcId){
        NpcData data = CoreAPI.getPlayerData(player.getUniqueId()).npcData;
        if (data == null){
            return;
        }

        int id = data.storyMissionId;

        if (id == -1){
            return;
        }

        if (storyMissions.get(id).check(npcId)){
            complete("Story",id,player);
        }
    }


    public static boolean idCheck(int id){
        return id > 0;
    }

    public static void openGUI(Player player){
        player.openInventory(MissionGUI.getGUI(player));
    }

    public static void complete(String type, int id, Player player){
        Mission mission = getMission(type, id);
        assert mission != null;
        //コンプリート処理
        NpcData data = CoreAPI.getPlayerData(player.getUniqueId()).npcData;
        data.setMissionId(type, -1);

        //報酬説明のメッセージ
        String title = mission.title;
        List<String> rewardInfoList = new ArrayList<>();
        rewardInfoList.add(ChatColor.GOLD+"==========================");
        rewardInfoList.add(ChatColor.GOLD+"ミッション「"+title+ChatColor.GOLD+"」を完了しました");
        rewardInfoList.add(ChatColor.GOLD+"==========================");

        //音
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, (float) 0.8);
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, (float) 1.2);

        //報酬をあげる
        Reward reward = mission.reward;
        if (reward.getExp() > 0){
            CoreAPI.addExp(player, reward.getExp());
            rewardInfoList.add(2,ChatColor.WHITE+"+"+reward.getExp()+"Exp");
        }
        //祝福pt
        //お金
        if (reward.getMoney() > 0){
            CoreAPI.getPlayerData(player.getUniqueId()).money += reward.getMoney();
            rewardInfoList.add(2,ChatColor.WHITE+"+"+reward.getMoney()+"G");
        }

        //アイテム
        Map<Integer,Integer> itemMap = reward.getItemMap();
        for (int itemId : itemMap.keySet()){
            ItemStack item = ItemAPI.getItem(itemId);
            item.setAmount(itemMap.get(itemId));
            player.getInventory().addItem(item);

            if (item.getItemMeta() == null){
                continue;
            }
            rewardInfoList.add(2,item.getItemMeta().getDisplayName()+ChatColor.WHITE+" ‣ "+itemMap.get(itemId)+"個");
        }
        //文章：報酬表示
        for (String rewardInfo : rewardInfoList){
            player.sendMessage(rewardInfo);
        }

        //文章2（会話形式）
        talk(player, reward.getLine());
    }

    public static void talk(Player player, TalkLine talkLine){
        Bukkit.getLogger().info("報酬talk開始");
        if (talkLine == null){
            Bukkit.getLogger().info("talklineなし");
            return;
        }
        NpcData data = CoreAPI.getPlayerData(player.getUniqueId()).npcData;
        data.isTalking = true;

        //delayTime：遅らせるtick数
        int delaySetting = data.talkDelay;
        int delayTime = 0;
        //会話を行う処理をサーバーに投げる
        for (Talk talk : talkLine.getTimeline()){
            if(talk instanceof TalkDelay){
                delayTime += delaySetting;
                continue;
            }
            if(talk instanceof Check){
                /*
                Check check = (Check) talk;
                if(!check.doCheck(player,npc)){
                    //checkが上手くいかなかったとき=checkのタイムラインを実行する
                    DiveCore.plugin.delayedTask(delayTime,() -> {
                        check.falsedText(player,npc);
                    });
                    break;
                }
                */
                continue;
            }
            DiveAPI.plugin.delayedTask(delayTime,() -> {
                talk.doTalk(player,null);
            });
        }

        //会話を終了するときの処理
        DiveAPI.plugin.delayedTask(delayTime,() -> {
            //会話を終了する
            data.isTalking = (false);
        });

        Bukkit.getLogger().info("talkline最終カウント："+delayTime);
    }

    public static boolean setCompass(Player player){
        PlayerData data = CoreAPI.getPlayerData(player.getUniqueId());
        if (data == null){
            return false;
        }

        int npcMissionId = data.npcData.storyMissionId;
        Mission mission = MissionMain.getMission("Story",npcMissionId);
        if (!(mission instanceof StoryMission)){
            return false;
        }
        int npcId = ((StoryMission) mission).getNpcId();
        if (npcId == -1){
            return false;
        }

        NPC npc = CitizensAPI.getNPCRegistry().getById(npcId);
        if (npc == null){
            return false;
        }
        Location target = npc.getStoredLocation();
        Location from = player.getLocation();

        String targetLayer = RegionAPI.getLayerName(target);
        String fromLayer = RegionAPI.getLayerName(from);
        if (targetLayer == null || !targetLayer.equals(fromLayer)){
            return false;
        }

        player.setCompassTarget(target);

        ItemStack compassItem = CoreAPI.getMenuIcon(player);
        if (compassItem == null || compassItem.getItemMeta() == null){
            return false;
        }
        ItemMeta meta = compassItem.getItemMeta();
        //重複
        if (meta.hasCustomModelData() && meta.getCustomModelData() == npcId){
            return true;
        }
        meta.setCustomModelData(npcId);
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.WHITE+"目標 ‣ "+npc.getFullName());
        meta.setLore(lore);
        compassItem.setItemMeta(meta);
        return true;
    }

    public static void startMission(String type, int id, Player player){
        Mission mission = MissionMain.getMission(type,id);
        if (mission == null){
            Bukkit.getLogger().info("[DiveCore-Mission] "+type+" - "+id+"はnullのため受注しませんでした");
            return;
        }
        NpcData data = CoreAPI.getPlayerData(player.getUniqueId()).npcData;
        switch (type){
            case "Story":
                data.storyMissionId = (id);
                break;
            case "Mob":
                data.mobMissionId = (id);
                break;
            case "Item":
                data.itemMissionId = (id);
                break;
            default:return;
        }
        String title = mission.title;
        player.sendMessage(ChatColor.GREEN+"ミッション「"+title+ChatColor.GREEN+"」を開始しました");
        //音
        player.playSound(player.getLocation(), Sound.ITEM_LODESTONE_COMPASS_LOCK, 1, (float) 1.2);
        DiveAPI.plugin.delayedTask(2, () -> player.playSound(player.getLocation(), Sound.ITEM_LODESTONE_COMPASS_LOCK, 1, (float) 1.0));
        DiveAPI.plugin.delayedTask(4, () -> player.playSound(player.getLocation(), Sound.ITEM_LODESTONE_COMPASS_LOCK, 1, (float) 0.6));
    }

    public static void setStoryMission(int id, StoryMission mission){
        //size以内のとき
        if(id < storyMissions.size()){
            storyMissions.set(id,mission);
            return;
        }

        //size +1以上のとき
        while (id > storyMissions.size()){
            storyMissions.add(null);
        }

        //sizeのとき
        storyMissions.add(mission);
    }

    public static void setMobMission(int id, MobMission mission){

        //size以内のとき
        if(id < mobMissions.size()){
            mobMissions.set(id,mission);
            return;
        }

        //size +1以上のとき
        while (id > mobMissions.size()){
            mobMissions.add(null);
        }

        //sizeのとき
        mobMissions.add(mission);
    }

    public static void setItemMission(int id, ItemMission mission){

        //size以内のとき
        if(id < itemMissions.size()){
            itemMissions.set(id,mission);
            return;
        }

        //size +1以上のとき
        while (id > itemMissions.size()){
            itemMissions.add(null);
        }

        //sizeのとき
        itemMissions.add(mission);
    }

    public static Mission getMission(String type, int id){

        try {
            return switch (type) {
                case "Story" -> storyMissions.get(id);
                case "Mob" -> mobMissions.get(id);
                case "Item" -> itemMissions.get(id);
                default -> null;
            };
        } catch (IndexOutOfBoundsException e){
            return null;
        }
    }
}
