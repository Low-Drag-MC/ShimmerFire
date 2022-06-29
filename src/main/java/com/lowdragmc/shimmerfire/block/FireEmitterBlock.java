package com.lowdragmc.shimmerfire.block;

import com.lowdragmc.shimmerfire.CommonProxy;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * @author KilaBash
 * @date 2022/06/17
 * @implNote FireEmitterBlock
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class FireEmitterBlock extends FirePortBlock {

    public static final VoxelShape SHAPE = Shapes.or(
            Block.box(4, 0, 4, 12, 3.5, 12)
    );

    public FireEmitterBlock() {
        super(SHAPE);
    }

    @Nullable
    @Override
    @ParametersAreNonnullByDefault
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        if (!pLevel.isClientSide) {
            return createTickerHelper(pBlockEntityType, CommonProxy.FIRE_PORT.get(), (pLevel1, pPos, pState1, pBlockEntity) -> pBlockEntity.emitTick());
        }
        return null;
    }
}
