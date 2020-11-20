package me.stevenlol.butter.listeners;

import me.stevenlol.butter.Main;
import me.stevenlol.butter.utils.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Set;

public class ChatListener implements Listener {

    @EventHandler
    public void mainChatListener(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer(); // player sending the message
        String message = e.getMessage(); // message getting sent
        Set<Player> recipients = e.getRecipients(); // players receiving the message
        String format = Main.getPlugin().getConfig().getString("chat-format");
        e.setFormat(ChatColor.chat(format.replace("%player%", player.getDisplayName())).replace("%message%", message));
    }

}
