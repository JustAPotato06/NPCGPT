package dev.potato.npcgpt;

import dev.potato.npcgpt.commands.SpawnNPCCommand;
import dev.potato.npcgpt.listeners.ConversationListener;
import dev.potato.npcgpt.traits.ConversationTrait;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.TraitInfo;
import org.bukkit.plugin.java.JavaPlugin;

public final class NPCGPT extends JavaPlugin {
    private static NPCGPT plugin;

    public static NPCGPT getPlugin() {
        return plugin;
    }

    @Override
    public void onEnable() {
        // Initialization
        plugin = this;

        // Configuration
        registerConfiguration();

        // Citizens Traits
        registerTraits();

        // Listeners
        registerListeners();

        // Commands
        registerCommands();
    }

    private void registerConfiguration() {
        // Config.yml
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();
    }

    private void registerTraits() {
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(ConversationTrait.class));
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new ConversationListener(), this);
    }

    private void registerCommands() {
        getCommand("spawnnpc").setExecutor(new SpawnNPCCommand());
    }
}