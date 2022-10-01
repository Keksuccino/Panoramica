package de.keksuccino.panoramica;

import java.io.File;

import de.keksuccino.konkrete.config.Config;
import net.minecraftforge.fml.common.Mod;

@Mod(modid = "panoramica", acceptedMinecraftVersions="[1.12,1.12.2]", dependencies = "required-after:konkrete@[1.3.2,];required:forge@[14.23.5.2855,]", clientSideOnly = true)
public class Panoramica {

	public static Config config;
	public static final String VERSION = "1.2.0";
	
    public Panoramica() {
    	
    	updateConfig();
    	
    	Keybinds.init();
    	
    	PanoramicaHandler.init();
    	
    }
    
    public static void updateConfig() {
    	try {
    		
    		File f = new File("config/panoramica");
    		if (!f.exists()) {
    			f.mkdirs();
    		}
    		
    		config = new Config("config/panoramica/config.txt");
        	
        	config.registerValue("panoramaresolution", 512, "general");
        	config.registerValue("screenshotdelay", 10, "general");

        	config.syncConfig();
        	
        	config.clearUnusedValues();
        	
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }

}
