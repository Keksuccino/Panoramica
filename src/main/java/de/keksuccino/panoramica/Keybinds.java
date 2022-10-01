package de.keksuccino.panoramica;

import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.ClientRegistry;

public class Keybinds {

	public static KeyMapping keybindCreatePano;
	
	public static void init() {
		
		keybindCreatePano = new KeyMapping("Take Panorama Screenshot", 82, "Panoramica");
		ClientRegistry.registerKeyBinding(keybindCreatePano);

	}
	
}
