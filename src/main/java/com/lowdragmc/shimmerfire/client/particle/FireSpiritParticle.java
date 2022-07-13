package com.lowdragmc.shimmerfire.client.particle;

import com.lowdragmc.lowdraglib.client.particle.LParticle;
import com.lowdragmc.lowdraglib.client.particle.impl.TextureParticle;
import com.lowdragmc.lowdraglib.client.shader.Shaders;
import com.lowdragmc.shimmerfire.CommonProxy;
import com.lowdragmc.shimmerfire.ShimmerFireMod;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author KilaBash
 * @date 2022/6/19
 * @implNote FireSpiritParticle
 */
@OnlyIn(Dist.CLIENT)
public class FireSpiritParticle extends TextureParticle {
    private static final ResourceLocation TEXTURE = new ResourceLocation(ShimmerFireMod.MODID, "textures/particle/fire_spirit.png");

    private boolean glint;
    private Function<FireSpiritParticle, Boolean> alive;

    public FireSpiritParticle(ClientLevel level, double x, double y, double z) {
        super(level, x, y, z);
        setTexture(TEXTURE);
        setMoveless(true);
        setLight(0xf000f0);
    }

    public void setAliveCondition(Function<FireSpiritParticle, Boolean> alive) {
        this.alive = alive;
    }

    public void setGlint(boolean glint) {
        this.glint = glint;
    }

    @NotNull
    @Override
    public ParticleRenderType getRenderType() {
        return TYPE;
    }

    @Override
    protected void update() {
        if (alive != null && !alive.apply(this)) {
            remove();
            return;
        }
        if (glint) {
            if (random.nextFloat() > 0.3) {
                SparkParticle particle = (SparkParticle) Minecraft.getInstance().particleEngine.createParticle(CommonProxy.FIRE_SPARK.get(),
                        x, y, z,
                        0, 0, 0);
                if (particle != null) {
                    particle.setGravity(0.15f);
                    particle.setSmoke(false);
                    particle.setColor(rCol, gCol, bCol);
                    particle.setParticleSpeed(random.nextFloat() * 0.1 - 0.05,
                            random.nextFloat() * 0.1 - 0.01,
                            random.nextFloat() * 0.1 - 0.05);
                    particle.scale(0.3f);
                }
            }
            if (this.age % 3 == 0) {
                if (quadSize == 0.12f) {
                    quadSize = 0.09f;
                } else {
                    quadSize = 0.12f;
                }
            }
        } else {
            if (this.age % 3 == 0) {
                if (alpha == 1f) {
                    setAlpha(0.9f);
                } else {
                    setAlpha(1f);
                }
            }
        }
    }

    @NotNull
    @Override
    public LParticle scale(float pScale) {
        quadSize = pScale;
        this.setSize(0.2F * pScale, 0.2F * pScale);
        return this;
    }

    protected static final ParticleRenderType TYPE = new ParticleRenderType() {
        @Override
        public void begin(@Nonnull BufferBuilder bufferBuilder, @Nonnull
        TextureManager textureManager) {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.depthMask(true);
            RenderSystem.setShader(GameRenderer::getParticleShader);
            RenderSystem.setShaderTexture(0, TEXTURE);
            RenderSystem.enableCull();
            bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        }

        @Override
        public void end(@Nonnull Tesselator tesselator) {
            tesselator.end();
            RenderSystem.depthMask(true);
        }
    };
}
