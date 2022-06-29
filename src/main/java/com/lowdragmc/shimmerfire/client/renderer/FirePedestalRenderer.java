package com.lowdragmc.shimmerfire.client.renderer;

import com.lowdragmc.shimmer.client.postprocessing.PostProcessing;
import com.lowdragmc.shimmer.client.shader.RenderUtils;
import com.lowdragmc.shimmerfire.ShimmerFireMod;
import com.lowdragmc.shimmerfire.blockentity.FirePedestalBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;

@OnlyIn(Dist.CLIENT)
public class FirePedestalRenderer extends GeoBlockRenderer<FirePedestalBlockEntity> {

   public FirePedestalRenderer(BlockEntityRendererProvider.Context pContext) {
      super(pContext, new AnimatedGeoModel<>() {
         public ResourceLocation getAnimationFileLocation(FirePedestalBlockEntity animatable) {
            return new ResourceLocation(ShimmerFireMod.MODID, "animations/fire_pedestal.animation.json");
         }

         public ResourceLocation getModelLocation(FirePedestalBlockEntity animatable) {
            return new ResourceLocation(ShimmerFireMod.MODID, "geo/fire_pedestal.geo.json");
         }

         public ResourceLocation getTextureLocation(FirePedestalBlockEntity entity) {
            return new ResourceLocation(ShimmerFireMod.MODID, "textures/blocks/fire_pedestal.png");
         }
      });
   }

   @Override
   public void renderRecursively(GeoBone bone, PoseStack stack, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
      if (bone.name.equals("bloom")) {
         PoseStack finalStack = RenderUtils.copyPoseStack(stack);
         PostProcessing.BLOOM_UNREAL.postEntity(bufferSource -> {
            super.renderRecursively(bone, finalStack,
                    bufferSource.getBuffer(RenderType.entityCutout(new ResourceLocation(ShimmerFireMod.MODID, "textures/blocks/fire_pedestal.png"))),
                    packedLightIn > 0 ? 0xf000f0 : packedLightIn, packedOverlayIn, red, green, blue, alpha);
         });

      } else {
         super.renderRecursively(bone, stack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
      }
   }
}