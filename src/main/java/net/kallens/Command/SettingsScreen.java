package net.kallens.Command;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import com.mojang.blaze3d.vertex.PoseStack;

public class SettingsScreen extends Screen {
    private EditBox textBox;

    protected SettingsScreen(Component title) {
        super(title);
    }

    @Override
    protected void init() {
        int boxWidth = 200;

        textBox = new EditBox(this.font, this.width / 2 - boxWidth / 2, this.height / 2 - 10, boxWidth, 20, Component.literal(""));
        this.addWidget(textBox);
    }

    @Override
 
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        // Call the new renderBackground method that accepts GuiGraphics:
        this.renderBackground(guiGraphics);
        // Call super.render with GuiGraphics:
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        // Render the text box using GuiGraphics:
        textBox.render(guiGraphics, mouseX, mouseY, partialTicks);
    }

    private void renderBackground(GuiGraphics guiGraphics) {
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (textBox.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
