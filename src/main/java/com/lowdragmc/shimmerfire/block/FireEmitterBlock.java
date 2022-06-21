package com.lowdragmc.shimmerfire.block;

import com.lowdragmc.shimmerfire.CommonProxy;
import com.lowdragmc.shimmerfire.blockentity.ColoredCampfireBlockEntity;
import com.lowdragmc.shimmerfire.blockentity.FirePortBlockEntity;
import com.lowdragmc.shimmerfire.utils.ShapeUtils;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
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
            Block.box(5, 0, 5, 11, 1, 11),
            Block.box(4, 1, 4, 12, 2, 12),
            Block.box(6, 2, 10, 11, 6, 11),
            Block.box(5, 2, 5, 10, 6, 6),
            Block.box(10, 2, 5, 11, 6, 10),
            Block.box(5, 2, 6, 6, 6, 11),
            Block.box(7, 2, 7, 9, 4, 9),
            Block.box(4, 2, 7, 5, 5, 9),
            Block.box(11, 2, 7, 12, 5, 9),
            Block.box(7, 2, 11, 9, 5, 12),
            Block.box(7, 2, 4, 9, 5, 5)
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
