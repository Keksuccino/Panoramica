package de.keksuccino.panoramica;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Screenshot;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;
import net.minecraft.client.Minecraft;

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
					Minecraft.getInstance().player.sendSystemMessage(Component.translatable("panoramica.error.fullscreen").withStyle(ChatFormatting.RED));
				}

			}

			if (this.active) {

				mc.mouseHandler.releaseMouse();

				if (this.tick == 0) {
					this.tick++;
					this.width = mc.getWindow().getWidth();
					this.height = mc.getWindow().getHeight();
					int res = Panoramica.getConfig().getOrDefault("panorama_resolution", 512);
					GLFW.glfwSetWindowSizeLimits(Minecraft.getInstance().getWindow().getWindow(), 100, 100, 20000, 20000);
					GLFW.glfwSetWindowSize(mc.getWindow().getWindow(), res, res);
					Minecraft.getInstance().resizeDisplay();
				} else {
					if (this.tick >= this.prevTick + this.delay) {
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

					this.tick = 0;
					this.active = false;

					this.finishPanorama();

					GLFW.glfwSetWindowSize(mc.getWindow().getWindow(), this.width, this.height);
					Minecraft.getInstance().resizeDisplay();

					mc.options.hideGui = this.cachedGuiVisibility;

					mc.mouseHandler.grabMouse();

				}

			}

		} catch (Exception ex) {
			ex.printStackTrace();
			this.active = false;
		}

		if (!this.active) {
			panoramaMode = false;
		}

	}

	private void setCameraRotation() {

		if (this.shotsTaken == 0) {
			mc.player.moveTo(mc.player.getX(), mc.player.getY(), mc.player.getZ(), -180, 0);
		}

		if (this.shotsTaken == 1) {
			mc.player.moveTo(mc.player.getX(), mc.player.getY(), mc.player.getZ(), -90, 0);
		}

		if (this.shotsTaken == 2) {
			mc.player.moveTo(mc.player.getX(), mc.player.getY(), mc.player.getZ(), 0, 0);
		}

		if (this.shotsTaken == 3) {
			mc.player.moveTo(mc.player.getX(), mc.player.getY(), mc.player.getZ(), 90, 0);
		}

		if (this.shotsTaken == 4) {
			mc.player.moveTo(mc.player.getX(), mc.player.getY(), mc.player.getZ(), -180, -90);
		}

		if (this.shotsTaken == 5) {
			mc.player.moveTo(mc.player.getX(), mc.player.getY(), mc.player.getZ(), -180, 90);
		}

	}

	private void takeScreenshot() {
		this.screenshots.add(Screenshot.takeScreenshot(mc.getMainRenderTarget()));
	}

	private boolean finishPanorama() {
		try {
			if (this.screenshots.size() == 6) {

				int i = 0;
				for (NativeImage ni : this.screenshots) {
					try {
						ni.writeToFile(new File(this.saveDirectory.getPath() + "/panorama_" + i + ".png"));
					} catch (IOException e) {
						e.printStackTrace();
					}
					i++;
				}

				Component compSaveDir = Component.literal(this.saveDirectory.getName()).withStyle(ChatFormatting.UNDERLINE).withStyle((style) -> {
					return style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, this.saveDirectory.getAbsolutePath()));
				});
				Component compSuccessMsg = Component.translatable("screenshot.success", compSaveDir);
				Minecraft.getInstance().player.sendSystemMessage(compSuccessMsg);

				return true;

			} else {
				Minecraft.getInstance().player.sendSystemMessage(Component.translatable("panoramica.error.screenshot_failed").withStyle(ChatFormatting.RED));
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

