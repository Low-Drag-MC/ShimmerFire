package com.lowdragmc.shimmerfire.core.mixins;

import net.minecraft.client.renderer.texture.Stitcher;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Stitcher.class)
interface StitcherMixin {

    @Accessor
    void setStorageX(int storageX);

    @Accessor
    void setStorageY(int storageY);

}
