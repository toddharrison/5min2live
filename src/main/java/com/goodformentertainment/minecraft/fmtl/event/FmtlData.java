package com.goodformentertainment.minecraft.fmtl.event;

import static com.goodformentertainment.minecraft.util.Log.*;
import static com.goodformentertainment.minecraft.util.PlayerUtil.*;

import java.io.File;
import java.io.IOException;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class FmtlData {
	private final File dataFile;
	private FileConfiguration data;
	
	public FmtlData(final File dataFile) {
		this.dataFile = dataFile;
		reload();
	}
	
	public void setOniLocation(final Block block) {
		data.set("oni.location", serializeLocation(block));
		save();
	}
	
	public Location getOniLocation() {
		return deserializeLocation(data.getString("oni.location"));
	}
	
	public void savePlayerStats(final Player player) {
		data.createSection("player." + player.getName(), serializePlayer(player));
		save();
		clearPlayer(player);
	}
	
	public void loadPlayerStats(final Player player) {
		clearPlayer(player);
		restorePlayer(player, data.getConfigurationSection("player." + player.getName())
				.getValues(true));
	}
	
	public Location getExitLocation(final Player player) {
		return deserializeLocation(data.getString("player." + player.getName() + ".location"));
	}
	
	public void save() {
		try {
			data.save(dataFile);
			logInfo("Saved data");
		} catch (final IOException e) {
			logSevere("Could not save data file to " + dataFile, e);
		}
	}
	
	public void reload() {
		data = YamlConfiguration.loadConfiguration(dataFile);
	}
}
