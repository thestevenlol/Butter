package me.stevenlol.butter.commands.report;

import me.stevenlol.butter.Main;
import me.stevenlol.butter.sql.MySQL;
import me.stevenlol.butter.utils.ChatColor;
import me.stevenlol.butter.utils.Config;
import me.stevenlol.butter.utils.ReportUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class ReportCommand implements CommandExecutor, Listener {

    private static String message = "";
    private static Player p = null;
    private static UUID uuid = null;
    private static ResultSet rs = null;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            return true;
        }

        // /report uuid message

        if (args.length == 0) return true;
        if (args.length == 1) return true;

        p = (Player) sender;
        Config config = new Config();
        if (p.hasPermission("butter.moderation.report")) {
            try {
                uuid = UUID.fromString(args[0]);
                StringBuilder x = new StringBuilder();
                for (int i = 1; i < args.length; i++) {
                    x.append(args[i]).append(" ");
                }
                message = x.toString().trim();

                MySQL sql = Main.getPlugin().getSql();
                try {
                    PreparedStatement ps = sql.getConnection().prepareStatement("SELECT * FROM reports WHERE REPORTEE=? AND MESSAGE=?");
                    ps.setString(1, uuid.toString());
                    ps.setString(2, message);
                    rs = ps.executeQuery();
                    if (rs.next()) {
                        p.sendMessage(ChatColor.chat(config.getPrefix() + "&cThat message has already been reported."));
                        return true;
                    }
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }

                Inventory inv = Bukkit.createInventory(null,27, ChatColor.chat("&4Report"));
                ItemStack lime = new ItemStack(Material.LIME_WOOL);
                ItemMeta limeMeta = lime.getItemMeta();
                limeMeta.setDisplayName(ChatColor.chat("&aConfirm report"));
                lime.setItemMeta(limeMeta);
                ItemStack red = new ItemStack(Material.RED_WOOL);
                ItemMeta redMeta = red.getItemMeta();
                redMeta.setDisplayName(ChatColor.chat("&cCancel report"));
                red.setItemMeta(redMeta);
                inv.setItem(12, lime);
                inv.setItem(14, red);
                p.openInventory(inv);
            } catch (IllegalArgumentException e) { return true; }
        } else {
            p.sendMessage(ChatColor.chat(config.getPrefix() + "&cYou do not have permission to run this command."));
        }

        return true;
    }

    private boolean reportCheck() {
        MySQL sql = Main.getPlugin().getSql();
        try {
            PreparedStatement ps = sql.getConnection().prepareStatement("SELECT * FROM reports WHERE REPORTEE=? AND MESSAGE=?");
            ps.setString(1, uuid.toString());
            ps.setString(2, message);
            rs = ps.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    @EventHandler
    public void confirm(InventoryClickEvent e)  {
        Player p = (Player) e.getWhoClicked();
        if (e.getView().getTitle().equals(ChatColor.chat("&4Report"))) {
            e.setCancelled(true);
            if (e.getCurrentItem() == null) return;
            if (e.getCurrentItem().getType().equals(Material.AIR)) return;
            if (e.getCurrentItem().getType().equals(Material.LIME_WOOL)) { // confirm
                ReportUtils reportUtils = new ReportUtils();
                Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(), () -> {
                    reportUtils.setReport(message, p, uuid);
                });
                p.closeInventory();
                Config config = new Config();
                p.sendMessage(ChatColor.chat(config.getPrefix() + "&aReport has been sent."));
                return;
            }
            if (e.getCurrentItem().getType().equals(Material.RED_WOOL)) {
                p.closeInventory();
            }
        }
    }

}
