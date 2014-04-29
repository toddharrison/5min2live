package com.goodformentertainment.minecraft.fmtl;

import static com.goodformentertainment.minecraft.util.Log.*;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

// TODO: reset the world if it is empty?
public class PlayerManager implements Listener {
	private static final int ONE_SECOND = 20;
	
	private final FmtlPlugin fmtl;
	private final WorldManager worldManager;
	private final ChallengeManager challengeManager;
	private final Map<Player, PlayerData> players;
	
	private final ScoreboardManager scoreboardManager;
	private final Scoreboard scoreboard;
	private final Objective objective;
	
	public PlayerManager(final FmtlPlugin fmtl, final WorldManager worldManager,
			final ChallengeManager challengeManager) {
		this.fmtl = fmtl;
		this.worldManager = worldManager;
		this.challengeManager = challengeManager;
		players = new HashMap<Player, PlayerData>();
		scoreboardManager = Bukkit.getScoreboardManager();
		scoreboard = scoreboardManager.getNewScoreboard();
		objective = scoreboard.registerNewObjective("test", "dummy");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		objective.setDisplayName("Level");
		
		// for (final Player player : fmtl.getServer().getOnlinePlayers()) {
		// players.add(player);
		// player.sendMessage(ChatColor.RED + "You have 5 minutes to live!");
		// logInfo(player.getName() + " joined");
		// }
		
		final BukkitTask task = Bukkit.getServer().getScheduler().runTaskTimer(fmtl, new Runnable() {
			@Override
			public void run() {
				if (!players.isEmpty()) {
					for (final PlayerData data : players.values()) {
						data.decrement();
					}
				}
			}
		}, 0, ONE_SECOND);
	}
	
	@EventHandler
	public void onPlayerJoin(final PlayerJoinEvent event) {
		final Player player = event.getPlayer();
		if (inFmtl(player)) {
			addPlayer(player);
		}
	}
	
	@EventHandler
	public void onPlayerChangeWorld(final PlayerChangedWorldEvent event) {
		final Player player = event.getPlayer();
		if (inFmtl(player)) {
			addPlayer(player);
		} else if (event.getFrom() == worldManager.getWorld()) {
			removePlayer(player);
		}
	}
	
	@EventHandler
	public void onPlayerQuit(final PlayerQuitEvent event) {
		final Player player = event.getPlayer();
		if (inFmtl(player)) {
			removePlayer(player);
		}
	}
	
	@EventHandler
	public void onPlayerDeath(final PlayerDeathEvent event) {
		final Player player = event.getEntity();
		if (inFmtl(player)) {
			removePlayer(player);
			player.sendMessage(ChatColor.RED + "You have met your sudden but inevitable demise.");
			logInfo(player.getName() + " has died");
		}
	}
	
	@EventHandler
	public void onPlayerRespawn(final PlayerRespawnEvent event) {
		final Player player = event.getPlayer();
		if (inFmtl(player)) {
			fmtl.getData().loadPlayerStats(player);
			event.setRespawnLocation(player.getLocation());
		}
		// if (players.isEmpty()) {
		// // worldManager.resetWorld();
		// }
	}
	
	public Challenge getCurrentChallenge(final Player player) {
		return players.get(player).getChallenge();
	}
	
	// TODO refactor and combine with other completeChallenge
	public boolean completeChallenge(final Player player, final ItemStack stack) {
		boolean success = false;
		final ItemStack challenge = players.get(player).getChallengeItem();
		if (stack.isSimilar(challenge)) {
			// Same type, check amounts
			if (stack.getAmount() >= challenge.getAmount()) {
				stack.setAmount(stack.getAmount() - challenge.getAmount());
				nextLevel(player);
				player.sendMessage(ChatColor.RED + "I am satisfied with your sacrifice, for now! Bring me "
						+ getCurrentChallenge(player).getName() + "!");
				success = true;
			}
		}
		return success;
	}
	
	// TODO refactor and combine with other completeChallenge
	public boolean completeChallenge(final Player player, final Inventory inventory) {
		boolean success = false;
		for (int i = 0; i < inventory.getSize(); i++) {
			final ItemStack stack = inventory.getItem(i);
			if (stack != null) {
				if (completeChallenge(player, stack)) {
					success = true;
					if (stack.getAmount() == 0) {
						inventory.clear(i);
					}
					break;
				}
			}
		}
		if (!success) {
			final Location location = player.getLocation();
			player.sendMessage(ChatColor.RED + "I am unsatisfied, be warned! Bring me "
					+ getCurrentChallenge(player).getName() + "!");
			worldManager.getWorld().strikeLightningEffect(location);
		}
		return success;
	}
	
	public void nextLevel(final Player player) {
		if (players.containsKey(player)) {
			players.get(player).nextLevel();
		}
	}
	
	private boolean inFmtl(final Player player) {
		return player.getWorld() == worldManager.getWorld();
	}
	
	private boolean addPlayer(final Player player) {
		boolean added = false;
		if (!players.containsKey(player)) {
			final PlayerData data = new PlayerData(player);
			players.put(player, data);
			player.getInventory().clear();
			player.getEquipment().clear();
			player.setHealth(20);
			player.setFoodLevel(20);
			added = true;
			player.sendMessage(ChatColor.RED + "I am the ONI I demand your supplication!");
			player.sendMessage(ChatColor.RED + "Bring me " + data.getChallengeName() + " or die!");
			player.setScoreboard(scoreboard);
		}
		return added;
	}
	
	private boolean removePlayer(final Player player) {
		boolean removed = false;
		if (players.containsKey(player)) {
			final PlayerData data = players.remove(player);
			removed = true;
			player.sendMessage(ChatColor.GREEN + "You scored " + data.getLevel() + " at 5min2live");
			player.setScoreboard(scoreboardManager.getNewScoreboard());
		}
		return removed;
	}
	
	private class PlayerData {
		private static final int FIVE_MINUTES = 60 * 5;
		
		private final Player player;
		private int level;
		private final Score score;
		private int secondsLeft;
		private Challenge challenge;
		
		public PlayerData(final Player player) {
			this.player = player;
			score = objective.getScore(player);
			score.setScore(level);
			secondsLeft = FIVE_MINUTES;
			challenge = challengeManager.getRandomChallenge(level);
		}
		
		public Challenge getChallenge() {
			return challenge;
		}
		
		public ItemStack getChallengeItem() {
			return challenge.getItemStack();
		}
		
		public String getChallengeName() {
			return challenge.getName();
		}
		
		public void decrement() {
			if (secondsLeft == 0) {
				player.sendMessage(ChatColor.RED + "You are out of time!");
				final World world = worldManager.getWorld();
				world.strikeLightningEffect(player.getLocation());
				player.setHealth(0);
			} else if (secondsLeft == 1) {
				notifyPlayer(player, ChatColor.RED + "You have 1 second to live!", Sound.FIREWORK_BLAST);
			} else if (secondsLeft <= 5) {
				notifyPlayer(player, ChatColor.RED + "You have " + secondsLeft + " seconds to live!",
						Sound.FIREWORK_BLAST);
			} else if (secondsLeft == 30) {
				notifyPlayer(player,
						ChatColor.RED + "You have 30 seconds to live! Bring me " + challenge.getName()
								+ " or else!", Sound.FIREWORK_BLAST);
			} else if (secondsLeft == 60) {
				notifyPlayer(player, ChatColor.RED + "You have 1 minute to live!", Sound.FIREWORK_BLAST);
			} else if (secondsLeft % 60 == 0) {
				notifyPlayer(player, ChatColor.RED + "You have " + secondsLeft / 60 + " minutes to live!",
						Sound.FIREWORK_BLAST);
			}
			player.setExp((float) secondsLeft / FIVE_MINUTES);
			player.setLevel(level);
			secondsLeft--;
		}
		
		public int getLevel() {
			return level;
		}
		
		public void nextLevel() {
			secondsLeft = FIVE_MINUTES;
			level++;
			score.setScore(level);
			challenge = challengeManager.getRandomChallenge(level, challenge);
		}
		
		private void notifyPlayer(final Player player, final String message, final Sound sound) {
			player.sendMessage(message);
			player.getWorld().playSound(player.getLocation(), sound, 1, 0);
		}
	}
}
