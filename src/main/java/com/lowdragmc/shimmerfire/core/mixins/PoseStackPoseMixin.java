package com.lowdragmc.shimmerfire.core.mixins;

import com.lowdragmc.shimmerfire.core.IPoseStackPose;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PoseStack.Pose.class)
public class PoseStackPoseMixin implements IPoseStackPose {
    Vector3f offset = Vector3f.ZERO.copy();

    @Override
    public Vector3f getOffset() {
        return offset;
    }

    @Override
    public void replaceOffset(Vector3f offset) {
        this.offset = offset;
    }

    @Override
    public void addOffset(float x, float y, float z) {
        this.offset = new Vector3f(offset.x() + x , offset.y() + y , offset.z() + z);
    }

    @Override
    public void addOffset(Vector3f offset) {
        this.offset.add(offset);
    }

    @Override
    public void setIdentity() {
        this.offset.set(0,0,0);
    }
}
