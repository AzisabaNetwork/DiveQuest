package com.flora30.divequest;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.flora30.divelib.data.player.PlayerData;
import com.flora30.divelib.data.player.PlayerDataObject;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

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
            PlayerData data = PlayerDataObject.INSTANCE.getPlayerDataMap().get(player.getUniqueId());
            if (data.getNpcData().isTalking()) {
                // 再表示向けにパケットをストックする
                data.getChatStackList().add(event.getPacket().deepClone());
                event.setCancelled(true);
            }
        }
    }
}
