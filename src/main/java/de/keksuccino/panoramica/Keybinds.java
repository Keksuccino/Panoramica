package de.keksuccino.panoramica;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class Keybinds {

	public static KeyBinding keybindCreatePano;
	
	public static void init() {
		
		keybindCreatePano = new KeyBinding("Take Panorama Screenshot", 19, "Panoramica");
		ClientRegistry.registerKeyBinding(keybindCreatePano);

	}
	
}
