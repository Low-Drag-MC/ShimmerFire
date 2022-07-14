package com.lowdragmc.shimmerfire.client;

import com.lowdragmc.shimmerfire.core.IPoseStackPose;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import com.mojang.math.Vector3f;

public class IBufferBuilder extends BufferBuilder {
    public IBufferBuilder(int pCapacity) {
        super(pCapacity);
    }

    public IBufferBuilder vertex(PoseStack.Pose pose, double x, double y, double z){
        super.vertex(pose.pose(), (float) x, (float) y, (float) z);
        if (this.currentElement().getUsage() != VertexFormatElement.Usage.POSITION){
            throw new RuntimeException();
        }
        Vector3f offset = ((IPoseStackPose) (Object) pose).getOffset();
        super.vertex(offset.x() + x,offset.y() + y,offset.z() + z);
        return this;
    }

    public IBufferBuilder progress(float progress){
        if (this.currentElement().getUsage() != RenderTypes.MimicDissolveRenderType.INSTANCE_ELEMENT){
            throw new RuntimeException();
        }
        this.putFloat(0,progress);
        this.nextElement();
        return this;
    }

}
