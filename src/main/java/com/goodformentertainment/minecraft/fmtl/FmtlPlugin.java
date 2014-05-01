package com.goodformentertainment.minecraft.fmtl;

import static com.goodformentertainment.minecraft.util.Log.*;

import java.io.File;

import org.bukkit.plugin.java.JavaPlugin;

import com.goodformentertainment.minecraft.fmtl.event.FmtlData;
import com.goodformentertainment.minecraft.fmtl.event.FmtlEventNotifier;
import com.goodformentertainment.minecraft.util.NoRain;

// 20 ticks == 1 second

public class FmtlPlugin extends JavaPlugin {
	private FmtlEventNotifier eventNotifier;
	private WorldManager worldManager;
	private PlayerManager playerManager;
	private OniManager oniManager;
	private ChallengeManager challengeManager;
	
	private FmtlData data = null;
	
	@Override
	public void onEnable() {
		setLogger(getLogger());
		
		saveDefaultConfig();
		getData();
		
		eventNotifier = new FmtlEventNotifier();
		challengeManager = new ChallengeManager(this);
		worldManager = new WorldManager(this, eventNotifier);
		playerManager = new PlayerManager(this, worldManager, challengeManager);
		oniManager = new OniManager(this, worldManager, playerManager);
		
		eventNotifier.registerEvents(oniManager);
		
		getServer().getPluginManager().registerEvents(worldManager, this);
		getServer().getPluginManager().registerEvents(playerManager, this);
		getServer().getPluginManager().registerEvents(oniManager, this);
		getServer().getPluginManager().registerEvents(new NoRain(), this);
		
		final FmtlCommand command = new FmtlCommand(this, worldManager);
		getCommand("5m2l").setExecutor(command);
		getCommand("oni").setExecutor(command);
		
		worldManager.getWorld();
		
		logInfo("Enabled");
	}
	
	@Override
	public void onDisable() {
		saveConfig();
		data.save();
		
		logInfo("Disabled");
	}
	
	protected FmtlData getData() {
		if (data == null) {
			data = new FmtlData(new File(getDataFolder(), "data.yml"));
		}
		return data;
	}
}
