package com.lowdragmc.shimmerfire.client.renderer;

import com.lowdragmc.shimmer.client.light.ColorPointLight;
import com.lowdragmc.shimmerfire.client.particle.VividFireParticle;
import com.lowdragmc.shimmerfire.entity.FireSpiritEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * @author KilaBash
 * @date 2022/5/10
 * @implNote FireSpiritRenderer
 */
@OnlyIn(Dist.CLIENT)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class FireSpiritRenderer extends EntityRenderer<FireSpiritEntity> {

    public FireSpiritRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    /**
     * Returns the location of an entity's texture.
     */
    public ResourceLocation getTextureLocation(FireSpiritEntity pEntity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }

    @Override
    public void render(FireSpiritEntity entity, float pEntityYaw, float partialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
        ColorPointLight light = entity.getOrCreateLight();
        VividFireParticle fire = entity.getOrCreateFire();
        Vec3 pos = entity.getPosition(partialTicks);

        if (light != null && !light.isRemoved()) {
            light.setPos((float) pos.x, (float) pos.y, (float) pos.z);
            light.update();
        }

        if (fire != null && fire.isAlive()) {
            fire.setPosition(pos);
        }
    }

}
