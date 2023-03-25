package com.flora30.divequest.story;

import com.flora30.diveapi.DiveAPI;
import com.flora30.diveapi.data.PlayerData;
import com.flora30.diveapi.data.Story;
import com.flora30.diveapi.plugins.CoreAPI;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class StoryMain {

    //string = layerName
    private static final Map<String, Story> storyMap = new HashMap<>();

    public static void play(Player player){
        PlayerData data = CoreAPI.getPlayerData(player.getUniqueId());
        if (data == null || data.layerData.layer == null) {
            Bukkit.getLogger().info("[DiveCore-Story]データ待ち : "+player.getDisplayName());
            DiveAPI.plugin.delayedTask(3, () -> {
                play(player);
            });
            return;
        }
        String layer = data.layerData.layer;
        storyMap.get(layer).play(player);
    }

    public static void skip(Player player){
        player.sendMessage(ChatColor.GRAY+"既に訪れているため、ストーリーをスキップしました");

        try{
            TextComponent text = new TextComponent(ChatColor.AQUA+"<もう一度見る？>");
            text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/sty 6zjQoB7SuFMd"));
            player.spigot().sendMessage(text);
        } catch (NoClassDefFoundError e){
            Bukkit.getLogger().info("[DiveCore-Story]BungeeCord未導入です");
        }

    }

    public static void playTitle(Player player){
        PlayerData data = CoreAPI.getPlayerData(player.getUniqueId());
        if (data == null || data.layerData.layer == null) {
            Bukkit.getLogger().info("[DiveCore-Story]データ待ち : " + player.getDisplayName());
            DiveAPI.plugin.delayedTask(3, () -> {
                playTitle(player);
            });
            return;
        }

        String layerName = data.layerData.layer;
        boolean noticeDisplay = StoryMain.getStory(layerName).noticeDisplay;
        if (noticeDisplay){
            String displayName = StoryMain.getStory(layerName).displayName;
            String displaySub = StoryMain.getStory(layerName).displaySub;
            player.sendTitle(displayName,displaySub,5,40,10);
        }
    }

    public static void putStory(String layer, Story story){
        storyMap.put(layer,story);
    }

    public static Story getStory(String layer){
        return storyMap.get(layer);
    }
}