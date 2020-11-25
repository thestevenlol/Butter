package me.stevenlol.butter.listeners;

import me.stevenlol.butter.Main;
import me.stevenlol.butter.utils.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class JoinLeaveMessage implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        e.setJoinMessage(ChatColor.chat(Main.getPlugin().getConfig().getString("join-message").replace("%player%", e.getPlayer().getName())));

        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        skullMeta.setOwningPlayer(e.getPlayer());
        skull.setItemMeta(skullMeta);
        e.getPlayer().getInventory().setItem(1, skull);
        e.getPlayer().getInventory().setItem(1, null);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        e.setQuitMessage(ChatColor.chat(Main.getPlugin().getConfig().getString("leave-message").replace("%player%", e.getPlayer().getName())));
    }

}
