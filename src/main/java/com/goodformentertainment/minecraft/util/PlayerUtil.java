package com.goodformentertainment.minecraft.util;

import static com.goodformentertainment.minecraft.util.Log.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PlayerUtil {
	public static Player getPlayer(final String playerName) {
		return Bukkit.getPlayer(playerName);
	}
	
	public static void clearPlayer(final Player player) {
		player.setCustomName(null);
		player.setDisplayName(null);
		player.setExhaustion(0.0f);
		player.setFoodLevel(20);
		player.setHealth(20);
		player.setLevel(0);
		player.setExp(0.0f);
		player.setFallDistance(0.0f);
		player.setFireTicks(-20);
		player.setRemainingAir(300);
		player.setSaturation(5.0f);
		player.setAllowFlight(false);
		player.setFlying(false);
		player.setFlySpeed(0.1f);
		player.setCanPickupItems(true);
		
		player.getEnderChest().clear();
		player.getInventory().clear();
		player.getEquipment().setArmorContents(
				new ItemStack[player.getEquipment().getArmorContents().length]);
		
		player.setBedSpawnLocation(null);
		// player.setCompassTarget(null);
		// player.setLocation();
		
		player.setGameMode(GameMode.SURVIVAL);
		
		for (final PotionEffect effect : player.getActivePotionEffects()) {
			player.removePotionEffect(effect.getType());
		}
	}
	
	public static Map<String, ?> serializePlayer(final Player player) {
		logInfo("Serializing " + player.getName());
		
		final Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("customName", player.getCustomName());
		map.put("displayName", player.getDisplayName());
		map.put("exhaustion", player.getExhaustion());
		map.put("foodLevel", player.getFoodLevel());
		map.put("health", player.getHealth());
		map.put("level", player.getLevel());
		map.put("exp", player.getExp());
		map.put("fallDistance", player.getFallDistance());
		map.put("fireTicks", player.getFireTicks());
		map.put("remainingAir", player.getRemainingAir());
		map.put("saturation", player.getSaturation());
		map.put("allowFlight", player.getAllowFlight());
		map.put("isFlying", player.isFlying());
		map.put("flySpeed", player.getFlySpeed());
		map.put("canPickupItems", player.getCanPickupItems());
		
		map.put("enderChest", serializeInventory(player.getEnderChest()));
		map.put("inventory", serializeInventory(player.getInventory()));
		map.put("equipment", serializeEntityEquipment(player.getEquipment()));
		
		map.put("bedSpawnLocation", serializeLocation(player.getBedSpawnLocation()));
		map.put("compassTarget", serializeLocation(player.getCompassTarget()));
		map.put("location", serializeLocation(player.getLocation()));
		
		map.put("gameMode", serializeEnum(player.getGameMode()));
		
		map.put("potionEffects", serializePotionEffects(player.getActivePotionEffects()));
		
		return map;
	}
	
	@SuppressWarnings("unchecked")
	public static void restorePlayer(final Player player, final Map<String, ?> map) {
		logInfo("Restoring " + player.getName());
		
		player.setCustomName((String) map.get("customName"));
		player.setDisplayName((String) map.get("displayName"));
		player.setExhaustion(toFloat(map.get("exhaustion")));
		player.setFoodLevel((Integer) map.get("foodLevel"));
		player.setHealth((Double) map.get("health"));
		player.setLevel((Integer) map.get("level"));
		player.setExp(toFloat(map.get("exp")));
		player.setFallDistance(toFloat(map.get("fallDistance")));
		player.setFireTicks((Integer) map.get("fireTicks"));
		player.setRemainingAir((Integer) map.get("remainingAir"));
		player.setSaturation(toFloat(map.get("saturation")));
		player.setAllowFlight((Boolean) map.get("allowFlight"));
		player.setFlying((Boolean) map.get("isFlying"));
		player.setFlySpeed(toFloat(map.get("flySpeed")));
		player.setCanPickupItems((Boolean) map.get("canPickupItems"));
		
		restoreInventory(player.getEnderChest(), (List<Map<String, Object>>) map.get("enderChest"));
		restoreInventory(player.getInventory(), (List<Map<String, Object>>) map.get("inventory"));
		restoreEntityEquipment(player.getEquipment(), (List<Map<String, Object>>) map.get("equipment"));
		
		player.setBedSpawnLocation(deserializeLocation((String) map.get("bedSpawnLocation")));
		player.setCompassTarget(deserializeLocation((String) map.get("compassTarget")));
		player.teleport(deserializeLocation((String) map.get("location")));
		
		player.setGameMode(deserializeEnum(GameMode.class, (String) map.get("gameMode")));
		
		restorePotionEffects(player, (List<?>) map.get("potionEffects"));
	}
	
	public static String serializeEnum(final Enum<?> e) {
		return e.name();
	}
	
	public static <T extends Enum<T>> T deserializeEnum(final Class<T> enumClass, final String value) {
		return Enum.valueOf(enumClass, value);
	}
	
	public static List<Map<String, ?>> serializePotionEffects(final Collection<PotionEffect> effects) {
		final List<Map<String, ?>> effectsList = new ArrayList<Map<String, ?>>();
		for (final PotionEffect effect : effects) {
			final Map<String, Object> effectMap = new HashMap<String, Object>();
			effectMap.put("amplifier", effect.getAmplifier());
			effectMap.put("duration", effect.getDuration());
			final PotionEffectType type = effect.getType();
			effectMap.put("typeName", type.getName());
			effectMap.put("ambient", effect.isAmbient());
			effectsList.add(effectMap);
		}
		return effectsList;
	}
	
	public static void restorePotionEffects(final Player player, final List<?> potionEffects) {
		for (final Object effect : potionEffects) {
			final Map<?, ?> effectMap = (Map<?, ?>) effect;
			final PotionEffectType type = PotionEffectType.getByName((String) effectMap.get("typeName"));
			final int duration = (Integer) effectMap.get("duration");
			final int amplifier = (Integer) effectMap.get("amplifier");
			final boolean ambient = (Boolean) effectMap.get("ambient");
			new PotionEffect(type, duration, amplifier, ambient).apply(player);
		}
	}
	
	public static List<Map<String, Object>> serializeItemStacks(final ItemStack[] itemStacks) {
		final List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for (final ItemStack stack : itemStacks) {
			if (stack != null && stack.getType() != Material.AIR) {
				list.add(stack.serialize());
			} else {
				list.add(null);
			}
		}
		return list;
	}
	
	public static ItemStack[] deserializeItemStacks(final List<Map<String, Object>> list) {
		final ItemStack[] items = new ItemStack[list.size()];
		for (int i = 0; i < list.size(); i++) {
			final Map<String, Object> map = list.get(i);
			if (map != null) {
				items[i] = ItemStack.deserialize(map);
			}
		}
		return items;
	}
	
	public static List<Map<String, Object>> serializeInventory(final Inventory inventory) {
		return serializeItemStacks(inventory.getContents());
	}
	
	public static void restoreInventory(final Inventory inventory,
			final List<Map<String, Object>> list) {
		inventory.setContents(deserializeItemStacks(list));
	}
	
	public static List<Map<String, Object>> serializeEntityEquipment(
			final EntityEquipment entityEquipment) {
		return serializeItemStacks(entityEquipment.getArmorContents());
	}
	
	public static void restoreEntityEquipment(final EntityEquipment entityEquipment,
			final List<Map<String, Object>> list) {
		entityEquipment.setArmorContents(deserializeItemStacks(list));
	}
	
	public static String serializeLocation(final Location location) {
		String locationString = null;
		if (location != null) {
			final StringBuffer sb = new StringBuffer();
			sb.append(location.getWorld().getName());
			sb.append(",");
			sb.append(location.getX());
			sb.append(",");
			sb.append(location.getY());
			sb.append(",");
			sb.append(location.getZ());
			sb.append(",");
			sb.append(location.getYaw());
			sb.append(",");
			sb.append(location.getPitch());
			locationString = sb.toString();
		}
		return locationString;
	}
	
	public static String serializeLocation(final Block block) {
		String locationString = null;
		if (block != null) {
			final StringBuffer sb = new StringBuffer();
			final Location loc = block.getLocation();
			sb.append(loc.getWorld().getName());
			sb.append(",");
			sb.append(loc.getBlockX());
			sb.append(",");
			sb.append(loc.getBlockY());
			sb.append(",");
			sb.append(loc.getBlockZ());
			locationString = sb.toString();
		}
		return locationString;
	}
	
	public static Location deserializeLocation(final String locationString) {
		Location loc = null;
		if (locationString != null) {
			final String[] locParams = locationString.split(",");
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
	
	private static Float toFloat(final Object obj) {
		Float f = null;
		if (obj != null) {
			if (obj instanceof Double) {
				f = ((Double) obj).floatValue();
			} else if (obj instanceof Float) {
				f = (Float) obj;
			}
		}
		return f;
	}
}
