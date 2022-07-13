package com.lowdragmc.shimmerfire.core.mixins;


import com.lowdragmc.shimmerfire.client.RenderTypes;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.opengl.GL20;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.ByteBuffer;

@Mixin(BufferUploader.class)
public class MixinBufferUploader {
    @Inject(method = "_end",
        at = @At(value = "INVOKE",shift = At.Shift.AFTER,target = "Lnet/minecraft/client/renderer/ShaderInstance;apply()V"))
    private static void trySetCameraPosUniform(ByteBuffer pBuffer, VertexFormat.Mode pMode, VertexFormat pFormat, int pVertexCount, VertexFormat.IndexType pIndexType, int pIndexCount, boolean pSequentialIndex, CallbackInfo ci){
        ShaderInstance shaderInstance = RenderSystem.getShader();
        if (shaderInstance == RenderTypes.MimicDissolveRenderType.SOLID_MIMIC_DISSOLVE_SHADER){
            Vec3 camera = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
            GL20.glUniform3f(GL20.glGetUniformLocation(shaderInstance.getId(),"camPos"),
                    (float) camera.x, (float) camera.y, (float) camera.z);
        }
    }

}
