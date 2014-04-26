package com.goodformentertainment.minecraft.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlayerUtil {
	public static Player getPlayer(final String playerName) {
		return Bukkit.getPlayer(playerName);
	}
}
