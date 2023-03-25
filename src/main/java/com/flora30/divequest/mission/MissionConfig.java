package com.flora30.divequest.mission;

import com.flora30.diveapi.tools.Config;
import com.flora30.divequest.DiveQuest;
import com.flora30.divequest.mission.Type.ItemMission;
import com.flora30.divequest.mission.Type.MobMission;
import com.flora30.divequest.mission.Type.StoryMission;
import com.flora30.divequest.npc.TalkLine;
import com.flora30.divequest.npc.talk.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;

public class MissionConfig extends Config {
    private static File[] missionFiles = new File[100];

    public MissionConfig(){
        folderCheck(DiveQuest.plugin.getDataFolder().getAbsolutePath() + "/mission");

        missionFiles = new File(DiveQuest.plugin.getDataFolder().getAbsolutePath() + "/mission").listFiles();
    }

    @Override
    public void load() {

        //missionフォルダ内のファイルを検索
        for (File separated : missionFiles) {
            FileConfiguration file2 = YamlConfiguration.loadConfiguration(separated);
            for (String key : file2.getKeys(false)) {
                int missionID;
                try {
                    missionID = getId(key);
                } catch (NumberFormatException e) {
                    Bukkit.getLogger().info("[DiveCore-Mission]「" + key + "」は数字ではありません");
                    continue;
                }
                //読込み
                String type = getType(key);
                String title;
                String layer;
                List<String> text;
                try{
                    title = replaceColorCode(loadOrDefault("mission", file2, key + ".title", ""));
                    layer = loadOrDefault("mission", file2, key + ".layer", "oldOrth");
                    text = file2.getStringList(key + ".text");
                    for (int i = 0; i < text.size(); i++) {
                        text.set(i, replaceColorCode(text.get(i)));
                    }
                } catch (IllegalArgumentException e){
                    Bukkit.getLogger().info("[DiveCore-Mission]色の取得に失敗しました("+file2.getName()+", "+key+")");
                    continue;
                }

                //報酬
                ConfigurationSection section = file2.getConfigurationSection(key+".reward");
                Reward reward = getReward(section, type+"-"+missionID);


                //mission作成
                Mission mission;
                switch (type) {
                    case "Story":
                        int npcId = loadOrDefault("mission", file2, key + ".npc", 0);
                        if (npcId == 0) {
                            mission = new StoryMission();
                        } else {
                            mission = new StoryMission(npcId);
                        }
                        break;
                    case "Mob":
                        String mobName = loadOrDefault("mission", file2, key + ".mobName", "None");
                        mission = new MobMission(mobName);
                        break;
                    case "Item":
                        int itemId = loadOrDefault("mission", file2, key + ".itemId", 0);
                        mission = new ItemMission(itemId);
                        break;
                    default:
                        continue;
                }

                mission.layer = layer;
                mission.title = title;
                mission.reward = reward;
                mission.text = text;

                switch (type) {
                    case "Story" -> MissionMain.setStoryMission(missionID, (StoryMission) mission);
                    case "Mob" -> MissionMain.setMobMission(missionID, (MobMission) mission);
                    case "Item" -> MissionMain.setItemMission(missionID, (ItemMission) mission);
                }

                Bukkit.getLogger().info("[DiveCore-Mission]「"+type+"-"+missionID+"」をロードしました");
            }
        }
        Bukkit.getLogger().info("[DiveCore-Mission]ミッションのロードが完了しました");
    }

    @Override
    public void save() {

    }

    private Reward getReward(ConfigurationSection section, String wholeId){
        int exp = loadOrDefault("mission",section,"exp",0);
        double blessPoint = loadOrDefault("mission",section,"bless",0.0);
        int money = loadOrDefault("mission",section,"money",0);
        List<String> itemStrList;
        try{
           itemStrList = section.getStringList("item");
        }  catch (NullPointerException e){
            itemStrList = null;
        }
        List<String> message;
        try{
            message = section.getStringList("message");
            for (int i = 0; i < message.size(); i++){
                message.set(i,replaceColorCode(message.get(i)));
            }
        } catch (NullPointerException e){
            message = null;
        }

        Reward reward = new Reward();
        reward.setExp(exp);
        reward.setBlessPoint(blessPoint);
        reward.setMoney(money);
        reward.setItemMap(getItemMap(itemStrList));
        reward.setLine(generateTalkLine(message, wholeId));

        return reward;
    }

    private Map<Integer,Integer> getItemMap (List<String> itemStrList){
        Map<Integer,Integer> itemMap = new HashMap<>();
        if (itemStrList == null){
            return itemMap;
        }
        for (String str : itemStrList){
            String[] s = str.split(",");
            int id, amount;
            try{
                id = Integer.parseInt(s[0]);
                amount = Integer.parseInt(s[1]);
            } catch (NumberFormatException e){
                continue;
            }
            itemMap.put(id,amount);
        }
        return itemMap;
    }

    private String getType(String name){
        String[] nameContents = name.split("-");
        return nameContents[0];
    }

    private int getId(String name) throws NumberFormatException{
        String[] nameContents = name.split("-");
        return Integer.parseInt(nameContents[1]);
    }

    private String replaceColorCode(String str){
        return ChatColor.translateAlternateColorCodes('&',str);
    }

    private TalkLine generateTalkLine(List<String> loadedList, String wholeId){
        if (loadedList == null){
            return null;
        }
        TalkLine generatedLine = new TalkLine();
        for(String key : loadedList){
            List<String> separatedKeys = Arrays.asList(key.split(" "));
            //type分岐
            try{
                switch (TalkType.valueOf(separatedKeys.get(0))) {
                    case Give -> {
                        int itemID = Integer.parseInt(separatedKeys.get(1));
                        int amount = Integer.parseInt(separatedKeys.get(2));
                        generatedLine.addTalk(new TalkGiveItem(itemID, amount));
                    }
                    case Loop -> {
                        if (separatedKeys.size() >= 2) {
                            generatedLine.addTalk(new TalkLoop(Integer.parseInt(separatedKeys.get(1))));
                        }
                        else {
                            generatedLine.addTalk(new TalkLoop(1));
                        }
                    }
                    case Text -> {
                        String str = separatedKeys.get(1);
                        generatedLine.addTalk(new TalkText(str));
                    }
                    case Delay -> {
                        generatedLine.addTalk(new TalkDelay());
                    }
                    case MissionStart -> {
                        String type = separatedKeys.get(1);
                        int id = Integer.parseInt(separatedKeys.get(2));
                        generatedLine.addTalk(new TalkMissionStart(type, id));
                    }
                    case CheckOther -> {
                        int loadID = Integer.parseInt(separatedKeys.get(1));
                        int progress = Integer.parseInt(separatedKeys.get(2));
                        String failed = separatedKeys.get(3);
                        generatedLine.addTalk(new CheckOtherProgress(failed, loadID, progress));
                    }
                    case Money -> {
                        int money = Integer.parseInt(separatedKeys.get(1));
                        generatedLine.addTalk(new TalkMoney(money));
                    }
                    case Help -> {
                        int helpId = Integer.parseInt(separatedKeys.get(1));
                        generatedLine.addTalk(new TalkHelp(helpId));
                    }
                    case Shop -> {
                        int shopId = Integer.parseInt(separatedKeys.get(1));
                        generatedLine.addTalk(new TalkShop(shopId));
                    }
                }
            } catch (IllegalArgumentException|IndexOutOfBoundsException|NullPointerException e){
                Bukkit.getLogger().info("[DiveCore-Mission]"+wholeId+"の報酬会話「"+key+"」の取得に失敗しました");
                e.printStackTrace();
            }
        }
        return generatedLine;
    }
}
