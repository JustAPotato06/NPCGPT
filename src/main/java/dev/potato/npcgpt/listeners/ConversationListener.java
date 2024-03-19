package dev.potato.npcgpt.listeners;

import dev.potato.npcgpt.traits.ConversationTrait;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.concurrent.CompletableFuture;

public class ConversationListener implements Listener {
    @EventHandler
    public void onChatMessage(AsyncChatEvent e) {
        Player player = e.getPlayer();
        String message = ((TextComponent) e.message()).content();

        for (NPC npc : CitizensAPI.getNPCRegistry().sorted()) {
            if (npc.getEntity() == null || npc.getTraitNullable(ConversationTrait.class) == null) continue;

            ConversationTrait trait = npc.getTraitNullable(ConversationTrait.class);
            Player targetPlayer = trait.getTargetPlayer();

            if (targetPlayer == null || !targetPlayer.equals(player)) continue;

            if (npc.getEntity().getLocation().distance(player.getLocation()) > 20) {
                trait.stopConversation();
            } else {
                trait.addMessage(message);

                CompletableFuture.runAsync(() -> {
                    trait.getResponse(player, message);
                });

                e.setCancelled(true);
            }
        }
    }
}