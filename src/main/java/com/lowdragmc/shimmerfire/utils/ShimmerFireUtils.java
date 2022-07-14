package com.lowdragmc.shimmerfire.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;

public class ShimmerFireUtils {

    public static void setSingleBlocksDirty(BlockPos pos){
        Minecraft.getInstance().levelRenderer.setBlocksDirty(
                pos.getX(), pos.getY(), pos.getZ(),
                pos.getX(), pos.getY(), pos.getZ()
        );
    }

}
