package com.goodformentertainment.minecraft.fmtl;

public class PlayerScore {
	private final String playerName;
	private int level;
	private double minutes;
	
	public PlayerScore(final String playerName, final int level, final double minutes) {
		this.playerName = playerName;
		this.level = level;
		this.minutes = minutes;
	}
	
	public String getPlayerName() {
		return playerName;
	}
	
	public int getLevel() {
		return level;
	}
	
	public double getMinutes() {
		return minutes;
	}
	
	public void update(final int level, final double minutes) {
		this.level = level;
		this.minutes = minutes;
	}
	
	@Override
	public String toString() {
		return playerName + ":" + level + ":" + minutes;
	}
}
