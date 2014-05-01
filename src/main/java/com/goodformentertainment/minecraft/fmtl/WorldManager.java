package com.goodformentertainment.minecraft.fmtl;

import static com.goodformentertainment.minecraft.util.Log.*;

import java.io.File;
import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.Difficulty;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.generator.BlockPopulator;

import com.goodformentertainment.minecraft.fmtl.event.FmtlEventNotifier;
import com.goodformentertainment.minecraft.fmtl.event.WorldReadyEvent;

public class WorldManager implements Listener {
	private final FmtlPlugin fmtl;
	private final FmtlEventNotifier eventNotifier;
	// private final Map<String, Location> exitLocations;
	private World world;
	
	private volatile boolean isGenerating = false;
	
	public WorldManager(final FmtlPlugin fmtl, final FmtlEventNotifier eventNotifier) {
		this.fmtl = fmtl;
		this.eventNotifier = eventNotifier;
		// exitLocations = new HashMap<String, Location>();
		
		// resetWorld();
		// createWorld();
	}
	
	public boolean isGeneratingWorld() {
		return isGenerating;
	}
	
	public World getWorld() {
		if (world == null) {
			createWorld();
		}
		return world;
	}
	
	// public void resetWorld() {
	// if (!isGenerating) {
	// fmtl.getServer().getScheduler().runTaskAsynchronously(fmtl, new Runnable() {
	// @Override
	// public void run() {
	// isGenerating = true;
	// if (fmtl.getServer().unloadWorld(world, true)) {
	// deleteRecursive(world.getWorldFolder());
	// logInfo("Deleted world");
	// } else {
	// // TODO check if world exists already
	// logWarn("Unable to unload");
	// }
	// world = WorldCreator.name("5min2live").type(WorldType.NORMAL)
	// .environment(World.Environment.NORMAL).generator("5min2live").createWorld();
	// world.setDifficulty(Difficulty.HARD);
	// logInfo("Created world");
	// isGenerating = false;
	//
	// eventNotifier.callEvent(new WorldReadyEvent(world));
	// }
	// });
	// }
	// }
	
	// public Map<String, Location> getExitLocations() {
	// return exitLocations;
	// }
	
	private void createWorld() {
		isGenerating = true;
		world = WorldCreator.name("5min2live").type(WorldType.NORMAL)
				.environment(World.Environment.NORMAL).generateStructures(false).createWorld();
		world.setDifficulty(Difficulty.HARD);
		final boolean pvpEnabled = fmtl.getConfig().getBoolean("pvp", false);
		world.setPVP(pvpEnabled);
		logInfo("PVP Enabled: " + pvpEnabled);
		eventNotifier.callEvent(new WorldReadyEvent(world));
		isGenerating = false;
	}
	
	private void deleteRecursive(final File file) {
		for (final File f : file.listFiles()) {
			if (f.isDirectory()) {
				deleteRecursive(f);
			} else {
				f.delete();
			}
		}
		file.delete();
	}
	
	@EventHandler
	public void onWorldInit(final WorldInitEvent event) {
		final World world = event.getWorld();
		logInfo("World Init: " + world.getName());
		if (world.getName().equals("5min2live")) {
			logInfo(world.getPopulators());
			
			// final SimplexNoiseGenerator noiseGen = new SimplexNoiseGenerator(world.getSeed());
			
			world.getPopulators().add(new BlockPopulator() {
				@Override
				public void populate(final World world, final Random random, final Chunk source) {
					for (int x = 0; x < 16; x++) {
						for (int z = 0; z < 16; z++) {
							if (random.nextDouble() > 0.99) {
								// if (noiseGen.noise(x, z) >= 0.5) {
								// for (int y = 0; y < 128; y++) {
								final Block block = world.getHighestBlockAt(source.getX() * 16 + x,
										16 * source.getZ() + z);
								if (block.getRelative(BlockFace.DOWN).getType() == Material.SAND
										&& block.getRelative(BlockFace.NORTH).getType() == Material.AIR
										&& block.getRelative(BlockFace.SOUTH).getType() == Material.AIR
										&& block.getRelative(BlockFace.EAST).getType() == Material.AIR
										&& block.getRelative(BlockFace.WEST).getType() == Material.AIR) {
									block.setType(Material.CACTUS);
								}
							}
						}
					}
				}
			});
		}
	}
}
