package com.nfkingd.nfkingplugin.auto_fill_inventory_slot;

import com.nfkingd.nfkingplugin.auto_fill_inventory_slot.listeners.AutoFillInventorySlotEventListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Application extends JavaPlugin {

    private static Application instance;

    @Override
    public void onEnable() {
        instance = this;
        Bukkit.getPluginManager().registerEvents(new AutoFillInventorySlotEventListener(), this);
    }

    public static Application getInstance() {
        return instance;
    }
}
