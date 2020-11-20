package me.stevenlol.butter.utils;

public class ChatColor {

    public static String chat(String message) {
        return org.bukkit.ChatColor.translateAlternateColorCodes('&', message);
    }

}
