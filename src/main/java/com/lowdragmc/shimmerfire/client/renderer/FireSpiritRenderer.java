package com.lowdragmc.shimmerfire.client.renderer;

import com.lowdragmc.shimmer.client.light.ColorPointLight;
import com.lowdragmc.shimmerfire.client.model.FireSpiritModel;
import com.lowdragmc.shimmerfire.entity.FireSpirit;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author KilaBash
 * @date 2022/5/10
 * @implNote FireSpiritRenderer
 */
@OnlyIn(Dist.CLIENT)
public class FireSpiritRenderer extends MobRenderer<FireSpirit, FireSpiritModel> {
    private static final ResourceLocation BAT_LOCATION = new ResourceLocation("textures/entity/bat.png");

    public FireSpiritRenderer(EntityRendererProvider.Context context) {
        super(context, new FireSpiritModel(context.bakeLayer(ModelLayers.BAT)), 0.25F);
    }

    /**
     * Returns the location of an entity's texture.
     */
    public ResourceLocation getTextureLocation(FireSpirit pEntity) {
        return BAT_LOCATION;
    }

    protected void scale(FireSpirit pLivingEntity, PoseStack pMatrixStack, float pPartialTickTime) {
        pMatrixStack.scale(0.35F, 0.35F, 0.35F);
    }

    @Override
    public void render(FireSpirit entity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
        super.render(entity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
        ColorPointLight light = entity.getOrCreateLight();
        Vec3 pos = entity.getPosition(pPartialTicks);
        light.setPos((float) pos.x, (float) pos.y, (float) pos.z);
        light.update();
    }

    protected void setupRotations(FireSpirit pEntityLiving, PoseStack pMatrixStack, float pAgeInTicks, float pRotationYaw, float pPartialTicks) {
        if (pEntityLiving.isResting()) {
            pMatrixStack.translate(0.0D, -0.1F, 0.0D);
        } else {
            pMatrixStack.translate(0.0D, Mth.cos(pAgeInTicks * 0.3F) * 0.1F, 0.0D);
        }

        super.setupRotations(pEntityLiving, pMatrixStack, pAgeInTicks, pRotationYaw, pPartialTicks);
    }
}
