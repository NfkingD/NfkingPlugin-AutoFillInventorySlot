package com.nfkingd.nfkingplugin.autofillinventoryslot;

import com.nfkingd.nfkingplugin.autofillinventoryslot.listeners.AutoFillInventorySlotEventListener;
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
