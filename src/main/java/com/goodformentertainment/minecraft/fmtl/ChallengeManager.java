package com.goodformentertainment.minecraft.fmtl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.inventory.ItemStack;

import com.goodformentertainment.util.RandomSet;

public class ChallengeManager {
	private final FmtlPlugin fmtl;
	private final List<Challenge> challenges;
	
	public ChallengeManager(final FmtlPlugin fmtl) {
		this.fmtl = fmtl;
		challenges = new ArrayList<Challenge>();
		loadConfiguration();
	}
	
	public Challenge getRandomChallenge(final int level) {
		final RandomSet<Challenge> set = new RandomSet<Challenge>();
		for (final Challenge challenge : challenges) {
			if (challenge.isInRange(level)) {
				set.add(challenge);
			}
		}
		return set.getRandom();
	}
	
	public Challenge getRandomChallenge(final int level, final Challenge excludeChallenge) {
		final RandomSet<Challenge> set = new RandomSet<Challenge>();
		for (final Challenge challenge : challenges) {
			if (challenge.isInRange(level) && challenge != excludeChallenge) {
				set.add(challenge);
			}
		}
		return set.getRandom();
	}
	
	private void loadConfiguration() {
		for (final Object o : fmtl.getConfig().getList("challengeSets")) {
			final Map<?, ?> m1 = (Map<?, ?>) o;
			final Integer minLevel = (Integer) m1.get("minLevel");
			final Integer maxLevel = (Integer) m1.get("maxLevel");
			for (final Object o2 : (List<?>) m1.get("challenges")) {
				final Map<?, ?> m2 = (Map<?, ?>) o2;
				final String name = (String) m2.get("name");
				final Map<?, ?> item = (Map<?, ?>) m2.get("item");
				@SuppressWarnings("unchecked")
				final ItemStack stack = ItemStack.deserialize((Map<String, Object>) item);
				challenges.add(new Challenge(name, stack, minLevel, maxLevel));
			}
		}
	}
}
