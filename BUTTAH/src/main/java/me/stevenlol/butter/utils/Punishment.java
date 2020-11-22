package me.stevenlol.butter.utils;

import me.stevenlol.butter.Main;
import me.stevenlol.butter.sql.MySQL;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class Punishment {

    public void mutePlayer(Player muter, OfflinePlayer mutee, String reason, int duration) throws SQLException {
        MySQL sql = Main.getPlugin().getSql();
        Config config = new Config();
        if (sql.isConnected()) {
            if (muted(mutee, sql)) {
                muter.sendMessage(ChatColor.chat(config.getPrefix() + "&c" + mutee.getName() + " is already muted."));
                return;
            }
            createPlayer(mutee, muter, duration, reason, sql);
            muter.sendMessage(ChatColor.chat(config.getPrefix() + "&6Muted " + mutee.getName() + "."));
        }
    }

    private void createPlayer(OfflinePlayer mutee, Player muter, int duration, String reason, MySQL sql) throws SQLException {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(), () -> {
            try {
                System.out.println("setting start");
                PreparedStatement ps = sql.getConnection().prepareStatement("INSERT INTO mutes (NAME,UUID,TIME,REASON,MUTER) VALUES (?,?,?,?,?)");
                ps.setString(1, mutee.getName());
                ps.setString(2, mutee.getUniqueId().toString());
                ps.setInt(3, duration);
                ps.setString(4, reason);
                ps.setString(5, muter.getUniqueId().toString());
                ps.executeUpdate();
                ps.close();
                System.out.println("setting end");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
    }

    public boolean muted(OfflinePlayer player, MySQL sql) throws SQLException {
        synchronized (Main.getPlugin()) {
            UUID uuid = player.getUniqueId();
            PreparedStatement ps = sql.getConnection().prepareStatement("SELECT * FROM mutes WHERE UUID=? AND TIME != ?");
            ps.setString(1, uuid.toString());
            ps.setInt(2, 0);
            ResultSet rs = ps.executeQuery();
            boolean result = rs.next();
            ps.close();
            return result;
        }
    }

    public void sendMuteMessage(Player player, MySQL sql) throws SQLException {

        if (!muted(player, sql)) {
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(), () -> {

            try {
                PreparedStatement ps = sql.getConnection().prepareStatement("SELECT * FROM mutes WHERE UUID=? AND TIME != 0");
                ps.setString(1, player.getUniqueId().toString());
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    String muter = rs.getString(5);
                    muter = Bukkit.getOfflinePlayer(UUID.fromString(muter)).getName();
                    String reason = rs.getString(4);
                    int duration = rs.getInt(3);
                    List<String> format = Main.getPlugin().getConfig().getStringList("punishment.mute.format");
                    for (String l : format) {
                        player.sendMessage(ChatColor.chat(l.replace("%muter%", muter)
                                .replace("%reason%", reason)
                                .replace("%duration%", timeFormat(duration))));
                    }
                }
            } catch (SQLException throwable) {
                throwable.printStackTrace();
            }

        });

    }

    public String timeFormat(int seconds) {

        String day = "";
        String hour = "";
        String min = "";
        String sec = "";

        int days = seconds / (60 * 60 * 24);
        if (days == 1) {
            day = "1 day";
        } else if (days > 1) {
            day = days + " days";
        }
        seconds -= days * (60 * 60 * 24);

        int hours = seconds / (60 * 60);
        if (hours == 1) {
            hour = "1 hour";
        } else if (hours > 1) {
            hour = hours + " hours";
        }
        seconds -= hours * (60 * 60);

        int mins = seconds / 60;
        if (mins == 1) {
            min = "1 minute";
        } else if (mins > 1) {
            min = mins + " minutes";
        }
        seconds -= mins * 60;

        if (seconds == 1) {
            sec = "1 second";
        } else if (seconds > 1) {
            sec = seconds + " seconds";
        }

        String fin = day + " " + hour + " " + min + " " + sec;

        if (hour.equals("")) {
            fin = day + " " + min + " " + sec;
            if (min.equals("")) {
                fin = day + " " + sec;
            }
        } else if (min.equals("")) {
            fin = day + " " + hour + " " + sec;
        }

        return fin.trim();

    }


}
