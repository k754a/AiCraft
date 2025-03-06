package net.kallens.Command;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
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
        try {
            ServerLevel world = source.getLevel(); // Get the world
            ServerPlayer server = source.getPlayerOrException(); // Get the player executing the command

            GameProfile profile = new GameProfile(UUID.randomUUID(), "AI");

            // Create a fake player with a minimal constructor setup
            ServerPlayer fakePlayer = new ServerPlayer(
                    source.getServer(),
                    world,
                    profile,
                    null
            );


            fakePlayer.setPos(server.getX(), server.getY(), server.getZ());


            world.addFreshEntity(fakePlayer);

            return 1;
        } catch (Exception e) {

            source.sendFailure(Component.literal("Failed to spawn AI player: " + e.getMessage()));
            return 0;
        }
    }
}
