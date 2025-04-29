package net.kallens.Command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

import net.minecraft.network.protocol.game.ClientboundChunksBiomesPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

import static net.kallens.Command.ChunkUtils.PullChunk;
import static net.kallens.Command.PullChunkData.pullChunkBlocks;

import static net.kallens.aiminecraft.Chatgpt.chatGPT;
import static net.kallens.aiminecraft.Ollama.ollama;


public class SummonAI {

    String input;
    public SummonAI(CommandDispatcher<CommandSourceStack> dispatcher) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {

            dispatcher.register(
                    Commands.literal("ai")
//                            .then(Commands.literal("spawn")
//                                    .executes(context -> summon(context.getSource()))
//                            )
                            .then(Commands.literal("TokenSet")//renamed from Settings to TokenSet
                                    .executes(context -> settings())
                            )
                            .then(Commands.literal("ask")
                                    .then(Commands.argument("prompt", StringArgumentType.greedyString())
                                            .executes(context -> ask(
                                                    context.getSource(),
                                                    StringArgumentType.getString(context, "prompt")
                                            ))
                                    )
                            )
                            .then(Commands.literal("analyze")
                                    .then(Commands.argument("prompt", StringArgumentType.greedyString())
                                            .executes(context -> analyze(
                                                    context.getSource(),
                                                    StringArgumentType.getString(context, "prompt")

                                            ))
                                    )
                            )

            );

        });


        //loop();


    }
    static String test;
//    public static int summon(CommandSourceStack source) {
//        try {
//
//            source.sendSuccess(() -> Component.literal("AI player '" + "name" + "' spawned successfully"), false);
//            source.sendSuccess(() -> Component.literal("this is a test" + test), false);
//            return 1;
//        } catch (Exception e) {
//            source.sendFailure(Component.literal("Failed-" + e.getMessage()));
//            e.printStackTrace();
//            return 0;
//        }
//    }


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

    void sendcasts(String message, CommandSourceStack source)
    {
        source.sendSuccess(() -> Component.literal(message), false);
    }

    public static int ask(CommandSourceStack source, String prompt)
        {
        try {




            new Thread(() -> {
                try {

                    String output = ollama( "NOTE: PLEASE START YOUR MESSAGE WITH -, SO IT CAN BE FILTERD OUT. keep answers short and formal, and respond the best you can vs the question, now this is the question: "  + prompt, SettingsScreen.TokenandID());
                    Minecraft.getInstance().execute(() -> {
                        source.sendSuccess(() -> Component.literal(output), false);
                    });
                } catch (Exception e) {
                    Minecraft.getInstance().execute(() -> {
                        source.sendFailure(Component.literal("Failed -" + e.getMessage()));
                    });
                    e.printStackTrace();
                }
            }).start();


            return 1;

        } catch (Exception e) {
            source.sendFailure(Component.literal("Failed -" + e.getMessage()));

            e.printStackTrace();
            return 0;
        }
    }













    public static int analyze(CommandSourceStack source, String prompt)
    {
        try {
            Minecraft mc = Minecraft.getInstance();

            MinecraftServer integratedServer = mc.getSingleplayerServer();

            ServerLevel overworld = integratedServer.getLevel(Level.OVERWORLD);
//            String output = chatGPT("test");
            //tested with:
            //deepseek-r1:7b
            //gemma3:27b

            // Suppose you want the chunk containing the player:
            LocalPlayer player = Minecraft.getInstance().player;
            double px = player.getX();
            double pz = player.getZ();
            double py = player.getY();
            int chunkX = ((int)Math.floor(px)) >> 4;
            int chunkZ = ((int)Math.floor(pz)) >> 4;
            int chunkY = ((int)Math.floor(py)) >> 4;



            new Thread(() -> {
                try {

                    String output = ollama("This is the block data:" + pullChunkBlocks(overworld) + "| My pos is = " + px + ","+ py + "," + pz + " And my chunk pos vs the chunk is, "+chunkX + "," + chunkZ +"," + chunkY + " using this info of the chunk and y pos, make informal decisions on whats happening, and keep answers short and formal, and respond the best you can vs the question, NOTE: PLEASE START YOUR MESSAGE WITH -, SO IT CAN BE FILTERD OUT. now this is the question: "  + prompt, SettingsScreen.TokenandID());
                    Minecraft.getInstance().execute(() -> {
                        source.sendSuccess(() -> Component.literal(output), false);
                    });
                } catch (Exception e) {
                    Minecraft.getInstance().execute(() -> {
                        source.sendFailure(Component.literal("Failed -" + e.getMessage()));
                    });
                    e.printStackTrace();
                }
            }).start();


            return 1;

        } catch (Exception e) {
            source.sendFailure(Component.literal("Failed -" + e.getMessage()));

            e.printStackTrace();
            return 0;
        }
    }

}