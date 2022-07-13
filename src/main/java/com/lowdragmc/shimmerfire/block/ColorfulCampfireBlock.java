package com.lowdragmc.shimmerfire.block;

import com.lowdragmc.shimmer.client.light.ColorPointLight;
import com.lowdragmc.shimmerfire.CommonProxy;
import com.lowdragmc.shimmerfire.blockentity.ColorfulCampfireBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.stats.Stats;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import java.util.Optional;

public class ColorfulCampfireBlock extends CampfireBlock {

    public ColorfulCampfireBlock() {
        super(true, 1, BlockBehaviour.Properties
                .of(Material.WOOD, MaterialColor.PODZOL)
                .strength(2.0F)
                .sound(SoundType.WOOD)
                .noOcclusion());
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(LIT, Boolean.TRUE)
                .setValue(SIGNAL_FIRE, Boolean.FALSE)
                .setValue(WATERLOGGED, Boolean.FALSE)
                .setValue(FACING, Direction.NORTH));
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        return state.hasProperty(LIT) ? 15 : getColor(level,pos);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(LIT, SIGNAL_FIRE, WATERLOGGED, FACING);
    }

    @Override
    @ParametersAreNonnullByDefault
    @Nonnull
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        BlockEntity blockentity = pLevel.getBlockEntity(pPos);
        if (blockentity instanceof ColorfulCampfireBlockEntity blockEntity) {
            ItemStack itemstack = pPlayer.getItemInHand(pHand);
            Optional<CampfireCookingRecipe> optional = blockEntity.getCookableRecipe(itemstack);
            if (optional.isPresent()) {
                if (!pLevel.isClientSide && blockEntity.placeFood(pPlayer.getAbilities().instabuild ? itemstack.copy() : itemstack, optional.get().getCookingTime())) {
                    pPlayer.awardStat(Stats.INTERACT_WITH_CAMPFIRE);
                    return InteractionResult.SUCCESS;
                }
                return InteractionResult.CONSUME;
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (!pState.is(pNewState.getBlock())) {
            BlockEntity blockentity = pLevel.getBlockEntity(pPos);
            if (blockentity instanceof ColorfulCampfireBlockEntity blockEntity) {
                Containers.dropContents(pLevel, pPos, blockEntity.getItems());
            }
            super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new ColorfulCampfireBlockEntity(pPos, pState);
    }

    @Nullable
    @Override
    @ParametersAreNonnullByDefault
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        if (pLevel.isClientSide) {
            return pState.getValue(LIT) ? createTickerHelper(pBlockEntityType, CommonProxy.COLORFUL_CAMPFIRE.get(), ColorfulCampfireBlockEntity::particleTick) : null;
        } else {
            return pState.getValue(LIT) ? createTickerHelper(pBlockEntityType, CommonProxy.COLORFUL_CAMPFIRE.get(), ColorfulCampfireBlockEntity::cookTick) : createTickerHelper(pBlockEntityType, CommonProxy.COLORFUL_CAMPFIRE.get(), ColorfulCampfireBlockEntity::cooldownTick);
        }
    }

    public static void setColor(BlockGetter level,BlockPos pos,int color){
        ((ColorfulCampfireBlockEntity)level.getBlockEntity(pos)).setColor(color);
    }

    public static int getColor(BlockGetter level, BlockPos pos){
        return ((ColorfulCampfireBlockEntity)level.getBlockEntity(pos)).getColor();
    }

    public static void setRadius(BlockGetter level,BlockPos pos,int radius){
        ((ColorfulCampfireBlockEntity)level.getBlockEntity(pos)).setRadius(radius);
    }

    public static int getRadius(BlockGetter level,BlockPos pos){
        return ((ColorfulCampfireBlockEntity)level.getBlockEntity(pos)).getRadius();
    }

    public static ColorPointLight.Template getColorPointLight(BlockGetter level, BlockPos pos){
        ColorfulCampfireBlockEntity blockEntity = (ColorfulCampfireBlockEntity)level.getBlockEntity(pos);
        return new ColorPointLight.Template(blockEntity.getRadius(), blockEntity.getColor());
    }
}
