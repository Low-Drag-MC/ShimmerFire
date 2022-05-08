package com.lowdragmc.shimmerfire.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

/**
 * @author KilaBash
 * @date 2022/05/09
 * @implNote ColoredCampfireBlockEntity
 */
public class ColoredCampfireBlockEntity extends CampfireBlockEntity {
    public ColoredCampfireBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(pWorldPosition, pBlockState);
    }
}
