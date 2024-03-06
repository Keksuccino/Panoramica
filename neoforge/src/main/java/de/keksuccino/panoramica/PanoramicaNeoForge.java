package de.keksuccino.panoramica;

import de.keksuccino.panoramica.platform.Services;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.apache.logging.log4j.LogManager;

@Mod(Panoramica.MOD_ID)
public class PanoramicaNeoForge {
    
    public PanoramicaNeoForge(IEventBus eventBus) {

        Panoramica.init();

        if (Services.PLATFORM.isOnClient()) {
            eventBus.addListener(PanoramicaNeoForge::registerKeybinds);
        }

    }

    @SubscribeEvent
    public static void registerKeybinds(RegisterKeyMappingsEvent e) {
        LogManager.getLogger().info("############### NEOFORGE KEYBINDS REGISTERED!");
        e.register(Keybinds.TAKE_PANORAMA_SCREENSHOT);
    }

}