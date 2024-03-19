package dev.potato.npcgpt.commands;

import dev.potato.npcgpt.traits.ConversationTrait;
import dev.potato.npcgpt.utilities.LanguageUtilities;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SpawnNPCCommand implements CommandExecutor {
    private final TextComponent INCORRECT_USAGE = LanguageUtilities.PREFIX.append(LanguageUtilities.INCORRECT_SPAWNNPC_USAGE);

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) return true;

        if (args.length < 2) {
            player.sendMessage(INCORRECT_USAGE);
            return true;
        }

        String name = args[0];
        String role = args[1];

        NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, name);
        npc.addTrait(new ConversationTrait(name, role));
        npc.spawn(player.getLocation());

        player.sendMessage(
                LanguageUtilities.PREFIX.append(
                        Component.text("NPC created with the name \"", NamedTextColor.GREEN)
                                .append(Component.text(name, NamedTextColor.GOLD))
                                .append(Component.text("\" and the role \"", NamedTextColor.GREEN))
                                .append(Component.text(role, NamedTextColor.GOLD))
                                .append(Component.text("\".", NamedTextColor.GREEN))
                )
        );
        player.sendMessage(
                LanguageUtilities.PREFIX.append(
                        Component.text("They will now have conversations with you as a ", NamedTextColor.GREEN)
                                .append(Component.text(role, NamedTextColor.GOLD))
                                .append(Component.text(".", NamedTextColor.GREEN))
                )
        );

        return true;
    }
}