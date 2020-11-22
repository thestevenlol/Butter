package me.stevenlol.butter;

import me.stevenlol.butter.commands.channel.GlobalChannelCommand;
import me.stevenlol.butter.commands.channel.StaffChannelCommand;
import me.stevenlol.butter.commands.punish.MuteCommand;
import me.stevenlol.butter.listeners.ChatListener;
import me.stevenlol.butter.listeners.JoinLeaveMessage;
import me.stevenlol.butter.sql.MySQL;
import me.stevenlol.butter.utils.ChatColor;
import me.stevenlol.butter.utils.Config;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

public final class Main extends JavaPlugin {

    public static Main plugin;
    private MySQL sql;
    private Config config;
    private static Chat chat = null;

    @Override
    public void onEnable() {
        plugin = this;
        config = new Config();
        sql = new MySQL();
        try {
            sql.connect();
        } catch (SQLException | ClassNotFoundException throwable) {
            Logger.getLogger("Minecraft").severe("Could not connect to database, plugin shutting down.");
            getPluginLoader().disablePlugin(this);
        }
        registerListener();
        registerCommand();

        setupChat();
        if (sql.isConnected()) {
            Bukkit.getLogger().info("Database connected!");
        }
        try {
            setupSQL();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }

        Bukkit.getOnlinePlayers().forEach(o -> {
            if (o.getPersistentDataContainer().get(new NamespacedKey(this, "channel"), PersistentDataType.STRING) == null) {
                o.getPersistentDataContainer().set(new NamespacedKey(this, "channel"), PersistentDataType.STRING, getConfig().getString("channels.global.prefix"));
            }
        });

        try {
            muteCountdown();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void setupSQL() throws SQLException {
        PreparedStatement ps = sql.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS mutes (NAME VARCHAR(100),UUID VARCHAR(100),TIME INT(10),REASON VARCHAR(100),MUTER VARCHAR(100))");
        PreparedStatement ps1 = sql.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS reports (MESSAGE VARCHAR(100),REPORTER VARCHAR(100))");
        ps.executeUpdate();
        ps1.executeUpdate();
    }

    public void registerCommand() {
        getCommand("global").setExecutor(new GlobalChannelCommand());
        getCommand("staff").setExecutor(new StaffChannelCommand());
        getCommand("mute").setExecutor(new MuteCommand());
    }

    public void registerListener() {
        getServer().getPluginManager().registerEvents(new ChatListener(), this);
        getServer().getPluginManager().registerEvents(new JoinLeaveMessage(), this);
    }

    @Override
    public void onDisable() {
        sql.disconnect();
    }

    private boolean setupChat() {
        RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
        chat = rsp.getProvider();
        return chat != null;
    }

    public static Main getPlugin() {
        return plugin;
    }

    public static Chat getChat() {
        return chat;
    }

    public MySQL getSql() {
        return sql;
    }

    private void muteCountdown() throws SQLException {
        PreparedStatement ps = sql.getConnection().prepareStatement("UPDATE mutes SET TIME=? WHERE UUID=? AND TIME != 0;");
        PreparedStatement ps1 = sql.getConnection().prepareStatement("SELECT TIME FROM mutes WHERE UUID=? AND TIME != 0;");
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                try {
                    ps1.setString(1, player.getUniqueId().toString());
                    ResultSet rs1 = ps1.executeQuery();
                    if (rs1.next()) {
                        int dur = rs1.getInt(1);
                        if (dur == 1) {
                            ps.setString(2, player.getUniqueId().toString());
                            ps.setInt(1, 0);
                            ps.executeUpdate();
                            player.sendMessage(ChatColor.chat(config.getPrefix() + "&aYou have been un-muted."));
                        } else if (dur > 0) {
                            ps.setString(2, player.getUniqueId().toString());
                            ps.setInt(1, dur - 1);
                            ps.executeUpdate();
                        }
                    }
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }, 0, 20);
    }
}
