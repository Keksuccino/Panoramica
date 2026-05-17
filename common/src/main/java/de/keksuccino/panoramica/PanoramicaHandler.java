package de.keksuccino.panoramica;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Screenshot;
import net.minecraft.network.chat.Component;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

@SuppressWarnings("all")
public class PanoramicaHandler {

	private static final Logger LOGGER = LogManager.getLogger();
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");
	public static final PanoramicaHandler INSTANCE = new PanoramicaHandler();

	public static boolean panoramaMode = false;

	private final List<NativeImage> screenshots = new ArrayList<>();
	private int tick = 1;
	private int prevTick = 1;
	private boolean active = false;
	private int shotsTaken = 0;
	private boolean prepareScreenshot = false;
	private int delay = 10;
	private int width = 0;
	private int height = 0;
	private boolean cachedGuiVisibility;
	private File saveDirectory;
	private final Minecraft mc = Minecraft.getInstance();

	public void onClientTick() {

		try {

			if (Keybinds.TAKE_PANORAMA_SCREENSHOT.isDown()) {

				if (!mc.getWindow().isFullscreen()) {
					if (!this.active) {
						this.active = true;
						this.tick = 0;
						this.prevTick = 0;
						this.prepareScreenshot = false;
						this.shotsTaken = 0;
						panoramaMode = true;
						this.delay = Panoramica.getConfig().getOrDefault("screenshot_delay", 10);
						this.width = 0;
						this.height = 0;
						this.screenshots.clear();
						this.saveDirectory = getUnusedPanoramaFolderName();
						if (!this.saveDirectory.exists()) {
							this.saveDirectory.mkdirs();
						}
						this.cachedGuiVisibility = mc.options.hideGui;
						mc.options.hideGui = true;
					}
				} else {
					if (mc.player != null) mc.player.sendSystemMessage(Component.translatable("panoramica.error.fullscreen").withStyle(ChatFormatting.RED));
				}

			}

			if (this.active) {

				mc.mouseHandler.releaseMouse();
				// Enable MC's built-in panoramic camera mode (provides 90° FOV) and hide block outline
				mc.gameRenderer.getMainCamera().enablePanoramicMode();
				mc.gameRenderer.setRenderBlockOutline(false);

				if (this.tick == 0) {
					this.tick++;
					this.width = mc.getWindow().getWidth();
					this.height = mc.getWindow().getHeight();
					int res = Panoramica.getConfig().getOrDefault("panorama_resolution", 512);
					GLFW.glfwSetWindowSizeLimits(mc.getWindow().handle(), 100, 100, 20000, 20000);
					GLFW.glfwSetWindowSize(mc.getWindow().handle(), res, res);
					mc.getWindow().setGuiScale(mc.getWindow().calculateScale(mc.options.guiScale().get(), mc.isEnforceUnicode()));
				} else {
					if (this.shotsTaken < 6 && this.tick >= this.prevTick + this.delay) {
						if (!this.prepareScreenshot) {
							this.setCameraRotation();
							this.prevTick = this.tick;
							this.prepareScreenshot = true;
						} else {
							this.takeScreenshot();
							this.shotsTaken++;
							this.prevTick = this.tick;
							this.prepareScreenshot = false;
						}
					}
					this.tick++;
				}

				if (this.shotsTaken >= 6) {
					if (this.screenshots.size() >= 6) {
						this.tick = 0;
						this.active = false;

						this.finishPanorama();

						mc.gameRenderer.getMainCamera().disablePanoramicMode();
						mc.gameRenderer.setRenderBlockOutline(true);

						GLFW.glfwSetWindowSize(mc.getWindow().handle(), this.width, this.height);
					mc.getWindow().setGuiScale(mc.getWindow().calculateScale(mc.options.guiScale().get(), mc.isEnforceUnicode()));

						mc.options.hideGui = this.cachedGuiVisibility;

						mc.mouseHandler.grabMouse();
					} else {
					}
				}

			}

		} catch (Exception ex) {
			ex.printStackTrace();
			this.active = false;
			try {
				mc.gameRenderer.getMainCamera().disablePanoramicMode();
				mc.gameRenderer.setRenderBlockOutline(true);
			} catch (Exception ignored) {}
		}

		if (!this.active) {
			panoramaMode = false;
		}

	}

	private void setCameraRotation() {
		if (mc.player == null) {
			return;
		}

		if (this.shotsTaken == 0) {
			this.applyCameraRotation(-180f, 0f);
		}

		if (this.shotsTaken == 1) {
			this.applyCameraRotation(-90f, 0f);
		}

		if (this.shotsTaken == 2) {
			this.applyCameraRotation(0f, 0f);
		}

		if (this.shotsTaken == 3) {
			this.applyCameraRotation(90f, 0f);
		}

		if (this.shotsTaken == 4) {
			this.applyCameraRotation(-180f, -90f);
		}

		if (this.shotsTaken == 5) {
			this.applyCameraRotation(-180f, 90f);
		}

	}

	private void applyCameraRotation(float yaw, float pitch) {
		mc.player.setYRot(yaw);
		mc.player.setYHeadRot(yaw);
		mc.player.setYBodyRot(yaw);
		mc.player.setXRot(pitch);
	}

	private void takeScreenshot() {
		Screenshot.takeScreenshot(mc.getMainRenderTarget(), img -> {
			this.screenshots.add(img);
		});
	}

	private boolean finishPanorama() {
		try {
			if (this.screenshots.size() == 6) {

				int i = 0;
				for (NativeImage ni : this.screenshots) {
					try {
						File output = new File(this.saveDirectory.getPath() + "/panorama_" + i + ".png");
						ni.writeToFile(output);
					} catch (IOException e) {
						e.printStackTrace();
					}
					i++;
				}

				Component compSaveDir = Component.literal(this.saveDirectory.getName()).withStyle(ChatFormatting.UNDERLINE);
				Component compSuccessMsg = Component.translatable("screenshot.success", compSaveDir);
				if (mc.player != null) mc.player.sendSystemMessage(compSuccessMsg);

				return true;

			} else {
				if (mc.player != null) mc.player.sendSystemMessage(Component.translatable("panoramica.error.screenshot_failed").withStyle(ChatFormatting.RED));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private static File getUnusedPanoramaFolderName() {
		File saveTo = new File(Minecraft.getInstance().gameDirectory.getPath() + "/screenshots");
		try {
			return getFolder(saveTo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new File(saveTo.getPath() + "/panorama_" + System.currentTimeMillis());
	}

	private static File getFolder(File file) {
		String string = DATE_FORMAT.format(new Date());
		int i = 1;
		File file2;
		while ((file2 = new File(file, "panorama_" + string + (i == 1 ? "" : "_" + i))).exists()) {
			++i;
		}
		return file2;
	}

}

