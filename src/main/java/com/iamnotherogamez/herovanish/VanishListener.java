package com.iamnotherogamez.herovanish;

import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class VanishListener implements Listener {

    private final HeroVanish plugin;

    public VanishListener(HeroVanish plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Hide currently vanished players from newly joined players
        for (Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {
            if (plugin.getVanishedPlayers().contains(onlinePlayer.getUniqueId())) {
                if (!event.getPlayer().hasPermission("hero.vanish")) {
                    event.getPlayer().hidePlayer(plugin, onlinePlayer);
                }
            }
        }
    }

    @EventHandler
    public void onDamageReceived(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (plugin.getVanishedPlayers().contains(player.getUniqueId())) {
                boolean canGetDamaged = plugin.getConfig().getBoolean("settings.can-get-damaged", false);
                if (!canGetDamaged) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onDamageDealt(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player attacker = (Player) event.getDamager();
            if (plugin.getVanishedPlayers().contains(attacker.getUniqueId())) {
                boolean canHarmOthers = plugin.getConfig().getBoolean("settings.can-harm-others", false);
                if (!canHarmOthers) {
                    event.setCancelled(true);
                    attacker.sendMessage(plugin.colorize("&cYou cannot attack while vanished!"));
                }
            }
        }
    }

    @EventHandler
    public void onChestOpen(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block block = event.getClickedBlock();
            if (block != null && (block.getType().name().contains("CHEST") || block.getType().name().contains("SHULKER"))) {
                Player player = event.getPlayer();
                if (plugin.getVanishedPlayers().contains(player.getUniqueId())) {
                    boolean silentChests = plugin.getConfig().getBoolean("settings.silent-chests", true);
                    if (silentChests) {
                        event.setCancelled(true); // Cancels the animation/sound packet sequence
                        if (block.getState() instanceof Container) {
                            Container container = (Container) block.getState();
                            player.openInventory(container.getInventory());
                            player.sendMessage(plugin.colorize("&7Silently opening container..."));
                        }
                    }
                }
            }
        }
    }
}