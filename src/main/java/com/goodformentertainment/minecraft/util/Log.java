package com.goodformentertainment.minecraft.util;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class Log {
	private static Logger log;
	
	public static void setLogger(final Logger log) {
		Log.log = log;
	}
	
	public static void logDebug(final Object msg) {
		if (log != null && msg != null) {
			log.fine(msg.toString());
		}
	}
	
	public static void logInfo(final Object msg) {
		if (log != null && msg != null) {
			log.info(msg.toString());
		}
	}
	
	public static void logWarn(final Object msg) {
		if (log != null && msg != null) {
			log.warning(msg.toString());
		}
	}
	
	public static void logSevere(final Object msg, final Throwable t) {
		if (log != null && msg != null) {
			log.log(Level.SEVERE, msg.toString(), t);
		}
	}
}
