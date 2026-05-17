package de.keksuccino.panoramica.mixin.mixins.common.client;

import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;

// In MC 26.x, getFov() and shouldRenderBlockOutline() were removed from GameRenderer.
// FOV is now controlled via Camera.enablePanoramicMode() and block outline via
// GameRenderer.setRenderBlockOutline(boolean), handled directly in PanoramicaHandler.
@Mixin(GameRenderer.class)
public class MixinGameRenderer {

}
