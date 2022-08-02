package com.lowdragmc.shimmerfire.client;

import com.google.common.collect.ImmutableMap;
import com.lowdragmc.shimmerfire.ShimmerFireMod;
import com.lowdragmc.shimmerfire.core.mixins.VertexFormatElementUsageAccessor;
import com.lowdragmc.shimmerfire.item.LighterSword;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;

import java.util.function.Function;

import static com.mojang.blaze3d.vertex.DefaultVertexFormat.*;

/**
 * @author KilaBash
 * @date 2022/6/24
 * @implNote RenderTypes
 */
public class RenderTypes {

    public static RenderType emissiveCutoutNoCull(ResourceLocation resourceLocation) {
        return EmissiveCutoutNoCullRenderType.EMISSIVE_CUTOUT_NO_CULL.apply(resourceLocation);
    }

    public static class EmissiveCutoutNoCullRenderType extends RenderType {
        public static ShaderInstance emissiveCutoutNoCullShader;
        private static final RenderStateShard.ShaderStateShard RENDERTYPE_EMISSIVE_SHADER = new RenderStateShard.ShaderStateShard(() -> emissiveCutoutNoCullShader);
        private static final Function<ResourceLocation, RenderType> EMISSIVE_CUTOUT_NO_CULL = Util.memoize((p_173206_) -> {
            RenderType.CompositeState rendertype$compositestate = CompositeState.builder().setShaderState(RENDERTYPE_EMISSIVE_SHADER).setTextureState(new RenderStateShard.TextureStateShard(p_173206_, false, false)).setTransparencyState(NO_TRANSPARENCY).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).setLayeringState(VIEW_OFFSET_Z_LAYERING).createCompositeState(true);
            return create("emissive_cutout_no_cull", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, false, rendertype$compositestate);
        });

        private EmissiveCutoutNoCullRenderType(String s, VertexFormat v, VertexFormat.Mode m, int i, boolean b, boolean b2, Runnable r, Runnable r2) {
            super(s, v, m, i, b, b2, r, r2);
            throw new IllegalStateException("This class is not meant to be constructed!");
        }
    }

    public static class MimicDissolveRenderType extends RenderType {

        public static ShaderInstance SOLID_MIMIC_DISSOLVE_SHADER;

        public static final VertexFormatElement.Usage INSTANCE_ELEMENT = VertexFormatElementUsageAccessor.constructor("progress", 0, "progress",
                (pCount, pGlType, pVertexSize, pOffset, pIndex, pStateIndex) -> {
                    GlStateManager._enableVertexAttribArray(pStateIndex);
                    GlStateManager._vertexAttribPointer(pStateIndex, pCount, pGlType, true, pVertexSize, pOffset);
//                    GL33.glVertexAttribDivisor(pStateIndex,1);
                },
                (pIndex, pElementIndex) -> {
                    GlStateManager._disableVertexAttribArray(pElementIndex);
//                    GL33.glVertexAttribDivisor(pElementIndex,0);
                });

        public static final VertexFormatElement DISSOLVE_PROGRESS = new VertexFormatElement(0,
                VertexFormatElement.Type.FLOAT, INSTANCE_ELEMENT, 1);

        public static final VertexFormat DissolveVertexFormat = new VertexFormat(ImmutableMap.<String, VertexFormatElement>builder()
                .put("Position", ELEMENT_POSITION)
                .put("WorldPosition",ELEMENT_POSITION)
                .put("Color", ELEMENT_COLOR)
                .put("UV0", ELEMENT_UV0)
                .put("UV2", ELEMENT_UV2)
                .put("Normal", ELEMENT_NORMAL)
                .put("DissolveProgress", DISSOLVE_PROGRESS)
                .build());

        public static final RenderStateShard.ShaderStateShard RENDERTYPE_MIMIC_DISSOLVE_SHADER = new RenderStateShard.ShaderStateShard(() -> SOLID_MIMIC_DISSOLVE_SHADER);

        public static final RenderStateShard.MultiTextureStateShard TEXTURE = new RenderStateShard.MultiTextureStateShard.Builder()
                .add(InventoryMenu.BLOCK_ATLAS, false, true)
                .add(new ResourceLocation(ShimmerFireMod.MODID,"textures/noise.png"),false,true)
                .build();

        public static final RenderStateShard.MultiTextureStateShard TEXTURE_ITEM = new RenderStateShard.MultiTextureStateShard.Builder()
                .add(LighterSword.textureLocation,false,true)
                .add(new ResourceLocation(ShimmerFireMod.MODID,"textures/noise.png"), false, true)
                .build();

        public static final RenderType MIMIC_DISSOLVE = create("mimic_dissolve", DissolveVertexFormat,
                VertexFormat.Mode.QUADS, 2097152, true, false,
                RenderType.CompositeState.builder()
                        .setLightmapState(LIGHTMAP)
                        .setShaderState(RENDERTYPE_MIMIC_DISSOLVE_SHADER)
                        .setTextureState(TEXTURE)
                        .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                        .createCompositeState(true));

        public static final RenderType MIMIC_DISSOLVE_ITEM = create("mimic_dissolve", DissolveVertexFormat,
                VertexFormat.Mode.QUADS, 2097152, true, false,
                RenderType.CompositeState.builder()
                        .setLightmapState(LIGHTMAP)
                        .setShaderState(RENDERTYPE_MIMIC_DISSOLVE_SHADER)
                        .setTextureState(TEXTURE_ITEM)
                        .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                        .createCompositeState(true));

        public MimicDissolveRenderType(String pName, VertexFormat pFormat, VertexFormat.Mode pMode, int pBufferSize, boolean pAffectsCrumbling, boolean pSortOnUpload, Runnable pSetupState, Runnable pClearState) {
            super(pName, pFormat, pMode, pBufferSize, pAffectsCrumbling, pSortOnUpload, pSetupState, pClearState);
            throw new IllegalStateException("This class is not meant to be constructed!");
        }
    }
}
