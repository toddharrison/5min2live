package com.goodformentertainment.minecraft.fmtl.event;

import static com.goodformentertainment.minecraft.util.Log.*;
import static com.goodformentertainment.minecraft.util.PlayerUtil.*;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.goodformentertainment.minecraft.fmtl.PlayerScore;

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
	
	// public List<Map<?, ?>> getTopTen() {
	// final Map<String, Object> scoreMap = data.getConfigurationSection("highscore").getValues(true);
	//
	// logInfo("ScoreMap: " + scoreMap);
	//
	// final List<Map<?, ?>> allScores = new ArrayList<Map<?, ?>>();
	// for (final Object o : scoreMap.values()) {
	// if (o != null && o instanceof Map) {
	// allScores.add((Map<?, ?>) o);
	// }
	// }
	// Collections.sort(allScores, new Comparator<Map<?, ?>>() {
	// @Override
	// public int compare(final Map<?, ?> o1, final Map<?, ?> o2) {
	// final Integer o1Score = (Integer) o1.get("score");
	// final Integer o2Score = (Integer) o2.get("score");
	// return o2Score.compareTo(o1Score);
	// }
	// });
	//
	// return allScores.subList(0, Math.min(allScores.size(), 10));
	// }
	//
	// public void savePlayerHighScore(final Player player, final int newScore) {
	// final int currentScore = data.getInt("highscore." + player.getUniqueId() + ".score", 0);
	// logInfo("Player: " + player.getName() + " " + currentScore + " : " + newScore);
	// if (newScore > currentScore) {
	// // TODO Migrate when Bukkit.getOfflinePlayer(UUID) is available
	// final Map<String, Object> playerScore = new HashMap<String, Object>();
	// playerScore.put("name", player.getName());
	// playerScore.put("score", newScore);
	// data.set("highscore." + player.getUniqueId(), playerScore);
	// logInfo("SAVING");
	// save();
	// }
	// }
	
	public void savePlayerStats(final Player player) {
		data.createSection("player." + player.getUniqueId(), serializePlayer(player));
		save();
		clearPlayer(player);
	}
	
	public void loadPlayerStats(final Player player) {
		clearPlayer(player);
		restorePlayer(player,
				data.getConfigurationSection("player." + player.getUniqueId()).getValues(true));
	}
	
	public Location getExitLocation(final Player player) {
		return deserializeLocation(data.getString("player." + player.getUniqueId() + ".location"));
	}
	
	public SortedSet<PlayerScore> getScoreTop(final int count) {
		final SortedSet<PlayerScore> result = new TreeSet<PlayerScore>(new Comparator<PlayerScore>() {
			private static final int LESS_THAN = -1;
			private static final int GREATER_THAN = 1;
			
			@Override
			public int compare(final PlayerScore s1, final PlayerScore s2) {
				if (s1.getLevel() < s2.getLevel()) {
					return GREATER_THAN;
				} else if (s1.getLevel() > s2.getLevel()) {
					return LESS_THAN;
				} else {
					if (s1.getMinutes() > s2.getMinutes()) {
						return GREATER_THAN;
					} else if (s1.getMinutes() < s2.getMinutes()) {
						return LESS_THAN;
					} else {
						return s1.getPlayerName().compareTo(s2.getPlayerName());
					}
				}
			}
		});
		final ConfigurationSection configSection = data.getConfigurationSection("highscore");
		if (configSection != null) {
			for (final String key : configSection.getValues(false).keySet()) {
				final String playerName = data.getString("highscore." + key + ".playerName");
				final int level = data.getInt("highscore." + key + ".level");
				final double minutes = data.getDouble("highscore." + key + ".minutes");
				final PlayerScore playerScore = new PlayerScore(playerName, level, minutes);
				result.add(playerScore);
			}
			while (result.size() > count) {
				result.remove(result.last());
			}
		}
		return result;
	}
	
	public Map<String, PlayerScore> getHighScores() {
		final Map<String, PlayerScore> result = new HashMap<String, PlayerScore>();
		final ConfigurationSection configSection = data.getConfigurationSection("highscore");
		if (configSection != null) {
			for (final String key : configSection.getValues(false).keySet()) {
				final String playerName = data.getString("highscore." + key + ".playerName");
				final int level = data.getInt("highscore." + key + ".level");
				final double minutes = data.getDouble("highscore." + key + ".minutes");
				result.put(key, new PlayerScore(playerName, level, minutes));
			}
		}
		return result;
	}
	
	public void setHighScore(final Player player, final int level, final double minutes) {
		final String playerId = player.getUniqueId().toString();
		data.set("highscore." + playerId + ".playerName", player.getName());
		data.set("highscore." + playerId + ".level", level);
		data.set("highscore." + playerId + ".minutes", minutes);
		save();
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
