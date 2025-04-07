package net.kallens.Command;

import net.kallens.events.IsKeyPressed;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.util.*;

import static com.mojang.text2speech.Narrator.LOGGER;

public class SettingsScreen extends Screen {
    public List<EditBox> textBox = new ArrayList<>();

    Button enterbutton;



    public SettingsScreen(Component title) {
        super(title);
    }
    String test;

    float textboxeslist = 2;
    @Override
    protected void init() {

        int boxWidth = 200;
        textBox.clear();


//         enterbutton = new Button(
//                16, 22,
//                98, 20,
//                // Text shown on the button
//                "text",
//                // Action performed when the button is pressed
//                button -> {
//                    System.out.println("button clicked");
//                }
//        );
        for(int i=0; i<textboxeslist;i++)
        {
            EditBox box = new EditBox(this.font, this.width / 2 - boxWidth / 2, this.height / 2 - 10, boxWidth, 20 , Component.literal(""));

            textBox.add(box);

            this.addWidget(textBox.get(0));
            LOGGER.info("Test: " + textBox.get(0));

        }


    }

    public void run(){


//        if(textBox.get(0) != null)
//        {
//
//            //LOGGER.info("Box input test: " + test);
//
//
//        }



    }



    @Override

    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        // Call the new renderBackground method that accepts GuiGraphics:
        this.renderBackground(guiGraphics);

        run();
        // Call super.render with GuiGraphics:
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        // Render the text box using GuiGraphics:
        for(int i=0; i<textboxeslist;i++) {
            textBox.get(i).render(guiGraphics, mouseX, mouseY, partialTicks);
        }

    }

    private void renderBackground(GuiGraphics guiGraphics) {

    }

    Player player;
    void sendcasts(String message, CommandSourceStack source)
    {
        source.sendSuccess(() -> Component.literal(message), false);
    }
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if(Minecraft.getInstance() != null)
        {
             player = Minecraft.getInstance().player;
        }

        if(keyCode == 257)
        {
            LOGGER.info("Enter");

            sendcasts("AI main connection has been updated!", player.createCommandSourceStack());

            test = textBox.get(0).getValue();
            return super.keyPressed(256, scanCode, modifiers);
        }
        if (textBox.get(0).keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }



        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}

