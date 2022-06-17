package com.lowdragmc.shimmerfire.client.renderer;

import com.lowdragmc.shimmer.client.postprocessing.PostProcessing;
import com.lowdragmc.shimmer.client.shader.RenderUtils;
import com.lowdragmc.shimmer.core.IBakedQuad;
import com.lowdragmc.shimmerfire.CommonProxy;
import com.lowdragmc.shimmerfire.block.ColoredFireBlock;
import com.lowdragmc.shimmerfire.blockentity.FireContainerBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.EmptyModelData;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@OnlyIn(Dist.CLIENT)
public class FireContainerRenderer implements BlockEntityRenderer<FireContainerBlockEntity> {
   private final BlockEntityRendererProvider.Context context;

   private final static Map<ColoredFireBlock.FireColor, List<BakedQuad>> FIRE_QUAD_CACHE = new EnumMap<>(ColoredFireBlock.FireColor.class);

   public FireContainerRenderer(BlockEntityRendererProvider.Context pContext) {
      context = pContext;
   }

   @ParametersAreNonnullByDefault
   public void render(FireContainerBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
      if (!pBlockEntity.isCore()) return;
      float scale = 0.5f;
      float yScale = 0.5f;
      pPoseStack.pushPose();
      pPoseStack.translate(0.5, 0, 0.5);
      pPoseStack.scale(scale, yScale, scale);
      pPoseStack.translate(-0.5, 1.1 / yScale, -0.5);

      PoseStack finalStack = RenderUtils.copyPoseStack(pPoseStack);
      pPoseStack.pushPose();
      pPoseStack.scale(1, -1, 1);
      pPoseStack.translate(0, -1 / 16f / yScale, 0);
      PoseStack finalStack2 = RenderUtils.copyPoseStack(pPoseStack);

      PostProcessing.BLOOM_UNREAL.postEntity(bufferSource -> {
         VertexConsumer consumer = bufferSource.getBuffer(RenderType.cutoutMipped());
         for(BakedQuad bakedquad : getQuads(ColoredFireBlock.FireColor.ORANGE)) {
            consumer.putBulkData(finalStack.last(), bakedquad,
                    new float[]{1, 1, 1, 1},
                    1, 1, 1,
                    new int[]{0XF000F0, 0XF000F0, 0XF000F0, 0XF000F0},
                    OverlayTexture.NO_OVERLAY, true);
         }
         for(BakedQuad bakedquad : getQuads(ColoredFireBlock.FireColor.ORANGE)) {
            consumer.putBulkData(finalStack2.last(), bakedquad,
                    new float[]{1, 1, 1, 1},
                    1, 1, 1,
                    new int[]{0XF000F0, 0XF000F0, 0XF000F0, 0XF000F0},
                    OverlayTexture.NO_OVERLAY, true);
         }
      });
      pPoseStack.popPose();
      pPoseStack.popPose();
   }

   private List<BakedQuad> getQuads(ColoredFireBlock.FireColor color) {
      return FIRE_QUAD_CACHE.computeIfAbsent(color, c -> {
         net.minecraftforge.client.ForgeHooksClient.setRenderType(RenderType.cutout());
         BlockState blockState = CommonProxy.CAMPFIRE_BLOCK.get().defaultBlockState().setValue(ColoredFireBlock.FIRE_COLOR, c);
         BakedModel model = context.getBlockRenderDispatcher().getBlockModel(blockState);
         List<BakedQuad> quads = model.getQuads(blockState, null, new Random(), EmptyModelData.INSTANCE);
         net.minecraftforge.client.ForgeHooksClient.setRenderType(null);
         return quads.stream().filter(quad -> ((IBakedQuad)quad).isBloom()).toList();
      });
   }

}