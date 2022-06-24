package com.lowdragmc.shimmerfire.client.renderer;

import com.lowdragmc.shimmer.client.postprocessing.PostProcessing;
import com.lowdragmc.shimmer.client.shader.RenderUtils;
import com.lowdragmc.shimmer.core.IBakedQuad;
import com.lowdragmc.shimmerfire.CommonProxy;
import com.lowdragmc.shimmerfire.api.RawFire;
import com.lowdragmc.shimmerfire.block.ColoredFireBlock;
import com.lowdragmc.shimmerfire.blockentity.FireCultureTankBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
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
public class FireContainerRenderer implements BlockEntityRenderer<FireCultureTankBlockEntity> {
   private final BlockEntityRendererProvider.Context context;

   private final static Map<RawFire, List<BakedQuad>> FIRE_QUAD_CACHE = new EnumMap<>(RawFire.class);

   public FireContainerRenderer(BlockEntityRendererProvider.Context pContext) {
      context = pContext;
   }

   @ParametersAreNonnullByDefault
   public void render(FireCultureTankBlockEntity fireContainer, float partialTick, PoseStack poseStack, MultiBufferSource pBufferSource, int packedLight, int packedOverlay) {
      if (!fireContainer.isCore()) return;
      RawFire fire = fireContainer.getFireType();
      if (fire == null) return;
      float scale = 0.2f + fireContainer.getStored() * 0.3f / fireContainer.getCapacity();
      poseStack.pushPose();
      poseStack.translate(0.5, 0, 0.5);
      poseStack.scale(scale, scale, scale);
      poseStack.translate(-0.5, 1.05 / scale, -0.5);

      PoseStack finalStack = RenderUtils.copyPoseStack(poseStack);
      poseStack.pushPose();
      poseStack.scale(1, -1, 1);
      poseStack.translate(0, -1 / 16f / scale, 0);
      PoseStack finalStack2 = RenderUtils.copyPoseStack(poseStack);

      PostProcessing.BLOOM_UNREAL.postEntity(bufferSource -> {
         VertexConsumer consumer = bufferSource.getBuffer(RenderType.cutoutMipped());
         for(BakedQuad bakedquad : getQuads(fire)) {
            consumer.putBulkData(finalStack.last(), bakedquad,
                    new float[]{1, 1, 1, 1},
                    1, 1, 1,
                    new int[]{0XF000F0, 0XF000F0, 0XF000F0, 0XF000F0},
                    OverlayTexture.NO_OVERLAY, true);
         }
         for(BakedQuad bakedquad : getQuads(fire)) {
            consumer.putBulkData(finalStack2.last(), bakedquad,
                    new float[]{1, 1, 1, 1},
                    1, 1, 1,
                    new int[]{0XF000F0, 0XF000F0, 0XF000F0, 0XF000F0},
                    OverlayTexture.NO_OVERLAY, true);
         }
      });
      poseStack.popPose();
      poseStack.popPose();
   }

   private List<BakedQuad> getQuads(RawFire fire) {
      return FIRE_QUAD_CACHE.computeIfAbsent(fire, c -> {
         net.minecraftforge.client.ForgeHooksClient.setRenderType(RenderType.cutout());
         BlockState blockState = CommonProxy.CAMPFIRE_BLOCK.get().defaultBlockState().setValue(ColoredFireBlock.FIRE, c);
         BakedModel model = context.getBlockRenderDispatcher().getBlockModel(blockState);
         List<BakedQuad> quads = model.getQuads(blockState, null, new Random(), EmptyModelData.INSTANCE);
         net.minecraftforge.client.ForgeHooksClient.setRenderType(null);
         return quads.stream().filter(quad -> ((IBakedQuad)quad).isBloom()).toList();
      });
   }

}