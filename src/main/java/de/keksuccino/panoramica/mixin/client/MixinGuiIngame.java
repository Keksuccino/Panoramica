package de.keksuccino.panoramica.mixin.client;

import de.keksuccino.panoramica.PanoramicaHandler;
import net.minecraft.client.gui.GuiIngame;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiIngame.class)
public class MixinGuiIngame {

    @Inject(at = @At("HEAD"), method = "renderGameOverlay", cancellable = true)
    private void onRenderGameOverlay(float partialTicks, CallbackInfo info) {
        if (PanoramicaHandler.panoramicMode) {
            info.cancel();
        }
    }

}
