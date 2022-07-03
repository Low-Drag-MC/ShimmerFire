package com.lowdragmc.shimmerfire.client.renderer;

import com.lowdragmc.multiblocked.client.renderer.impl.GeoComponentRenderer;
import com.lowdragmc.shimmerfire.ShimmerFireMod;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * @author KilaBash
 * @date 2022/7/2
 * @implNote FireBaptismRenderer
 */
public class FireBaptismRenderer  extends GeoComponentRenderer {

    private static final ResourceLocation RUNE_TEXTURE = new ResourceLocation(ShimmerFireMod.MODID, "textures/rune_magic_cycle.png");

    public FireBaptismRenderer() {
        super("fire_baptism", false);
    }

    public void render(BlockEntity te, float partialTicks, PoseStack stack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        super.render(te, partialTicks, stack, buffer, combinedLight, combinedOverlay);
    }
}
