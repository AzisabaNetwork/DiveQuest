package com.flora30.divequest;

import com.comphenix.protocol.ProtocolLibrary;
import com.flora30.divelib.DiveLib;
import com.flora30.divequest.mission.MissionConfig;
import com.flora30.divequest.npc.NpcConfig;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.concurrent.atomic.AtomicInteger;

public final class DiveQuest extends JavaPlugin {

    public static DiveQuest plugin;
    final AtomicInteger count = new AtomicInteger();
    final BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
    private final Listeners listeners = new Listeners();
    public static boolean firstLoaded = false;

    @Override
    public void onEnable() {

        //デフォルトのconfigを作成
        saveDefaultConfig();

        plugin = this;

        // コマンドと Executor を紐づける
        getCommand("mission").setExecutor(listeners);
        getCommand("sty").setExecutor(listeners);
        getCommand("diveNpc").setExecutor(listeners);

        // イベントと Listener を紐づける
        getServer().getPluginManager().registerEvents(listeners, this);
        ProtocolLibrary.getProtocolManager().addPacketListener(new ChatListener());
        DiveLib.plugin.setQuestEventReady(true);

        // 設定ファイルをロードする
        loadConfig();

        onTimer();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void delayedTask(int delay,Runnable task){
        scheduler.scheduleSyncDelayedTask(this,task,delay);
    }

    public void asyncTask(Runnable task){
        scheduler.runTaskAsynchronously(this,task);
    }

    public void syncTask(Runnable task) {
        scheduler.scheduleSyncDelayedTask(this, task,0);
    }

    // どちらもTalkでNPCを使う -> GiveItemでItemStackが必要無い形に変更
    public void loadConfig() {
        new MissionConfig().load();
        new NpcConfig().load();
        firstLoaded = true;
    }

    private void onTimer(){
        int time = 1;
        if(count.intValue() == 0){
            Bukkit.getLogger().info("Timer Started");
        }

        scheduler.scheduleSyncDelayedTask(this, () -> {
            count.getAndIncrement();
            onTimer();
            //ここでやりたいことを入れる
            listeners.onTimer();
        }, time);
    }
}
