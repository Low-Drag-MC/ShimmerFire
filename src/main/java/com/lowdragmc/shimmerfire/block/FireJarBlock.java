package com.lowdragmc.shimmerfire.block;

import com.lowdragmc.shimmerfire.api.RawFire;
import com.lowdragmc.shimmerfire.item.FireJarItem;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Random;

/**
 * @author KilaBash
 * @date 2022/6/20
 * @implNote FireJarBlock
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class FireJarBlock extends Block {
    public static final EnumProperty<RawFire> FIRE = ColoredFireBlock.FIRE;
    public static final BooleanProperty EMPTY = BooleanProperty.create("empty");

    public static final VoxelShape SHAPE = Shapes.or(
            Block.box(3, 0, 3, 13, 12, 13),
            Block.box(4, 12, 4, 12, 14, 12)
    );

    public FireJarBlock() {
        super(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.PODZOL).strength(3.0F)
                .lightLevel(state -> 5)
                .sound(SoundType.METAL).noOcclusion());
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FIRE, RawFire.DESTROY).setValue(EMPTY, true));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        int damage = pContext.getItemInHand().getDamageValue();
        if (damage > 0 && damage <= RawFire.values().length) {
            return defaultBlockState().setValue(EMPTY, false).setValue(FIRE, RawFire.values()[damage - 1]);
        }
        return super.getStateForPlacement(pContext);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FIRE, EMPTY);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Deprecated //Forge: Use more sensitive version
    public ItemStack getCloneItemStack(BlockGetter pLevel, BlockPos pPos, BlockState pState) {
        if (pState.getValue(EMPTY)) {
            return new ItemStack(this);
        } else return FireJarItem.getStack(pState.getValue(FIRE));
    }

    @Override
    public List<ItemStack> getDrops(BlockState pState, LootContext.Builder pBuilder) {
        if (pState.getValue(EMPTY)) {
            return List.of(new ItemStack(this));
        } else return List.of(FireJarItem.getStack(pState.getValue(FIRE)));
    }

    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, Random pRandom) {
        if (!pState.getValue(EMPTY)) {
            if (pRandom.nextFloat() >= 0.8) {
                pLevel.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, false,
                        (double)pPos.getX() + 0.5D + pRandom.nextDouble() / 3.0D * (double)(pRandom.nextBoolean() ? 1 : -1),
                        (double)pPos.getY() + 0.5D + pRandom.nextDouble() + pRandom.nextDouble(),
                        (double)pPos.getZ() + 0.5D + pRandom.nextDouble() / 3.0D * (double)(pRandom.nextBoolean() ? 1 : -1),
                        0.0D, 0.07D, 0.0D);
            }
        }
    }
}
