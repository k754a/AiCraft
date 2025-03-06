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

            // Create client information with valid parameters
            ClientInformation dummyInfo = new ClientInformation(
                    "en_us",               // language
                    10,                    // view distance
                    ChatVisiblity.FULL,    // chat visibility
                    true,                  // chat colors
                    127,                   // displayed skin parts
                    HumanoidArm.RIGHT,     // main hand
                    false,                 // textFilteringEnabled
                    false                  // allowsListing
            );

            // Create a fake player
            ServerPlayer fakePlayer = new ServerPlayer(
                    source.getServer(),
                    world,
                    profile,
                    dummyInfo
            );

            // Create the cookie matching your constructor signature (GameProfile, int, ClientInformation, boolean)
            CommonListenerCookie dummyCookie = new CommonListenerCookie(
                    profile,       // The GameProfile
                    0,             // An int - perhaps latency/ping or a placeholder
                    dummyInfo,     // Your ClientInformation
                    false          // A boolean - possibly 'isUtility' or similar
            );

            // Create the network listener impl
            fakePlayer.connection = new ServerGamePacketListenerImpl(
                    source.getServer(),
                    new Connection(null), // Dummy connection
                    fakePlayer,
                    dummyCookie
            );

            // Position the new player
            fakePlayer.setPos(serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ());
            world.addFreshEntity(fakePlayer);

            return 1;
        } catch (Exception e) {
            source.sendFailure(Component.literal("Failed to spawn AI player: " + e.getMessage()));
            e.printStackTrace();
            return 0;
        }
    }
}