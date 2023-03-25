package com.flora30.divequest.npc;

import com.flora30.diveapi.tools.Config;
import com.flora30.divequest.DiveQuest;
import com.flora30.divequest.npc.talk.*;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
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
                NPC npc = new NPC(npcID,name);

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
                    TalkLine talks = generateTalkLine(name,key2,talkStringList);
                    npc.addLine(talks);
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


    private TalkLine generateTalkLine(String name, String n, List<String> loadedList){
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
                    case Delay -> generatedLine.addTalk(new TalkDelay());
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
                    case Whistle -> generatedLine.addTalk(new TalkWhistle());
                }
            } catch (IllegalArgumentException|IndexOutOfBoundsException|NullPointerException e){
                Bukkit.getLogger().info("[DiveCore-Npc]「"+name+"」の"+n+"回目会話「"+key+"」の取得に失敗しました");
                e.printStackTrace();
            }
        }
        return generatedLine;
    }
}
