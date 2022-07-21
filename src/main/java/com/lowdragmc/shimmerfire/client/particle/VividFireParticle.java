package com.lowdragmc.shimmerfire.client.particle;

import com.lowdragmc.lowdraglib.client.particle.LParticle;
import com.lowdragmc.lowdraglib.client.shader.Shaders;
import com.lowdragmc.lowdraglib.client.shader.management.Shader;
import com.lowdragmc.lowdraglib.client.shader.management.ShaderManager;
import com.lowdragmc.shimmer.client.postprocessing.PostProcessing;
import com.lowdragmc.shimmer.core.mixins.ShimmerMixinPlugin;
import com.lowdragmc.shimmerfire.ShimmerFireMod;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import javax.annotation.Nonnull;
import java.util.Random;
import java.util.function.Function;

/**
 * @author KilaBash
 * @date 2022/7/13
 * @implNote VividFireParticle
 */
@OnlyIn(Dist.CLIENT)
public class VividFireParticle extends LParticle {
    private final int color;
    private float u0, v0, u1, v1;
    private double ox;
    private double oy;
    private double oz;
    private final boolean source;
    private float oQuadSize = 1;
    private Function<VividFireParticle, Boolean> alive;
    private float alpha, oAlpha;
    private boolean rollClock;
    private float size;

    public VividFireParticle(ClientLevel level, double x, double y, double z, int color, float size, boolean source) {
        super(level, x, y, z);
        this.source = source;
        this.color = color;
        setMoveless(true);
        this.ox = x;
        this.oy = y;
        this.oz = z;
        this.size = size;
        if (source) {
            setImmortal();
            return;
        }
        setFullLight();
        Random random = level.getRandom();
        setLifetime(random.nextInt(16, 32));
        float s = 0.1f;
        setParticleSpeed(random.nextFloat(-0.1f * s, 0.1f * s),
                random.nextFloat(0.2f * s, 0.5f * s),
                random.nextFloat(-0.1f * s, 0.1f * s));
        this.roll = random.nextFloat(-360, 360);
        this.oRoll = roll;
        this.u0 = 0 * random.nextFloat(0.6f, 2);
        this.u1 = 1 * random.nextFloat(0.6f, 2);
        this.v0 = 0 * random.nextFloat(0.6f, 2);
        this.v1 = 1 * random.nextFloat(0.6f, 2);
        oQuadSize = size;
        quadSize = size;
        alpha = 0;
        oAlpha = 0;
        this.rollClock = random.nextBoolean();
    }

    public void setPosition(Vec3 position) {
        this.ox = position.x;
        this.oy = position.y;
        this.oz = position.z;
    }

    public void setAliveCondition(Function<VividFireParticle, Boolean> alive) {
        this.alive = alive;
    }

    @Override
    protected void update() {
        if (source) {
            if (alive != null && !alive.apply(this)) {
                remove();
                return;
            }
            VividFireParticle particle = new VividFireParticle(level, ox, oy, oz, color, size, false);
            particle.addParticle();

            if (level.random.nextBoolean()) {
                particle = new VividFireParticle(level, ox, oy, oz, color, size, false);
                particle.addParticle();
            }

        } else {
            float time = getAge() * 1f / getLifetime();
            oQuadSize = quadSize;
            oAlpha = alpha;
            quadSize = (1 - time) * size;
            if (time > 0.2) {
                alpha = (1 - time);
            } else {
                alpha = time / 0.2f * 0.8f;
            }
//            if (time > 0.5) {
//                alpha = (2- 2 * time);
//            }
            this.x += this.xd;
            this.y += this.yd;
            this.z += this.zd;

            this.oRoll = roll;
            if (rollClock) {
                this.roll += Mth.HALF_PI / (2 * getLifetime());
            }
        }
    }

    @Override
    public void render(@NotNull VertexConsumer pBuffer, Camera pRenderInfo, float pPartialTicks) {
        setAlpha(Mth.lerp(pPartialTicks, this.oAlpha, this.alpha));
        super.render(pBuffer, pRenderInfo, pPartialTicks);
    }

    @Override
    public float getQuadSize(float pPartialTicks) {
        return Mth.lerp(pPartialTicks, this.oQuadSize, this.quadSize);
    }

    @Override
    protected float getU0(float pPartialTicks) {
        return u0;
    }

    @Override
    protected float getU1(float pPartialTicks) {
        return u1;
    }

    @Override
    protected float getV0(float pPartialTicks) {
        return v0;
    }

    @Override
    protected float getV1(float pPartialTicks) {
        return v1;
    }

    @Override
    @Nonnull
    public ParticleRenderType getRenderType() {
        return TYPE.apply(color);
    }

    protected static final Function<Integer, ParticleRenderType> TYPE = Util.memoize(color -> new ParticleRenderType(){
        @Override
        public void begin(@NotNull BufferBuilder bufferBuilder, @NotNull TextureManager textureManager) {
            RenderTarget mainTarget = Minecraft.getInstance().getMainRenderTarget();
            int lastID = GL11.glGetInteger(GL30.GL_FRAMEBUFFER_BINDING);

            ShaderManager.getTempTarget().clear(false);
            RenderTarget target = ShaderManager.getInstance().renderFullImageInFramebuffer(ShaderManager.getTempTarget(),
                    Shaders.load(Shader.ShaderType.FRAGMENT, new ResourceLocation(ShimmerFireMod.MODID, "vivid_fire")), uniformCache -> {
                uniformCache.glUniform4F("color", (color >> 16 & 0xff)/256f,(color >> 8 & 0xff)/256f,(color & 0xff)/256f, 1);
            }, shaderProgram -> shaderProgram.bindTexture("fire", new ResourceLocation("shimmerfire:textures/particle/fire_mask_2.png")));

            GlStateManager._glBindFramebuffer(36160, lastID);
            if (!ShaderManager.getInstance().hasViewPort()) {
                GlStateManager._viewport(0, 0, mainTarget.viewWidth, mainTarget.viewHeight);
            }

            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
//            RenderSystem.defaultBlendFunc();
            RenderSystem.depthMask(false);
            RenderSystem.setShader(Shaders::getParticleShader);
            RenderSystem.setShaderTexture(0, target.getColorTextureId());
            RenderSystem.enableCull();
            bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        }

        @Override
        public void end(@Nonnull Tesselator tesselator) {
            tesselator.end();
            RenderSystem.defaultBlendFunc();
            RenderSystem.depthMask(true);
        }
    });

    @Override
    public void addParticle() {
        PostProcessing.BLOOM_UNREAL.postParticle(this);
    }

    public void setSize(float size) {
        this.size = size;
    }
}
