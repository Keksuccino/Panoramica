package de.keksuccino.panoramica;

import java.io.File;

import de.keksuccino.konkrete.config.Config;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod("panoramica")
public class Panoramica {

	public static Config config;
	public static final String VERSION = "1.2.0";
	
    public Panoramica() {

		if (FMLEnvironment.dist == Dist.CLIENT) {

			updateConfig();

			Keybinds.init();

			PanoramicaHandler.init();

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

        	config.syncConfig();
        	
        	config.clearUnusedValues();
        	
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }

}
