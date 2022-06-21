package com.lowdragmc.shimmerfire.block;

import com.lowdragmc.shimmerfire.api.RawFire;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

/**
 * @author KilaBash
 * @date 2022/5/5
 * @implNote ColoredFireBlock
 */
public class ColoredFireBlock extends FireBlock {
    public static final EnumProperty<RawFire> FIRE = EnumProperty.create("fire", RawFire.class, RawFire.values());

    public ColoredFireBlock() {
        super(Properties.of(Material.FIRE, MaterialColor.FIRE).noCollission().instabreak().sound(SoundType.WOOL));
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(AGE, 0)
                .setValue(NORTH, Boolean.FALSE)
                .setValue(EAST, Boolean.FALSE)
                .setValue(SOUTH, Boolean.FALSE)
                .setValue(WEST, Boolean.FALSE)
                .setValue(UP, Boolean.FALSE)
                .setValue(FIRE, RawFire.DESTROY));

    }
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(AGE, NORTH, EAST, SOUTH, WEST, UP, FIRE);
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        return 4;
    }

}
