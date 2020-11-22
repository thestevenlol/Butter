package me.stevenlol.butter.listeners;

import me.stevenlol.butter.Main;
import me.stevenlol.butter.sql.MySQL;
import me.stevenlol.butter.utils.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class CheckBan implements Listener {

    @EventHandler
    public void onJoinCheckIfBanned(PlayerLoginEvent e) {
        Player p = e.getPlayer();
        MySQL sql = Main.getPlugin().getSql();
        try {
            PreparedStatement ps = sql.getConnection().prepareStatement("SELECT * FROM bans WHERE UUID=?");
            ps.setString(1, p.getUniqueId().toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String reason = rs.getString(3);
                String bannerId = rs.getString(4);
                String banner = Bukkit.getOfflinePlayer(UUID.fromString(bannerId)).getName();
                StringBuilder x = new StringBuilder();
                List<String> format = Main.getPlugin().getConfig().getStringList("punishment.ban.format");
                for (String l : format) {
                    x.append(l.replace("%reason%", reason)
                            .replace("%banner%", banner) + "\n");
                }
                e.disallow(PlayerLoginEvent.Result.KICK_BANNED, ChatColor.chat(x.toString().trim()));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

}
