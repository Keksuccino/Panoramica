package de.keksuccino.panoramica;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import net.minecraft.client.gui.screen.IngameMenuScreen;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import org.lwjgl.glfw.GLFW;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.util.ScreenShotHelper;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class PanoramicaHandler {

	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");

	public static boolean panoramicMode = false;

	private List<NativeImage> screenshots = new ArrayList<>();
	private int tick = 1;
	private int prevTick = 1;
	private boolean active = false;
	private int shotsTaken = 0;
	private boolean prepareScreenshot = false;
	private int delay = 10;

	private int width = 0;
	private int height = 0;

//	private boolean cachedDaylightCycle;
	private boolean cachedGuiVisibility;

	private File saveDirectory;

	private Minecraft mc = Minecraft.getInstance();

	public static void init() {

		MinecraftForge.EVENT_BUS.register(new PanoramicaHandler());

	}

	@SubscribeEvent
	public void onTick(ClientTickEvent e) {

		try {

			if (Keybinds.keybindCreatePano.isPressed()) {

				if (!mc.getMainWindow().isFullscreen()) {
					if (!this.active) {
						this.active = true;
						this.tick = 0;
						this.prevTick = 0;
						this.prepareScreenshot = false;
						this.shotsTaken = 0;
						panoramicMode = true;
						this.delay = Panoramica.config.getOrDefault("screenshotdelay", 10);
						this.width = 0;
						this.height = 0;
						this.screenshots.clear();
						this.saveDirectory = getUnusedPanoramaFolderName();
						if (!this.saveDirectory.exists()) {
							this.saveDirectory.mkdirs();
						}
						this.cachedGuiVisibility = mc.gameSettings.hideGUI;
						mc.gameSettings.hideGUI = true;
						this.mc.displayGuiScreen(new DummyScreen());
					}
				} else {
					Minecraft.getInstance().player.sendMessage(new StringTextComponent("§cPlease disable fullscreen mode before creating a panorama!"), null);
				}

			}

			if (this.active) {

				mc.mouseHelper.ungrabMouse();

				if (this.tick == 0) {
					this.tick++;
					this.width = mc.getMainWindow().getWidth();
					this.height = mc.getMainWindow().getHeight();
					int res = Panoramica.config.getOrDefault("panoramaresolution", 512);
					GLFW.glfwSetWindowSizeLimits(Minecraft.getInstance().getMainWindow().getHandle(), 100, 100, 20000, 20000);
					GLFW.glfwSetWindowSize(mc.getMainWindow().getHandle(), res, res);
					Minecraft.getInstance().updateWindowSize();
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
					if (this.mc.currentScreen instanceof DummyScreen) {
						this.mc.displayGuiScreen(null);
					}

					this.finishPanorama();

					GLFW.glfwSetWindowSize(mc.getMainWindow().getHandle(), this.width, this.height);
					Minecraft.getInstance().updateWindowSize();

					mc.gameSettings.hideGUI = this.cachedGuiVisibility;

					mc.mouseHelper.grabMouse();

				}

			}

		} catch (Exception ex) {
			ex.printStackTrace();
			this.active = false;
			if (this.mc.currentScreen instanceof DummyScreen) {
				this.mc.displayGuiScreen(null);
			}
		}

		if (!this.active) {
			panoramicMode = false;
		}

	}

	private void setCameraRotation() {

		if (this.shotsTaken == 0) {
			mc.player.setPositionAndRotation(mc.player.getPosX(), mc.player.getPosY(), mc.player.getPosZ(), -180, 0);
		}

		if (this.shotsTaken == 1) {
			mc.player.setPositionAndRotation(mc.player.getPosX(), mc.player.getPosY(), mc.player.getPosZ(), -90, 0);
		}

		if (this.shotsTaken == 2) {
			mc.player.setPositionAndRotation(mc.player.getPosX(), mc.player.getPosY(), mc.player.getPosZ(), 0, 0);
		}

		if (this.shotsTaken == 3) {
			mc.player.setPositionAndRotation(mc.player.getPosX(), mc.player.getPosY(), mc.player.getPosZ(), 90, 0);
		}

		if (this.shotsTaken == 4) {
			mc.player.setPositionAndRotation(mc.player.getPosX(), mc.player.getPosY(), mc.player.getPosZ(), -180, -90);
		}

		if (this.shotsTaken == 5) {
			mc.player.setPositionAndRotation(mc.player.getPosX(), mc.player.getPosY(), mc.player.getPosZ(), -180, 90);
		}

	}

	private void takeScreenshot() {
		this.screenshots.add(ScreenShotHelper.createScreenshot(mc.getMainWindow().getFramebufferWidth(), mc.getMainWindow().getFramebufferHeight(), mc.getFramebuffer()));
	}

	private boolean finishPanorama() {
		try {
			if (this.screenshots.size() == 6) {

				int i = 0;
				for (NativeImage ni : this.screenshots) {
					try {
						ni.write(new File(this.saveDirectory.getPath() + "/panorama_" + i + ".png"));
					} catch (IOException e) {
						e.printStackTrace();
					}
					i++;
				}

				ITextComponent compSaveDir = (new StringTextComponent(this.saveDirectory.getName())).mergeStyle(TextFormatting.UNDERLINE).modifyStyle((style) -> {
					return style.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, this.saveDirectory.getAbsolutePath()));
				});
				TranslationTextComponent compSuccessMsg = new TranslationTextComponent("screenshot.success", compSaveDir);
				Minecraft.getInstance().player.sendMessage(compSuccessMsg, null);

				return true;

			} else {
				Minecraft.getInstance().player.sendMessage(new StringTextComponent("§cError! Unable to save panorama!"), null);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private static File getUnusedPanoramaFolderName() {
		File saveTo = new File(Minecraft.getInstance().gameDir.getPath() + "/screenshots");
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

