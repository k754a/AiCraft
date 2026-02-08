package net.kallens.Command;

import net.kallens.aiminecraft.ClientEvents;
import net.kallens.aiminecraft.UserSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class SettingsScreen extends Screen {
    private static final int MIN_PANEL_WIDTH = 240;
    private static final int MAX_PANEL_WIDTH = 320;
    private static final int MIN_ROW_HEIGHT = 20;
    private static final int MAX_ROW_HEIGHT = 28;
    private static final int LABEL_GAP = 4;
    private static final int FIELD_GAP = 10;

    private EditBox modelBox;
    private EditBox radiusBox;
    private EditBox maxBlocksBox;
    private Button streamToggle;
    private Button autoExecToggle;
    private Button saveButton;
    private Button resetButton;
    private Button openPromptsButton;
    private Button closeButton;

    private UserSettings settings;
    private int panelWidth;
    private int rowHeight;
    private int panelHeight;
    private int left;
    private int top;
    private int fieldWidth;
    private int labelHeight;

    public SettingsScreen(Component title) {
        super(title);
    }

    @Override
    protected void init() {
        settings = UserSettings.get();

        updateLayout();

        int cursorY = top + rowHeight;

        modelBox = new EditBox(this.font, left + 10, cursorY + labelHeight + LABEL_GAP, fieldWidth, 20, Component.literal(""));
        modelBox.setMaxLength(200);
        modelBox.setValue(settings.modelName);
        this.addRenderableWidget(modelBox);

        cursorY = cursorY + labelHeight + LABEL_GAP + 20 + FIELD_GAP;
        radiusBox = new EditBox(this.font, left + 10, cursorY + labelHeight + LABEL_GAP, fieldWidth, 20, Component.literal(""));
        radiusBox.setMaxLength(6);
        radiusBox.setFilter(value -> value.matches("\\d*"));
        radiusBox.setValue(Integer.toString(settings.chunkRadiusY));
        this.addRenderableWidget(radiusBox);

        cursorY = cursorY + labelHeight + LABEL_GAP + 20 + FIELD_GAP;
        maxBlocksBox = new EditBox(this.font, left + 10, cursorY + labelHeight + LABEL_GAP, fieldWidth, 20, Component.literal(""));
        maxBlocksBox.setMaxLength(8);
        maxBlocksBox.setFilter(value -> value.matches("\\d*"));
        maxBlocksBox.setValue(Integer.toString(settings.maxBlocks));
        this.addRenderableWidget(maxBlocksBox);

        cursorY = cursorY + labelHeight + LABEL_GAP + 20 + FIELD_GAP + 4;
        streamToggle = Button.builder(Component.literal(toggleLabel("Stream Output", settings.streamOutput)), button -> {
            settings.streamOutput = !settings.streamOutput;
            button.setMessage(Component.literal(toggleLabel("Stream Output", settings.streamOutput)));
        }).bounds(left + 10, cursorY, fieldWidth, 20).build();
        this.addRenderableWidget(streamToggle);

        cursorY += 24;
        autoExecToggle = Button.builder(Component.literal(toggleLabel("Auto Execute Commands", settings.autoExecuteCommands)), button -> {
            settings.autoExecuteCommands = !settings.autoExecuteCommands;
            button.setMessage(Component.literal(toggleLabel("Auto Execute Commands", settings.autoExecuteCommands)));
        }).bounds(left + 10, cursorY, fieldWidth, 20).build();
        this.addRenderableWidget(autoExecToggle);

        cursorY += 30;
        saveButton = Button.builder(Component.literal("Save"), button -> saveSettings()).bounds(left + 10, cursorY, 80, 20).build();
        resetButton = Button.builder(Component.literal("Reset"), button -> resetDefaults()).bounds(left + 100, cursorY, 80, 20).build();
        closeButton = Button.builder(Component.literal("Close"), button -> onClose()).bounds(left + 190, cursorY, 60, 20).build();
        this.addRenderableWidget(saveButton);
        this.addRenderableWidget(resetButton);
        this.addRenderableWidget(closeButton);

        cursorY += 28;
        openPromptsButton = Button.builder(Component.literal("Open Prompts Folder"), button -> openPrompts()).bounds(left + 10, cursorY, fieldWidth, 20).build();
        this.addRenderableWidget(openPromptsButton);

        this.setInitialFocus(modelBox);
        this.setFocused(modelBox);
        modelBox.setFocused(true);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTicks);

        updateLayout();
        int right = left + panelWidth;
        int bottom = top + panelHeight;

        guiGraphics.fill(left, top - 10, right, bottom, 0xFF101010);

        super.render(guiGraphics, mouseX, mouseY, partialTicks);

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0, 0, 200);
        guiGraphics.drawString(this.font, "AI Settings", left + 10, top - 4, 0xFFFFFF, false);
        int labelY = top + rowHeight;
        guiGraphics.drawString(this.font, "Model Name", left + 10, labelY, 0xF0F0F0, false);
        labelY = labelY + labelHeight + LABEL_GAP + 20 + FIELD_GAP;
        guiGraphics.drawString(this.font, "Chunk Radius Y", left + 10, labelY, 0xF0F0F0, false);
        labelY = labelY + labelHeight + LABEL_GAP + 20 + FIELD_GAP;
        guiGraphics.drawString(this.font, "Max Blocks/Chunks", left + 10, labelY, 0xF0F0F0, false);
        guiGraphics.pose().popPose();
    }

    private void updateLayout() {
        panelWidth = Math.min(MAX_PANEL_WIDTH, Math.max(MIN_PANEL_WIDTH, this.width - 40));
        rowHeight = Math.min(MAX_ROW_HEIGHT, Math.max(MIN_ROW_HEIGHT, (this.height - 40) / 12));
        labelHeight = this.font.lineHeight;
        panelHeight = rowHeight * 12;
        left = (this.width - panelWidth) / 2;
        top = Math.max(20, (this.height - panelHeight) / 2);
        fieldWidth = panelWidth - 20;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 257) {
            saveSettings();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void saveSettings() {
        settings.modelName = modelBox.getValue().trim();
        settings.chunkRadiusY = clamp(parseInt(radiusBox.getValue(), 32), 4, 128);
        settings.maxBlocks = clamp(parseInt(maxBlocksBox.getValue(), 6000), 500, 20000);

        try {
            settings.save();
            saveLegacyToken(settings.modelName);
            sendcasts("AI settings saved.", getPlayerSource());
        } catch (IOException e) {
            sendcasts("Failed to save settings.", getPlayerSource());
        }
    }

    private void resetDefaults() {
        settings.modelName = "";
        settings.chunkRadiusY = 32;
        settings.maxBlocks = 6000;
        settings.streamOutput = true;
        settings.autoExecuteCommands = true;

        modelBox.setValue(settings.modelName);
        radiusBox.setValue(Integer.toString(settings.chunkRadiusY));
        maxBlocksBox.setValue(Integer.toString(settings.maxBlocks));
        streamToggle.setMessage(Component.literal(toggleLabel("Stream Output", settings.streamOutput)));
        autoExecToggle.setMessage(Component.literal(toggleLabel("Auto Execute Commands", settings.autoExecuteCommands)));
    }

    private void openPrompts() {
        File dir = ClientEvents.promptsFolderPath;
        if (dir != null && dir.exists()) {
            try {
                Runtime.getRuntime().exec(new String[]{"explorer.exe", dir.getAbsolutePath()});
            } catch (IOException e) {
                sendcasts("Failed to open prompts folder.", getPlayerSource());
            }
        }
    }

    private CommandSourceStack getPlayerSource() {
        Player player = Minecraft.getInstance().player;
        if (player == null) {
            return null;
        }
        return player.createCommandSourceStack();
    }

    private void sendcasts(String message, CommandSourceStack source) {
        if (source != null) {
            source.sendSuccess(() -> Component.literal(message), false);
        }
    }

    private static int parseInt(String value, int fallback) {
        if (value == null || value.isEmpty()) {
            return fallback;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private static String toggleLabel(String name, boolean enabled) {
        return name + ": " + (enabled ? "On" : "Off");
    }

    public static String TokenandID() {
        String model = UserSettings.get().modelName;
        return model == null ? "" : model.trim();
    }

    private static void saveLegacyToken(String content) throws IOException {
        File dir = ClientEvents.promptsFolderPath;
        if (dir == null) {
            String roamingPath = System.getenv("APPDATA");
            if (roamingPath == null) {
                return;
            }
            dir = new File(roamingPath, "prompts");
        }

        if (!dir.exists() && !dir.mkdirs()) {
            throw new IOException("Failed to create directory: " + dir.getAbsolutePath());
        }

        File file = new File(dir, "token.txt");
        if (!file.exists() && !file.createNewFile()) {
            throw new IOException("Failed to create file: " + file.getAbsolutePath());
        }

        try (FileWriter writer = new FileWriter(file)) {
            writer.write(content == null ? "" : content);
        }
    }
}

