package com.flora30.divequest.story;

import com.flora30.diveapi.data.player.LayerData;
import com.flora30.diveapi.event.LayerChangeEvent;
import com.flora30.diveapi.event.LayerLoadEvent;
import com.flora30.diveapi.plugins.CoreAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class StoryListener {

    public static void onLayerLoad(LayerLoadEvent e){
        StoryConfig.load(e.getKey(), e.getSection());
    }

    public static void onLayerChange(LayerChangeEvent e){
        Player player = Bukkit.getPlayer(e.getUuid());
        if (player == null){
            return;
        }
        LayerData data = CoreAPI.getPlayerData(e.getUuid()).layerData;
        StoryMain.playTitle(player);
        // ログなのでCO
        //for (String visited : data.visitedLayers){
        //    Bukkit.getLogger().info("visited : "+visited);
        //}
        //Bukkit.getLogger().info("次の階層："+e.getNextLayer());
        if (data.visitedLayers.contains(e.getNextLayer())){
            //既に訪れていたとき
            StoryMain.skip(player);
        }
        else{
            StoryMain.play(player);
        }
    }

    public static void onCommand(Player player, String pass) {
        if (!pass.equals("6zjQoB7SuFMd")){
            return;
        }
        StoryMain.play(player);
    }
}
