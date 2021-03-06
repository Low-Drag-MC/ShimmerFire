package com.lowdragmc.shimmerfire.core.mixins.sideshow;

import com.lowdragmc.shimmer.client.postprocessing.PostProcessing;
import com.lowdragmc.shimmer.client.shader.RenderUtils;
import com.lowdragmc.shimmerfire.core.IShimmerEffectProjector;
import com.mojang.blaze3d.vertex.PoseStack;
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
import org.teacon.slides.renderer.SlideState;

/**
 * @author KilaBash
 * @date 2022/6/22
 * @implNote ProjectorScreenMixin
 */
@Mixin(ProjectorRenderer.class)
public abstract class ProjectorRendererMixin {

    @Inject(
            method = {"render(Lorg/teacon/slides/projector/ProjectorBlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V"},
            at = @At(value = "INVOKE", target = "Lorg/teacon/slides/renderer/Slide;render(Lnet/minecraft/client/renderer/MultiBufferSource;Lcom/mojang/math/Matrix4f;Lcom/mojang/math/Matrix3f;FFIIIZZJF)V"),
            locals = LocalCapture.CAPTURE_FAILHARD,
            remap = false,
            cancellable = true)
    private void injectRender(ProjectorBlockEntity tile, float partialTick, PoseStack pStack, MultiBufferSource source, int packedLight, int packedOverlay, CallbackInfo ci,
                              Slide slide,
                              int color,
                              PoseStack.Pose _last,
                              boolean flipped) {
        String effect = ((IShimmerEffectProjector)(Object)tile).getEffect();
        PostProcessing postProcessing = effect == null ? null : PostProcessing.getPost(effect);
        if (postProcessing != null) {
            PoseStack.Pose last = RenderUtils.copyPoseStack(pStack).last();

            postProcessing.postEntity(bufferSource -> slide.render(bufferSource, last.pose(), last.normal(), tile.mWidth, tile.mHeight, color, 15728880, OverlayTexture.NO_OVERLAY, flipped || tile.mDoubleSided, !flipped || tile.mDoubleSided, SlideState.getAnimationTick(), partialTick));
            pStack.popPose();
            ci.cancel();
        }
    }

}
