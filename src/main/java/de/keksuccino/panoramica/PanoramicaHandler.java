package de.keksuccino.panoramica;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import de.keksuccino.konkrete.Konkrete;
import de.keksuccino.konkrete.events.SubscribeEvent;
import de.keksuccino.panoramica.events.TickEvent;
import net.minecraft.client.Minecraft;

public class PanoramicaHandler {

	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");

	private boolean active = false;
	private Minecraft mc = Minecraft.getInstance();

	public static void init() {

		Konkrete.getEventHandler().registerEventsFrom(new PanoramicaHandler());

	}

	@SubscribeEvent
	public void onClientTick(TickEvent e) {
		try {
			//Listen to and handle panorama keybind press
			if (Keybinds.keybindCreatePano.isDown()) {
				if (!this.active) {
					this.createPanorama();
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void createPanorama() {

		this.active = true;

		File savePath = getUnusedPanoramaFolderName();
		if (!savePath.exists()) {
			savePath.mkdirs();
		}

		int res = Panoramica.config.getOrDefault("panoramaresolution", 512);
		mc.player.sendMessage(Minecraft.getInstance().grabPanoramixScreenshot(savePath, res, res), UUID.randomUUID());

		this.active = false;

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
