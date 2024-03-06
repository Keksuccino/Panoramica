package de.keksuccino.panoramica;

import de.keksuccino.konkrete.config.Config;
import de.keksuccino.panoramica.platform.Services;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import java.io.File;

public class Panoramica {

	private static final Logger LOGGER = LogManager.getLogger();

	public static final String VERSION = "1.3.0";
	public static final String MOD_LOADER = Services.PLATFORM.getPlatformName();
	public static final String MOD_ID = "panoramica";

	private static Config config;

	public static void init() {

		if (Services.PLATFORM.isOnClient()) {
			LOGGER.info("[PANORAMICA] Loading v" + VERSION + " on " + MOD_LOADER.toUpperCase() + "...");
		} else {
			LOGGER.info("[PANORAMICA] Disabling Panoramica since it's loaded server-side.");
		}

	}

	@NotNull
	public static Config getConfig() {
		if (config == null) updateConfig();
		return config;
	}

	public static void updateConfig() {

		try {

			File f = new File("config/panoramica");
			if (!f.exists()) {
				f.mkdirs();
			}

			config = new Config("config/panoramica/config.txt");

			config.registerValue("panorama_resolution", 512, "general");
			config.registerValue("screenshot_delay", 10, "general");

			config.syncConfig();

			config.clearUnusedValues();

		} catch (Exception ex) {
			LOGGER.error("[PANORAMICA] Failed to update config!", ex);
		}

	}

}
