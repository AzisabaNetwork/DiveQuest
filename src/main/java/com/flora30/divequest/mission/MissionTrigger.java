package com.flora30.divequest.mission;

import com.flora30.diveapi.plugins.CoreAPI;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class MissionTrigger {
    public static void onKill(MythicMobDeathEvent e){
        //無効なMobを除外
        switch(e.getEntity().getType()){
            case ARMOR_STAND:
            case PLAYER:
                return;
        }
        //プレイヤー以外を除外
        if(!(e.getKiller() instanceof Player)){
            return;
        }
        Player player = (Player)e.getKiller();
        //kill判定
        MissionMain.onKillMob(player,e.getMobType().getInternalName());
    }

    public static void onPickup(EntityPickupItemEvent e){
        if (e.isCancelled()){
            return;
        }
        if (!(e.getEntity() instanceof  Player)){
            return;
        }
        Player player = (Player)e.getEntity();

        MissionMain.onGetItem(player,e.getItem().getItemStack());
    }

    public static void onClickItem(InventoryClickEvent e){
        if (e.getClickedInventory() == null){
            return;
        }
        ItemStack item = e.getClickedInventory().getItem(e.getSlot());
        if (item == null){
            return;
        }
        MissionMain.onGetItem((Player) e.getWhoClicked(),item);
    }

    public static void onClick(InventoryClickEvent e){
        e.setCancelled(true);
    }

    public static void onCommand(Player player){
        MissionMain.openGUI(player);
    }

    // ストーリー無しなので誘導用のコンパスも必要ない
    public static void onTickCompass(Player player){
        /*
        if(!MissionMain.setCompass(player)){
            ItemStack compassItem = CoreAPI.getMenuIcon(player);
            if (compassItem == null){
                return;
            }

            player.setCompassTarget(player.getEyeLocation());

            ItemMeta meta = compassItem.getItemMeta();
            assert meta != null;
            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add(ChatColor.WHITE+"目標 ‣ なし");
            meta.setLore(lore);
            compassItem.setItemMeta(meta);
        }
         */
    }
}
