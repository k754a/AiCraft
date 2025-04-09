package net.kallens.events;

import net.kallens.Command.SummonAI;
import net.kallens.aiminecraft.AiMinecraft;
import net.minecraftforge.event.RegisterCommandsEvent;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.command.ConfigCommand;

@Mod.EventBusSubscriber(modid = AiMinecraft.MODID)
public class Modevents {

    @SubscribeEvent
    public static void onCommandsRegister(RegisterCommandsEvent event) {

        new SummonAI(event.getDispatcher());


        ConfigCommand.register(event.getDispatcher());
    }

//    @SubscribeEvent
//    public static void onPlayerCloneEvent(PlayerEvent.Clone event) {
//
//        if (!event.getOriginal().getLevel().isClientSide()) {
//
//            event.getPlayer().getPersistentData().putIntArray(AiMinecraft.MODID +"aipos",event.getOriginal().getPersistentData().getIntArray(AiMinecraft.MODID +"aipos"));
//        }
//    }
}