package io.xeron.zenthic.plugin;

import io.xeron.zenthic.plugin.commands.RedeemCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class Plugin extends JavaPlugin {

    @Override
    public void onEnable() {
        this.getCommand("redeem").setExecutor(new RedeemCommand());
        getLogger().info("Added the 'redeem' command.");
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabled the 'redeem' command.");
    }
}