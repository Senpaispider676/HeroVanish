package com.iamnotherogamez.herovanish;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class HeroVanish extends JavaPlugin {

    private final Set<UUID> vanishedPlayers = new HashSet<>();
    private boolean isLicensed = false;

    @Override
    public void onEnable() {
        checkLicenceLifecycle();

        if (!isLicensed) {
            getLogger().severe("========================================");
            getLogger().severe("HeroVanish: INVALID OR MISSING LICENSE!");
            getLogger().severe("Please check plugins/HeroVanish/Licence.txt");
            getLogger().severe("========================================");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        // Save default config if not exists
        saveDefaultConfig();

        // Register Commands & Listeners
        this.getCommand("vanish").setExecutor(new VanishCommand(this));
        getServer().getPluginManager().registerEvents(new VanishListener(this), this);

        getLogger().info("HeroVanish has been successfully activated with License: HERO-2000");
    }

    private void checkLicenceLifecycle() {
        File dataFolder = getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        File licenceFile = new File(dataFolder, "Licence.txt");

        if (!licenceFile.exists()) {
            // Check if config already exists (meaning it was unlocked before)
            File configFile = new File(dataFolder, "config.yml");
            if (configFile.exists()) {
                isLicensed = true;
                return;
            }
            try {
                // First boot: create file and instruct user
                Files.write(licenceFile.toPath(), "PASTE_YOUR_LICENCE_KEY_HERE".getBytes());
                isLicensed = false;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                String content = new String(Files.readAllBytes(licenceFile.toPath())).trim();
                if (content.equals("HERO-2000")) {
                    isLicensed = true;
                    // Delete the token file as requested upon verification
                    licenceFile.delete();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Set<UUID> getVanishedPlayers() {
        return vanishedPlayers;
    }

    public String colorize(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}