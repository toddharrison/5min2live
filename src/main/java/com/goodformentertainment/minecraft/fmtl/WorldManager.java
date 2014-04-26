package com.goodformentertainment.minecraft.fmtl;

import java.io.File;

import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;

import com.goodformentertainment.minecraft.fmtl.event.FmtlEventNotifier;
import com.goodformentertainment.minecraft.fmtl.event.WorldReadyEvent;

public class WorldManager {
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
				.environment(World.Environment.NORMAL).generator("5min2live").createWorld();
		world.setDifficulty(Difficulty.HARD);
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
}
