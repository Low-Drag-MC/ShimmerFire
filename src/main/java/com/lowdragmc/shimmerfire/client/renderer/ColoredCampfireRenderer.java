package com.lowdragmc.shimmerfire.client.renderer;

import com.lowdragmc.shimmer.client.postprocessing.PostProcessing;
import com.lowdragmc.shimmer.client.shader.RenderUtils;
import com.lowdragmc.shimmerfire.CommonProxy;
import com.lowdragmc.shimmerfire.blockentity.ColoredCampfireBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.EmptyModelData;

import javax.annotation.ParametersAreNonnullByDefault;

@OnlyIn(Dist.CLIENT)
public class ColoredCampfireRenderer implements BlockEntityRenderer<ColoredCampfireBlockEntity> {
   private final BlockEntityRendererProvider.Context context;
   public ColoredCampfireRenderer(BlockEntityRendererProvider.Context pContext) {
      context = pContext;
   }

   @ParametersAreNonnullByDefault
   public void render(ColoredCampfireBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
      Direction direction = pBlockEntity.getBlockState().getValue(CampfireBlock.FACING);
      NonNullList<ItemStack> cooked = pBlockEntity.getItems();
      int i = (int)pBlockEntity.getBlockPos().asLong();

      for(int j = 0; j < cooked.size(); ++j) {
         ItemStack itemstack = cooked.get(j);
         if (itemstack != ItemStack.EMPTY) {
            pPoseStack.pushPose();
            pPoseStack.translate(0.5D, 0.44921875D, 0.5D);
            Direction direction1 = Direction.from2DDataValue((j + direction.get2DDataValue()) % 4);
            float f = -direction1.toYRot();
            pPoseStack.mulPose(Vector3f.YP.rotationDegrees(f));
            pPoseStack.mulPose(Vector3f.XP.rotationDegrees(90.0F));
            pPoseStack.translate(-0.3125D, -0.3125D, 0.0D);
            pPoseStack.scale(0.375F, 0.375F, 0.375F);
            Minecraft.getInstance().getItemRenderer().renderStatic(itemstack, ItemTransforms.TransformType.FIXED, pPackedLight, pPackedOverlay, pPoseStack, pBufferSource, i + j);
            pPoseStack.popPose();
         }
      }

      pPoseStack.pushPose();
      pPoseStack.scale(0.6f,0.8f,0.6f);
      pPoseStack.translate(.3f,0.4,.3f);

      PoseStack finalStack = RenderUtils.copyPoseStack(pPoseStack);
      pPoseStack.popPose();
      PostProcessing.WARP.postEntity(multiBufferSource -> {
         BlockRenderDispatcher brd = Minecraft.getInstance().getBlockRenderer();
         brd.renderSingleBlock(CommonProxy.FIRE_BLOCK.get().defaultBlockState(), finalStack, multiBufferSource, pPackedLight, pPackedOverlay, EmptyModelData.INSTANCE);
      });


   }
}