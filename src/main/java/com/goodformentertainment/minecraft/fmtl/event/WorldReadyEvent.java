package com.goodformentertainment.minecraft.fmtl.event;

import org.bukkit.World;

public class WorldReadyEvent extends FmtlEvent {
	private final World world;
	
	public WorldReadyEvent(final World world) {
		this.world = world;
	}
	
	public World getWorld() {
		return world;
	}
}
