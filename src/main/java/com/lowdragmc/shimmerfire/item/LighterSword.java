package com.lowdragmc.shimmerfire.item;

import com.lowdragmc.shimmer.client.postprocessing.PostProcessing;
import com.lowdragmc.shimmer.client.shader.RenderUtils;
import com.lowdragmc.shimmerfire.CommonProxy;
import com.lowdragmc.shimmerfire.ShimmerFireMod;
import com.lowdragmc.shimmerfire.client.IBufferBuilder;
import com.lowdragmc.shimmerfire.client.RenderTypes;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.IItemRenderProperties;
import net.minecraftforge.network.PacketDistributor;
import software.bernie.geckolib3.core.AnimationState;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoCube;
import software.bernie.geckolib3.geo.render.built.GeoQuad;
import software.bernie.geckolib3.geo.render.built.GeoVertex;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.network.GeckoLibNetwork;
import software.bernie.geckolib3.network.ISyncable;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;
import software.bernie.geckolib3.util.GeckoLibUtil;

import java.util.function.Consumer;

public class LighterSword extends Item implements IAnimatable, ISyncable {

    public final AnimationFactory factory = new AnimationFactory(this);
    private static String name = "lighter_sword";
    private static final String controllerName = name + "_controller";

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController(this, controllerName, 5, this::predicate));
    }

    private <P extends Item & IAnimatable> PlayState predicate(AnimationEvent<P> event) {
        return PlayState.CONTINUE;
    }

    public LighterSword() {
        super(new Properties().tab(CommonProxy.TAB_ITEMS).stacksTo(1));
        setRegistryName(ShimmerFireMod.MODID + ":" + name);
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

    private static final boolean isSelf(int bit) {
        return (bit & selfBit) != 0;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        if (!pLevel.isClientSide) {
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
            if (controller.getAnimationState() == AnimationState.Stopped) {
                controller.markNeedsReload();
                if (isSelf(state)) {
                    if (Minecraft.getInstance().options.getCameraType() == CameraType.FIRST_PERSON) {
                        controller.setAnimation(new AnimationBuilder().addAnimation("FP_view", false));
                        return;
                    }
                }
                controller.setAnimation(new AnimationBuilder().addAnimation("basic", false));
            }
        }
    }

    public static final ResourceLocation textureLocation = new ResourceLocation(ShimmerFireMod.MODID, "textures/items/" + name + ".png");


    public static class FlameSwordModel extends AnimatedGeoModel<LighterSword> {

        @Override
        public ResourceLocation getAnimationFileLocation(LighterSword animatable) {
            return new ResourceLocation(ShimmerFireMod.MODID, "animations/" + name + ".animation.json");
        }

        @Override
        public ResourceLocation getModelLocation(LighterSword animatable) {
            return new ResourceLocation(ShimmerFireMod.MODID, "geo/" + name + ".geo.json");
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

        private static IBufferBuilder bufferBuilder = new IBufferBuilder(1024 * 20);

        @Override
        public void renderRecursively(GeoBone bone, PoseStack stack, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
            if (bone.name.equals("flame") && (packedLightIn & 0x01) != 0) {
                PoseStack finalStack = RenderUtils.copyPoseStack(stack);
                PostProcessing.BLOOM_UNREAL.postEntity((bufferSource -> {
                    VertexConsumer buffer = bufferSource.getBuffer(RenderType.entityCutout(textureLocation));
                    super.renderRecursively(bone, finalStack, buffer, packedLightIn > 0 ? 0xf000f0 : packedLightIn, packedOverlayIn, red, green, blue, alpha);
                }));
                // dissolve animation part
//                var renderType = RenderTypes.MimicDissolveRenderType.MIMIC_DISSOLVE_ITEM;
//                bufferBuilder.begin(renderType.mode(), renderType.format());
//                super.renderRecursively(bone, stack, bufferBuilder, packedLightIn, packedOverlayIn, red, green, blue, alpha);
//                RenderTypes.MimicDissolveRenderType.MIMIC_DISSOLVE_ITEM.end(bufferBuilder, 0, 0, 0);
            } else {
                super.renderRecursively(bone, stack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            }
        }

        // way one , renderRecursively will call renderCube , detect flame part by instanceof
//        @Override
//        public void renderCube(GeoCube cube, PoseStack stack, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
//            software.bernie.geckolib3.util.RenderUtils.moveToPivot(cube, stack);
//            software.bernie.geckolib3.util.RenderUtils.rotate(cube, stack);
//            RenderUtils.moveBackFromPivot(cube, stack);
//            Matrix3f matrix3f = stack.last().normal();
//            Matrix4f matrix4f = stack.last().pose();
//
//            for (GeoQuad quad : cube.quads) {
//                if (quad == null) {
//                    continue;
//                }
//                Vector3f normal = quad.normal.copy();
//                normal.transform(matrix3f);
//
//                /*
//                 * Fix shading dark shading for flat cubes + compatibility wish Optifine shaders
//                 */
//                if ((cube.size.y() == 0 || cube.size.z() == 0) && normal.x() < 0) {
//                    normal.mul(-1, 1, 1);
//                }
//                if ((cube.size.x() == 0 || cube.size.z() == 0) && normal.y() < 0) {
//                    normal.mul(1, -1, 1);
//                }
//                if ((cube.size.x() == 0 || cube.size.y() == 0) && normal.z() < 0) {
//                    normal.mul(1, 1, -1);
//                }
//
//                for (GeoVertex vertex : quad.vertices) {
//                    Vector4f vector4f = new Vector4f(vertex.position.x(), vertex.position.y(), vertex.position.z(), 1.0F);
//                    vector4f.transform(matrix4f);
////                    if (bufferIn instanceof IBufferBuilder ibufferBuilder) {
////                        ibufferBuilder.vertex(vector4f.x(), vector4f.y(), vector4f.z());
////                        ibufferBuilder.samplerVertex(vertex.position.x(), vertex.position.y(), vertex.position.z());
////                        ibufferBuilder.color(red, green, blue, alpha);
////                        ibufferBuilder.uv(vertex.textureU, vertex.textureV);
////                        ibufferBuilder.uv2(packedLightIn);
////                        ibufferBuilder.normal(normal.x(),normal.y(),normal.z());
////                        ibufferBuilder.globalProgress();
////                        ibufferBuilder.endVertex();
////                    } else {
//                    bufferIn.vertex(vector4f.x(), vector4f.y(), vector4f.z(), red, green, blue, alpha, vertex.textureU,
//                            vertex.textureV, packedOverlayIn, packedLightIn, normal.x(), normal.y(), normal.z());
////                    }
//                }
//            }
//        }

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