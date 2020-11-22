package me.stevenlol.butter.commands.punish;

import me.stevenlol.butter.Main;
import me.stevenlol.butter.utils.ChatColor;
import me.stevenlol.butter.utils.Config;
import me.stevenlol.butter.utils.Punishment;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UnBanCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            return true;
        }

        Player p = (Player) sender;
        Config config = new Config();
        if (p.hasPermission("butter.moderation.unban")) {
            if (args.length == 0) {
                p.sendMessage(ChatColor.chat(config.getPrefix() + "&c/unban <player>"));
            } else {
                OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
                Punishment punishment = new Punishment();
                Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(), () -> {
                    try {
                        if (!punishment.banned(target, Main.getPlugin().getSql())) {
                            p.sendMessage(ChatColor.chat(config.getPrefix() + "&c" + target.getName() + " is not banned!"));
                            return;
                        }
                        PreparedStatement ps = Main.getPlugin().getSql().getConnection().prepareStatement("DELETE FROM bans WHERE UUID=?");
                        ps.setString(1, target.getUniqueId().toString());
                        ps.executeUpdate();
                        p.sendMessage(ChatColor.chat(config.getPrefix() + "&6" + target.getName() + " has been unbanned."));
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                });
            }
        } else {
            p.sendMessage(ChatColor.chat(config.getPrefix() + "&cYou do not have permission to run this command."));
        }

        return true;
    }
}
