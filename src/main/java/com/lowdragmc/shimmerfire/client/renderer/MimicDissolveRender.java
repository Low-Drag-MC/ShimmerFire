package com.lowdragmc.shimmerfire.client.renderer;

import com.lowdragmc.shimmerfire.blockentity.MimicDissolveBlockEntity;
import com.lowdragmc.shimmerfire.client.IBufferBuilder;
import com.lowdragmc.shimmerfire.client.RenderTypes;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;
import org.lwjgl.system.MemoryStack;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

import static net.minecraft.client.renderer.LevelRenderer.DIRECTIONS;

public class MimicDissolveRender implements BlockEntityRenderer<MimicDissolveBlockEntity> {

    public static IBufferBuilder bufferBuilder = new IBufferBuilder(1024 * 100);

    public static ThreadLocal<Boolean> needUpload = ThreadLocal.withInitial(()->false);

    public MimicDissolveRender(BlockEntityRendererProvider.Context pContext) {

    }

    @Override
    public void render(MimicDissolveBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        needUpload.set(true);
        if (pBlockEntity.updateProgress(pPartialTick)){
            return;
        }
        float progress = pBlockEntity.getProgress();

        var renderType = RenderTypes.MimicDissolveRenderType.MIMIC_DISSOLVE;
        if (!bufferBuilder.building()) {
            bufferBuilder.begin(renderType.mode(), renderType.format());
        }
        var target = pBlockEntity.mimicBlockState;
        var model = Minecraft.getInstance().getBlockRenderer().getBlockModel(target);
        int tint = Minecraft.getInstance().getBlockColors().getColor(target, pBlockEntity.getLevel(), pBlockEntity.getBlockPos());
        float r = FastColor.ARGB32.red(tint) / 255.0f;
        float g = FastColor.ARGB32.green(tint)/ 255.0f;
        float b = FastColor.ARGB32.blue(tint)/ 255.0f;
        renderModel(pPoseStack.last(),bufferBuilder,target,model,r,g,b,
                pPackedLight,pPackedOverlay, model.getModelData(pBlockEntity.getLevel(),pBlockEntity.getBlockPos(),target, EmptyModelData.INSTANCE),progress);
    }

    /**
     * @see net.minecraft.client.renderer.block.BlockRenderDispatcher#renderSingleBlock(BlockState, PoseStack, MultiBufferSource, int, int, IModelData)
     */
    private static void renderModel(PoseStack.Pose pPose, IBufferBuilder pConsumer, @Nullable BlockState pState, BakedModel pModel,
                                    float pRed, float pGreen, float pBlue, int pPackedLight, int pPackedOverlay, IModelData modelData,float progress) {
        Random random = new Random();

        for (Direction direction : DIRECTIONS) {
            random.setSeed(42L);
            renderQuadList(pPose, pConsumer, pRed, pGreen, pBlue, pModel.getQuads(pState, direction,
                    random, modelData), pPackedLight, pPackedOverlay,progress);
        }

        random.setSeed(42L);
        renderQuadList(pPose, pConsumer, pRed, pGreen, pBlue, pModel.getQuads(pState, null,
                random, modelData), pPackedLight, pPackedOverlay,progress);
    }

    private static void renderQuadList(PoseStack.Pose pPose, IBufferBuilder pConsumer, float pRed, float pGreen, float pBlue,
                                       List<BakedQuad> pQuads, int pPackedLight, int pPackedOverlay, float progress) {
        for (BakedQuad bakedquad : pQuads) {
            float r;
            float g;
            float b;
            if (bakedquad.isTinted()) {
                r = Mth.clamp(pRed, 0.0F, 1.0f);
                g = Mth.clamp(pGreen, 0.0F, 1.0f);
                b = Mth.clamp(pBlue, 0.0F, 1.0f);
            } else {
                r = 1.0F;
                g = 1.0F;
                b = 1.0F;
            }

            putBulkData(pConsumer,pPose,bakedquad, r,g,b,
                    pPackedLight,pPackedOverlay,progress);
        }
    }

    private static void putBulkData(IBufferBuilder vertexConsumer, PoseStack.Pose pPoseEntry, BakedQuad pQuad
            , Float pRed, Float pGreen, Float pBlue, int pCombinedLight, int pCombinedOverlay,float progress) {
        MemoryStack memoryStack = MemoryStack.stackPush();

        var vertices = pQuad.getVertices();
        var quadNormal = pQuad.getDirection().getNormal();
        var transformedQuadNormal = new Vector3f(quadNormal.getX(), quadNormal.getY(), quadNormal.getX());
        transformedQuadNormal.transform(pPoseEntry.normal());
        var j = vertices.length / 8;
        var buffer = memoryStack.malloc(RenderTypes.MimicDissolveRenderType.DissolveVertexFormat.getVertexSize());
        var intBuffer = buffer.asIntBuffer();
        for (int index = 0; index < j; index++) {
            intBuffer.clear();
            intBuffer.put(vertices, index * 8, 8);
            var x = buffer.getFloat(0);
            var y = buffer.getFloat(4);
            var z = buffer.getFloat(8);
            var r = pRed;
            var g = pGreen;
            var b = pBlue;

            var lightmapUV = vertexConsumer.applyBakedLighting(pCombinedLight, buffer);
            var texU = buffer.getFloat(16);
            var texV = buffer.getFloat(20);
            vertexConsumer.applyBakedNormals(transformedQuadNormal, buffer, pPoseEntry.normal());
            vertexConsumer.vertex(pPoseEntry,x,y,z)
                    .color(r,g,b,1.0f)
                    .uv(texU,texV)
                    .uv2(lightmapUV)
                    .normal(transformedQuadNormal.x(), transformedQuadNormal.y(), transformedQuadNormal.z());
            vertexConsumer.progress(progress).endVertex();
        }
        memoryStack.close();
    }
}
