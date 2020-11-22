package me.stevenlol.butter.listeners;

import me.stevenlol.butter.Main;
import me.stevenlol.butter.utils.ChatColor;
import me.stevenlol.butter.utils.Punishment;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.persistence.PersistentDataType;

import java.sql.SQLException;

public class ChatListener implements Listener {

    private TextComponent getChannel(Player player) {
        String channel = player.getPersistentDataContainer().get(new NamespacedKey(Main.getPlugin(), "channel"), PersistentDataType.STRING);
        return new TextComponent(ChatColor.chat(channel + " "));
    }

    private void channelHandler(Player player, TextComponent channel, TextComponent name, TextComponent message) {
        String gPrefix = Main.getPlugin().getConfig().getString("channels.global.prefix");
        String staffPrefix = Main.getPlugin().getConfig().getString("channels.staff.prefix");
        if (player.getPersistentDataContainer().get(new NamespacedKey(Main.getPlugin(), "channel"), PersistentDataType.STRING).equals(gPrefix)) {
            Bukkit.getOnlinePlayers().forEach(o -> o.spigot().sendMessage(channel, name, message));
        } else if (player.getPersistentDataContainer().get(new NamespacedKey(Main.getPlugin(), "channel"), PersistentDataType.STRING).equals(staffPrefix)) {
            message.setColor(net.md_5.bungee.api.ChatColor.RED);
            Bukkit.getOnlinePlayers().forEach(o -> {
                if (o.hasPermission("butter.staff")) {
                    o.spigot().sendMessage(channel, name, message);
                }
            });
        }
    }

    public String ranks(Player player) {
        Chat chat = Main.getChat();
        return chat.getPlayerPrefix(player);
    }

    @EventHandler
    public void mainChatListener(AsyncPlayerChatEvent e) throws SQLException {
        e.setCancelled(true);
        Player player = e.getPlayer(); // player sending the message
        String message = e.getMessage(); // message getting sent
        Punishment punishment = new Punishment();
        if (punishment.muted(player, Main.getPlugin().getSql())) {
            punishment.sendMuteMessage(player, Main.plugin.getSql());
            return;
        }

        // Main Handler
        TextComponent component1 = new TextComponent(ChatColor.chat(ranks(player) + player.getDisplayName() + "&e >&f"));
        component1.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.chat("&aClick to tag player")).create()));
        component1.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "@" + player.getName() + " "));

        TextComponent component2 = new TextComponent(ChatColor.chat(" " + message));
        component2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.chat("&cClick to report message.")).create()));


        channelHandler(player, getChannel(player), component1, component2);
    }

    @EventHandler
    public void checkTag(AsyncPlayerChatEvent e) {
        String message = e.getMessage();
        String[] words = message.split("\\s+");
        String tagged = null;
        for (String word : words) {
            if (word.startsWith("@")) {
                tagged = word;
            }
        }
        if (tagged == null) {
            return;
        }
        StringBuilder x = new StringBuilder();
        x.append(tagged);
        x.deleteCharAt(0);
        Bukkit.getOnlinePlayers().forEach(o -> {
            if (o.getName().equalsIgnoreCase(x.toString().trim())) {
                TextComponent c = new TextComponent(ChatColor.chat(e.getPlayer().getDisplayName() + "&a has pinged you in chat!"));
                o.spigot().sendMessage(ChatMessageType.ACTION_BAR, c);
                o.playSound(o.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 2, 1);
            }
        });
    }

}
