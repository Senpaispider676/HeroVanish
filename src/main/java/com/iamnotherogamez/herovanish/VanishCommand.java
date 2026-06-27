package com.iamnotherogamez.herovanish;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VanishCommand implements CommandExecutor {

    private final HeroVanish plugin;

    public VanishCommand(HeroVanish plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("hero.vanish")) {
            player.sendMessage(plugin.colorize("&cYou do not have permission to use this command!"));
            return true;
        }

        if (plugin.getVanishedPlayers().contains(player.getUniqueId())) {
            // Unvanish
            plugin.getVanishedPlayers().remove(player.getUniqueId());

            // Show to everyone
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                onlinePlayer.showPlayer(plugin, player);
            }

            // Handle Tab/Scoreboard re-entry implicitly handled by showPlayer, clear custom naming if desired
            player.setGlowing(false);

            // Welcome Message
            String welcomeStr = plugin.getConfig().getString("messages.welcome-msg", "&e%player% has joined the game.");
            Bukkit.broadcastMessage(plugin.colorize(welcomeStr.replace("%player%", player.getName())));
            player.sendMessage(plugin.colorize("&aYou are no longer vanished."));
        } else {
            // Vanish
            plugin.getVanishedPlayers().add(player.getUniqueId());

            // Hide from everyone
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (!onlinePlayer.hasPermission("hero.vanish")) {
                    onlinePlayer.hidePlayer(plugin, player);
                }
            }

            // Leave Message
            String leaveStr = plugin.getConfig().getString("messages.leave-msg", "&e%player% has left the game.");
            Bukkit.broadcastMessage(plugin.colorize(leaveStr.replace("%player%", player.getName())));
            player.sendMessage(plugin.colorize("&aYou are now vanished."));
        }

        return true;
    }
}