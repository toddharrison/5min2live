package com.goodformentertainment.minecraft.fmtl;

import org.bukkit.inventory.ItemStack;

public class Challenge {
	private final String name;
	private final ItemStack stack;
	private final Integer minLevel;
	private final Integer maxLevel;
	
	public Challenge(final String name, final ItemStack stack, final Integer minLevel,
			final Integer maxLevel) {
		this.name = name;
		this.stack = stack;
		this.minLevel = minLevel;
		this.maxLevel = maxLevel;
	}
	
	public String getName() {
		return name;
	}
	
	public ItemStack getItemStack() {
		return stack;
	}
	
	public boolean isInRange(final int level) {
		boolean inRange = true;
		if (minLevel != null && minLevel > level) {
			inRange = false;
		} else if (maxLevel != null && maxLevel < level) {
			inRange = false;
		}
		return inRange;
	}
}
