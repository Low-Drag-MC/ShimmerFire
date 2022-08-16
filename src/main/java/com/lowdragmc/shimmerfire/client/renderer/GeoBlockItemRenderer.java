package com.lowdragmc.shimmerfire.client.renderer;

import com.lowdragmc.shimmerfire.ShimmerFireMod;
import com.lowdragmc.shimmerfire.item.GeoBlockItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;

/**
 * @author KilaBash
 * @date 2022/6/27
 * @implNote GeoBlockItemRenderer
 */
@OnlyIn(Dist.CLIENT)
public class GeoBlockItemRenderer extends GeoItemRenderer<GeoBlockItem> {
    public static GeoBlockItemRenderer INSTANCE = new GeoBlockItemRenderer();

    public GeoBlockItemRenderer() {
        super(new AnimatedGeoModel<>() {
            public ResourceLocation getAnimationFileLocation(GeoBlockItem animatable) {
                return ShimmerFireMod.rl( "animations/%s.animation.json".formatted(animatable.getModelName()));
            }

            public ResourceLocation getModelLocation(GeoBlockItem animatable) {
                return ShimmerFireMod.rl( "geo/%s.geo.json".formatted(animatable.getModelName()));
            }

            public ResourceLocation getTextureLocation(GeoBlockItem animatable) {
                return ShimmerFireMod.rl( "textures/blocks/%s.png".formatted(animatable.getModelName()));
            }
        });
    }

}
