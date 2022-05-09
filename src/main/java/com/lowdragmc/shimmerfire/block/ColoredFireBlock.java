package com.lowdragmc.shimmerfire.block;

import net.minecraft.core.BlockPos;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

import javax.annotation.Nonnull;

/**
 * @author KilaBash
 * @date 2022/5/5
 * @implNote ColoredFireBlock
 */
public class ColoredFireBlock extends FireBlock {
    public static final EnumProperty<FireColor> FIRE_COLOR = EnumProperty.create("color", FireColor.class, FireColor.values());

    public ColoredFireBlock() {
        super(Properties.of(Material.FIRE, MaterialColor.FIRE).noCollission().instabreak().sound(SoundType.WOOL));
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(AGE, 0)
                .setValue(NORTH, Boolean.FALSE)
                .setValue(EAST, Boolean.FALSE)
                .setValue(SOUTH, Boolean.FALSE)
                .setValue(WEST, Boolean.FALSE)
                .setValue(UP, Boolean.FALSE)
                .setValue(FIRE_COLOR, FireColor.ORANGE));

    }
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(AGE, NORTH, EAST, SOUTH, WEST, UP, FIRE_COLOR);
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        return 4;
    }

    public enum FireColor implements StringRepresentable {
        ORANGE("orange", 0xFFFFA500, 10),
        CYAN("cyan", 0xff00FFFF, 10),
        GREEN("green", 0xff008000, 10),
        PURPLE("purple", 0xff800080, 10);

        public final String colorName;
        public final int colorVale;
        public final float radius;

        FireColor(String colorName, int colorVale, float radius){
            this.colorName = colorName;
            this.colorVale = colorVale;
            this.radius = radius;
        }

        @Override
        @Nonnull
        public String getSerializedName() {
            return colorName;
        }
    }
}
