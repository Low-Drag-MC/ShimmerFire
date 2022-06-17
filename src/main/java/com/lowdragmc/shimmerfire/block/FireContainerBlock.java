package com.lowdragmc.shimmerfire.block;

import com.lowdragmc.shimmerfire.blockentity.FireContainerBlockEntity;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;


/**
 * @author KilaBash
 * @date 2022/05/12
 * @implNote FireContainerBlock
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class FireContainerBlock extends BaseEntityBlock {
    protected static final VoxelShape SHAPE_LOWER = Shapes.or(
            Block.box(0, 0, 0, 16, 1, 16),
            Block.box(1, 1, 1, 15, 6, 15),
            Block.box(2, 6, 2, 14, 8, 14),
            Block.box(3, 8, 3, 13, 27, 13),
            Block.box(2, 27, 2, 14, 29, 14),
            Block.box(1, 29, 1, 15, 32, 15)
    );
    protected static final VoxelShape SHAPE_UPPER = Shapes.or(
            Block.box(0, 0 - 16, 0, 16, 1 - 16, 16),
            Block.box(1, 1 - 16, 1, 15, 6 - 16, 15),
            Block.box(2, 6 - 16, 2, 14, 8 - 16, 14),
            Block.box(3, 8 - 16, 3, 13, 27 - 16, 13),
            Block.box(2, 27 - 16, 2, 14, 29 - 16, 14),
            Block.box(1, 29 - 16, 1, 15, 32 - 16, 15)
    );
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;

    public FireContainerBlock() {
        super(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.PODZOL).strength(3.0F)
                .sound(SoundType.METAL).noOcclusion());
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH).setValue(HALF, DoubleBlockHalf.LOWER));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING, HALF);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new FireContainerBlockEntity(pPos, pState);
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        BlockPos blockpos = pContext.getClickedPos();
        Level level = pContext.getLevel();
        if (blockpos.getY() < level.getMaxBuildHeight() - 1 && level.getBlockState(blockpos.above()).canBeReplaced(pContext)) {
            return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
        } else {
            return null;
        }
    }

    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
        pLevel.setBlock(pPos.above(), pState.setValue(HALF, DoubleBlockHalf.UPPER), 3);
    }

    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
        DoubleBlockHalf doubleblockhalf = pState.getValue(HALF);

        if (pFacing.getAxis() == Direction.Axis.Y && doubleblockhalf == DoubleBlockHalf.LOWER == (pFacing == Direction.UP)) {
            return pFacingState.is(this) && pFacingState.getValue(HALF) != doubleblockhalf ?
                    pState.setValue(FACING, pFacingState.getValue(FACING)) :
                    Blocks.AIR.defaultBlockState();
        } else {
            return doubleblockhalf == DoubleBlockHalf.LOWER && pFacing == Direction.DOWN && !pState.canSurvive(pLevel, pCurrentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
        }
    }

    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        BlockPos blockpos = pPos.below();
        BlockState blockstate = pLevel.getBlockState(blockpos);
        return pState.getValue(HALF) == DoubleBlockHalf.LOWER || blockstate.is(this);
    }

    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return pState.getValue(HALF) == DoubleBlockHalf.UPPER ? SHAPE_UPPER : SHAPE_LOWER;
    }

    public RenderShape getRenderShape(BlockState pState) {
        return pState.getValue(HALF) == DoubleBlockHalf.LOWER ? RenderShape.MODEL : RenderShape.INVISIBLE;
    }

}
