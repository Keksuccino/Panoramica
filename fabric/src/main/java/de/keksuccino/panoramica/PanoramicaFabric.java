package de.keksuccino.panoramica;

import de.keksuccino.panoramica.platform.Services;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;

public class PanoramicaFabric implements ModInitializer {
    
    @Override
    public void onInitialize() {

        Panoramica.init();

        if (Services.PLATFORM.isOnClient()) {
            this.registerKeybinds();
        }

    }

    protected void registerKeybinds() {
        KeyBindingHelper.registerKeyBinding(Keybinds.TAKE_PANORAMA_SCREENSHOT);
    }

}
