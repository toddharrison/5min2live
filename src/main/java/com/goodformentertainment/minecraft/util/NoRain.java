package com.goodformentertainment.minecraft.util;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;

/**
 * Derived from Codisimus at https://forums.bukkit.org/threads/no-weather-plugin.226622/
 * 
 * @author todd
 */
public class NoRain implements Listener {
	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
	public void onWeatherChange(final WeatherChangeEvent event) {
		if (event.toWeatherState()) {
			event.setCancelled(true);
		}
	}
}
