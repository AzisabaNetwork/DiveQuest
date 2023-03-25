package com.flora30.divequest.story;

import com.flora30.diveapi.data.Story;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

public class StoryConfig {

    // Layerの ID section から
    public static void load(String layerID, ConfigurationSection section){
        Story story = new Story();

        story.displayName = section.getString("displayName");
        story.displaySub = (section.getString("displaySub"));
        story.noticeDisplay = (section.getBoolean("noticeDisplay"));
        story.storyList = (generateStory(section.getStringList("story")));

        StoryMain.putStory(layerID,story);
    }

    //storyを生成
    private static List<String> generateStory(List<String> loadedList){
        List<String> generated = new ArrayList<>();
        for(String str : loadedList){
            generated.add(ChatColor.translateAlternateColorCodes('&', str));
        }
        return generated;
    }
}
