package net.kallens.items.custom;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraftforge.unsafe.UnsafeFieldAccess;

import static net.kallens.Command.SummonAI.settings;


public class settingsItem extends Item {
    public settingsItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext){
        if(!pContext.getLevel().isClientSide()){
            settings();
        }

        return InteractionResult.SUCCESS;

    }
}
