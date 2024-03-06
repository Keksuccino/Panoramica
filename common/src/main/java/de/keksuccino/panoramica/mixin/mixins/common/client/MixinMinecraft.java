package de.keksuccino.panoramica.mixin.mixins.common.client;

import de.keksuccino.panoramica.PanoramicaHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Timer;
import net.minecraft.client.server.IntegratedServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import javax.annotation.Nullable;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft {

    @Shadow private volatile boolean pause;
    @Shadow private float pausePartialTick;
    @Shadow @Final private Timer timer;
    @Shadow @Nullable private IntegratedServer singleplayerServer;

    @Unique private static final Logger LOGGER_PANORAMICA = LogManager.getLogger();

    @Inject(method = "runTick", at = @At("HEAD"))
    private void headRunTick_Panoramica(CallbackInfo info) {
        try {
            PanoramicaHandler.INSTANCE.onClientTick();
        } catch (Exception ex) {
            LOGGER_PANORAMICA.error("[PANORAMICA] Client tick failed!", ex);
        }
    }

    @Inject(method = "runTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/DebugScreenOverlay;logFrameDuration(J)V"))
    private void afterSetPause_Panoramica(boolean $$0, CallbackInfo info) {
        //Force-pause game when taking panorama screenshot (will automatically get reset by runTick() when panoramicMode is false again)
        boolean isPausePossible = this.hasSingleplayerServer() && (this.singleplayerServer != null) && !this.singleplayerServer.isPublished();
        if (isPausePossible && PanoramicaHandler.panoramaMode && !this.pause) {
            this.pause = true;
            this.pausePartialTick = this.timer.partialTick;
        }
    }

    @Shadow public abstract boolean hasSingleplayerServer();

}
