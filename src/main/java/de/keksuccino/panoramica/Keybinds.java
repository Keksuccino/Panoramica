package de.keksuccino.panoramica;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;

public class Keybinds {

	public static KeyMapping keybindCreatePano;

	public static void init() {

		keybindCreatePano = new KeyMapping("Take Panorama Screenshot", 82, "Panoramica");
		KeyBindingHelper.registerKeyBinding(keybindCreatePano);

	}

}
