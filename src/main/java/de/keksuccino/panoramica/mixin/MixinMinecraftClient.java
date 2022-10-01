package de.keksuccino.panoramica.mixin;

import de.keksuccino.konkrete.Konkrete;
import de.keksuccino.panoramica.events.TickEvent;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinMinecraftClient {

    @Inject(at = @At("HEAD"), method = "tick")
    private void onTick(CallbackInfo info) {

        TickEvent e = new TickEvent();
        Konkrete.getEventHandler().callEventsFor(e);

    }

}
