package de.keksuccino.panoramica.mixin.client;

import de.keksuccino.panoramica.PanoramicaHandler;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {

    @Inject(at = @At("HEAD"), method = "getFOVModifier", cancellable = true)
    private void onGetFOV(ActiveRenderInfo activeRenderInfoIn, float partialTicks, boolean useFOVSetting, CallbackInfoReturnable<Double> info) {
        if (PanoramicaHandler.panoramicMode) {
            info.setReturnValue(90.0D);
        }
    }

    @Inject(at = @At("HEAD"), method = "isDrawBlockOutline", cancellable = true)
    private void onIsDrawBlockOutline(CallbackInfoReturnable<Boolean> info) {
        if (PanoramicaHandler.panoramicMode) {
            info.setReturnValue(false);
        }
    }

}
