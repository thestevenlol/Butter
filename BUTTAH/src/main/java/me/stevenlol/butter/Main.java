package me.stevenlol.butter;

import me.stevenlol.butter.listeners.ChatListener;
import me.stevenlol.butter.utils.Config;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    public static Main plugin;

    @Override
    public void onEnable() {
        plugin = this;
        new Config();
        registerListener();
    }

    public void registerListener() {
        getServer().getPluginManager().registerEvents(new ChatListener(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static Main getPlugin() {
        return plugin;
    }
}
