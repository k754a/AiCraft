package net.kallens.Command;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.level.ClientInformation;
import java.lang.reflect.Constructor;
import java.util.UUID;

public class SummonAI {
    public SummonAI(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("ai").then(Commands.literal("spawn").executes((command) -> {
            return summon(command.getSource());
        })));
    }

    private static int summon(CommandSourceStack source) {
        try {
            ServerLevel world = source.getLevel();
            ServerPlayer server = source.getPlayerOrException();
            GameProfile profile = new GameProfile(UUID.randomUUID(), "AI");


            Constructor<ClientInformation> ciConstructor = ClientInformation.class.getDeclaredConstructor(String.class, boolean.class, boolean.class, String.class);
            ciConstructor.setAccessible(true);
            ClientInformation dummyInfo = ciConstructor.newInstance("127.0.0.1", true, false, "dummy");


            ServerPlayer fakePlayer = new ServerPlayer(
                    source.getServer(),
                    world,
                    profile,
                    dummyInfo
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

