package com.lowdragmc.shimmerfire.client.renderer;

import com.lowdragmc.shimmer.client.postprocessing.PostProcessing;
import com.lowdragmc.shimmer.client.shader.RenderUtils;
import com.lowdragmc.shimmerfire.ShimmerFireMod;
import com.lowdragmc.shimmerfire.blockentity.FirePedestalBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;

import java.util.Random;

@OnlyIn(Dist.CLIENT)
public class FirePedestalRenderer extends GeoBlockRenderer<FirePedestalBlockEntity> {

   private final Random random = new Random();
   private final ItemRenderer itemRenderer;

   public FirePedestalRenderer(BlockEntityRendererProvider.Context pContext) {
      super(pContext, new AnimatedGeoModel<>() {
         public ResourceLocation getAnimationFileLocation(FirePedestalBlockEntity animatable) {
            return ShimmerFireMod.rl( "animations/fire_pedestal.animation.json");
         }

         public ResourceLocation getModelLocation(FirePedestalBlockEntity animatable) {
            return ShimmerFireMod.rl( "geo/fire_pedestal.geo.json");
         }

         public ResourceLocation getTextureLocation(FirePedestalBlockEntity entity) {
            return ShimmerFireMod.rl( "textures/blocks/fire_pedestal.png");
         }
      });
      itemRenderer = Minecraft.getInstance().getItemRenderer();
   }

   @Override
   public void render(FirePedestalBlockEntity tile, float partialTicks, PoseStack stack, MultiBufferSource bufferIn, int packedLightIn) {
      super.render(tile, partialTicks, stack, bufferIn, packedLightIn);
      ItemStack itemStack = tile.getItemStack();
      if (!itemStack.isEmpty()) {
         stack.pushPose();
         int i = itemStack.isEmpty() ? 187 : Item.getId(itemStack.getItem()) + itemStack.getDamageValue();
         this.random.setSeed(i);
         BakedModel bakedmodel = this.itemRenderer.getModel(itemStack, tile.getLevel(), null, i);
         boolean flag = bakedmodel.isGui3d();
         float yOffset = Mth.sin((tile.getLevel().dayTime() + partialTicks) / 10.0F) * 0.1F + 1.3F;
         stack.translate(0.5D, yOffset, 0.5D);
         float rotation = (tile.getLevel().dayTime() + partialTicks) * Mth.TWO_PI / 80;
         stack.mulPose(Vector3f.YP.rotation(rotation));
         stack.pushPose();
         this.itemRenderer.render(itemStack, ItemTransforms.TransformType.GROUND, false, stack, bufferIn, packedLightIn, OverlayTexture.NO_OVERLAY, bakedmodel);
         stack.popPose();
         if (!flag) {
            stack.translate(0.0, 0.0, 0.09375F);
         }
         stack.popPose();
      }
   }

   @Override
   public void renderRecursively(GeoBone bone, PoseStack stack, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
      if (bone.name.equals("bloom")) {
         PoseStack finalStack = RenderUtils.copyPoseStack(stack);
         PostProcessing.BLOOM_UNREAL.postEntity(bufferSource -> {
            super.renderRecursively(bone, finalStack,
                    bufferSource.getBuffer(RenderType.entityCutout(ShimmerFireMod.rl( "textures/blocks/fire_pedestal.png"))),
                    packedLightIn > 0 ? 0xf000f0 : packedLightIn, packedOverlayIn, red, green, blue, alpha);
         });

      } else {
         super.renderRecursively(bone, stack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
      }
   }
}