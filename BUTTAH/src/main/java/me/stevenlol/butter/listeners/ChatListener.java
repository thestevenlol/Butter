package me.stevenlol.butter.listeners;

import me.stevenlol.butter.Main;
import me.stevenlol.butter.utils.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.persistence.PersistentDataType;

import java.sql.SQLException;
import java.util.Set;

public class ChatListener implements Listener {

    private TextComponent getChannel(Player player) {
        String channel = player.getPersistentDataContainer().get(new NamespacedKey(Main.getPlugin(), "channel"), PersistentDataType.STRING);
        return new TextComponent(ChatColor.chat(channel + " "));
    }

    private void channelHandler(Player player, TextComponent channel, TextComponent name, TextComponent message) {
        String gPrefix = Main.getPlugin().getConfig().getString("channels.global.prefix");
        String staffPrefix = Main.getPlugin().getConfig().getString("channels.staff.prefix");
        if (player.getPersistentDataContainer().get(new NamespacedKey(Main.getPlugin(), "channel"), PersistentDataType.STRING).equals(gPrefix)) {
            Bukkit.getOnlinePlayers().stream().filter(c -> c.getPersistentDataContainer().get(new NamespacedKey(Main.getPlugin(), "channel"), PersistentDataType.STRING).equals(gPrefix))
                    .forEach(o -> o.spigot().sendMessage(channel, name, message));
        } else if (player.getPersistentDataContainer().get(new NamespacedKey(Main.getPlugin(), "channel"), PersistentDataType.STRING).equals(staffPrefix)) {
            message.setColor(net.md_5.bungee.api.ChatColor.RED);
            Bukkit.getOnlinePlayers().stream().filter(c -> c.getPersistentDataContainer().get(new NamespacedKey(Main.getPlugin(), "channel"), PersistentDataType.STRING).equals(staffPrefix))
                    .forEach(o -> o.spigot().sendMessage(channel, name, message));
        }
    }

    public TextComponent concatenateRanks() {
        return new TextComponent("");
    }

    @EventHandler
    public void mainChatListener(AsyncPlayerChatEvent e) throws SQLException {
        Player player = e.getPlayer(); // player sending the message
        String message = e.getMessage(); // message getting sent
        Set<Player> recipients = e.getRecipients(); // players receiving the message


        // Main Handler
        TextComponent component1 = new TextComponent(ChatColor.chat(player.getDisplayName() + "&e >&f"));
        component1.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.chat("&aClick to tag player")).create()));
        component1.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "@" + player.getName() + " "));

        TextComponent component2 = new TextComponent(ChatColor.chat(" " + message));
        component2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.chat("&cClick to report message.")).create()));

        e.setCancelled(true);

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
