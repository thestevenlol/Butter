package me.stevenlol.butter.utils;

import me.stevenlol.butter.Main;
import me.stevenlol.butter.sql.MySQL;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class ReportUtils {

    public void setReport(String message, Player reporter, UUID reportee) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(), () -> {
            MySQL sql = Main.getPlugin().getSql();
            try {
                PreparedStatement ps = sql.getConnection().prepareStatement("INSERT IGNORE INTO reports (MESSAGE,REPORTER,REPORTEE) VALUES (?,?,?)");
                ps.setString(1, message);
                ps.setString(2, reporter.getUniqueId().toString());
                ps.setString(3, reportee.toString());
                ps.executeUpdate();
            } catch (SQLException throwable) {
                throwable.printStackTrace();
            }
        });
    }

}
