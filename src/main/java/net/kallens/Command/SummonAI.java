package net.kallens.Command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.kallens.aiminecraft.ClientEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static net.kallens.Command.PullChunkData.pullChunkBlocks;

import static net.kallens.aiminecraft.ClientEvents.createfolders;
import static net.kallens.aiminecraft.Ollama.ollama;


//cleaned up by ai, but not coded by it
public class SummonAI {

    public SummonAI(CommandDispatcher<CommandSourceStack> dispatcher) {


        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            dispatcher.register(
                    Commands.literal("ai")
                            .then(Commands.literal("tokenSet")
                                    .executes(context -> settings()))
                            .then(Commands.literal("reset")
                                    .executes(context -> stopcontext(context.getSource())))
                            .then(Commands.literal("ask")
                                    .then(Commands.argument("prompt", StringArgumentType.greedyString())
                                            .executes(context -> ask(
                                                    context.getSource(),
                                                    StringArgumentType.getString(context, "prompt")))))
                            .then(Commands.literal("analyze")
                                    .then(Commands.argument("prompt", StringArgumentType.greedyString())
                                            .executes(context -> analyze(
                                                    context.getSource(),
                                                    StringArgumentType.getString(context, "prompt")))))
                            .then(Commands.literal("usecommands")
                                    .then(Commands.argument("prompt", StringArgumentType.greedyString())
                                            .executes(context -> command(
                                                    context.getSource(),
                                                    StringArgumentType.getString(context, "prompt")))))
                            .then(Commands.literal("changeprompt")
                                    .executes(context -> Ingame()))
            );
        });
    }

    public static int settings() {
        try {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                if (Minecraft.getInstance() != null) {
                    Minecraft.getInstance().execute(() -> {
                        if (Minecraft.getInstance().level != null) {
                            Minecraft.getInstance().setScreen(new SettingsScreen(Component.literal("Settings")));
                        }
                    });
                }
            });
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    public static int stopcontext(CommandSourceStack source) {
        try {
            ollama("/clear", SettingsScreen.TokenandID(), source);
            Minecraft.getInstance().execute(() ->
                    source.sendSuccess(() -> Component.literal("model has been reset."), false));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }
    //this is for mod testing
//    public static String loadPromptTemplate(String name) throws IOException {
//        File file = new File("../run/prompts/" + name + ".txt");
//
//        StringBuilder builder = new StringBuilder();
//        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
//            String line;
//            while ((line = reader.readLine()) != null) {
//                builder.append(line).append("\n");
//            }
//        }
//        return builder.toString();
//    }
//

//
    public static String loadPromptTemplate(String name) throws IOException {
        String roamingPath = System.getenv("APPDATA");
        File file = new File(roamingPath + "/prompts/" + name + ".txt");

        StringBuilder builder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
            }
        }
        return builder.toString();
    }


    public static int ask(CommandSourceStack source, String prompt) {
        createfolders();

        try {
            new Thread(() -> {
                try {

                    source.sendSuccess(() -> {
                        try {
                            return Component.literal( SettingsScreen.TokenandID());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }, false);

                    String template = loadPromptTemplate("ask");
                    String output = ollama(
                            template.replace("{{user_prompt}}", prompt),
                            SettingsScreen.TokenandID(), source);
                    Minecraft.getInstance().execute(() ->
                            source.sendSuccess(() -> Component.literal(output), false));
                } catch (Exception e) {
                    Minecraft.getInstance().execute(() ->
                            source.sendFailure(Component.literal("Failed -" + e.getMessage())));
                    e.printStackTrace();
                }
            }).start();
            return 1;
        } catch (Exception e) {
            source.sendFailure(Component.literal("Failed -" + e.getMessage()));
            return 0;
        }
    }

    public static int analyze(CommandSourceStack source, String prompt) {
        createfolders();


        try {
            Minecraft mc = Minecraft.getInstance();
            MinecraftServer integratedServer = mc.getSingleplayerServer();
            ServerLevel overworld = integratedServer.getLevel(Level.OVERWORLD);
            LocalPlayer player = mc.player;

            double px = player.getX();
            double py = player.getY();
            double pz = player.getZ();
            int chunkX = ((int)Math.floor(px)) >> 4;
            int chunkZ = ((int)Math.floor(pz)) >> 4;
            int chunkY = ((int)Math.floor(py)) >> 4;

            new Thread(() -> {
                try {
                    String template = loadPromptTemplate("analyze");
                    String formatted = template
                            .replace("{{chunk_blocks}}", pullChunkBlocks(overworld))
                            .replace("{{player_x}}", Integer.toString((int) px))
                            .replace("{{player_y}}", Integer.toString((int) py))
                            .replace("{{player_z}}", Integer.toString((int) pz))
                            .replace("{{chunk_x}}", Integer.toString(chunkX))
                            .replace("{{chunk_y}}", Integer.toString(chunkY))
                            .replace("{{chunk_z}}", Integer.toString(chunkZ))
                            .replace("{{user_prompt}}", prompt);

                    String output = ollama(formatted, SettingsScreen.TokenandID(), source);
                    Minecraft.getInstance().execute(() ->
                            source.sendSuccess(() -> Component.literal(output), false));
                } catch (Exception e) {
                    Minecraft.getInstance().execute(() ->
                            source.sendFailure(Component.literal("Failed -" + e.getMessage())));
                    e.printStackTrace();
                }
            }).start();

            return 1;
        } catch (Exception e) {
            source.sendFailure(Component.literal("Failed -" + e.getMessage()));
            return 0;
        }
    }

    public static int command(CommandSourceStack source, String prompt) {

        createfolders();

        try {
            Minecraft mc = Minecraft.getInstance();
            MinecraftServer integratedServer = mc.getSingleplayerServer();
            ServerLevel overworld = integratedServer.getLevel(Level.OVERWORLD);
            LocalPlayer player = mc.player;

            double px = player.getX();
            double py = player.getY();
            double pz = player.getZ();
            int chunkX = ((int)Math.floor(px)) >> 4;
            int chunkZ = ((int)Math.floor(pz)) >> 4;
            int chunkY = ((int)Math.floor(py)) >> 4;

            new Thread(() -> {
                try {
                    String template = loadPromptTemplate("commands");
                    String formatted = template
                            .replace("{{chunk_blocks}}", pullChunkBlocks(overworld))
                            .replace("{{player_x}}", Integer.toString((int) px))
                            .replace("{{player_y}}", Integer.toString((int) py))
                            .replace("{{player_z}}", Integer.toString((int) pz))
                            .replace("{{chunk_x}}", Integer.toString(chunkX))
                            .replace("{{chunk_y}}", Integer.toString(chunkY))
                            .replace("{{chunk_z}}", Integer.toString(chunkZ))
                            .replace("{{user_prompt}}", prompt);

                    String output = ollama(formatted, SettingsScreen.TokenandID(), source);

                    Minecraft.getInstance().execute(() ->
                            source.sendSuccess(() -> Component.literal(output), false));

                    String[] commands = output.split("\n");
                    for (String cmd : commands) {
                        cmd = cmd.replaceFirst("^-\\s*", "").trim();
                        if (cmd.startsWith("/")) cmd = cmd.substring(1);
                        integratedServer.getCommands().performPrefixedCommand(source, cmd);
                    }

                } catch (Exception e) {
                    Minecraft.getInstance().execute(() ->
                            source.sendFailure(Component.literal("Failed -" + e.getMessage())));
                    e.printStackTrace();
                }
            }).start();

            return 1;
        } catch (Exception e) {
            source.sendFailure(Component.literal("Failed -" + e.getMessage()));
            return 0;
        }
    }



    public static int Ingame()
    {
        if (ClientEvents.promptsFolderPath != null && ClientEvents.promptsFolderPath.exists()) {
            try {
                Runtime.getRuntime().exec(new String[]{"explorer.exe", ClientEvents.promptsFolderPath.getAbsolutePath()});
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Error, prompt folder does not exist.");
        }

        return  1;
    }
}