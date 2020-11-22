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

import java.sql.SQLException;

public class MuteCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;
        Config config = new Config();
        if (player.hasPermission("butter.moderation.mute")) {
            if (args.length == 0) {
                player.sendMessage(ChatColor.chat(config.getPrefix() + "&c/mute <player> <reason>"));
            } else if (args.length == 1) {
                player.sendMessage(ChatColor.chat(config.getPrefix() + "&c/mute " + args[0] + " <reason>"));
            } else {
                StringBuilder reason = new StringBuilder();
                for (int i = 1; i < args.length; i++) {
                    reason.append(args[i]).append(" ");
                }
                Punishment punishment = new Punishment();
                OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
                if (!target.hasPlayedBefore()) {
                    player.sendMessage(ChatColor.chat(config.getPrefix() + "&c" + args[0] + " has never played on the server before."));
                    return true;
                }
                try {
                    punishment.mutePlayer(player, target, reason.toString().trim(), punishment.calculateMuteDuration(punishment.getTotalMutes(target)));
                    if (target.isOnline()) {
                        punishment.sendMuteMessage(target.getPlayer(), Main.getPlugin().getSql());
                    }
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        } else {
            player.sendMessage(ChatColor.chat(config.getPrefix() + "&cYou do not have permission to run this command."));
        }

        return true;
    }
}
