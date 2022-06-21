package com.lowdragmc.shimmerfire.block;

import com.google.common.collect.Maps;
import com.lowdragmc.lowdraglib.utils.Vector3;
import com.lowdragmc.shimmerfire.blockentity.FirePortBlockEntity;
import com.lowdragmc.shimmerfire.utils.ShapeUtils;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;


/**
 * @author KilaBash
 * @date 2022/06/17
 * @implNote FireEmitterBlock
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class FirePortBlock extends BaseEntityBlock {

    private final Map<Direction, VoxelShape> SHAPE_BY_DIRECTION;

    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    public FirePortBlock(VoxelShape SHAPE) {
        super(Properties.of(Material.METAL, MaterialColor.PODZOL).strength(3.0F).sound(SoundType.METAL).noOcclusion());
        this.SHAPE_BY_DIRECTION = Util.make(Maps.newEnumMap(Direction.class), (map) -> {
            map.put(Direction.NORTH, ShapeUtils.rotate(ShapeUtils.rotate(SHAPE, new Vector3(1, 0, 0), 90), new Vector3(0, 1, 0), 180));
            map.put(Direction.EAST,  ShapeUtils.rotate(ShapeUtils.rotate(SHAPE, new Vector3(1, 0, 0), 90), new Vector3(0, 1, 0), 90));
            map.put(Direction.SOUTH, ShapeUtils.rotate(SHAPE, new Vector3(1, 0, 0), 90));
            map.put(Direction.WEST,  ShapeUtils.rotate(ShapeUtils.rotate(SHAPE, new Vector3(1, 0, 0), 90), new Vector3(0, 1, 0), 270));
            map.put(Direction.UP,  SHAPE);
            map.put(Direction.DOWN,  ShapeUtils.rotate(SHAPE, new Vector3(1, 0, 0), 180));
        });
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new FirePortBlockEntity(pPos, pState);
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(FACING, pContext.getClickedFace());
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE_BY_DIRECTION.get(pState.getValue(FACING));
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
        if (blockEntity instanceof FirePortBlockEntity firePort) {
            firePort.destroy();
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }
}
