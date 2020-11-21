package me.stevenlol.butter;

import me.stevenlol.butter.listeners.ChatListener;
import me.stevenlol.butter.listeners.JoinLeaveMessage;
import me.stevenlol.butter.sql.MySQL;
import me.stevenlol.butter.utils.Config;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.logging.Logger;

public final class Main extends JavaPlugin {

    public static Main plugin;
    private MySQL sql;

    @Override
    public void onEnable() {
        plugin = this;
        new Config();
        sql = new MySQL();
        try {
            sql.connect();
        } catch (SQLException | ClassNotFoundException throwables) {
            Logger.getLogger("Minecraft").severe("Could not connect to database, plugin shutting down.");
            getPluginLoader().disablePlugin(this);
        }
        registerListener();
        registerCommand();
    }

    public void registerCommand() {

    }

    public void registerListener() {
        getServer().getPluginManager().registerEvents(new ChatListener(), this);
        getServer().getPluginManager().registerEvents(new JoinLeaveMessage(), this);
    }

    @Override
    public void onDisable() {
        try {
            sql.disconnect();
        } catch (SQLException | ClassNotFoundException throwables) {
            Logger.getLogger("Minecraft").severe("Could not disconnect database.");
        }
    }

    public static Main getPlugin() {
        return plugin;
    }
}
