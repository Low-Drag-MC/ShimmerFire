package com.lowdragmc.shimmerfire.item;

import com.lowdragmc.shimmer.client.postprocessing.PostProcessing;
import com.lowdragmc.shimmer.client.shader.RenderUtils;
import com.lowdragmc.shimmerfire.CommonProxy;
import com.lowdragmc.shimmerfire.ForgeCommonEventListener;
import com.lowdragmc.shimmerfire.ShimmerFireMod;
import com.lowdragmc.shimmerfire.core.IBloomParticle;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.IItemRenderProperties;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import software.bernie.geckolib3.core.AnimationState;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.Animation;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.network.GeckoLibNetwork;
import software.bernie.geckolib3.network.ISyncable;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;
import software.bernie.geckolib3.util.GeckoLibUtil;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class LighterSword extends SwordItem implements IAnimatable, ISyncable {

    public final AnimationFactory factory = new AnimationFactory(this);
    public static String name = "lighter_sword";
    private static final String controllerName = name + "_controller";
    private static final String animationBasic = "basic";
    private static final String animationInitial = "initial";
    private static final String animationFinal = "final";
    private static final String animationFirstperson = "FP_view";

    @SubscribeEvent
    public static void attachFire(LivingHurtEvent event) {
        Entity entity = event.getSource().getEntity();
        if (entity instanceof Player player) {
            if (!player.getItemInHand(InteractionHand.MAIN_HAND).is(CommonProxy.LIGHTER_SWORD.get())){
                return;
            }
            Entity target = event.getEntity();
            if (target instanceof Player s && s.isCreative()){
                return;
            }
            if (!target.fireImmune()) {
                target.setSecondsOnFire(5);
                Level level = target.level;
                if (!level.isClientSide && level instanceof ServerLevel serverLevel){
                    Vec3 position = target.position();
                    List<ServerPlayer> players = serverLevel.players();
                    var packet = new ClientboundLevelParticlesPacket(ParticleTypes.LAVA, false,
                            position.x,position.y,position.z,
                            0.2f,0.0f,0.2f,0.2f,30);
                    if (packet instanceof IBloomParticle bloomParticle) {
                        bloomParticle.setBloom();
                    }
                    for (var receiver : players){
                        ForgeCommonEventListener.sendBloomParticles(serverLevel,
                                receiver,false,
                                position.x,position.y,position.z,packet);
                    }
                }
            }
        }
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController(this, controllerName, 5, this::predicate));
    }

    private <P extends Item & IAnimatable> PlayState predicate(AnimationEvent<P> event) {
        AnimationController<?> controller = event.getController();
        Animation currentAnimation = controller.getCurrentAnimation();
        if (currentAnimation != null) {
            String animationName = currentAnimation.animationName;
            if (Objects.equals(animationName, animationBasic) || Objects.equals(animationName, animationFirstperson)) {
                double progress = (event.animationTick - controller.tickOffset) / currentAnimation.animationLength;
                if (progress >= 1) {
                    controller.setAnimation(new AnimationBuilder().addAnimation(animationFinal, false));
                }
            }
        }
        return PlayState.CONTINUE;
    }

    public LighterSword() {
        super(Tiers.DIAMOND,3,-2.4f,new Properties().tab(CommonProxy.TAB_ITEMS).stacksTo(1));
        GeckoLibNetwork.registerSyncable(this);
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    private static final int beginBit = 0x01;
    private static final int selfBit = 0x01 << 1;

    private static boolean isBegin(int bit) {
        return (bit & beginBit) != 0;
    }

    private static boolean isSelf(int bit) {
        return (bit & selfBit) != 0;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        if (!pLevel.isClientSide ) {
            if (pUsedHand == InteractionHand.OFF_HAND || !pPlayer.getItemInHand(InteractionHand.OFF_HAND).isEmpty())
                return super.use(pLevel, pPlayer, pUsedHand);
            ItemStack stack = pPlayer.getItemInHand(pUsedHand);
            int id = GeckoLibUtil.guaranteeIDForStack(stack, (ServerLevel) pLevel);
            var nearBy = PacketDistributor.TRACKING_ENTITY.with(() -> pPlayer);
            GeckoLibNetwork.syncAnimation(nearBy, this, id, beginBit);
            var self = PacketDistributor.PLAYER.with(() -> (ServerPlayer) pPlayer);
            GeckoLibNetwork.syncAnimation(self, this, id, beginBit | selfBit);
        }
        return super.use(pLevel, pPlayer, pUsedHand);
    }

    @Override
    public void onAnimationSync(int id, int state) {
        if (isBegin(state)) {
            var controller = GeckoLibUtil.getControllerForID(this.factory, id, controllerName);
            Animation currentAnimation = controller.getCurrentAnimation();
            boolean animationNull = currentAnimation != null;

            if (controller.getAnimationState() == AnimationState.Stopped || (animationNull && Objects.equals(currentAnimation.animationName, animationInitial))) {
                controller.markNeedsReload();
                if (isSelf(state)) {
                    if (Minecraft.getInstance().options.getCameraType() == CameraType.FIRST_PERSON) {
                        controller.setAnimation(new AnimationBuilder().addAnimation(animationFirstperson, false));
                        return;
                    }
                }
                controller.setAnimation(new AnimationBuilder().addAnimation(animationBasic, false));
                return;
            }

            if (animationNull && Objects.equals(currentAnimation.animationName, animationFinal)) {
                controller.setAnimation(new AnimationBuilder().addAnimation(animationInitial, false));
            }
        }
    }

    public static final ResourceLocation textureLocation = ShimmerFireMod.rl( "textures/items/" + name + ".png");


    public static class FlameSwordModel extends AnimatedGeoModel<LighterSword> {

        @Override
        public ResourceLocation getAnimationFileLocation(LighterSword animatable) {
            return ShimmerFireMod.rl( "animations/" + name + ".animation.json");
        }

        @Override
        public ResourceLocation getModelLocation(LighterSword animatable) {
            return ShimmerFireMod.rl( "geo/" + name + ".geo.json");
        }

        @Override
        public ResourceLocation getTextureLocation(LighterSword entity) {
            return textureLocation;
        }
    }

    public static class FlameSwordItemRender extends GeoItemRenderer<LighterSword> {

        public FlameSwordItemRender() {
            super(new FlameSwordModel());
        }

        @Override
        public void renderByItem(ItemStack itemStack, ItemTransforms.TransformType transformType, PoseStack matrixStack, MultiBufferSource bufferIn, int combinedLightIn, int p_239207_6_) {
            if (transformType != ItemTransforms.TransformType.GUI) {
                combinedLightIn |= 0x01;
            }
            super.renderByItem(itemStack, transformType, matrixStack, bufferIn, combinedLightIn, p_239207_6_);
        }


        @Override
        public void renderRecursively(GeoBone bone, PoseStack stack, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
            if (bone.name.equals("flame") && (packedLightIn & 0x01) != 0) {
                PoseStack finalStack = RenderUtils.copyPoseStack(stack);
                PostProcessing.BLOOM_UNREAL.postEntity((bufferSource -> {
                    VertexConsumer buffer = bufferSource.getBuffer(RenderType.entityCutout(textureLocation));
                    super.renderRecursively(bone, finalStack, buffer, packedLightIn > 0 ? 0xf000f0 : packedLightIn, packedOverlayIn, red, green, blue, alpha);
                }));
            } else {
                super.renderRecursively(bone, stack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            }
        }

    }


    @Override
    public void initializeClient(Consumer<IItemRenderProperties> consumer) {
        super.initializeClient(consumer);
        consumer.accept(new IItemRenderProperties() {
            private final FlameSwordItemRender renderer = new FlameSwordItemRender();

            @Override
            public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
                return renderer;
            }
        });
    }
}