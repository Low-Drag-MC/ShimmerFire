package com.lowdragmc.shimmerfire.core.mixins;


import com.lowdragmc.shimmerfire.core.IPoseStackPose;
import com.mojang.blaze3d.vertex.PoseStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Deque;

@Mixin(PoseStack.class)
public class PoseStackMixin {

    @Shadow
    Deque<PoseStack.Pose> poseStack;

    @Inject(method = "pushPose", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILHARD)
    public void injectPush(CallbackInfo ci, PoseStack.Pose pose) {
        ((IPoseStackPose) (Object) (poseStack.getLast())).addOffset(
                ((IPoseStackPose) (Object) pose).getOffset()
        );
    }

    @Inject(method = "translate", at = @At("TAIL"))
    public void injectTranslate(double pX, double pY, double pZ, CallbackInfo ci) {
        ((IPoseStackPose) (Object) (poseStack.getLast())).addOffset((float) pX, (float) pY, (float) pZ);
    }

    @Inject(method = "setIdentity", at = @At("TAIL"))
    public void injectSetIdentity(CallbackInfo ci) {
        ((IPoseStackPose) (Object) (poseStack.getLast())).setIdentity();
    }
}
