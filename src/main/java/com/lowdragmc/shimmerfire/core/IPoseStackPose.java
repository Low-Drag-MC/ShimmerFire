package com.lowdragmc.shimmerfire.core;

import com.mojang.math.Vector3f;

public interface IPoseStackPose {
    Vector3f getOffset();
    void addOffset(float x ,float y , float z);
    void addOffset(Vector3f offset);
    void replaceOffset(Vector3f offset);
    void setIdentity();
}
