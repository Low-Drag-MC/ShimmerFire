package com.lowdragmc.shimmerfire.client;

import com.lowdragmc.shimmerfire.ShimmerFireMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.TextureAtlasHolder;
import net.minecraft.resources.ResourceLocation;

import java.util.stream.Stream;


public class GlassAtlas extends TextureAtlasHolder {

    public static ResourceLocation glassTextureLocation = ShimmerFireMod.rl("items/cyberpunk_glasses");

    public static final GlassAtlas instance = new GlassAtlas();
    public static final ResourceLocation location = ShimmerFireMod.rl("cyberpunk_glasses");

    private GlassAtlas() {
        super(Minecraft.getInstance().textureManager, ShimmerFireMod.rl("cyberpunk_glasses"), "cyberpunk_glasses");
    }

    @Override
    public ResourceLocation resolveLocation(ResourceLocation resourceLocation) {
        return resourceLocation;
    }

    @Override
    protected Stream<ResourceLocation> getResourcesToLoad() {
        return Stream.of(glassTextureLocation);
    }
}