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

public class UnMuteCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            return true;
        }

        Player p = (Player) sender;
        Config config = new Config();
        Punishment punishment = new Punishment();
        if (p.hasPermission("butter.moderation.unmute")) {

            if (args.length == 0) {
                p.sendMessage(ChatColor.chat(config.getPrefix() + "&c/unmute <player>"));
            } else {
                OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
                if (!target.hasPlayedBefore()) {
                    p.sendMessage(ChatColor.chat(config.getPrefix() + "&c" + args[0] + " has never played on the server before."));
                    return true;
                }
                try {
                    if (punishment.muted(target, Main.getPlugin().getSql())) {
                        PreparedStatement ps = Main.getPlugin().getSql().getConnection().prepareStatement("DELETE FROM mutes WHERE UUID=? AND TIME != 0");
                        ps.setString(1, target.getUniqueId().toString());
                        ps.executeUpdate();
                        p.sendMessage(ChatColor.chat(config.getPrefix() + "&6Unmuted " + target.getName() + "."));
                        if (target.isOnline()) {
                            target.getPlayer().sendMessage(ChatColor.chat(config.getPrefix() + "&aYou have been unmuted by " + p.getName() + "."));
                        }
                    }
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }

        } else {
            p.sendMessage(ChatColor.chat(config.getPrefix() + "&cYou do not have permission to run this command."));
        }

        return true;
    }
}
