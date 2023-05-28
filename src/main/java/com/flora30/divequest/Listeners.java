package com.flora30.divequest;

import com.flora30.divelib.event.LayerChangeEvent;
import com.flora30.divequest.mission.MissionMain;
import com.flora30.divequest.mission.MissionTrigger;
import com.flora30.divequest.npc.NpcListener;
import com.flora30.divequest.story.StoryListener;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent;
import net.citizensnpcs.api.event.NPCCreateEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;

public class Listeners implements Listener, CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            //コマンドの実行者がプレイヤーだった時

            Player player = (Player) sender;
            //Player型変数playerに今の実行者を代入する

            String subCommand = args.length == 0 ? "" : args[0];
            String[] subCommands = new String[10];
            for (int i = 1; i <= 10; i++) {
                try{
                    subCommands[i-1] = args[i];
                } catch (IllegalArgumentException| ArrayIndexOutOfBoundsException | NullPointerException e){
                    subCommands[i-1] = "";
                }
            }
            //subCommandに引数を入れる（null対応）

            switch (command.getName()) {
                case "mission" -> {
                    MissionTrigger.onCommand(player);
                    return true;
                }
                case "sty" -> {
                    StoryListener.onCommand(player, subCommand);
                    return true;
                }
                case "diveNpc" -> {
                    NpcListener.onCommand(player, subCommand, subCommands[0]);
                    return true;
                }
            }
        }
        return false;
    }

    @EventHandler
    public void onLayerChange(LayerChangeEvent e){
        StoryListener.onLayerChange(e);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e){
        MissionTrigger.onClickItem(e);
        //キャンセル判定
        if(e.isCancelled()){
            return;
        }
        switch (e.getWhoClicked().getOpenInventory().getTitle()) {
            case "ミッションリスト" -> MissionTrigger.onClick(e);
            case "ヘルプ" -> e.setCancelled(true);
        }
    }

    @EventHandler
    public void onTalk(NPCRightClickEvent e){
        MissionMain.onTalkNPC(e.getClicker(),e.getNPC().getId());
        NpcListener.onTalk(e);
    }

    @EventHandler
    public void onNpcCreate(NPCCreateEvent e){
        NpcListener.onCreate(e);
    }


    @EventHandler
    public void onMythicMobDeath(MythicMobDeathEvent e){
        MissionTrigger.onKill(e);
    }

    @EventHandler
    public void onPickupItem(EntityPickupItemEvent e){
        MissionTrigger.onPickup(e);
    }


    public static int npcParticleTick = 40;
    public static int compassTick = 5;

    //とりあえずshiftキーで
    //1Tickごとに送られている
    private static int count = 0;
    public void onTimer() {
        count++;
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (count % npcParticleTick == 0) {
                NpcListener.onTickParticle(player);
            }
            if (count % compassTick == 0) {
                MissionTrigger.onTickCompass(player);
            }
        }
    }
}
