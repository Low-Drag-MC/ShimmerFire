package com.lowdragmc.shimmerfire.core.mixins.sideshow;

import com.lowdragmc.shimmer.ShimmerMod;
import com.lowdragmc.shimmer.client.postprocessing.PostProcessing;
import com.lowdragmc.shimmer.client.shader.RenderUtils;
import com.lowdragmc.shimmerfire.core.IBloomProjector;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.teacon.slides.projector.ProjectorBlock;
import org.teacon.slides.projector.ProjectorBlockEntity;
import org.teacon.slides.renderer.ProjectorRenderer;
import org.teacon.slides.renderer.Slide;

/**
 * @author KilaBash
 * @date 2022/6/22
 * @implNote ProjectorScreenMixin
 */
@Mixin(ProjectorRenderer.class)
public abstract class ProjectorRendererMixin {

    @Inject(
            method = {"render(Lorg/teacon/slides/projector/ProjectorBlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V"},
            at = @At(value = "INVOKE", target = "Lorg/teacon/slides/renderer/Slide;render(Lnet/minecraft/client/renderer/MultiBufferSource;Lcom/mojang/math/Matrix4f;Lcom/mojang/math/Matrix3f;FFIIIZZ)V"),
            locals = LocalCapture.CAPTURE_FAILHARD,
            remap = false,
            cancellable = true)
    private void injectRender(ProjectorBlockEntity tile,
                              float partialTick,
                              PoseStack pStack,
                              MultiBufferSource source,
                              int packedLight,
                              int packedOverlay,
                              CallbackInfo ci,
                              Slide slide,
                              int color,
                              ProjectorBlock.InternalRotation rotation,
                              boolean flipped,
                              float width,
                              float height) {
        if (((IBloomProjector)(Object)tile).isBloom()) {
            PoseStack.Pose last = RenderUtils.copyPoseStack(pStack).last();
            PostProcessing.BLOOM_UNREAL.postEntity(bufferSource -> {
                slide.render(bufferSource, last.pose(), last.normal(), width, height, color, 15728880, OverlayTexture.NO_OVERLAY, flipped || tile.mDoubleSided, !flipped || tile.mDoubleSided);
            });
            pStack.popPose();
            ci.cancel();
        }
    }

}
