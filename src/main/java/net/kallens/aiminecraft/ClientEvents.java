package net.kallens.aiminecraft;

import com.mojang.blaze3d.vertex.PoseStack;
import net.kallens.Command.SettingsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.SplashRenderer;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.client.gui.components.Button;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.WeakHashMap;

@Mod.EventBusSubscriber(modid = "aiminecraft", value = Dist.CLIENT)
public class ClientEvents  {

    // Only run the prompts folder check/creation once, twin!
    private static boolean promptsFolderReady = false;
    private static File promptsFolderPath = null; // assign this when you create/check the folder

    // keep track of which TitleScreen instances we've patched
    private static final WeakHashMap<TitleScreen, Boolean> patchedScreens = new WeakHashMap<>();

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        // *** PROMPTS FOLDER CHECK/CREATE AND FILL LOGIC ***
        if (!promptsFolderReady) {
            promptsFolderReady = true; // so it don’t run more than once, twin

            try {
                File gameDir = Minecraft.getInstance().gameDirectory;
                File parentDir = gameDir.getParentFile();
                if (parentDir == null) {
                    parentDir = gameDir; // fallback if no parent
                }

                File promptsFolder = new File(parentDir, "prompts");
                promptsFolderPath = promptsFolder; // keep for button

                if (!promptsFolder.exists()) {
                    boolean created = promptsFolder.mkdirs();
                    if (created) {
                        System.out.println("[AI Minecraft] Created prompts folder at " + promptsFolder.getAbsolutePath());
                    } else {
                        System.out.println("[AI Minecraft] Failed to create prompts folder.");
                    }
                } else {
                    System.out.println("[AI Minecraft] Prompts folder already exists.");
                }

                // Setup file contents with your exact texts
                // Using strings with literal "overworld", "px" etc per your specs, no vars replaced here
                String askContent = "NOTE: PLEASE START YOUR MESSAGE WITH -, SO IT CAN BE FILTERED OUT. keep answers short and formal, and respond the best you can vs the question, now this is the question: {{user_prompt}}\n";

                String commandsContent = "You are generating Minecraft Java Edition commands based on the provided chunk block data and player position. Here's the information you need:\n" +
                        "- Chunk blocks: {{chunk_blocks}}\n" +
                        "- Player position: {{player_x}}, {{player_y}}, {{player_z}}\n" +
                        "- Chunk coordinates: {{chunk_x}}, {{chunk_y}}, {{chunk_z}}\n" +
                        "\n" +
                        "Using ONLY this data, generate efficient, **valid in-game commands** that accomplish the following prompt:\n" +
                        "{{user_prompt}}\n" +
                        "\n" +
                        "Rules:\n" +
                        "- Only output Minecraft Java Edition commands.\n" +
                        "- NO explanations or extra text.\n" +
                        "- Each command MUST start with a dash (-).\n" +
                        "- Do NOT use /setblock. Use optimized commands like /fill, /fill ... hollow, /clone, /execute, etc.\n" +
                        "- Use only integer coordinates. No floats or decimals (e.g., use 64 not 64.0).\n" +
                        "- Commands must be compact and efficient. For example, fill a full area rather than placing blocks one by one.\n" +
                        "- Maintain correct command syntax. so make sure and double check its going to work, it should be correct like for example fill would be <pos> <pos> <pos> <pos> <block> and so on\n" +
                        "- Use formal tone, no fluff, just the commands.\n" +
                        "- Remember older commands and positions you filled and edited, for context, so you can continue to make edits things\n";

                String analyzeContent = "This is the block data: {{chunk_blocks}} | My pos is = {{player_x}},{{player_y}},{{player_z}} And my chunk pos vs the chunk is, {{chunk_x}}, {{chunk_z}}, {{chunk_y}} using this info of the chunk and y pos, make informal decisions on whats happening, and keep answers short and formal, and respond the best you can vs the question, NOTE: PLEASE START YOUR MESSAGE WITH -, SO IT CAN BE FILTERED OUT. ALSO REMEMBER, ONLY COMMANDS, NO OTHER TEXT OR THINGS PLEASE now this is the question: {{user_prompt}}\n";

                checkAndFillFile(new File(promptsFolder, "ask.txt"), askContent);
                checkAndFillFile(new File(promptsFolder, "commands.txt"), commandsContent);
                checkAndFillFile(new File(promptsFolder, "analyze.txt"), analyzeContent);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // The rest is your old stuff
        if (!(Minecraft.getInstance().screen instanceof TitleScreen screen)) return;

        if (!patchedScreens.containsKey(screen)) {
            try {
                Field splashField = TitleScreen.class.getDeclaredField("splash");
                splashField.setAccessible(true);

                SplashRenderer customSplash = new SplashRenderer("With AI");
                splashField.set(screen, customSplash);

                patchedScreens.put(screen, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void checkAndFillFile(File file, String content) throws IOException {
        if (!file.exists() || Files.size(file.toPath()) == 0) {
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(content);
                System.out.println("[PromptFolderManager] Created/fixed " + file.getName());
            }
        }
    }

    @SubscribeEvent
    public static void onTitleScreenRender(ScreenEvent.Render.Post event) {
        if (!(event.getScreen() instanceof TitleScreen)) return;

        GuiGraphics gui = event.getGuiGraphics();
        Minecraft mc = Minecraft.getInstance();
        String splash = "--This Mod needs Ollama to run--";

        float rawX = mc.getWindow().getGuiScaledWidth() / 2f + 90f;
        float rawY = 70f;

        float baseScale = 1;

        PoseStack pose = gui.pose();
        pose.pushPose();

        pose.scale(baseScale, baseScale, 1f);

        int offsetX = -10;
        int offsetY = -10;

        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        int textWidth = mc.font.width(splash);

        int x = screenWidth - textWidth + offsetX;
        int y = screenHeight - mc.font.lineHeight + offsetY;

        gui.drawString(
                mc.font,
                Component.literal(splash),
                x,
                y,
                0xFFFF00
        );

        pose.popPose();
    }

    @SubscribeEvent
    public static void onTitleScreenInit(ScreenEvent.Init.Post event) {
        if (!(event.getScreen() instanceof TitleScreen)) return;

        Minecraft mc = Minecraft.getInstance();
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        int singleplayerY = screenHeight / 4 + 48;

        int size = 20;
        int spacing = 4;

        int singleplayerX = screenWidth / 2 - 100;

        int iconX = singleplayerX + 200 + spacing;
        int iconY = singleplayerY;

        Button iconButton = Button.builder(
                Component.literal("\uD83D\uDCD6"),
                btn -> {
                    if (promptsFolderPath != null && promptsFolderPath.exists()) {
                        try {
                            Runtime.getRuntime().exec(new String[]{"explorer.exe", promptsFolderPath.getAbsolutePath()});
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("Bruh, prompts folder not ready yet.");
                    }
                }
        ).bounds(iconX, iconY, size, size).build();

        event.addListener(iconButton);
    }
}
