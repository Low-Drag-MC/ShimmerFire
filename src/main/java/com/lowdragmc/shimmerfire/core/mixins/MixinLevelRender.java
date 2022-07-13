package com.lowdragmc.shimmerfire.core.mixins;

import com.lowdragmc.shimmerfire.client.RenderTypes;
import com.lowdragmc.shimmerfire.client.renderer.MimicDissolveRender;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import org.lwjgl.opengl.GL43;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.logging.Level;

@Mixin(LevelRenderer.class)
public class MixinLevelRender {

    @Inject(method = "renderLevel",
        at = @At(value = "INVOKE",ordinal = 1,target = "Lnet/minecraft/client/renderer/LevelRenderer;checkPoseStack(Lcom/mojang/blaze3d/vertex/PoseStack;)V"))
    private void a(PoseStack pPoseStack, float pPartialTick, long pFinishNanoTime, boolean pRenderBlockOutline, Camera pCamera, GameRenderer pGameRenderer, LightTexture pLightTexture, Matrix4f pProjectionMatrix, CallbackInfo ci){
        if (MimicDissolveRender.needUpload) {
            GL43.glPushDebugGroup(GL43.GL_DEBUG_SOURCE_APPLICATION,0,"mimic_dissolve_render");
            RenderTypes.MimicDissolveRenderType.MIMIC_DISSOLVE.end(MimicDissolveRender.bufferBuilder,0,0,0);
            GL43.glPopDebugGroup();
            MimicDissolveRender.needUpload = false;
        }
    }
}
