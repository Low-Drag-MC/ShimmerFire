package com.lowdragmc.shimmerfire.client.renderer;

import com.lowdragmc.multiblocked.client.renderer.impl.GeoComponentRenderer;
import com.lowdragmc.shimmer.client.postprocessing.PostProcessing;
import com.lowdragmc.shimmerfire.ShimmerFireMod;
import com.lowdragmc.shimmerfire.api.RawFire;
import com.lowdragmc.shimmerfire.blockentity.multiblocked.HexGateBlockEntity;
import com.lowdragmc.shimmerfire.client.RenderTypes;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * @author KilaBash
 * @date 2022/6/23
 * @implNote HexGateRenderer
 */
public class HexGateRenderer extends GeoComponentRenderer {

    private static final ResourceLocation RUNE_TEXTURE = new ResourceLocation(ShimmerFireMod.MODID, "textures/rune_magic_cycle.png");

    public HexGateRenderer() {
        super("hex_gate", false);
    }

    @Override
    public void render(BlockEntity te, float partialTicks, PoseStack stack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        stack.pushPose();
        stack.translate(0.5, 0.5, 0.5);
        stack.scale(3,3,3);
        stack.translate(-0.5, -0.5, -0.5);
        stack.translate(0,0.5,0);

        super.render(te, partialTicks, stack, buffer, combinedLight, combinedOverlay);
        stack.popPose();
        if (te instanceof HexGateBlockEntity hexGate) {
            Direction front = hexGate.getFrontFacing();
            float timer = partialTicks + hexGate.getLevel().dayTime();


            if (hexGate.isPreWorking()) {
                float rotate = (timer % 200) / 200 * 360;
                renderRune(stack, front, rotate, 6, 3);
            } else if (hexGate.isWorking()) {
                float rotate = (timer % 20) / 20 * 360;
                renderRune(stack, front, rotate, 6, 3);
                if (hexGate.workingStage >= 10) {
                    renderRune(stack, front, -rotate + 10, 3, 6);
                }
                if (hexGate.workingStage >= 20) {
                    renderRune(stack, front, rotate + 30, 2f, 7);
                }
                if (hexGate.workingStage >= 30) {
                    renderRune(stack, front, -rotate + 50, 2f, 8);
                }
                if (hexGate.workingStage >= 40) {
                    renderRune(stack, front, rotate + 70, 1.5f, 9);
                }
                if (hexGate.workingStage >= 50) {
                    renderRune(stack, front, -rotate + 90, 1.5f, 10);
                }
            } else if (hexGate.isPostWorking()) {
                float rotate = (timer % 200) / 200 * 360;
                renderRune(stack, front, rotate, 6, 3);
                renderRune(stack, front, -rotate + 10, 3, 6);
                renderRune(stack, front, rotate + 30, 2f, 7);
                renderRune(stack, front, -rotate + 50, 2f, 8);
                renderRune(stack, front, rotate + 70, 1.5f, 9);
                renderRune(stack, front, -rotate + 90, 1.5f, 10);
            }
        }


    }

    private void renderRune(PoseStack stack, Direction front, float rotate, float scale, float dur) {
        stack.pushPose();
        stack.translate(0.5, 0.5, 0.5);
        stack.mulPose(front.getRotation());
        stack.scale(scale, 1, scale);
        stack.translate(0.0, 0.5, 0.0);
        stack.mulPose(new Quaternion(new Vector3f(0, 1, 0), rotate, true));
        stack.translate(-0.5, 0.0, -0.5);
        stack.translate(0, dur, 0);

        int color = RawFire.ARCANE.colorVale;

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
