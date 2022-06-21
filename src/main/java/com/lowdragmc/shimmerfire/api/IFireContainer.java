package com.lowdragmc.shimmerfire.api;

import org.jetbrains.annotations.Nullable;

/**
 * @author KilaBash
 * @date 2022/6/20
 * @implNote IFireContainer
 */
public interface IFireContainer {
    int getStored();
    int getCapacity();
    // extracted
    default int extract(@Nullable RawFire fire, int heat, boolean simulate) {
        return 0;
    }
    // inserted
    default int insert(@Nullable RawFire fire, int heat, boolean simulate) {
        return 0;
    }
    @Nullable
    RawFire getFireType();
    void setFireType(RawFire fire);
}
