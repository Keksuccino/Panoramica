package de.keksuccino.panoramica;

import net.minecraft.client.gui.GuiScreen;

import java.io.IOException;

public class DummyScreen extends GuiScreen {

    @Override
    public boolean doesGuiPauseGame() {
        return true;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
    }

}
