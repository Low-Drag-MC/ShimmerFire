package com.lowdragmc.shimmerfire.block;

import com.lowdragmc.shimmerfire.CommonProxy;
import com.lowdragmc.shimmerfire.blockentity.MimicDissolveBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@SuppressWarnings("deprecation")
public class MimicDissolveBlock extends BaseEntityBlock {

    public static final BooleanProperty MIMIC_SELF_DESTROY_STATE = BooleanProperty.create("self_destroy");


    public MimicDissolveBlock() {
        super(BlockBehaviour.Properties
                .of(Material.AIR, MaterialColor.NONE)
                .noOcclusion()
                .strength(-1f,3600000.8F)
                .noDrops()
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder.add(MIMIC_SELF_DESTROY_STATE));
    }

    @Override
    protected boolean isAir(BlockState state) {
        return state.getValue(MIMIC_SELF_DESTROY_STATE);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return pState.getValue(MIMIC_SELF_DESTROY_STATE) ? Shapes.empty() : Shapes.block();
    }

    @Override
    public boolean isValidSpawn(BlockState state, BlockGetter level, BlockPos pos, SpawnPlacements.Type type, EntityType<?> entityType) {
        return false;
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new MimicDissolveBlockEntity(pPos, pState);
    }

    @Override
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
        if (pPlacer instanceof Player player && player.isCreative()){
            pLevel.setBlock(pPos,pState.setValue(MIMIC_SELF_DESTROY_STATE,false),3);
        }
    }

    @Override
    public @NotNull InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (pPlayer.isCreative() && pPlayer.getItemInHand(pHand).getItem() instanceof BlockItem blockItem) {
            if (pLevel.getBlockEntity(pPos) instanceof MimicDissolveBlockEntity blockEntity && !pState.getValue(MIMIC_SELF_DESTROY_STATE)) {
                blockEntity.mimicBlockState = blockItem.getBlock().defaultBlockState();
                blockEntity.resetProgress();
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return createTickerHelper(pBlockEntityType, CommonProxy.MIMIC_DISSOLVE.get(), (pLevel1, pPos, pState1, pBlockEntity) -> pBlockEntity.tick());
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return super.getStateForPlacement(pContext);
    }
}
