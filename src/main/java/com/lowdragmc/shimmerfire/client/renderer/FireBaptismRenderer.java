package com.lowdragmc.shimmerfire.client.renderer;

import com.lowdragmc.multiblocked.client.renderer.impl.GeoComponentRenderer;
import com.lowdragmc.shimmer.client.postprocessing.PostProcessing;
import com.lowdragmc.shimmer.client.shader.RenderUtils;
import com.lowdragmc.shimmerfire.ShimmerFireMod;
import com.lowdragmc.shimmerfire.api.RawFire;
import com.lowdragmc.shimmerfire.blockentity.multiblocked.FireBaptismBlockEntity;
import com.lowdragmc.shimmerfire.client.RenderTypes;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * @author KilaBash
 * @date 2022/7/2
 * @implNote FireBaptismRenderer
 */
public class FireBaptismRenderer  extends GeoComponentRenderer {

    private static final ResourceLocation RUNE_TEXTURE = new ResourceLocation(ShimmerFireMod.MODID, "textures/rune_magic_cycle.png");

    public FireBaptismRenderer() {
        super("fire_baptism", false);
    }

    @Override
    public void renderItem(ItemStack stack, ItemTransforms.TransformType transformType, boolean leftHand, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay, BakedModel bakedModel) {
        bakedModel = net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(matrixStack, bakedModel, transformType, leftHand);
        matrixStack.translate(-0.5, 0, -0.5);
        super.renderItem(stack, transformType, leftHand, matrixStack, buffer, combinedLight, combinedOverlay, bakedModel);
    }

    public void render(BlockEntity te, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        super.render(te, partialTicks, poseStack, buffer, combinedLight, combinedOverlay);
        FireBaptismBlockEntity fireBaptism = (FireBaptismBlockEntity) te;
        RawFire fire = fireBaptism.getFireType();
        if (fireBaptism.isWorking() && fire != null) {
            float scale = 0.5f;
            poseStack.pushPose();
            poseStack.translate(0.5, 0, 0.5);
            poseStack.scale(scale, scale, scale);
            poseStack.translate(-0.5, 0, -0.5);

            PoseStack finalStack = RenderUtils.copyPoseStack(poseStack);
            poseStack.pushPose();
            poseStack.scale(1, -1, 1);
            poseStack.translate(0, -1 / 16f / scale, 0);
            PoseStack finalStack2 = RenderUtils.copyPoseStack(poseStack);

            PostProcessing.BLOOM_UNREAL.postEntity(bufferSource -> {
                VertexConsumer consumer = bufferSource.getBuffer(RenderType.cutoutMipped());
                for(BakedQuad bakedquad : FireCultureTankRenderer.getQuads(fire)) {
                    consumer.putBulkData(finalStack.last(), bakedquad,
                            new float[]{1, 1, 1, 1},
                            1, 1, 1,
                            new int[]{0XF000F0, 0XF000F0, 0XF000F0, 0XF000F0},
                            OverlayTexture.NO_OVERLAY, true);
                }
                for(BakedQuad bakedquad : FireCultureTankRenderer.getQuads(fire)) {
                    consumer.putBulkData(finalStack2.last(), bakedquad,
                            new float[]{1, 1, 1, 1},
                            1, 1, 1,
                            new int[]{0XF000F0, 0XF000F0, 0XF000F0, 0XF000F0},
                            OverlayTexture.NO_OVERLAY, true);
                }
            });
            poseStack.popPose();
            poseStack.popPose();


            float timer = partialTicks + fireBaptism.getLevel().dayTime();
            float rotate = (timer % 200) / 200 * 360;
            int dur = 0;
            BlockPos blockPos = fireBaptism.getBlockPos();
            while (dur <= 8) {
                dur++;
                blockPos = blockPos.below();
                if (!fireBaptism.getLevel().isEmptyBlock(blockPos)) break;
            }
            renderRune(poseStack, fireBaptism.getFireType().colorVale, rotate, 16, dur - 1.01f);
        }
    }

    private void renderRune(PoseStack stack, int color, float rotate, float scale, float dur) {
        stack.pushPose();
        stack.translate(0.5, 0.5, 0.5);
        stack.mulPose(Direction.DOWN.getRotation());
        stack.scale(scale, 1, scale);
        stack.translate(0.0, 0.5, 0.0);
        stack.mulPose(new Quaternion(new Vector3f(0, 1, 0), rotate, true));
        stack.translate(-0.5, 0.0, -0.5);
        stack.translate(0, dur, 0);

        PoseStack.Pose last = stack.last();
        Matrix4f matrix = last.pose().copy();
        Matrix3f normal = last.normal().copy();
        PostProcessing.BLOOM_UNREAL.postEntity(bufferSource -> {
            VertexConsumer builder = bufferSource.getBuffer(RenderTypes.emissiveCutoutNoCull(RUNE_TEXTURE));
            builder.vertex(matrix, 0.0F, 0.005F, 1.0F).color(color).uv(0.0F, 1.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xf000f0).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
            builder.vertex(matrix, 1.0F, 0.005F, 1.0F).color(color).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xf000f0).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
            builder.vertex(matrix, 1.0F, 0.005F, 0.0F).color(color).uv(1.0F, 0.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xf000f0).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
            builder.vertex(matrix, 0.0F, 0.005F, 0.0F).color(color).uv(1, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0xf000f0).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
        });
        stack.popPose();
    }

}
