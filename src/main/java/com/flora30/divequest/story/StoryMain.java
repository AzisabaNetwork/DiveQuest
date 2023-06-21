package com.flora30.divequest.story;

import com.flora30.divelib.data.LayerObject;
import com.flora30.divelib.DiveLib;
import com.flora30.divelib.data.player.PlayerData;
import com.flora30.divelib.data.player.PlayerDataObject;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class StoryMain {

    public static void play(Player player){
        PlayerData data = PlayerDataObject.INSTANCE.getPlayerDataMap().get(player.getUniqueId());
        if (data == null || data.getLayerData().getLayer() == null) {
            Bukkit.getLogger().info("[DiveCore-Story]データ待ち : "+player.getDisplayName());
            DiveLib.plugin.delayedTask(3, () -> {
                play(player);
            });
            return;
        }
        String layer = data.getLayerData().getLayer();
        LayerObject.INSTANCE.getLayerMap().get(layer).getStory().play(player);
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
        PlayerData data = PlayerDataObject.INSTANCE.getPlayerDataMap().get(player.getUniqueId());
        if (data == null || data.getLayerData().getLayer() == null) {
            Bukkit.getLogger().info("[DiveCore-Story]データ待ち : " + player.getDisplayName());
            DiveLib.plugin.delayedTask(3, () -> {
                playTitle(player);
            });
            return;
        }

        String layerName = data.getLayerData().getLayer();
        boolean noticeDisplay = LayerObject.INSTANCE.getLayerMap().get(layerName).getStory().getNoticeDisplay();
        if (noticeDisplay){
            String displayName = LayerObject.INSTANCE.getLayerMap().get(layerName).getDisplayName();
            String displaySub = LayerObject.INSTANCE.getLayerMap().get(layerName).getStory().getDisplaySub();
            player.sendTitle(displayName,displaySub,5,40,10);
        }
    }
}