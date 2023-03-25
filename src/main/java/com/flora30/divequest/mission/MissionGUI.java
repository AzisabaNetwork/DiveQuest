package com.flora30.divequest.mission;

import com.flora30.diveapi.data.PlayerData;
import com.flora30.diveapi.data.Story;
import com.flora30.diveapi.data.player.NpcData;
import com.flora30.diveapi.plugins.CoreAPI;
import com.flora30.diveapi.plugins.ItemAPI;
import com.flora30.diveapi.tools.GuiItem;
import com.flora30.divequest.story.StoryMain;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class MissionGUI {
    public static Inventory getGUI(Player player){
        PlayerData data = CoreAPI.getPlayerData(player.getUniqueId());
        Inventory gui = Bukkit.createInventory(null,27, "ミッションリスト");

        //暗いガラス板で初期化
        for (int i = 0; i < 27; i++){
            gui.setItem(i, GuiItem.getItem(Material.GRAY_STAINED_GLASS_PANE));
        }

        //PlayerDataがnull ＝ ミッション進行の取得ができない
        if (data == null){
            return gui;
        }

        gui.setItem(11,getStoryMissionIcon(data.npcData));
        gui.setItem(13,getMobMissionIcon(data.npcData));
        gui.setItem(15,getItemMissionIcon(data.npcData));

        return gui;
    }

    private static ItemStack getStoryMissionIcon(NpcData data){

        ItemStack icon = GuiItem.getItem(Material.BOOK);
        ItemMeta meta = icon.getItemMeta();
        assert meta != null;

        meta.setDisplayName(ChatColor.GOLD+"＊－－＊  人々の繋がり  ＊－－＊");

        meta.setLore(getLore("Story", data.storyMissionId));

        icon.setItemMeta(meta);
        return icon;
    }

    private static ItemStack getMobMissionIcon(NpcData data){
        ItemStack icon = GuiItem.getItem(Material.BOOK);
        ItemMeta meta = icon.getItemMeta();
        assert meta != null;

        meta.setDisplayName(ChatColor.GOLD+"＊－－＊  原生生物の調査  ＊－－＊");

        meta.setLore(getLore("Mob", data.mobMissionId));

        icon.setItemMeta(meta);
        return icon;
    }

    private static ItemStack getItemMissionIcon(NpcData data){
        ItemStack icon = GuiItem.getItem(Material.BOOK);
        ItemMeta meta = icon.getItemMeta();
        assert meta != null;

        meta.setDisplayName(ChatColor.GOLD+"＊－－＊  遺物の発掘  ＊－－＊");

        meta.setLore(getLore("Item", data.itemMissionId));

        icon.setItemMeta(meta);
        return icon;
    }

    private static List<String> getLore(String type, int id){
        List<String> lore = new ArrayList<>();

        if (!MissionMain.idCheck(id)){
            lore.add(ChatColor.WHITE + "進行可能なミッションはありません");
        }
        else{
            Mission mission = MissionMain.getMission(type, id);
            if (mission == null){
                lore.add(ChatColor.WHITE + "null");
                return lore;
            }
            Story story = StoryMain.getStory(mission.layer);
            String name;
            if (story == null){
                name = "???";
            }
            else{
                name = story.displayName;
            }

            lore.add(ChatColor.GREEN + "目的 ‣ " +ChatColor.WHITE+mission.title);
            lore.add("");
            lore.add(ChatColor.GREEN + "推奨階層 ‣ "+ChatColor.WHITE+name);
            lore.add("");
            lore.add(ChatColor.GREEN + "報酬 ‣");

            Reward reward = mission.reward;

            if (reward.getExp() != 0){
                lore.add(ChatColor.WHITE + "" + reward.getExp()+"Exp");
            }
            if (reward.getBlessPoint() != 0){
                lore.add(ChatColor.WHITE + String.format("%5.2f",reward.getBlessPoint())+"祝福pt");
            }
            for (int itemId : reward.getItemMap().keySet()){
                ItemStack item = ItemAPI.getItem(itemId);
                item.setAmount(reward.getItemMap().get(itemId));
                ItemMeta meta = item.getItemMeta();
                if (meta == null){
                    continue;
                }
                lore.add(ChatColor.WHITE + "" + item.getItemMeta().getDisplayName()+" "+item.getAmount()+"個");
            }

            lore.add("");
            lore.add(ChatColor.GREEN + "ヒント ‣");
            lore.addAll(mission.text);

        }
        return lore;
    }
}
