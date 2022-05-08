package com.lowdragmc.shimmerfire.block;

import com.lowdragmc.shimmerfire.ShimmerFireMod;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

import static com.lowdragmc.shimmerfire.block.ColoredFireBlock.FIRE_COLOR;

/**
 * @author KilaBash
 * @date 2022/05/08
 * @implNote TODO
 */
public class ColoredCampfireBlock extends CampfireBlock {

    public ColoredCampfireBlock() {
        super(true, 1, BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.PODZOL).strength(2.0F).sound(SoundType.WOOD).noOcclusion());
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(LIT, Boolean.TRUE)
                .setValue(SIGNAL_FIRE, Boolean.FALSE)
                .setValue(WATERLOGGED, Boolean.FALSE)
                .setValue(FACING, Direction.NORTH)
                .setValue(FIRE_COLOR, ColoredFireBlock.FireColor.ORANGE));
        this.setRegistryName(new ResourceLocation(ShimmerFireMod.MODID, "color_campfire"));
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        return 4;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(LIT, SIGNAL_FIRE, WATERLOGGED, FACING, FIRE_COLOR);
    }
}
