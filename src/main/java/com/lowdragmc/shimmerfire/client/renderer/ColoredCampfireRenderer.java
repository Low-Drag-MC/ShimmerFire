package com.lowdragmc.shimmerfire.client.renderer;

import com.lowdragmc.shimmer.client.postprocessing.PostProcessing;
import com.lowdragmc.shimmer.client.shader.RenderUtils;
import com.lowdragmc.shimmerfire.CommonProxy;
import com.lowdragmc.shimmerfire.blockentity.ColoredCampfireBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.EmptyModelData;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Random;

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
      pPoseStack.scale(0.7f,0.85f,0.7f);
      pPoseStack.translate(.2f,0.4,.2f);
      BlockPos pos = pBlockEntity.getBlockPos();
      float finalDist = (float) Minecraft.getInstance().cameraEntity.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);

      PoseStack finalStack = RenderUtils.copyPoseStack(pPoseStack);
      PostProcessing.WARP.postEntity(multiBufferSource -> {
         BlockRenderDispatcher brd = Minecraft.getInstance().getBlockRenderer();
//         brd.renderSingleBlock(CommonProxy.FIRE_BLOCK.get().defaultBlockState(), finalStack, multiBufferSource, pPackedLight, pPackedOverlay, EmptyModelData.INSTANCE);
         BlockState blockState = CommonProxy.FIRE_BLOCK.get().defaultBlockState();
         BakedModel bakedmodel = brd.getBlockModel(blockState);
         float scale = 0.3f * (1 - Mth.clamp(finalDist, 0, 40f) / 40f);
         renderModel(null, finalStack.last(),
                         multiBufferSource.getBuffer(Sheets.cutoutBlockSheet()), blockState,
                         bakedmodel, scale, scale, scale, pPackedLight, pPackedOverlay, EmptyModelData.INSTANCE);
      });
      pPoseStack.popPose();
   }

   public void renderModel(Direction direction, PoseStack.Pose pPose, VertexConsumer pConsumer, @Nullable BlockState pState, BakedModel pModel, float pRed, float pGreen, float pBlue, int pPackedLight, int pPackedOverlay, net.minecraftforge.client.model.data.IModelData modelData) {
      Random random = new Random();
      random.setSeed(42L);
      renderQuadList(pPose, pConsumer, pRed, pGreen, pBlue, pModel.getQuads(pState, direction, random, modelData), pPackedLight, pPackedOverlay);

   }

   private static void renderQuadList(PoseStack.Pose pPose, VertexConsumer pConsumer, float pRed, float pGreen, float pBlue, List<BakedQuad> pQuads, int pPackedLight, int pPackedOverlay) {
      for(BakedQuad bakedquad : pQuads) {
         pConsumer.putBulkData(pPose, bakedquad, pRed, pGreen, pBlue, pPackedLight, pPackedOverlay);
      }

   }
}