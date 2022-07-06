package com.lowdragmc.shimmerfire.client.particle;

import com.lowdragmc.lowdraglib.client.particle.TrailParticle;
import com.lowdragmc.lowdraglib.client.shader.Shaders;
import com.lowdragmc.lowdraglib.client.shader.management.Shader;
import com.lowdragmc.lowdraglib.client.shader.management.ShaderManager;
import com.lowdragmc.lowdraglib.utils.Vector3;
import com.lowdragmc.shimmer.client.postprocessing.PostProcessing;
import com.lowdragmc.shimmerfire.ShimmerFireMod;
import com.lowdragmc.shimmerfire.api.RawFire;
import com.lowdragmc.shimmerfire.utils.curve.CubicBezierCurve3;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Function;

/**
 * @author KilaBash
 * @date 2022/6/18
 * @implNote FireTailParticle
 */
@OnlyIn(Dist.CLIENT)
public class FireTailParticle extends TrailParticle {
    private final List<Vector3> points;
    private final RawFire color;
    private final FireSpiritParticle headParticle;
    private Entity entity;

    public FireTailParticle(ClientLevel level, BlockPos from, Direction fromFace, BlockPos to, Direction toFace, int dur, RawFire color) {
        this(level, new Vector3(from).add(0.5), new Vector3(to).add(0.5).add(toFace.getStepX() * -0.4, toFace.getStepY() * -0.4, toFace.getStepZ() * -0.4), fromFace, toFace, dur, color);
    }

    public FireTailParticle(ClientLevel level, Vector3 from, Vector3 destination, Direction fromFace, Direction toFace, int dur, RawFire color) {
        super(level, from.x, from.y, from.z + 0.5);
        this.color = color;
        setLifetime(dur + 15);
        setLight(0xf000f0);
        points = new CubicBezierCurve3(new Vector3(from),
                new Vector3(fromFace.getStepX(), fromFace.getStepY(), fromFace.getStepZ()).multiply(3).add(new Vector3(from)),
                new Vector3(toFace.getStepX(), toFace.getStepY(), toFace.getStepZ()).multiply(3).add(destination),
                destination).getPoints(dur);
        maxTail = 15;
        width = 0.3f;
        headParticle = new FireSpiritParticle(level, from.x, from.y, from.z);
        headParticle.setLifetime(dur);
        headParticle.scale(0.12f);
        headParticle.setGlint(true);
        headParticle.setColor((color.colorVale >> 16 & 0xff)/256f,(color.colorVale >> 8 & 0xff)/256f,(color.colorVale & 0xff)/256f);
    }

    public FireTailParticle setEntity(Entity entity) {
        this.entity = entity;
        return this;
    }

    @Override
    protected void update() {
        headParticle.tick();
        if (getAge() > 0 && getAge() < points.size()) {
            Vector3 point = points.get(age);
            setPos(point.x, point.y, point.z);
            headParticle.setPos(point.x, point.y, point.z);
        } else if (entity != null) {
            Vector3 point = new Vector3(entity.position()).add(0, entity.getBbHeight() / 2, 0);
            setPos(point.x, point.y, point.z);
            headParticle.setPos(point.x, point.y, point.z);
        }
        if (this.lifetime - this.age < 15) {
            tails.remove(0);
        }
    }

    @Override
    public void render(@NotNull VertexConsumer pBuffer, @NotNull Camera camera, float partialTicks) {
        int texture = RenderSystem.getShaderTexture(0);
        Tesselator tesselator = Tesselator.getInstance();
        var builder = tesselator.getBuilder();

        headParticle.getRenderType().begin(builder, null);
        headParticle.render(builder, camera, partialTicks);
        headParticle.getRenderType().end(tesselator);
        RenderSystem.setShaderTexture(0, texture);

        builder.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.PARTICLE);
        super.render(pBuffer, camera, partialTicks);
        tesselator.end();
    }

    @Override
    @Nonnull
    public ParticleRenderType getRenderType() {
        return TYPE.apply(color);
    }

    protected static final Function<RawFire, ParticleRenderType> TYPE = Util.memoize(color -> new ParticleRenderType(){
        @Override
        public void begin(@NotNull BufferBuilder bufferBuilder, @NotNull TextureManager textureManager) {
            RenderTarget target = ShaderManager.getInstance().renderFullImageInFramebuffer(ShaderManager.getTempTarget(), Shaders.load(Shader.ShaderType.FRAGMENT, new ResourceLocation(ShimmerFireMod.MODID, "fire_trail")), uniformCache -> {
                uniformCache.glUniform4F("color1", (color.colorVale >> 16 & 0xff)/256f,(color.colorVale >> 8 & 0xff)/256f,(color.colorVale & 0xff)/256f, 1);
                uniformCache.glUniform4F("color2", 1,1,1, 1);
            }, shaderProgram -> shaderProgram.bindTexture("iChannel0", new ResourceLocation("ldlib:textures/particle/kila_tail.png")));
            PostProcessing.BLOOM_UNREAL.getPostTarget().bindWrite(!ShaderManager.getInstance().hasViewPort());
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.depthMask(true);
            RenderSystem.setShader(Shaders::getParticleShader);
            RenderSystem.setShaderTexture(0, target.getColorTextureId());
            RenderSystem.enableCull();
        }

        @Override
        public void end(@Nonnull Tesselator tesselator) {
            RenderSystem.depthMask(true);
        }
    });

    @Override
    public void addParticle() {
        PostProcessing.BLOOM_UNREAL.postParticle(this);
    }
}
