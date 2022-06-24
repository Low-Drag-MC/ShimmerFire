package com.lowdragmc.shimmerfire.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

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
}
