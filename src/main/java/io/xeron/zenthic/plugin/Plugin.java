package io.xeron.zenthic.plugin;

import io.xeron.zenthic.plugin.commands.RedeemCommand;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class Plugin extends JavaPlugin {
    private File customConfigFile;
    private FileConfiguration customConfig;

    @Override
    public void onEnable() {
        createCustomConfig();
        this.getCommand("redeem").setExecutor(new RedeemCommand());
        getLogger().info("Added the 'redeem' command.");
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabled the 'redeem' command.");
    }

    public FileConfiguration getCustomConfig() {
        return this.customConfig;
    }

    private void createCustomConfig() {
        customConfigFile = new File(getDataFolder(), "config.yml");
        if (!customConfigFile.exists()) {
            customConfigFile.getParentFile().mkdirs();
            saveResource("config.yml", false);
        }

        customConfig = new YamlConfiguration();
        try {
            customConfig.load(customConfigFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }
}