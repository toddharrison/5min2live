package com.goodformentertainment.minecraft.util;

import java.util.Random;

import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;

public class EmptyChunkGenerator extends ChunkGenerator {
	@Override
	public byte[] generate(final World world, final Random random, final int cx, final int cz) {
		final byte[] result = new byte[32768];
		
		return result;
	}
}
