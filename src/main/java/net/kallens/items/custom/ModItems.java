package net.kallens.items.custom;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static net.kallens.aiminecraft.AiMinecraft.MODID;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    public static final RegistryObject<Item> SETTING = ITEMS.register("setting", ()-> new settingsItem(new Item.Properties().fireResistant().durability(-1)));


            public static void register(IEventBus eventBus) {ITEMS.register(eventBus); }
}
