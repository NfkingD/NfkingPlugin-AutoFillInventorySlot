package com.nfkingd.nfkingplugin.autofillinventoryslot.listeners;

import com.nfkingd.nfkingplugin.autofillinventoryslot.Application;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class AutoFillInventorySlotEventListener implements Listener {

    @EventHandler
    public void onBlockPlaceEvent(BlockPlaceEvent event) {
        var itemStack = event.getItemInHand();
        var itemCount = itemStack.getAmount();
        var player = event.getPlayer();
        var inventory = player.getInventory();
        var indexOfHand = inventory.getHeldItemSlot();
        var material = itemStack.getType();

        if (event.getHand().equals(EquipmentSlot.OFF_HAND)) {
            indexOfHand = 40;
        }

        if (itemCount == 1 && !isExcludedItem(material)) {
            updateInventory(inventory, indexOfHand, material);
        }
    }

    @EventHandler
    public void onEatEvent(PlayerItemConsumeEvent event) {
        var itemStack = event.getItem();
        var itemCount = itemStack.getAmount();
        var player = event.getPlayer();
        var inventory = player.getInventory();
        var indexOfHand = inventory.getHeldItemSlot();
        var material = itemStack.getType();
        var itemOnHand = inventory.getItem(indexOfHand);

        if (itemOnHand == null || !itemOnHand.equals(itemStack)) {
            indexOfHand = 40;
        }

        if (itemCount == 1 && isOneUseFoodItem(material)) {
            updateInventory(inventory, indexOfHand, material);
        }
    }

    @EventHandler
    public void onItemBreakEvent(PlayerItemBreakEvent event) {
        var itemStack = event.getBrokenItem();
        var player = event.getPlayer();
        var inventory = player.getInventory();
        var indexOfHand = inventory.getHeldItemSlot();
        var material = itemStack.getType();
        var itemInHand = inventory.getItem(indexOfHand);
        var itemIsInHand = itemInHand != null && itemInHand.equals(itemStack);

        if (itemIsInHand) {
            updateInventory(inventory, indexOfHand, material);
        }
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        var projectile = event.getEntity();
        var shooter = projectile.getShooter();

        if (shooter instanceof Player player) {
            var inventory = player.getInventory();
            var indexOfHand = inventory.getHeldItemSlot();
            var itemStack = inventory.getItem(indexOfHand);

            if (itemStack != null && isProjectile(itemStack.getType())) {
                runUpdateInventoryTask(itemStack, inventory, indexOfHand);
            } else {
                itemStack = inventory.getItemInOffHand();
                var indexOfOffHand = 40;

                runUpdateInventoryTask(itemStack, inventory, indexOfOffHand);
            }
        }
    }

    private boolean isExcludedItem(Material material) {
        return material.equals(Material.POWDER_SNOW_BUCKET)
                || material.equals(Material.SHULKER_BOX)
                || material.equals(Material.BLACK_SHULKER_BOX)
                || material.equals(Material.BLUE_SHULKER_BOX)
                || material.equals(Material.BROWN_SHULKER_BOX)
                || material.equals(Material.CYAN_SHULKER_BOX)
                || material.equals(Material.GRAY_SHULKER_BOX)
                || material.equals(Material.GREEN_SHULKER_BOX)
                || material.equals(Material.LIGHT_BLUE_SHULKER_BOX)
                || material.equals(Material.LIME_SHULKER_BOX)
                || material.equals(Material.MAGENTA_SHULKER_BOX)
                || material.equals(Material.ORANGE_SHULKER_BOX)
                || material.equals(Material.PINK_SHULKER_BOX)
                || material.equals(Material.PURPLE_SHULKER_BOX)
                || material.equals(Material.RED_SHULKER_BOX)
                || material.equals(Material.WHITE_SHULKER_BOX)
                || material.equals(Material.YELLOW_SHULKER_BOX);
    }

    private boolean isProjectile(Material material) {
        return material.equals(Material.SNOWBALL)
                || material.equals(Material.EGG)
                || material.equals(Material.FIREWORK_ROCKET)
                || material.equals(Material.SPLASH_POTION)
                || material.equals(Material.ENDER_PEARL)
                || material.equals(Material.LINGERING_POTION)
                || material.equals(Material.EXPERIENCE_BOTTLE);
    }

    private void runUpdateInventoryTask(ItemStack itemStack, PlayerInventory inventory, int indexOfHand) {
        var material = itemStack.getType();
        var itemCount = itemStack.getAmount();

        if (itemCount == 1) {
            Bukkit.getScheduler().runTask(Application.getInstance(), () -> updateInventory(inventory, indexOfHand, material));
        }
    }

    private void updateInventory(PlayerInventory inventory, int indexOfHand, Material material) {
        var inventoryContents = inventory.getContents();

        var newItemStack = getNewItemStack(inventoryContents, indexOfHand, material);

        if (newItemStack.isPresent()) {
            var itemStackEntry = newItemStack.get();

            inventoryContents[itemStackEntry.getKey()] = null;
            inventoryContents[indexOfHand] = itemStackEntry.getValue();

            inventory.setContents(inventoryContents);
        }
    }

    private boolean isOneUseFoodItem(Material material) {
        return material.isEdible()
                && !(material.equals(Material.BEETROOT_SOUP)
                || material.equals(Material.MUSHROOM_STEW)
                || material.equals(Material.HONEY_BOTTLE));
    }

    private Optional<Map.Entry<Integer, ItemStack>> getNewItemStack(ItemStack[] inventoryContents, int indexOfHand, Material material) {
        var i = 0;
        Map<Integer, ItemStack> itemMap = new HashMap<>();

        for (ItemStack itemStackFromInventory : inventoryContents) {
            itemMap.put(i, itemStackFromInventory);
            i++;
        }

        return itemMap.entrySet().stream()
                .filter(itemStackEntry -> itemStackEntry.getKey() != indexOfHand)
                .filter(itemStackEntry -> itemStackEntry.getValue() != null)
                .filter(itemStackEntry -> itemStackEntry.getValue().getType().equals(material))
                .findFirst();
    }
}
