package com.flora30.divequest.npc;

import com.flora30.divelib.data.talk.*;
import com.flora30.divelib.event.HelpType;
import com.flora30.divelib.util.Config;
import com.flora30.divequest.DiveQuest;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NpcConfig extends Config {
    private final File file;
    private static File[] npcFiles = new File[100];

    public NpcConfig(){
        folderCheck(DiveQuest.plugin.getDataFolder().getAbsolutePath() + "/npc");
        file = new File(DiveQuest.plugin.getDataFolder().getAbsolutePath() + "/npc",File.separator+"firstNpc.yml");

        npcFiles = new File(DiveQuest.plugin.getDataFolder().getAbsolutePath() + "/npc").listFiles();
    }

    public void firstSave(int id){
        Bukkit.getLogger().info("[DiveCore-NPC]初期化セーブ - "+id);
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        NPC npc = NpcMain.getNPC(id);
        config.createSection(String.valueOf(id));
        config.createSection(id+".name");
        config.set(id+".name",npc.getName());
        config.createSection(id+".talkLine");
        try{
            config.save(file);
        } catch (IOException e){
            e.printStackTrace();
        }
    }


    @Override
    public void load() {
        //npcフォルダ内のファイルを検索
        for(File separated : npcFiles){
            FileConfiguration file2 = YamlConfiguration.loadConfiguration(separated);
            for(String key : file2.getKeys(false)){
                int npcID;
                try{
                    npcID = Integer.parseInt(key);
                } catch (NumberFormatException e){
                    Bukkit.getLogger().info("[DiveCore-Npc]「"+key+"」は数字ではありません");
                    continue;
                }
                //name
                String name = file2.getString(key+".name");
                NPC npc = new NPC(npcID,name,new ArrayList<>());

                //talkLineの読み込み
                ConfigurationSection talkLineSection = file2.getConfigurationSection(key+".talkLine");
                if(talkLineSection == null){
                    Bukkit.getLogger().info("[DiveCore-Npc]「"+name+"」にtalklineの記述がありません");
                    continue;
                }
                for(String key2 : talkLineSection.getKeys(false)){
                    if (!talkLineSection.isList(key2)){
                        continue;
                    }
                    List<String> talkStringList = talkLineSection.getStringList(key2);
                    //talkLineを自動生成する
                    List<Talk> talks = generateTalkLine(name,key2,talkStringList);
                    npc.getTalks().add(talks);
                }

                NpcMain.setNPC(npcID,npc);
                Bukkit.getLogger().info("[DiveCore-Npc]「"+name+"」をロードしました");
            }
        }
        Bukkit.getLogger().info("[DiveCore-Npc]NPCのロードが完了しました");
    }

    @Override
    public void save() {

    }


    private List<Talk> generateTalkLine(String name, String n, List<String> loadedList){
        List<Talk> generatedLine = new ArrayList<>();
        for(String key : loadedList){
            List<String> separatedKeys = Arrays.asList(key.split(" "));
            //type分岐
            try{
                switch (TalkType.valueOf(separatedKeys.get(0))) {
                    case Give -> {
                        int itemID = Integer.parseInt(separatedKeys.get(1));
                        int amount = Integer.parseInt(separatedKeys.get(2));
                        generatedLine.add(new TalkGiveItem(itemID, amount));
                    }
                    case Loop -> {
                        if (separatedKeys.size() >= 2) {
                            generatedLine.add(new TalkLoop(Integer.parseInt(separatedKeys.get(1))));
                        }
                        else {
                            generatedLine.add(new TalkLoop(1));
                        }
                    }
                    case Text -> {
                        String str = separatedKeys.get(1);
                        generatedLine.add(new TalkText(str));
                    }
                    case Delay -> generatedLine.add(new TalkDelay());
                    /*
                    case MissionStart -> {
                        String type = separatedKeys.get(1);
                        int id = Integer.parseInt(separatedKeys.get(2));
                        generatedLine.addTalk(new TalkMissionStart(type, id));
                    }
                     */
                    case CheckOther -> {
                        int loadID = Integer.parseInt(separatedKeys.get(1));
                        int progress = Integer.parseInt(separatedKeys.get(2));
                        String failed = separatedKeys.get(3);
                        generatedLine.add(new CheckOther(loadID, progress, failed));
                    }
                    case Money -> {
                        int money = Integer.parseInt(separatedKeys.get(1));
                        generatedLine.add(new TalkMoney(money));
                    }
                    case Help -> {
                        HelpType type = HelpType.valueOf(separatedKeys.get(1));
                        generatedLine.add(new TalkHelp(type));
                    }
                    case Shop -> {
                        int shopId = Integer.parseInt(separatedKeys.get(1));
                        generatedLine.add(new TalkShop(shopId));
                    }
                    case Whistle -> generatedLine.add(new TalkWhistle());
                }
            } catch (IllegalArgumentException|IndexOutOfBoundsException|NullPointerException e){
                Bukkit.getLogger().info("[DiveCore-Npc]「"+name+"」の"+n+"回目会話「"+key+"」の取得に失敗しました");
                e.printStackTrace();
            }
        }
        return generatedLine;
    }
}
