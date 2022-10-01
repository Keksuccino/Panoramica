package de.keksuccino.panoramica;

import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class Keybinds {

	public static KeyMapping keybindCreatePano;

	public static boolean initialized = false;
	
	public static void init() {

		FMLJavaModLoadingContext.get().getModEventBus().register(Keybinds.class);

	}

	@SubscribeEvent
	public static void onRegisterKeyBinds(RegisterKeyMappingsEvent e) {

		if (!initialized) {
			keybindCreatePano = new KeyMapping("Take Panorama Screenshot", 82, "Panoramica");
			initialized = true;
		}

		e.register(keybindCreatePano);

	}
	
}
