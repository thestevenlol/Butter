package me.stevenlol.butter.commands.channel;

import me.stevenlol.butter.Main;
import me.stevenlol.butter.utils.ChatColor;
import me.stevenlol.butter.utils.Config;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

public class GlobalChannelCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        Config config = new Config();
        String gPrefix = Main.getPlugin().getConfig().getString("channels.global.prefix");
        if (!(sender instanceof Player)) {
            System.out.println("You must be a player to use this command!");
            return true;
        }

        Player p = (Player) sender;
        if (p.hasPermission("butter.channel.global")) {
            if (p.getPersistentDataContainer().has(new NamespacedKey(Main.getPlugin(), "channel"), PersistentDataType.STRING)) {
                String channel = p.getPersistentDataContainer().get(new NamespacedKey(Main.getPlugin(), "channel"), PersistentDataType.STRING);
                if (channel.equals(gPrefix)) {
                    p.sendMessage(ChatColor.chat(config.getPrefix() + "&cYou are already in Global Chat!"));
                    return true;
                }
            }
            p.getPersistentDataContainer().set(new NamespacedKey(Main.getPlugin(), "channel"), PersistentDataType.STRING, gPrefix);
            p.sendMessage(ChatColor.chat(config.getPrefix() + "&aYou are now in Global Chat!"));
        } else {
            p.sendMessage(ChatColor.chat(config.getPrefix() + "&cYou do not have permission to run this command."));
        }

        return true;
    }
}
