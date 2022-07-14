package com.lowdragmc.shimmerfire.client.particle;

import com.lowdragmc.lowdraglib.client.particle.impl.TextureBeamParticle;
import com.lowdragmc.lowdraglib.utils.Vector3;
import com.lowdragmc.shimmerfire.ShimmerFireMod;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.function.Function;


/**
 * @author KilaBash
 * @date 2022/6/25
 * @implNote BeamParticle
 */
public class ColouredBeamParticle extends TextureBeamParticle {

    public ColouredBeamParticle(ClientLevel level, Vector3 from, Vector3 end) {
        super(level, from, end);
        setTexture(new ResourceLocation(ShimmerFireMod.MODID, "textures/blocks/white.png"));
        setLifetime(50);
        setLight(0xf000f0);
    }

    protected static final Function<ResourceLocation, ParticleRenderType> TYPE = Util.memoize((texture) -> new ParticleRenderType() {
        @Override
        public void begin(@Nonnull BufferBuilder bufferBuilder, @Nonnull
        TextureManager textureManager) {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.depthMask(true);
            RenderSystem.setShader(GameRenderer::getParticleShader);
            RenderSystem.setShaderTexture(0, texture);
            RenderSystem.enableCull();
            bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        }

        @Override
        public void end(@Nonnull Tesselator tesselator) {
            tesselator.end();
        }
    });

    @Override
    @Nonnull
    public ParticleRenderType getRenderType() {
        return TYPE.apply(texture);
    }
}
