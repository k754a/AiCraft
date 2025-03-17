package net.kallens.Command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod(SummonAI.MOD_ID)
public class SummonAI {
    public static final String MOD_ID = "summonai";

    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MOD_ID);
    public static final RegistryObject<EntityType<AIPlayerEntity>> AI_PLAYER = ENTITIES.register("ai_player",
            () -> EntityType.Builder.of(AIPlayerEntity::new, MobCategory.MISC)
                    .sized(0.6F, 1.8F)
                    .build("ai_player"));

    public SummonAI(CommandDispatcher<CommandSourceStack> dispatcher) {
        FMLJavaModLoadingContext.get().getModEventBus().register(this);
        MinecraftForge.EVENT_BUS.register(this);
        ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    @SubscribeEvent
    public void setup(final FMLCommonSetupEvent event) {
        // Common setup code if needed
    }

    @SubscribeEvent
    public void clientSetup(final FMLClientSetupEvent event) {
        // Client setup code if needed
    }

    @SubscribeEvent
    public void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(AI_PLAYER.get(), AIPlayerRenderer::new);
    }

    @SubscribeEvent
    public void registerCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(
                Commands.literal("ai")
                        .then(Commands.literal("spawn")
                                .executes(context -> summon(context.getSource())))
        );
    }

    private static int summon(CommandSourceStack source) {
        try {
            ServerLevel world = source.getLevel();
            ServerPlayer serverPlayer = source.getPlayerOrException();
            AIPlayerEntity aiPlayer = new AIPlayerEntity(AI_PLAYER.get(), world);
            aiPlayer.setPos(serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ());
            aiPlayer.setCustomName(Component.literal("AI"));
            aiPlayer.setCustomNameVisible(true);
            aiPlayer.setPersistenceRequired(); // Optional: prevents despawning
            world.addFreshEntity(aiPlayer);
            source.sendSuccess(() -> Component.literal("AI player spawned successfully."), true);
            return 1;
        } catch (Exception e) {
            source.sendFailure(Component.literal("Failed to spawn AI player: " + e.getMessage()));
            e.printStackTrace();
            return 0;
        }
    }

    public static class AIPlayerEntity extends PathfinderMob {
        public AIPlayerEntity(EntityType<? extends PathfinderMob> type, Level level) {
            super(type, level);
        }

        @Override
        protected void registerGoals() {
            this.goalSelector.addGoal(0, new RandomStrollGoal(this, 1.0D));
        }


        public static AttributeSupplier.Builder createAttributes() {
            return PathfinderMob.createMobAttributes()
                    .add(Attributes.MAX_HEALTH, 20.0D)
                    .add(Attributes.MOVEMENT_SPEED, 0.3D);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class AIPlayerRenderer extends LivingEntityRenderer<AIPlayerEntity, PlayerModel<AIPlayerEntity>> {
        private static final ResourceLocation STEVE_SKIN = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/entity/steve.png");

        public AIPlayerRenderer(EntityRendererProvider.Context context) {
            super(context, new PlayerModel<>(context.bakeLayer(ModelLayers.PLAYER), false), 0.5F);
        }

        @Override
        public ResourceLocation getTextureLocation(AIPlayerEntity entity) {
            return STEVE_SKIN;
        }
    }
}