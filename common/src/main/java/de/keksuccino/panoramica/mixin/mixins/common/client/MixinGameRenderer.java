package de.keksuccino.panoramica.mixin.mixins.common.client;

import de.keksuccino.panoramica.PanoramicaHandler;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {

    @Inject(method = "getFov", at = @At("HEAD"), cancellable = true)
    private void headGetFov_Panoramica(Camera camera, float f, boolean bl, CallbackInfoReturnable<Double> info) {
        if (PanoramicaHandler.panoramaMode) {
            info.setReturnValue(90.0D);
        }
    }

    @Inject(method = "shouldRenderBlockOutline", at = @At("HEAD"), cancellable = true)
    private void headShouldRenderBlockOutline_Panoramica(CallbackInfoReturnable<Boolean> info) {
        if (PanoramicaHandler.panoramaMode) {
            info.setReturnValue(false);
        }
    }

}
