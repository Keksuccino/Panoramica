package de.keksuccino.panoramica;

import java.io.File;

import de.keksuccino.konkrete.config.Config;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod("panoramica")
public class Panoramica {

	public static Config config;
	public static final String VERSION = "1.2.1";
	
    public Panoramica() {

		if (FMLEnvironment.dist == Dist.CLIENT) {

			updateConfig();

			Keybinds.init();

			if (config.getOrDefault("use_new_system", false)) {
				PanoramicaHandler.init();
			} else {
				LegacyPanoramicaHandler.init();
			}

		}
    	
    }
    
    public static void updateConfig() {
    	try {
    		
    		File f = new File("config/panoramica");
    		if (!f.exists()) {
    			f.mkdirs();
    		}
    		
    		config = new Config("config/panoramica/config.txt");
        	
        	config.registerValue("panoramaresolution", 512, "general");
			config.registerValue("use_new_system", false, "general", "The new system takes panorama screenshots without rotating the player, but is not compatible with mods like OptiFine and Iris.");

			config.syncConfig();
        	
        	config.clearUnusedValues();
        	
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }

}
