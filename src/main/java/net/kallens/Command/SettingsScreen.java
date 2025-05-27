package net.kallens.Command;

import net.kallens.events.IsKeyPressed;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Debug;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.Collections;

import static com.mojang.text2speech.Narrator.LOGGER;
import static net.kallens.Command.SummonAI.loadPromptTemplate;

public class SettingsScreen extends Screen {
    public List<EditBox> textBox = new ArrayList<>();

    Button enterbutton;



    public SettingsScreen(Component title) {
        super(title);
    }
    static String test;

    float textboxeslist = 2;
    @Override
    protected void init() {
        int boxWidth = 200;
        textBox.clear();

        for (int i = 0; i < textboxeslist; i++) {
            EditBox box = new EditBox(this.font, this.width / 2 - boxWidth / 2, this.height / 2 - 10, boxWidth, 20, Component.literal(""));
            textBox.add(box);
            this.addWidget(box);
        }

//        enterbutton = Button.builder(Component.literal("Submit"), button -> {
//
//                    if (Minecraft.getInstance() != null) {
//                        player = Minecraft.getInstance().player;
//                    }
//
//                    test = textBox.get(0).getValue();
//                    LOGGER.info("Button clicked. Current value: " + test);
//
//                    if (player != null) {
//                        sendcasts("AI token has been u3pdated!", player.createCommandSourceStack());
//                    }
//                })
//                .pos(this.width / 2 - 49, this.height / 2 + 30)
//                .size(100, 100)
//                .build();
//
//
//        this.addWidget(enterbutton);
//    }
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

        this.renderBackground(guiGraphics);

        run();

        super.render(guiGraphics, mouseX, mouseY, partialTicks);

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

            sendcasts("AI token has been updated!", player.createCommandSourceStack());

            test = textBox.get(0).getValue();

            try {
                savePromptTemplate("token", test);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }


//            try {
//                String template = loadPromptTemplate("analyze");
//
//
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }


            return super.keyPressed(256, scanCode, modifiers);
        }
        if (textBox.get(0).keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }



        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    public static String TokenandID() throws IOException {

        String fix = loadPromptTemplate("token");

        return fix;

    }




    private static void savePromptTemplate(String name, String content) throws IOException {
        File dir = new File("../run/prompts/");
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new IOException("Failed to create directory: " + dir.getAbsolutePath());
            }
        }

        File file = new File(dir, name + ".txt");
        if (!file.exists()) {
            if (!file.createNewFile()) {
                throw new IOException("Failed to create file: " + file.getAbsolutePath());
            }
        }

        try (FileWriter writer = new FileWriter(file)) {
            writer.write(content);
        }
    }



}

