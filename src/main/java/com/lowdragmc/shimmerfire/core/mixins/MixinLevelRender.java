package com.lowdragmc.shimmerfire.core.mixins;

import com.lowdragmc.shimmerfire.client.ClientProxy;
import com.lowdragmc.shimmerfire.client.RenderTypes;
import com.lowdragmc.shimmerfire.client.renderer.MimicDissolveRender;
import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.*;
import org.lwjgl.opengl.GL43;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.teacon.nocaet.client.GarlicRenderTypes;

import javax.annotation.Nullable;

@Mixin(LevelRenderer.class)
public class MixinLevelRender {

    @Shadow @Nullable private ClientLevel level;

    @Inject(method = "renderLevel",
        at = @At(value = "INVOKE",ordinal = 1,target = "Lnet/minecraft/client/renderer/LevelRenderer;checkPoseStack(Lcom/mojang/blaze3d/vertex/PoseStack;)V"))
    private void a(PoseStack pPoseStack, float pPartialTick, long pFinishNanoTime, boolean pRenderBlockOutline, Camera pCamera, GameRenderer pGameRenderer, LightTexture pLightTexture, Matrix4f pProjectionMatrix, CallbackInfo ci){
        if (MimicDissolveRender.needUpload.get()) {
            GL43.glPushDebugGroup(GL43.GL_DEBUG_SOURCE_APPLICATION,0,"mimic_dissolve_render");
            RenderTypes.MimicDissolveRenderType.MIMIC_DISSOLVE.end(MimicDissolveRender.bufferBuilder,0,0,0);
            GL43.glPopDebugGroup();
            MimicDissolveRender.needUpload.set(false);
        }
    }

    @Inject(method = "renderChunkLayer",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setupShaderLights(Lnet/minecraft/client/renderer/ShaderInstance;)V"))
    private void b(RenderType pRenderType, PoseStack pPoseStack, double pCamX, double pCamY, double pCamZ, Matrix4f pProjectionMatrix, CallbackInfo ci){
        if (pRenderType == GarlicRenderTypes.GARLIC_CUTOUT) {
            ShaderInstance instance = RenderSystem.getShader();
            if (instance != null) {
                Uniform uniform = instance.getUniform("isBloom");
                if (uniform != null) {
                    uniform.set((ClientProxy.BLOOM_LEAVE && level != null && level.getSkyDarken(Minecraft.getInstance().getFrameTime()) < 0.25) ? 1f : 0f);
                }
            }
        }
    }

    @Redirect(method = "renderSky",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;getTimeOfDay(F)F"))
    private float b(ClientLevel level, float partialTicks){
        if (ClientProxy.isWearingGlasses()) {
            return 0.5f;
        }
        return level.getTimeOfDay(partialTicks);
    }
}
