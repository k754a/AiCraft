package net.kallens.Command;

import net.kallens.events.IsKeyPressed;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.player.Player;
import org.lwjgl.glfw.GLFW;

import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;

import static com.mojang.text2speech.Narrator.LOGGER;

public class SettingsScreen extends Screen {
    public EditBox textBox;

    public SettingsScreen(Component title) {
        super(title);
    }
    String test;
    @Override
    protected void init() {

        int boxWidth = 200;

        textBox = new EditBox(this.font, this.width / 2 - boxWidth / 2, this.height / 2 - 10, boxWidth, 20, Component.literal(""));

        this.addWidget(textBox);

    }

    public void run(){


        if(this.textBox != null)
        {

            //LOGGER.info("Box input test: " + test);


        }



    }



    @Override
 
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        // Call the new renderBackground method that accepts GuiGraphics:
        this.renderBackground(guiGraphics);

        run();
        // Call super.render with GuiGraphics:
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        // Render the text box using GuiGraphics:
        textBox.render(guiGraphics, mouseX, mouseY, partialTicks);
    }

    private void renderBackground(GuiGraphics guiGraphics) {

    }
    Player player = Minecraft.getInstance().player;
    void sendcasts(String message, CommandSourceStack source)
    {
        source.sendSuccess(() -> Component.literal(message), false);
    }
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {

        if(keyCode == 257)
        {
            LOGGER.info("Enter");

            sendcasts("AI main connection has been updated!", player.createCommandSourceStack());

            test = textBox.getValue();
            return super.keyPressed(256, scanCode, modifiers);
        }
        if (textBox.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }



        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
