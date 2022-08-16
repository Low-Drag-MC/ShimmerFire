package com.lowdragmc.shimmerfire.core.mixins;

import com.google.common.collect.Lists;
import com.lowdragmc.shimmerfire.client.GlassAtlas;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.Stitcher;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

@Mixin(TextureAtlas.class)
public class TextureAtlasMixin {
    @Redirect(method = "getLoadedSprites", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Lists;newArrayList(Ljava/lang/Iterable;)Ljava/util/ArrayList;"))
    private ArrayList<TextureAtlasSprite> removeMissingTexture(Iterable<TextureAtlasSprite> elements) {
        if (((TextureAtlas) (Object) this).location().equals(GlassAtlas.location) && elements instanceof Queue<TextureAtlasSprite> q) {
            q.removeIf(sprite -> sprite instanceof MissingTextureAtlasSprite);
        }
        return Lists.newArrayList(elements);
    }

    @Inject(method = "getLoadedSprites",at=@At("RETURN"))
    private void setStitcherSize(ResourceManager pResourceManager, Stitcher pStitcher, int pMipLevel, CallbackInfoReturnable<List<TextureAtlasSprite>> cir){
        if (((TextureAtlas) (Object) this).location().equals(GlassAtlas.location)){
            StitcherMixin stitcher = (StitcherMixin) pStitcher;
            stitcher.setStorageX(32);
            stitcher.setStorageY(32);
        }
    }
}
