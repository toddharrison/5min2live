package com.goodformentertainment.minecraft.fmtl.event;

import static com.goodformentertainment.minecraft.util.Log.*;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

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
	
	public void setPlayerExit(final Player player, final Location location) {
		data.set("player." + player.getName() + ".exit", serializeLocation(location));
		save();
	}
	
	public Location getPlayerExit(final Player player) {
		return deserializeLocation(data.getString("player." + player.getName() + ".exit"));
	}
	
	public void savePlayerStats(final Player player) {
		data.set("player." + player.getName() + ".health", player.getHealth());
		data.set("player." + player.getName() + ".exp", player.getExp());
		data.set("player." + player.getName() + ".food", player.getFoodLevel());
		save();
	}
	
	public void loadPlayerStats(final Player player) {
		player.setHealth(data.getDouble("player." + player.getName() + ".health"));
		player.setExp((float) data.getDouble("player." + player.getName() + ".exp"));
		player.setFoodLevel(data.getInt("player." + player.getName() + ".food"));
	}
	
	public void savePlayerInventory(final Player player) {
		final Map<Integer, String> inv = new HashMap<Integer, String>();
		int slot = 0;
		for (final ItemStack stack : player.getInventory().getContents()) {
			if (stack == null) {
				inv.put(slot++, null);
			} else {
				inv.put(slot++, serializeItemStack(stack));
			}
		}
		data.createSection("player." + player.getName() + ".inv", inv);
		
		save();
	}
	
	public void loadPlayerInventory(final Player player) {
		final PlayerInventory inv = player.getInventory();
		final Map<String, Object> map = data.getConfigurationSection(
				"player." + player.getName() + ".inv").getValues(false);
		for (final String slot : map.keySet()) {
			final ItemStack stack = deserializeItemStack((String) map.get(slot));
			inv.setItem(Integer.parseInt(slot), stack);
		}
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
	
	private String serializeLocation(final Block block) {
		final StringBuffer sb = new StringBuffer();
		final Location loc = block.getLocation();
		sb.append(loc.getWorld().getName());
		sb.append(",");
		sb.append(loc.getBlockX());
		sb.append(",");
		sb.append(loc.getBlockY());
		sb.append(",");
		sb.append(loc.getBlockZ());
		return sb.toString();
	}
	
	private String serializeLocation(final Location loc) {
		final StringBuffer sb = new StringBuffer();
		sb.append(loc.getWorld().getName());
		sb.append(",");
		sb.append(loc.getX());
		sb.append(",");
		sb.append(loc.getY());
		sb.append(",");
		sb.append(loc.getZ());
		sb.append(",");
		sb.append(loc.getYaw());
		sb.append(",");
		sb.append(loc.getPitch());
		return sb.toString();
	}
	
	private Location deserializeLocation(final String locString) {
		Location loc = null;
		if (locString != null) {
			final String[] locParams = locString.split(",");
			final World world = Bukkit.getWorld(locParams[0]);
			final double x = Double.parseDouble(locParams[1]);
			final double y = Double.parseDouble(locParams[2]);
			final double z = Double.parseDouble(locParams[3]);
			if (locParams.length == 4) {
				loc = new Location(world, x, y, z);
			} else if (locParams.length == 6) {
				final float yaw = Float.parseFloat(locParams[4]);
				final float pitch = Float.parseFloat(locParams[5]);
				loc = new Location(world, x, y, z, yaw, pitch);
			} else {
				throw new IllegalArgumentException("The specified location String is invalid.");
			}
		}
		return loc;
	}
	
	private String serializeItemStack(final ItemStack itemStack) {
		final StringBuffer sb = new StringBuffer();
		sb.append(itemStack.getTypeId());
		sb.append(",");
		sb.append(itemStack.getAmount());
		sb.append(",");
		sb.append(itemStack.getDurability());
		// TODO: save enchantment
		// sb.append(",");
		// sb.append(itemStack.getEnchantments());
		return sb.toString();
	}
	
	private ItemStack deserializeItemStack(final String itemStackString) {
		ItemStack stack = null;
		if (itemStackString != null) {
			final String[] stackParams = itemStackString.split(",");
			final int type = Integer.parseInt(stackParams[0]);
			final int amount = Integer.parseInt(stackParams[1]);
			final short damage = Short.parseShort(stackParams[2]);
			// TODO: restore enchantment
			stack = new ItemStack(type, amount, damage);
		}
		return stack;
	}
}
