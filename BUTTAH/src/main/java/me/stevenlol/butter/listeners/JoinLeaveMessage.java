package me.stevenlol.butter.listeners;

import me.stevenlol.butter.Main;
import me.stevenlol.butter.utils.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinLeaveMessage implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        e.setJoinMessage(ChatColor.chat(Main.getPlugin().getConfig().getString("join-message")));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        e.setQuitMessage(ChatColor.chat(Main.getPlugin().getConfig().getString("leave-message")));
    }

}
