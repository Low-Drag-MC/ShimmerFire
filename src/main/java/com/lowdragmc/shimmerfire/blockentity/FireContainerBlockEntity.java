package com.lowdragmc.shimmerfire.blockentity;

import com.lowdragmc.shimmerfire.CommonProxy;
import com.lowdragmc.shimmerfire.block.FireContainerBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

/**
 * @author KilaBash
 * @date 2022/05/12
 * @implNote FireContainerBlockEntity
 */
public class FireContainerBlockEntity extends BlockEntity {
    private FireContainerBlockEntity core;

    public FireContainerBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(CommonProxy.FIRE_CONTAINER.get(), pWorldPosition, pBlockState);
    }

    public boolean isCore() {
        return getBlockState().getValue(FireContainerBlock.HALF) == DoubleBlockHalf.LOWER;
    }

    public FireContainerBlockEntity getCore() {
        if (core != null) return core;
        if (isCore()) return core = this;
        BlockEntity blockEntity = getLevel().getBlockEntity(getBlockPos().below());
        if (blockEntity instanceof FireContainerBlockEntity) {
            return core = (FireContainerBlockEntity) blockEntity;
        }
        getLevel().removeBlock(getBlockPos(), false);
        return null;
    }

}
