package de.keksuccino.panoramica;

import java.awt.image.BufferedImage;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.keksuccino.konkrete.gui.screens.popup.PopupHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ScreenShotHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import javax.imageio.ImageIO;

public class PanoramicaHandler {

	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");

	public static boolean panoramicMode = false;

	private List<BufferedImage> screenshots = new ArrayList<>();
	private int tick = 1;
	private int lastTick = 1;
	private boolean active = false;
	private boolean popupActive = false;
	private int shotsTaken = 0;
	private boolean prepareScreenshot = false;
	private int delay = 10;
	
	private int width = 0;
	private int height = 0;
	
	private File saveDirectory;
	
	private Minecraft mc = Minecraft.getMinecraft();
	
	public static void init() {
		
		MinecraftForge.EVENT_BUS.register(new PanoramicaHandler());
		
	}
	
	@SubscribeEvent
	public void onTick(TickEvent.ClientTickEvent e) {

		try {
			if (this.popupActive && (mc.currentScreen == null) && (PopupHandler.getCurrentPopup() != null)) {
				PopupHandler.getCurrentPopup().setDisplayed(false);
			}
			
			if (Keybinds.keybindCreatePano.isPressed()) {
				
				if (!mc.isFullScreen()) {
					if (!this.active) {
						this.active = true;
						this.tick = 0;
						this.lastTick = 0;
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
						this.mc.displayGuiScreen(new DummyScreen());
					}
				} else {
					mc.player.sendMessage(new TextComponentString("§cPlease disable fullscreen mode before creating a panorama!"));
				}

			}

			if (this.active) {
				
				mc.mouseHelper.ungrabMouseCursor();
				
				if (this.tick == 0) {
					this.tick++;
					this.width = mc.displayWidth;
					this.height = mc.displayHeight;
					int res = Panoramica.config.getOrDefault("panoramaresolution", 512);
					mc.resize(res, res);
				} else {
					if (this.tick >= this.lastTick + this.delay) {
						if (!this.prepareScreenshot) {
							this.setCameraRotation();
							this.lastTick = this.tick;
							this.prepareScreenshot = true;
						} else {
							this.takeScreenshot();
							this.shotsTaken++;
							this.lastTick = this.tick;
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

					mc.resize(this.width, this.height);
					
					mc.mouseHelper.grabMouseCursor();

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
			mc.player.setPositionAndRotation(mc.player.posX, mc.player.posY, mc.player.posZ, -180, 0);
		}
		
		if (this.shotsTaken == 1) {
			mc.player.setPositionAndRotation(mc.player.posX, mc.player.posY, mc.player.posZ, -90, 0);
		}
		
		if (this.shotsTaken == 2) {
			mc.player.setPositionAndRotation(mc.player.posX, mc.player.posY, mc.player.posZ, 0, 0);
		}
		
		if (this.shotsTaken == 3) {
			mc.player.setPositionAndRotation(mc.player.posX, mc.player.posY, mc.player.posZ, 90, 0);
		}
		
		if (this.shotsTaken == 4) {
			mc.player.setPositionAndRotation(mc.player.posX, mc.player.posY, mc.player.posZ, -180, -90);
		}
		
		if (this.shotsTaken == 5) {
			mc.player.setPositionAndRotation(mc.player.posX, mc.player.posY, mc.player.posZ, -180, 90);
		}
		
	}
	
	private void takeScreenshot() {
		this.screenshots.add(ScreenShotHelper.createScreenshot(mc.displayWidth, mc.displayHeight, mc.getFramebuffer()));
	}
	
	private boolean finishPanorama() {
		try {
			if (this.screenshots.size() == 6) {
				
				int i = 0;
				for (BufferedImage ni : this.screenshots) {
					writeScreenshot(ni, new File(this.saveDirectory.getPath() + "/panorama_" + i + ".png"));
					i++;
				}

				ITextComponent compSavePath = new TextComponentString(this.saveDirectory.getCanonicalFile().getName());
				compSavePath.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, this.saveDirectory.getCanonicalFile().getAbsolutePath()));
				compSavePath.getStyle().setUnderlined(Boolean.valueOf(true));
				ITextComponent compSuccessMsg = new TextComponentTranslation("screenshot.success", compSavePath);
				this.mc.player.sendMessage(compSuccessMsg);

				return true;

			} else {
				mc.player.sendMessage(new TextComponentString("§cError! Unable to save panorama!"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private void writeScreenshot(BufferedImage screenshot, File saveTo) {
		try {
			ImageIO.write(screenshot, "png", saveTo);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static File getUnusedPanoramaFolderName() {
		File saveTo = new File(Minecraft.getMinecraft().mcDataDir, "screenshots");
		try {
			return getFolder(saveTo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new File(saveTo, "panorama_" + System.currentTimeMillis());
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
