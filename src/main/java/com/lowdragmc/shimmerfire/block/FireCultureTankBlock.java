package com.lowdragmc.shimmerfire.block;

import com.lowdragmc.shimmerfire.CommonProxy;
import com.lowdragmc.shimmerfire.blockentity.FireCultureTankBlockEntity;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.phys.BlockHitResult;
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
public class FireCultureTankBlock extends BaseEntityBlock {
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
    public static final BooleanProperty CHARGING = BooleanProperty.create("charging");

    public FireCultureTankBlock() {
        super(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.PODZOL).strength(3.0F)
                .sound(SoundType.METAL).noOcclusion());
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(HALF, DoubleBlockHalf.LOWER)
                .setValue(CHARGING, false));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING, HALF, CHARGING);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new FireCultureTankBlockEntity(pPos, pState);
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
        BlockState blockState;
        if (pFacing.getAxis() == Direction.Axis.Y && doubleblockhalf == DoubleBlockHalf.LOWER == (pFacing == Direction.UP)) {
            blockState = pFacingState.is(this) && pFacingState.getValue(HALF) != doubleblockhalf ?
                    pState.setValue(FACING, pFacingState.getValue(FACING)) :
                    Blocks.AIR.defaultBlockState();
        } else {
            blockState = doubleblockhalf == DoubleBlockHalf.LOWER && pFacing == Direction.DOWN && !pState.canSurvive(pLevel, pCurrentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
        }
        if (!(blockState.getBlock() instanceof FireCultureTankBlock)) return blockState;
        if (pFacing == Direction.DOWN && doubleblockhalf == DoubleBlockHalf.LOWER) {
            blockState = blockState.setValue(CHARGING, canCharge(pFacingState));
        }
        return blockState;
    }

    public boolean canCharge(BlockState blockState) {
        if (blockState.hasProperty(BlockStateProperties.LIT) && blockState.getValue(BlockStateProperties.LIT)) {
            return true;
        } else if (blockState.getBlock() instanceof BaseFireBlock) {
            return true;
        } else if (blockState.getFluidState().getType().getAttributes().getTemperature() > 1300) {
            return true;
        }
        return false;
    }

    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        BlockPos blockpos = pPos.below();
        BlockState blockstate = pLevel.getBlockState(blockpos);
        return pState.getValue(HALF) == DoubleBlockHalf.LOWER || blockstate.is(this);
    }

    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return pState.getValue(HALF) == DoubleBlockHalf.UPPER ? SHAPE_UPPER : SHAPE_LOWER;
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (pLevel.getBlockEntity(pPos) instanceof FireCultureTankBlockEntity fireCultureTankBlockEntity) {
            return fireCultureTankBlockEntity.use(pPlayer, pHand);
        }
        return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        if (pState.getValue(CHARGING)) {
            return createTickerHelper(pBlockEntityType, CommonProxy.FIRE_CULTURE_TANK.get(), (pLevel1, pPos, pState1, pBlockEntity) -> pBlockEntity.chargingTick());
        }
        return null;
    }
}
