package com.lowdragmc.shimmerfire.block;

import com.lowdragmc.shimmer.client.light.ColorPointLight;
import com.lowdragmc.shimmerfire.blockentity.ColorfulFireBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

import javax.annotation.Nullable;

public class ColorfulFireBlock extends FireBlock implements EntityBlock {

    public ColorfulFireBlock() {
        super(Properties.of(Material.FIRE, MaterialColor.FIRE).noCollission()
                .instabreak()
                .sound(SoundType.WOOL));
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(AGE, 0)
                .setValue(NORTH, Boolean.FALSE)
                .setValue(EAST, Boolean.FALSE)
                .setValue(SOUTH, Boolean.FALSE)
                .setValue(WEST, Boolean.FALSE)
                .setValue(UP, Boolean.FALSE));

    }
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(AGE, NORTH, EAST, SOUTH, WEST, UP);
    }

    public static void setColor(BlockGetter level,BlockPos pos,int color){
        ((ColorfulFireBlockEntity)level.getBlockEntity(pos)).setColor(color);
    }

    public static int getColor(BlockAndTintGetter level, BlockPos pos){
        return ((ColorfulFireBlockEntity)level.getBlockEntity(pos)).getColor();
    }

    public static void setRadius(BlockGetter level,BlockPos pos,int radius){
        ((ColorfulFireBlockEntity)level.getBlockEntity(pos)).setRadius(radius);
    }

    public static int getRadius(BlockGetter level,BlockPos pos){
        return ((ColorfulFireBlockEntity)level.getBlockEntity(pos)).getRadius();
    }

    public static ColorPointLight.Template getColorPointLight(Level level, BlockPos pos){
        ColorfulFireBlockEntity blockEntity = (ColorfulFireBlockEntity)level.getBlockEntity(pos);
        return new ColorPointLight.Template(blockEntity.getRadius(), blockEntity.getColor());
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        return level!=null ? getRadius(level,pos) : 10;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new ColorfulFireBlockEntity(pPos,pState);
    }

    @Override
    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
        return this.canSurvive(pState, pLevel, pCurrentPos) ? pState : Blocks.AIR.defaultBlockState();
    }
}
