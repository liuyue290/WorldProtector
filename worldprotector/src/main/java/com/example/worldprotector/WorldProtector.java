package com.example.worldprotector;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class WorldProtector extends JavaPlugin implements Listener {

    private String protectedWorldName = "zc"; // 受保护的世界名称

    @Override
    public void onEnable() {
        // 注册事件监听器
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("世界保护插件已启用! 保护世界: " + protectedWorldName);

        // 保存默认配置
        saveDefaultConfig();

        // 从配置加载受保护世界名称
        protectedWorldName = getConfig().getString("protected-world", "zc");
    }

    @Override
    public void onDisable() {
        getLogger().info("世界保护插件已禁用");
    }

    // 检查位置是否在受保护世界中
    private boolean isInProtectedWorld(Location loc) {
        return loc.getWorld().getName().equalsIgnoreCase(protectedWorldName);
    }

    // 检查玩家是否有权限绕过保护
    private boolean canBypassProtection(Player player) {
        return player.hasPermission("worldprotector.bypass") || player.getGameMode() == GameMode.CREATIVE;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();

        if (isInProtectedWorld(block.getLocation()) && !canBypassProtection(player)) {
            event.setCancelled(true);
            player.sendMessage("§c你不能在主城世界破坏方块!");
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();

        if (isInProtectedWorld(block.getLocation()) && !canBypassProtection(player)) {
            event.setCancelled(true);
            player.sendMessage("§c你不能在主城世界放置方块!");
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();

        // 防止使用物品（如打火石、桶等）在受保护世界
        if (block != null && isInProtectedWorld(block.getLocation()) && !canBypassProtection(player)) {
            Material itemType = player.getInventory().getItemInMainHand().getType();

            // 禁止使用的物品列表
            if (itemType == Material.FLINT_AND_STEEL || itemType == Material.LAVA_BUCKET ||
                    itemType == Material.WATER_BUCKET || itemType == Material.FIRE_CHARGE) {
                event.setCancelled(true);
                player.sendMessage("§c你不能在主城世界使用这个物品!");
            }
        }
    }
}