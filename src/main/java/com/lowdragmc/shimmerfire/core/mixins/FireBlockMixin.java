package com.lowdragmc.shimmerfire.core.mixins;


import com.lowdragmc.shimmerfire.CommonProxy;
import com.lowdragmc.shimmerfire.blockentity.MimicDissolveBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Random;

@Mixin(FireBlock.class)
public class FireBlockMixin {

    @Inject(method = "tryCatchFire", at = @At(value = "INVOKE", shift = At.Shift.AFTER,
            target = "Lnet/minecraft/world/level/Level;removeBlock(Lnet/minecraft/core/BlockPos;Z)Z"),
            locals = LocalCapture.CAPTURE_FAILHARD)
    public void injectTryCatchFire(Level pLevel, BlockPos pPos, int pChance, Random pRandom,
                                   int pAge, Direction face, CallbackInfo ci, int i, BlockState blockState) {
        pLevel.setBlock(pPos, CommonProxy.MIMIC_DISSOLVE_BLOCK.get().defaultBlockState(), 3);
        if (pLevel.getBlockEntity(pPos) instanceof MimicDissolveBlockEntity blockEntity) {
            blockEntity.mimicBlockState = blockState;
            blockEntity.notifyUpdate();
        }
    }
}
