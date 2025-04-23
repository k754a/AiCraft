package net.kallens.Command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

import static net.kallens.aiminecraft.Chatgpt.chatGPT;
import static net.kallens.aiminecraft.Ollama.ollama;


public class SummonAI {

    String input;
    public SummonAI(CommandDispatcher<CommandSourceStack> dispatcher) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {

            dispatcher.register(
                    Commands.literal("ai")
                            .then(Commands.literal("spawn")
                                    .executes(context -> summon(context.getSource()))
                            )
                            .then(Commands.literal("settings")
                                    .executes(context -> settings())
                            )
                            .then(Commands.literal("ask")
                                            .executes(context -> ask(context.getSource()))
                            )
            );

        });


        //loop();


    }
    static String test;
    public static int summon(CommandSourceStack source) {
        try {

            source.sendSuccess(() -> Component.literal("AI player '" + "name" + "' spawned successfully"), false);
            source.sendSuccess(() -> Component.literal("this is a test" + test), false);
            return 1;
        } catch (Exception e) {
            source.sendFailure(Component.literal("Failed-" + e.getMessage()));
            e.printStackTrace();
            return 0;
        }
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

    void sendcasts(String message, CommandSourceStack source)
    {
        source.sendSuccess(() -> Component.literal(message), false);
    }

    public static int ask(CommandSourceStack source) {
        try {
//            String output = chatGPT("test");
            String output = ollama("whats 5*5", "deepseek-r1:1.5b");
            source.sendSuccess(() -> Component.literal("The output is "+output), false);

            return 1;

        } catch (Exception e) {
            source.sendFailure(Component.literal("Failed -" + e.getMessage()));

            e.printStackTrace();
            return 0;
        }
    }
}