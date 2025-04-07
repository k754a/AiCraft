package net.kallens.Command;
import java.util.UUID;
import java.util.function.Supplier;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import org.openjdk.nashorn.api.tree.WhileLoopTree;

import static com.mojang.text2speech.Narrator.LOGGER;

public class SummonAI {

    SettingsScreen settingsScreen = new SettingsScreen(Component.literal("Settings"));
    public SummonAI(CommandDispatcher<CommandSourceStack> dispatcher) {
        if(Minecraft.getInstance() != null)
        {
            dispatcher.register(
                    Commands.literal("ai")
                            .then(Commands.literal("spawn")
                                    .executes(context -> summon(context.getSource()))
                            )

                            .then(Commands.literal("settings")

                                    .executes(context -> settings())
                            )

            );
        }




        //loop();


    }

    public static int summon(CommandSourceStack source) {
        try {

            source.sendSuccess(() -> Component.literal("AI player '" + "name" + "' spawned successfully"), false);
            return 1;
        } catch (Exception e) {
            source.sendFailure(Component.literal("Failed to spawn AI player: " + e.getMessage()));
            e.printStackTrace();
            return 0;
        }
    }



    public static int settings() {
        try {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> {
            if(Minecraft.getInstance() != null)
            {
                // Client-side code to open the settings GUI
                Minecraft.getInstance().setScreen(new SettingsScreen(Component.literal("Settings")));
            }

            return null;
        });
        return 1;

        } catch (Exception e) {;
            return 0;
        }
    }


    public void loop()
    {

       settingsScreen.run();

    }


}