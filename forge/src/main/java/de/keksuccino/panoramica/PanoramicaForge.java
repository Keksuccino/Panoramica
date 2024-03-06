package de.keksuccino.panoramica;

import de.keksuccino.panoramica.platform.Services;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Panoramica.MOD_ID)
public class PanoramicaForge {
    
    public PanoramicaForge() {

        Panoramica.init();

        if (Services.PLATFORM.isOnClient()) {
            FMLJavaModLoadingContext.get().getModEventBus().register(PanoramicaForge.class);
        }
        
    }

    @SubscribeEvent
    public static void registerKeybinds(RegisterKeyMappingsEvent e) {
        e.register(Keybinds.TAKE_PANORAMA_SCREENSHOT);
    }

}