package com.lowdragmc.shimmerfire.client.renderer;

import com.lowdragmc.shimmer.client.light.ColorPointLight;
import com.lowdragmc.shimmer.client.postprocessing.PostProcessing;
import com.lowdragmc.shimmer.client.shader.RenderUtils;
import com.lowdragmc.shimmerfire.CommonProxy;
import com.lowdragmc.shimmerfire.block.ColoredFireBlock;
import com.lowdragmc.shimmerfire.entity.FireSpiritEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.EmptyModelData;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

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
        ColoredFireBlock.FireColor fireColor = entity.getColor();
        BlockState blockstate = CommonProxy.CAMPFIRE_BLOCK.get().defaultBlockState().setValue(ColoredFireBlock.FIRE_COLOR, fireColor);
        pMatrixStack.pushPose();
        BlockPos blockpos = new BlockPos(entity.getX(), entity.getBoundingBox().maxY, entity.getZ());

        float rotate = ((float)entity.tickCount + pPackedLight) / 20 * 30;
        float scale = 1f;
        pMatrixStack.pushPose();
        pMatrixStack.scale(scale, -scale, scale);
        pMatrixStack.mulPose(Vector3f.YP.rotationDegrees(rotate));
        pMatrixStack.translate(-0.5D, -0.1D, -0.5D);

        final PoseStack finalStack1 = RenderUtils.copyPoseStack(pMatrixStack);
        pMatrixStack.popPose();

        pMatrixStack.scale(scale, scale, scale);
        pMatrixStack.mulPose(Vector3f.YP.rotationDegrees(rotate));
        pMatrixStack.translate(-0.5D, -0.1D, -0.5D);
        final PoseStack finalStack2 = RenderUtils.copyPoseStack(pMatrixStack);

        final float finalLight = (Math.abs((entity.tickCount + pPackedLight) % 20) / 30f) + 0.3f;
        final Level level = entity.getLevel();

        PostProcessing.BLOOM_UNITY.postEntity(bufferSource -> {
            net.minecraftforge.client.ForgeHooksClient.setRenderType(RenderType.cutout());
            VertexConsumer consumer = bufferSource.getBuffer(Sheets.cutoutBlockSheet());
            renderModel(consumer, finalStack1, blockstate, level, blockpos, finalLight);
            renderModel(consumer, finalStack2, blockstate, level, blockpos, finalLight);
            net.minecraftforge.client.ForgeHooksClient.setRenderType(null);
        });

        pMatrixStack.popPose();
        super.render(entity, pEntityYaw, partialTicks, pMatrixStack, pBuffer, pPackedLight);

        updatePointLight(entity, partialTicks);
    }

    private void updatePointLight(FireSpiritEntity entity, float partialTicks) {
        ColorPointLight light = entity.getOrCreateLight();
        if (light != null && !light.isRemoved()) {
            Vec3 pos = entity.getPosition(partialTicks);
            light.setPos((float) pos.x, (float) pos.y, (float) pos.z);
            light.update();
        }
    }

    private void renderModel(VertexConsumer consumer, PoseStack poseStack, BlockState blockState, Level level, BlockPos pos, float light) {
        BlockRenderDispatcher brd = Minecraft.getInstance().getBlockRenderer();
        BakedModel model = brd.getBlockModel(blockState);

        Vec3 vec3 = blockState.getOffset(level, pos);
        poseStack.translate(vec3.x, vec3.y, vec3.z);

        for(Direction direction : Direction.values()) {
            List<BakedQuad> list = model.getQuads(blockState, direction, level.random, EmptyModelData.INSTANCE);
            if (!list.isEmpty()) {
                this.renderModelFaceFlat(light, poseStack, consumer, list);
            }
        }

        List<BakedQuad> list1 = model.getQuads(blockState, null, level.random, EmptyModelData.INSTANCE);
        if (!list1.isEmpty()) {
            this.renderModelFaceFlat(light, poseStack, consumer, list1);
        }
    }

    private void renderModelFaceFlat(float light, PoseStack pPoseStack, VertexConsumer pConsumer, List<BakedQuad> pQuads) {
        for(BakedQuad bakedquad : pQuads) {
            pConsumer.putBulkData(pPoseStack.last(), bakedquad, new float[]{1, 1, 1, 1}, light, light, light, new int[]{0, 0, 0, 0}, OverlayTexture.NO_OVERLAY, true);
        }
    }

}
