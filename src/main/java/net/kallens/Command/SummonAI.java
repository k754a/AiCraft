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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

public class SummonAI {
    public SummonAI(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("ai")
                        .then(Commands.literal("spawn")
                                .executes(context -> summon(context.getSource()))
                        )
        );
        dispatcher.register(
                Commands.literal("ai")
                        .then(Commands.literal("settings")
                                .executes(context -> settings(context.getSource()))
                        )
        );
    }

    private static int summon(CommandSourceStack source) {
        try {

            source.sendSuccess(() -> Component.literal("AI player '" + "name" + "' spawned successfully"), false);
            return 1;
        } catch (Exception e) {
            source.sendFailure(Component.literal("Failed to spawn AI player: " + e.getMessage()));
            e.printStackTrace();
            return 0;
        }
    }



    private static int settings(CommandSourceStack source) {
        try {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> {
            // Client-side code to open the settings GUI
            Minecraft.getInstance().setScreen(new SettingsScreen(Component.literal("Settings")));
            return null;
        });
        return 1;

        } catch (Exception e) {;
            return 0;
        }
    }


}