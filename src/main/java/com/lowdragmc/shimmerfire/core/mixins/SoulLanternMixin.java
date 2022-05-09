package com.lowdragmc.shimmerfire.core.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

/**
 * @author KilaBash
 * @date 2022/5/10
 * @implNote SoulLanternMixin
 */
@Mixin(LanternBlock.class)
public abstract class SoulLanternMixin extends Block {

    public SoulLanternMixin(Properties p_49795_) {
        super(p_49795_);
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        return state.getBlock() == Blocks.SOUL_LANTERN ? 4 : super.getLightEmission(state, level, pos);
    }
}
