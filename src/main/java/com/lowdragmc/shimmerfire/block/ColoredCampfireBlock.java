package com.lowdragmc.shimmerfire.block;

import com.lowdragmc.shimmerfire.ShimmerFireMod;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

import java.util.function.ToIntFunction;

import static com.lowdragmc.shimmerfire.block.ColoredFireBlock.FIRE_COLOR;

/**
 * @author KilaBash
 * @date 2022/05/08
 * @implNote TODO
 */
public class ColoredCampfireBlock extends CampfireBlock {

    private static ToIntFunction<BlockState> litBlockEmission() {
        return (state) -> state.getValue(BlockStateProperties.LIT) ? 15 : 0;
    }
    public ColoredCampfireBlock() {
        super(true, 1, BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.PODZOL).strength(2.0F).sound(SoundType.WOOD).lightLevel(litBlockEmission()).noOcclusion());
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(LIT, Boolean.TRUE)
                .setValue(SIGNAL_FIRE, Boolean.FALSE)
                .setValue(WATERLOGGED, Boolean.FALSE)
                .setValue(FACING, Direction.NORTH)
                .setValue(FIRE_COLOR, ColoredFireBlock.FireColor.ORANGE));
        this.setRegistryName(new ResourceLocation(ShimmerFireMod.MODID, "color_campfire"));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(LIT, SIGNAL_FIRE, WATERLOGGED, FACING, FIRE_COLOR);
    }
}
