package com.lowdragmc.shimmerfire.core.mixins.framedblocks;

import com.lowdragmc.shimmerfire.block.decorated.ColoredDecorationBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xfacthd.framedblocks.api.block.FramedBlockEntity;
import xfacthd.framedblocks.client.util.FramedBlockColor;

/**
 * @author KilaBash
 * @date 2022/7/2
 * @implNote FBContentAccessor
 */
@Mixin(FramedBlockColor.class)
public abstract class FramedBlockColorMixin {

    @Inject(
            method = {"getColor"},
            at = @At("HEAD"),
            cancellable = true,
            remap = false)
    private void injectGetColor(BlockState state, BlockAndTintGetter level, BlockPos pos, int tintIndex, CallbackInfoReturnable<Integer> cir) {
        if (tintIndex == -101) {
            if (level != null && pos != null) {
                BlockEntity be = level.getBlockEntity(pos);
                if (be instanceof FramedBlockEntity) {
                    if (be.getBlockState().getBlock() instanceof ColoredDecorationBlock block) {
                        cir.setReturnValue(block.color.color);
                    }
                }
            }
        }
    }
}
