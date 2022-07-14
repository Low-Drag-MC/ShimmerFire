package com.lowdragmc.shimmerfire.blockentity;

import com.lowdragmc.shimmerfire.CommonProxy;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;

public class ColorfulFireBlockEntity extends BlockEntity {

    private int color;
    private int radius;

    public static final String colorTagKey = "color";
    public static final String radiusTagKey = "radius";

    public ColorfulFireBlockEntity( BlockPos pWorldPosition, BlockState pBlockState) {
        super(CommonProxy.COLORFUL_FIRE.get(), pWorldPosition, pBlockState);
    }

    public void setColor(int color){
        this.color = color;
    }

    public void setRadius(int radius){
        this.radius = radius;
    }

    public int getColor(){
        return color;
    }

    public int getRadius(){
        return radius;
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        if (pTag.contains(colorTagKey)){
            color = pTag.getInt(colorTagKey);
        }
        if (pTag.contains(radiusTagKey)){
            radius = pTag.getInt(radiusTagKey);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.putInt(colorTagKey, this.color);
        pTag.putInt(radiusTagKey, this.radius);
    }

    @Override
    @Nonnull
    public CompoundTag getUpdateTag() {
        CompoundTag compoundtag = super.getUpdateTag();
        compoundtag.putInt(colorTagKey,color);
        compoundtag.putInt(radiusTagKey,radius);
        return compoundtag;
    }
}
