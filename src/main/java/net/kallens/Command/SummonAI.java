package net.kallens.Command;

import java.util.UUID;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;

public class SummonAI {
    public SummonAI(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("ai")
                        .then(Commands.literal("spawn")
                                .executes(context -> summon(context.getSource()))
                        )
        );
    }

    private static int summon(CommandSourceStack source) {
        try {
            // Get server and world from the command source
            MinecraftServer server = source.getServer();
            ServerLevel world = source.getLevel();

            // Define the fake player's name (hardcoded for simplicity)
            String name = "AI_Player";

            // Create a GameProfile with a random UUID and the name
            GameProfile profile = new GameProfile(UUID.randomUUID(), name);

            // Spawn the fake player using FakePlayerFactory
            FakePlayer fakePlayer = FakePlayerFactory.get(world, profile);

            // Add the fake player to the world
            world.addFreshEntity(fakePlayer);

            // Send success message to the command executor
            source.sendSuccess(Component.literal("AI player '" + name + "' spawned successfully"), false);
            return 1; // Command success
        } catch (Exception e) {
            // Send failure message if something goes wrong
            source.sendFailure(Component.literal("Failed to spawn AI player: " + e.getMessage()));
            e.printStackTrace();
            return 0; // Command failure
        }
    }
}