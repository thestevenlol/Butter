package me.stevenlol.butter.commands.punish;

import me.stevenlol.butter.utils.ChatColor;
import me.stevenlol.butter.utils.Config;
import me.stevenlol.butter.utils.Punishment;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;

public class BanCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            return true;
        }

        Player p = (Player) sender;
        Config config = new Config();
        if (p.hasPermission("butter.moderation.ban")) {
            if (args.length == 0) {
                p.sendMessage(ChatColor.chat(config.getPrefix() + "&c/ban <player> <reason>"));
            } else if (args.length == 1) {
                p.sendMessage(ChatColor.chat(config.getPrefix() + "&c/ban " + args[0] + " <reason>"));
            } else {
                StringBuilder x = new StringBuilder();
                for (int i = 1; i < args.length; i++) {
                    x.append(args[i]).append(" ");
                }
                String reason = x.toString().trim();
                Punishment punishment = new Punishment();
                OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
                try {
                    punishment.banPlayer(target, p, reason);
                } catch (SQLException throwable) {
                    throwable.printStackTrace();
                }
            }
        } else {
            p.sendMessage(ChatColor.chat(config.getPrefix() + "&cYou do not have permission to run this command."));
        }

        return true;
    }
}
