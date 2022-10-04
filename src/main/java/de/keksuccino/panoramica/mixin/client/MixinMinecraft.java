package de.keksuccino.panoramica.mixin.client;

import de.keksuccino.konkrete.Konkrete;
import de.keksuccino.panoramica.PanoramicaHandler;
import de.keksuccino.panoramica.events.TickEvent;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public class MixinMinecraft {

    @Inject(at = @At("HEAD"), method = "tick")
    private void onTick(CallbackInfo info) {

        TickEvent e = new TickEvent();
        Konkrete.getEventHandler().callEventsFor(e);

    }

    @Inject(at = @At("HEAD"), method = "useShaderTransparency", cancellable = true)
    private static void onIsFabulousGraphicsEnabled(CallbackInfoReturnable<Boolean> info) {
        if (PanoramicaHandler.panoramicMode) {
            info.setReturnValue(false);
        }
    }

}
