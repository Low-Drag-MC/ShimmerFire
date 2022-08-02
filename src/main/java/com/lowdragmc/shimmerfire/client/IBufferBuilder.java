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

//
//    private static final float globalProgress = 0.5f;
//    private static final float scale = 5;
//
//    /**
//     specialized for geoRender , the origin implementation renderCube will fill data by this , adapt to data we need
//     */
//    @Override
//    public void vertex(float pX, float pY, float pZ, float pRed, float pGreen, float pBlue, float pAlpha, float pTexU, float pTexV, int pOverlayUV, int pLightmapUV, float pNormalX, float pNormalY, float pNormalZ) {
//        this.vertex(pX,pY,pZ);
//        float l = System.currentTimeMillis() % 1000 / 1000f;
//        this.vertex(pTexU * scale,pTexV * scale,0);
//        this.color(pRed, pGreen, pBlue, pAlpha);
//        this.uv(pTexU,pTexV);
//        this.uv2(pLightmapUV);
//        this.normal(pNormalX,pNormalY,pNormalZ);
//        this.progress(globalProgress);
//        this.endVertex();
//    }
//
//    public IBufferBuilder globalProgress(){
//        return progress(globalProgress);
//    }

    public IBufferBuilder progress(float progress){
        if (this.currentElement().getUsage() != RenderTypes.MimicDissolveRenderType.INSTANCE_ELEMENT){
            throw new RuntimeException();
        }
        this.putFloat(0,progress);
        this.nextElement();
        return this;
    }

}
