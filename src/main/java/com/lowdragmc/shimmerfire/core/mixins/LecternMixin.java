package com.lowdragmc.shimmerfire.core.mixins;

import com.lowdragmc.lowdraglib.gui.factory.BlockEntityUIFactory;
import com.lowdragmc.shimmerfire.blockentity.multiblocked.HexGateBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author KilaBash
 * @date 2022/7/22
 * @implNote LecternMixin
 */

@Mixin(LecternBlock.class)
public class LecternMixin {
    @Inject(method = "use", at = @At(value = "HEAD", shift = At.Shift.AFTER), cancellable = true)
    public void injectUse(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit, CallbackInfoReturnable<InteractionResult> cir) {
        if (!pLevel.isClientSide && !pState.getValue(LecternBlock.HAS_BOOK)) {
            Direction facing = pState.getValue(LecternBlock.FACING).getOpposite();
            for (int i = 1; i <= 3; i++) {
                BlockPos pos = pPos.relative(facing, i);
                for (int j = -1; j <= 1 ; j++) {
                    for (int k = -1; k <= 1; k++) {
                        BlockPos blockPos = pos.relative(Direction.UP, j).relative(facing.getClockWise(), k);
                        if (pLevel.getBlockEntity(blockPos) instanceof HexGateBlockEntity hexGate && hexGate.isIdle()) {
                            if (!pPlayer.isCrouching()) {
                                if (pPlayer instanceof ServerPlayer) {
                                    BlockEntityUIFactory.INSTANCE.openUI(hexGate, (ServerPlayer) pPlayer);
                                    cir.setReturnValue(InteractionResult.SUCCESS);
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
