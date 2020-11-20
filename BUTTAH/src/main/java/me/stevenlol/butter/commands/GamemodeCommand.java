package me.stevenlol.butter.commands;

import me.stevenlol.butter.utils.ChatColor;
import me.stevenlol.butter.utils.Config;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GamemodeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        Config config = new Config();

        if (!(sender instanceof Player)) {
            if (args.length != 1) {
                System.out.println("gms/c/a/sp <player>");
                return true;
            }

            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                System.out.println(args[0] + "is not online.");
                return true;
            }

            switch (label) {
                case "gms":
                    if (target.getGameMode().equals(GameMode.SURVIVAL)) {
                        System.out.println(target.getName() + " is already in Gamemode Survival.");
                        return true;
                    }
                    target.setGameMode(GameMode.SURVIVAL);
                    target.sendMessage(ChatColor.chat(config.getPrefix() + "&7Your gamemode has been updated."));
                    System.out.println("Set the gamemode of " + target.getName() + " to Gamemode Survival.");
                    break;
                case "gmc":
                    if (target.getGameMode().equals(GameMode.CREATIVE)) {
                        System.out.println(target.getName() + " is already in Gamemode Creative.");
                        return true;
                    }
                    target.setGameMode(GameMode.CREATIVE);
                    target.sendMessage(ChatColor.chat(config.getPrefix() + "&7Your gamemode has been updated."));
                    System.out.println("Set the gamemode of " + target.getName() + " to Gamemode Creative.");
                    break;
                case "gma":
                    if (target.getGameMode().equals(GameMode.ADVENTURE)) {
                        System.out.println(target.getName() + " is already in Gamemode Adventure.");
                        return true;
                    }
                    target.setGameMode(GameMode.ADVENTURE);
                    target.sendMessage(ChatColor.chat(config.getPrefix() + "&7Your gamemode has been updated."));
                    System.out.println("Set the gamemode of " + target.getName() + " to Gamemode Adventure.");
                    break;
                case "gmsp":
                    if (target.getGameMode().equals(GameMode.SPECTATOR)) {
                        System.out.println(target.getName() + " is already in Gamemode Spectator.");
                        return true;
                    }
                    target.setGameMode(GameMode.SPECTATOR);
                    target.sendMessage(ChatColor.chat(config.getPrefix() + "&7Your gamemode has been updated."));
                    System.out.println("Set the gamemode of " + target.getName() + " to Gamemode Spectator.");
                    break;
                default:
                    System.out.println("gms/c/a/sp <player>");
                    break;
            }
            return true;

        }

        return true;
    }
}
