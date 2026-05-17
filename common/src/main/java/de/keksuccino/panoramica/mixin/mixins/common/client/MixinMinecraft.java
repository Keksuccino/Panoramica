package de.keksuccino.panoramica.mixin.mixins.common.client;

import de.keksuccino.panoramica.PanoramicaHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.server.IntegratedServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
    @Shadow @Nullable private IntegratedServer singleplayerServer;

    @Unique private static final Logger LOGGER_PANORAMICA = LogManager.getLogger();

    @Inject(method = "tick", at = @At("HEAD"))
    private void headRunTick_Panoramica(CallbackInfo info) {
        this.forcePauseForPanorama();
        try {
            PanoramicaHandler.INSTANCE.onClientTick();
        } catch (Exception ex) {
            LOGGER_PANORAMICA.error("[PANORAMICA] Client tick failed!", ex);
        }
        // Ensure the activation tick is paused too right after panoramaMode flips to true.
        this.forcePauseForPanorama();
    }

    @Unique
    private void forcePauseForPanorama() {
        boolean isPausePossible = this.hasSingleplayerServer() && (this.singleplayerServer != null) && !this.singleplayerServer.isPublished();
        if (isPausePossible && PanoramicaHandler.panoramaMode && !this.pause) {
            this.pause = true;
        }
    }

    @Shadow public abstract boolean hasSingleplayerServer();

}
