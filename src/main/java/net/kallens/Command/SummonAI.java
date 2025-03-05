package net.kallens.Command;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;

import java.util.UUID;

public class SummonAI {
    public SummonAI(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("ai").then(Commands.literal("spawn").executes((command) -> {
            return summon(command.getSource());
        })));
    }

    private static int summon(CommandSourceStack source) {
        ServerLevel world = source.getLevel(); // Get the world
        GameProfile profile = new GameProfile(UUID.randomUUID(), "AI");

        // Create a fake player without a network connection
        ServerPlayer fakePlayer = new ServerPlayer(source.getServer(), world, profile, null);

        // Add the fake player to the world
        world.addFreshEntity(fakePlayer);

        return 1;
    }
}
