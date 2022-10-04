package de.keksuccino.panoramica.mixin.client;

import de.keksuccino.panoramica.PanoramicaHandler;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {

    @Inject(at = @At("HEAD"), method = "getFov", cancellable = true)
    private void onGetFOV(Camera camera, float f, boolean bl, CallbackInfoReturnable<Double> info) {
        if (PanoramicaHandler.panoramicMode) {
            info.setReturnValue(90.0D);
        }
    }

    @Inject(at = @At("HEAD"), method = "shouldRenderBlockOutline", cancellable = true)
    private void onIsDrawBlockOutline(CallbackInfoReturnable<Boolean> info) {
        if (PanoramicaHandler.panoramicMode) {
            info.setReturnValue(false);
        }
    }

}
