package com.flora30.divequest;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLib;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.flora30.diveapi.data.PlayerData;
import com.flora30.diveapi.plugins.CoreAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatListener extends PacketAdapter {
    public ChatListener() {
        super(DiveQuest.plugin, ListenerPriority.HIGH, PacketType.Play.Server.CHAT);
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        if (event.isCancelled()) return;

        // 「Server.CHAT」パケットの「getPlayer」は受信者
        if (event.getPacketType() == PacketType.Play.Server.CHAT) {

            // 送信者がプレイヤーではない場合は通す
            UUID senderUUID = event.getPacket().getUUIDs().read(0);
            Player sender = Bukkit.getPlayer(senderUUID);
            if (sender == null) return;
            //if (sender.isOp()) return;

            // 会話状態の場合はキャンセル
            Player player = event.getPlayer();
            PlayerData data = CoreAPI.getPlayerData(player.getUniqueId());
            if (data.npcData.isTalking) {
                // 再表示向けにパケットをストックする
                data.chatStackList.add(event.getPacket().deepClone());
                event.setCancelled(true);
            }
        }
    }
}
