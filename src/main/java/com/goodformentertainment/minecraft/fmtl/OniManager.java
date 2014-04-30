package com.goodformentertainment.minecraft.fmtl;

import static com.goodformentertainment.minecraft.util.Log.*;

import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BlockIterator;

import com.goodformentertainment.minecraft.fmtl.event.FmtlData;
import com.goodformentertainment.minecraft.fmtl.event.FmtlEventHandler;
import com.goodformentertainment.minecraft.fmtl.event.FmtlListener;
import com.goodformentertainment.minecraft.fmtl.event.WorldReadyEvent;

public class OniManager implements Listener, FmtlListener {
	private static final String ONI = "Oni";
	
	private static final int MAX_HEIGHT = 255;
	
	private final FmtlPlugin fmtl;
	private final WorldManager worldManager;
	private final PlayerManager playerManager;
	
	private Location oniLocation;
	
	public OniManager(final FmtlPlugin fmtl, final WorldManager worldManager,
			final PlayerManager playerManager) {
		this.fmtl = fmtl;
		this.worldManager = worldManager;
		this.playerManager = playerManager;
	}
	
	@FmtlEventHandler
	public void onWorldReady(final WorldReadyEvent event) {
		final World world = event.getWorld();
		final FmtlData data = fmtl.getData();
		oniLocation = data.getOniLocation();
		if (oniLocation == null) {
			final Location spawnLocation = world.getSpawnLocation();
			oniLocation = world.getHighestBlockAt(spawnLocation).getLocation();
			oniLocation.setY(oniLocation.getY() + 3);
			data.setOniLocation(oniLocation.getBlock());
		}
		createOni();
	}
	
	@EventHandler
	public void onPlayerInteract(final PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_AIR) {
			final Player player = event.getPlayer();
			final BlockIterator iter = new BlockIterator(player, 6);
			while (iter.hasNext()) {
				final Block block = iter.next();
				if (block.getType() != Material.AIR) {
					if (block.getLocation().equals(oniLocation)) {
						event.setCancelled(true);
						
						// Check the item in hand first to give to the Oni
						// TODO refactor to combine with logic below (DRY)
						final ItemStack is = player.getItemInHand();
						if (playerManager.completeChallenge(player, is)) {
							// TODO refactor and move into playerManager
							if (is.getAmount() == 0) {
								player.setItemInHand(null);
							}
						} else {
							// Otherwise open up an inventory for the Oni
							final Inventory inventory = Bukkit.createInventory(player, 9, ONI);
							player.openInventory(inventory);
						}
					}
					break;
				}
			}
			
		} else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			final Block block = event.getClickedBlock();
			if (block.getLocation().equals(oniLocation)) {
				event.setCancelled(true);
				final Player player = event.getPlayer();
				
				// Check the item in hand first to give to the Oni
				final ItemStack is = player.getItemInHand();
				if (playerManager.completeChallenge(player, is)) {
					// TODO refactor and move into playerManager
					if (is.getAmount() == 0) {
						player.setItemInHand(null);
					}
				} else {
					// Otherwise open up an inventory for the Oni
					final Inventory inventory = Bukkit.createInventory(player, 9, ONI);
					player.openInventory(inventory);
				}
			}
		}
	}
	
	@EventHandler
	public void onInventoryClose(final InventoryCloseEvent event) {
		final Inventory inventory = event.getInventory();
		if (inventory.getName().equals(ONI)) {
			final HumanEntity entity = event.getPlayer();
			if (entity instanceof Player) {
				final Player player = (Player) entity;
				playerManager.completeChallenge(player, inventory);
				
				// Drop remaining contents
				for (final ItemStack itemStack : inventory.getContents()) {
					if (itemStack != null) {
						worldManager.getWorld().dropItemNaturally(oniLocation, itemStack);// .setPickupDelay(20);
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onBlockBreak(final BlockBreakEvent event) {
		final Block block = event.getBlock();
		final int blockX = block.getX();
		final int blockY = block.getY();
		final int blockZ = block.getZ();
		final int oniX = oniLocation.getBlockX();
		final int oniY = oniLocation.getBlockY();
		final int oniZ = oniLocation.getBlockZ();
		if (blockX >= oniX - 10 && blockX <= oniX + 10 && blockZ >= oniZ - 10 && blockZ <= oniZ + 10
				&& blockY > oniY - 10) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onBlockPlace(final BlockPlaceEvent event) {
		final Block block = event.getBlock();
		final int blockX = block.getX();
		final int blockY = block.getY();
		final int blockZ = block.getZ();
		final int oniX = oniLocation.getBlockX();
		final int oniY = oniLocation.getBlockY();
		final int oniZ = oniLocation.getBlockZ();
		if (blockX >= oniX - 10 && blockX <= oniX + 10 && blockZ >= oniZ - 10 && blockZ <= oniZ + 10
				&& blockY > oniY - 10) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onEntityExplode(final EntityExplodeEvent event) {
		final int oniX = oniLocation.getBlockX();
		final int oniY = oniLocation.getBlockY();
		final int oniZ = oniLocation.getBlockZ();
		final Iterator<Block> iter = event.blockList().iterator();
		while (iter.hasNext()) {
			final Block block = iter.next();
			final int blockX = block.getX();
			final int blockY = block.getY();
			final int blockZ = block.getZ();
			if (blockX >= oniX - 10 && blockX <= oniX + 10 && blockZ >= oniZ - 10 && blockZ <= oniZ + 10
					&& blockY > oniY - 10) {
				iter.remove();
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	private void createOni() {
		if (oniLocation.getBlock().getType().equals(Material.GLASS)) {
			logInfo("Using existing Oni");
		} else {
			// TODO: change into a worldedit schematic?
			final World world = oniLocation.getWorld();
			
			final int x = oniLocation.getBlockX();
			int y = oniLocation.getBlockY() + 2;
			final int z = oniLocation.getBlockZ();
			
			// Clean up around the Oni
			
			for (int blockX = x - 10; blockX <= x + 10; blockX++) {
				for (int blockZ = z - 10; blockZ <= z + 10; blockZ++) {
					for (int blockY = y - 11; blockY <= y - 5; blockY++) {
						final Block block = world.getBlockAt(blockX, blockY, blockZ);
						block.setType(Material.GRASS);
					}
				}
			}
			for (int blockX = x - 10; blockX <= x + 10; blockX++) {
				for (int blockZ = z - 10; blockZ <= z + 10; blockZ++) {
					for (int blockY = y - 5; blockY <= MAX_HEIGHT; blockY++) {
						// logInfo("X: " + blockX + " Y: " + blockY + " Z: " + blockZ);
						final Block block = world.getBlockAt(blockX, blockY, blockZ);
						if (!block.isEmpty()) {
							block.setType(Material.AIR);
						}
					}
				}
			}
			
			// Build the Oni
			
			world.getBlockAt(x - 1, y, z - 1).setTypeIdAndData(109, (byte) 3, false);
			world.getBlockAt(x - 1, y, z).setType(Material.SMOOTH_BRICK);
			world.getBlockAt(x - 1, y, z + 1).setTypeIdAndData(109, (byte) 2, false);
			world.getBlockAt(x, y, z - 1).setTypeIdAndData(44, (byte) 5, false);
			world.getBlockAt(x, y, z + 1).setTypeIdAndData(44, (byte) 5, false);
			
			y--;
			world.getBlockAt(x - 2, y, z).setTypeIdAndData(109, (byte) 0, false);
			world.getBlockAt(x - 1, y, z - 1).setType(Material.GLOWSTONE);
			world.getBlockAt(x - 1, y, z).setType(Material.SMOOTH_BRICK);
			world.getBlockAt(x - 1, y, z + 1).setType(Material.GLOWSTONE);
			world.getBlockAt(x, y, z - 1).setType(Material.SMOOTH_BRICK);
			world.getBlockAt(x, y, z).setType(Material.GLASS);
			world.getBlockAt(x, y, z + 1).setType(Material.SMOOTH_BRICK);
			world.getBlockAt(x + 1, y, z - 1).setTypeIdAndData(109, (byte) 1, false);
			world.getBlockAt(x + 1, y, z).setTypeIdAndData(109, (byte) 1, false);
			world.getBlockAt(x + 1, y, z + 1).setTypeIdAndData(109, (byte) 1, false);
			
			y--;
			world.getBlockAt(x - 1, y, z - 1).setTypeIdAndData(98, (byte) 3, false);
			world.getBlockAt(x - 1, y, z).setType(Material.AIR);
			world.getBlockAt(x - 1, y, z + 1).setTypeIdAndData(98, (byte) 3, false);
			world.getBlockAt(x, y, z - 1).setType(Material.SMOOTH_BRICK);
			world.getBlockAt(x, y, z).setType(Material.GLASS);
			world.getBlockAt(x, y, z + 1).setType(Material.SMOOTH_BRICK);
			world.getBlockAt(x + 1, y, z - 1).setType(Material.SMOOTH_BRICK);
			world.getBlockAt(x + 1, y, z).setType(Material.SMOOTH_BRICK);
			world.getBlockAt(x + 1, y, z + 1).setType(Material.SMOOTH_BRICK);
			
			y--;
			world.getBlockAt(x - 1, y, z - 2).setTypeIdAndData(109, (byte) 4, false);
			world.getBlockAt(x - 1, y, z + 2).setTypeIdAndData(109, (byte) 4, false);
			world.getBlockAt(x, y, z - 2).setTypeIdAndData(44, (byte) 13, false);
			world.getBlockAt(x, y, z + 2).setTypeIdAndData(44, (byte) 13, false);
			
			world.getBlockAt(x - 1, y, z - 1).setTypeIdAndData(109, (byte) 4, false);
			world.getBlockAt(x - 1, y, z).setType(Material.SMOOTH_BRICK);
			world.getBlockAt(x - 1, y, z + 1).setTypeIdAndData(109, (byte) 4, false);
			world.getBlockAt(x, y, z - 1).setType(Material.SMOOTH_BRICK);
			world.getBlockAt(x, y, z + 1).setType(Material.SMOOTH_BRICK);
			world.getBlockAt(x + 1, y, z - 1).setType(Material.SMOOTH_BRICK);
			world.getBlockAt(x + 1, y, z).setType(Material.SMOOTH_BRICK);
			world.getBlockAt(x + 1, y, z + 1).setType(Material.SMOOTH_BRICK);
			
			y--;
			world.getBlockAt(x - 2, y, z - 1).setTypeIdAndData(44, (byte) 5, false);
			world.getBlockAt(x - 2, y, z).setTypeIdAndData(109, (byte) 0, false);
			world.getBlockAt(x - 2, y, z + 1).setTypeIdAndData(44, (byte) 5, false);
			world.getBlockAt(x - 1, y, z - 1).setType(Material.SMOOTH_BRICK);
			world.getBlockAt(x - 1, y, z).setType(Material.SMOOTH_BRICK);
			world.getBlockAt(x - 1, y, z + 1).setType(Material.SMOOTH_BRICK);
			world.getBlockAt(x, y, z - 1).setType(Material.SMOOTH_BRICK);
			world.getBlockAt(x, y, z).setType(Material.BEACON);
			world.getBlockAt(x, y, z + 1).setType(Material.SMOOTH_BRICK);
			world.getBlockAt(x + 1, y, z - 1).setType(Material.SMOOTH_BRICK);
			world.getBlockAt(x + 1, y, z).setType(Material.SMOOTH_BRICK);
			world.getBlockAt(x + 1, y, z + 1).setType(Material.SMOOTH_BRICK);
			
			y--;
			world.getBlockAt(x - 4, y, z - 2).setTypeId(44);
			world.getBlockAt(x - 4, y, z - 1).setTypeId(44);
			world.getBlockAt(x - 4, y, z).setTypeId(44);
			world.getBlockAt(x - 4, y, z + 1).setTypeId(44);
			world.getBlockAt(x - 4, y, z + 2).setTypeId(44);
			world.getBlockAt(x - 3, y, z - 3).setTypeId(44);
			world.getBlockAt(x - 2, y, z - 3).setTypeId(44);
			world.getBlockAt(x - 1, y, z - 3).setTypeId(44);
			world.getBlockAt(x - 0, y, z - 3).setTypeId(44);
			world.getBlockAt(x + 1, y, z - 3).setTypeId(44);
			world.getBlockAt(x + 2, y, z - 3).setTypeId(44);
			world.getBlockAt(x - 3, y, z + 3).setTypeId(44);
			world.getBlockAt(x - 2, y, z + 3).setTypeId(44);
			world.getBlockAt(x - 1, y, z + 3).setTypeId(44);
			world.getBlockAt(x - 0, y, z + 3).setTypeId(44);
			world.getBlockAt(x + 1, y, z + 3).setTypeId(44);
			world.getBlockAt(x + 2, y, z + 3).setTypeId(44);
			world.getBlockAt(x + 3, y, z - 2).setTypeId(44);
			world.getBlockAt(x + 3, y, z - 1).setTypeId(44);
			world.getBlockAt(x + 3, y, z).setTypeId(44);
			world.getBlockAt(x + 3, y, z + 1).setTypeId(44);
			world.getBlockAt(x + 3, y, z + 2).setTypeId(44);
			
			world.getBlockAt(x - 3, y, z - 2).setType(Material.SMOOTH_BRICK);
			world.getBlockAt(x - 3, y, z - 1).setType(Material.SMOOTH_BRICK);
			world.getBlockAt(x - 3, y, z).setType(Material.SMOOTH_BRICK);
			world.getBlockAt(x - 3, y, z + 1).setType(Material.SMOOTH_BRICK);
			world.getBlockAt(x - 3, y, z + 2).setType(Material.SMOOTH_BRICK);
			world.getBlockAt(x - 2, y, z - 2).setType(Material.SMOOTH_BRICK);
			world.getBlockAt(x - 2, y, z - 1).setType(Material.SMOOTH_BRICK);
			world.getBlockAt(x - 2, y, z).setType(Material.SMOOTH_BRICK);
			world.getBlockAt(x - 2, y, z + 1).setType(Material.SMOOTH_BRICK);
			world.getBlockAt(x - 2, y, z + 2).setType(Material.SMOOTH_BRICK);
			world.getBlockAt(x - 1, y, z - 2).setType(Material.SMOOTH_BRICK);
			world.getBlockAt(x - 1, y, z - 1).setType(Material.IRON_BLOCK);
			world.getBlockAt(x - 1, y, z).setType(Material.IRON_BLOCK);
			world.getBlockAt(x - 1, y, z + 1).setType(Material.IRON_BLOCK);
			world.getBlockAt(x - 1, y, z + 2).setType(Material.SMOOTH_BRICK);
			world.getBlockAt(x, y, z - 2).setType(Material.SMOOTH_BRICK);
			world.getBlockAt(x, y, z - 1).setType(Material.IRON_BLOCK);
			world.getBlockAt(x, y, z).setType(Material.IRON_BLOCK);
			world.getBlockAt(x, y, z + 1).setType(Material.IRON_BLOCK);
			world.getBlockAt(x, y, z + 2).setType(Material.SMOOTH_BRICK);
			world.getBlockAt(x + 1, y, z - 2).setType(Material.SMOOTH_BRICK);
			world.getBlockAt(x + 1, y, z - 1).setType(Material.IRON_BLOCK);
			world.getBlockAt(x + 1, y, z).setType(Material.IRON_BLOCK);
			world.getBlockAt(x + 1, y, z + 1).setType(Material.IRON_BLOCK);
			world.getBlockAt(x + 1, y, z + 2).setType(Material.SMOOTH_BRICK);
			world.getBlockAt(x + 2, y, z - 2).setType(Material.SMOOTH_BRICK);
			world.getBlockAt(x + 2, y, z - 1).setType(Material.SMOOTH_BRICK);
			world.getBlockAt(x + 2, y, z).setType(Material.SMOOTH_BRICK);
			world.getBlockAt(x + 2, y, z + 1).setType(Material.SMOOTH_BRICK);
			world.getBlockAt(x + 2, y, z + 2).setType(Material.SMOOTH_BRICK);
			
			world.setSpawnLocation(x - 6, y, z);
			
			logInfo("Created new Oni");
		}
	}
}
