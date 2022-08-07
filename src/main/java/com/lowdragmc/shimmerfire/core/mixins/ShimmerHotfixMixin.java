package com.lowdragmc.shimmerfire.core.mixins;

import com.lowdragmc.shimmer.client.postprocessing.PostProcessing;
import com.lowdragmc.shimmer.client.rendertarget.CopyDepthTarget;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PostProcessing.class)
public class ShimmerHotfixMixin{
    @Shadow private CopyDepthTarget postTarget;

    @Inject(method = "getPostTarget", at = @At(value = "INVOKE",
            target = "Lcom/lowdragmc/shimmer/client/rendertarget/CopyDepthTarget;setClearColor(FFFF)V",
            shift = At.Shift.AFTER))
    public void injectTick(CallbackInfoReturnable<CopyDepthTarget> cir) {
        this.postTarget.setClearColor(0, 0, 0, 0);
        this.postTarget.clear(Minecraft.ON_OSX);
    }
}
