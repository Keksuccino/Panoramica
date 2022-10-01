package de.keksuccino.panoramica.mixin.client;

import de.keksuccino.panoramica.PanoramicaHandler;
import net.minecraft.client.renderer.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class)
public class MixinEntityRenderer {

    @Inject(at = @At("HEAD"), method = "getFOVModifier", cancellable = true)
    private void onGetFOV(float partialTicks, boolean useFOVSetting, CallbackInfoReturnable<Float> info) {
        if (PanoramicaHandler.panoramicMode) {
            info.setReturnValue(90.0F);
        }
    }

    @Inject(at = @At("HEAD"), method = "isDrawBlockOutline", cancellable = true)
    private void onIsDrawBlockOutline(CallbackInfoReturnable<Boolean> info) {
        if (PanoramicaHandler.panoramicMode) {
            info.setReturnValue(false);
        }
    }

    @Inject(at = @At("HEAD"), method = "renderHand", cancellable = true)
    private void onRenderHand(float partialTicks, int pass, CallbackInfo info) {
        if (PanoramicaHandler.panoramicMode) {
            info.cancel();
        }
    }

}
