package com.flora30.divequest.mission;

import com.flora30.diveconstant.data.LayerObject;
import com.flora30.diveconstant.data.Story;
import com.flora30.divelib.ItemMain;
import com.flora30.divelib.data.player.NpcData;
import com.flora30.divelib.data.player.PlayerData;
import com.flora30.divelib.data.player.PlayerDataObject;
import com.flora30.divelib.util.GuiItem;
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
        PlayerData data = PlayerDataObject.INSTANCE.getPlayerDataMap().get(player.getUniqueId());
        Inventory gui = Bukkit.createInventory(null,27, "ミッションリスト");

        //暗いガラス板で初期化
        for (int i = 0; i < 27; i++){
            gui.setItem(i, GuiItem.INSTANCE.getItem(Material.GRAY_STAINED_GLASS_PANE));
        }

        //PlayerDataがnull ＝ ミッション進行の取得ができない
        if (data == null){
            return gui;
        }

        gui.setItem(11,getStoryMissionIcon(data.getNpcData()));
        gui.setItem(13,getMobMissionIcon(data.getNpcData()));
        gui.setItem(15,getItemMissionIcon(data.getNpcData()));

        return gui;
    }

    private static ItemStack getStoryMissionIcon(NpcData data){

        ItemStack icon = GuiItem.INSTANCE.getItem(Material.BOOK);
        ItemMeta meta = icon.getItemMeta();
        assert meta != null;

        meta.setDisplayName(ChatColor.GOLD+"＊－－＊  人々の繋がり  ＊－－＊");

        meta.setLore(getLore("Story", data.getStoryMissionId()));

        icon.setItemMeta(meta);
        return icon;
    }

    private static ItemStack getMobMissionIcon(NpcData data){
        ItemStack icon = GuiItem.INSTANCE.getItem(Material.BOOK);
        ItemMeta meta = icon.getItemMeta();
        assert meta != null;

        meta.setDisplayName(ChatColor.GOLD+"＊－－＊  原生生物の調査  ＊－－＊");

        meta.setLore(getLore("Mob", data.getMobMissionId()));

        icon.setItemMeta(meta);
        return icon;
    }

    private static ItemStack getItemMissionIcon(NpcData data){
        ItemStack icon = GuiItem.INSTANCE.getItem(Material.BOOK);
        ItemMeta meta = icon.getItemMeta();
        assert meta != null;

        meta.setDisplayName(ChatColor.GOLD+"＊－－＊  遺物の発掘  ＊－－＊");

        meta.setLore(getLore("Item", data.getItemMissionId()));

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
            Story story = LayerObject.INSTANCE.getLayerMap().get(mission.layer).getStory();
            String name = LayerObject.INSTANCE.getLayerMap().get(mission.layer).getDisplayName();

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
                ItemStack item = ItemMain.INSTANCE.getItem(itemId);
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
