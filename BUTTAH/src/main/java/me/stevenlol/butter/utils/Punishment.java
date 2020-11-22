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
            createMutePlayer(mutee, muter, duration, reason, sql);
            muter.sendMessage(ChatColor.chat(config.getPrefix() + "&6Muted " + mutee.getName() + "."));
        }
    }

    private void createMutePlayer(OfflinePlayer mutee, Player muter, int duration, String reason, MySQL sql) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(), () -> {
            try {
                PreparedStatement ps = sql.getConnection().prepareStatement("INSERT INTO mutes (NAME,UUID,TIME,REASON,MUTER) VALUES (?,?,?,?,?)");
                ps.setString(1, mutee.getName());
                ps.setString(2, mutee.getUniqueId().toString());
                ps.setInt(3, duration);
                ps.setString(4, reason);
                ps.setString(5, muter.getUniqueId().toString());
                ps.executeUpdate();
                ps.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
    }

    public boolean muted(OfflinePlayer player, MySQL sql) throws SQLException {
        synchronized (Main.getPlugin()) {
            UUID uuid = player.getUniqueId();
            PreparedStatement ps = sql.getConnection().prepareStatement("SELECT * FROM mutes WHERE UUID=? AND TIME != 0");
            ps.setString(1, uuid.toString());
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


    public boolean banned(OfflinePlayer player, MySQL sql) throws SQLException {
        synchronized (Main.getPlugin()) {
            UUID uuid = player.getUniqueId();
            PreparedStatement ps = sql.getConnection().prepareStatement("SELECT * FROM bans WHERE UUID=?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            boolean result = rs.next();
            ps.close();
            return result;
        }
    }

    public void banPlayer(OfflinePlayer banee, Player banner, String reason) throws SQLException {
        MySQL sql = Main.getPlugin().getSql();
        Config config = new Config();
        if (sql.isConnected()) {
            if (banned(banee, sql)) {
                banner.sendMessage(ChatColor.chat(config.getPrefix() + "&c" + banee.getName() + " is already banned."));
                return;
            }
            createBanPlayer(banee, banner, reason, sql);
            if (banee.isOnline()) {
                StringBuilder x = new StringBuilder();
                List<String> format = Main.getPlugin().getConfig().getStringList("punishment.ban.format");
                for (String l : format) {
                    x.append(l.replace("%reason%", reason)
                            .replace("%banner%", banner.getName()) + "\n");
                }
                banee.getPlayer().kickPlayer(ChatColor.chat(x.toString().trim()));
            }
            banner.sendMessage(ChatColor.chat(config.getPrefix() + "&6Permanently banned " + banee.getName() + "."));
        }
    }

    private void createBanPlayer(OfflinePlayer banee, Player banner, String reason, MySQL sql) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(), () -> {
            try {
                PreparedStatement ps = sql.getConnection().prepareStatement("INSERT INTO bans (NAME,UUID,REASON,BANNER) VALUES (?,?,?,?)");
                ps.setString(1, banee.getName());
                ps.setString(2, banee.getUniqueId().toString());
                ps.setString(3, reason);
                ps.setString(4, banner.getUniqueId().toString());
                ps.executeUpdate();
                ps.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
    }

    public int calculateMuteDuration(int totalMutes) {
        switch (totalMutes) {
            case 0:
                return 300;
            case 1:
                return 600;
            case 2:
                return 900;
            case 3:
                return 1800;
            case 4:
                return 3600;
            case 5:
                return 7200;
            case 6:
                return 21600;
            case 7:
                return 86400;
            case 8:
                return 604800;
            case 9:
                return -1;
        }
        return 0;
    }

    public int getTotalMutes(OfflinePlayer player) {
        MySQL sql = Main.getPlugin().getSql();
        try {
            PreparedStatement ps = sql.getConnection().prepareStatement("SELECT COUNT(?) FROM mutes");
            ps.setString(1, player.getUniqueId().toString());
            ResultSet rs = ps.executeQuery();
            return rs.getFetchSize();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return 0;
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
