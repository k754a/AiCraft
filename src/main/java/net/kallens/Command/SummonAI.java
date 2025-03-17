package net.kallens.Command;

import java.util.UUID;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.player.ChatVisiblity;
import net.minecraft.world.entity.HumanoidArm;

public class SummonAI {
    public SummonAI(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("ai")
                        .then(Commands.literal("spawn").executes(context -> summon(context.getSource())))
        );
    }

    private static int summon(CommandSourceStack source) {
        try {
            ServerLevel world = source.getLevel();
            ServerPlayer serverPlayer = source.getPlayerOrException();

            GameProfile profile = new GameProfile(UUID.randomUUID(), "AI");

            // Create client information
            ClientInformation dummyInfo = new ClientInformation(
                    "en_us",
                    10,
                    ChatVisiblity.FULL,
                    true,
                    127,
                    HumanoidArm.RIGHT,
                    false,
                    false
            );

            // Create AI player
            ServerPlayer fakePlayer = new ServerPlayer(
                    source.getServer(),
                    world,
                    profile,
                    dummyInfo
            );

            // Assign a dummy cookie/connection so fakePlayer.connection isn't null
            CommonListenerCookie dummyCookie = new CommonListenerCookie(
                    profile,    // GameProfile
                    0,          // Int (e.g., placeholder ping)
                    dummyInfo,  // ClientInformation
                    false       // Boolean (e.g., isUtility)
            );

            fakePlayer.connection = new ServerGamePacketListenerImpl(
                    source.getServer(),
                    new Connection(null), // Dummy Connection
                    fakePlayer,
                    dummyCookie
            );

            // Position and add the AI player to the world
            fakePlayer.setPos(serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ());
            world.addFreshEntity(fakePlayer);

            // Confirm spawn
            source.sendSuccess(() -> Component.literal("AI player spawned successfully."), true);

            return 1;
        } catch (Exception e) {
            source.sendFailure(Component.literal("Failed to spawn AI player: " + e.getMessage()));
            e.printStackTrace();
            return 0;
        }
    }
}