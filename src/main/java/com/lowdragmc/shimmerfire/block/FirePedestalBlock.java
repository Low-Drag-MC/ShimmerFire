package com.lowdragmc.shimmerfire.block;

import com.lowdragmc.shimmerfire.CommonProxy;
import com.lowdragmc.shimmerfire.blockentity.FirePedestalBlockEntity;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * @author KilaBash
 * @date 2022/6/23
 * @implNote FirePedestalBlock
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class FirePedestalBlock extends BaseEntityBlock {

    public static final VoxelShape SHAPE = Shapes.or(
            Block.box(0, 0, 0, 16, 4, 16),
            Block.box(2, 12, 2, 14, 13.5, 14),
            Block.box(2, 14.5, 2, 14, 16, 14),
            Block.box(3, 4, 3, 13, 12, 13),
            Block.box(3, 13.5, 3, 13, 14.5, 13),
            Block.box(5, 5, 13, 11, 11, 16),
            Block.box(13, 5, 5, 16, 11, 11),
            Block.box(5, 5, 0, 11, 11, 3),
            Block.box(0, 5, 5, 3, 11, 11)
    );

    public FirePedestalBlock() {
        super(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.PODZOL).strength(3.0F)
                .sound(SoundType.METAL).noOcclusion());
    }

    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new FirePedestalBlockEntity(pPos, pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        if (pLevel.isClientSide) {
            return createTickerHelper(pBlockEntityType, CommonProxy.FIRE_PEDESTAL.get(), (pLevel1, pPos, pState1, pBlockEntity) -> pBlockEntity.clientTick());
        }
        return null;
    }
}
