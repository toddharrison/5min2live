package com.goodformentertainment.minecraft.fmtl;

import static com.goodformentertainment.minecraft.util.Log.*;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FmtlCommand implements CommandExecutor {
	private final FmtlPlugin fmtl;
	private final WorldManager worldManager;
	
	public FmtlCommand(final FmtlPlugin fmtl, final WorldManager worldManager) {
		this.fmtl = fmtl;
		this.worldManager = worldManager;
	}
	
	@Override
	public boolean onCommand(final CommandSender sender, final Command command, final String label,
			final String[] attrs) {
		boolean didExecute = false;
		
		if (sender instanceof Player) {
			// Command called from within Minecraft client
			final Player player = (Player) sender;
			
			logInfo("Command called: " + label);
			if (label.equalsIgnoreCase("fmtl") || label.equalsIgnoreCase("5m2l")) {
				if (player.getWorld() == worldManager.getWorld()) {
					// Player is in 5min2live world
					if (attrs.length > 0) {
						if (attrs[0].equalsIgnoreCase("exit")) {
							fmtl.getData().loadPlayerStats(player);
							player.sendMessage(ChatColor.GREEN + "Leaving 5min2live");
							fmtl.getData().loadPlayerStats(player);
							didExecute = true;
						}
					}
				} else {
					// Player is not in 5min2live world
					if (!worldManager.isGeneratingWorld()) {
						// Teleport to 5min2live world
						fmtl.getData().savePlayerStats(player);
						player.sendMessage(ChatColor.GREEN + "Joining 5min2live");
						player.teleport(worldManager.getWorld().getSpawnLocation());
						didExecute = true;
					} else {
						player.sendMessage(ChatColor.GREEN
								+ "5min2live is generating the map, try again in a moment.");
						didExecute = true;
					}
				}
			} else if (label.equalsIgnoreCase("oni")) {
				if (player.getWorld() == worldManager.getWorld()) {
					player.teleport(worldManager.getWorld().getSpawnLocation());
				} else {
					player.sendMessage(ChatColor.GREEN + "You can only return to the ONI in 5min2live!");
				}
			} else {
				logWarn("Unrecognized command called: " + label);
			}
		} else {
			// Command called from console
			logInfo("Called from console");
		}
		
		return didExecute;
	}
}
