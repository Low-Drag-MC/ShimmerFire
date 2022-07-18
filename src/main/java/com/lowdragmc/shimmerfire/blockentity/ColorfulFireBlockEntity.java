package com.lowdragmc.shimmerfire.blockentity;

import com.lowdragmc.shimmerfire.CommonProxy;
import com.lowdragmc.shimmerfire.utils.ShimmerFireUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import org.lwjgl.system.CallbackI;

public class ColorfulFireBlockEntity extends SyncedBlockEntity {

    private int color;
    private int radius;

    public static final String colorTagKey = "color";
    public static final String radiusTagKey = "radius";

    public ColorfulFireBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(CommonProxy.COLORFUL_FIRE.get(), pWorldPosition, pBlockState);
    }

    public void setColor(int color) {
        this.color = color;
        notifyUpdate();
        if (level.isClientSide){
            ShimmerFireUtils.setSingleBlocksDirty(worldPosition);
        }
    }

    public void setRadius(int radius) {
        this.radius = radius;
        notifyUpdate();
        if (level.isClientSide){
            ShimmerFireUtils.setSingleBlocksDirty(worldPosition);
        }
    }

    public int getColor() {
        return color;
    }

    public int getRadius() {
        return radius;
    }

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        setRadius(tag.getInt(radiusTagKey));
        setColor(tag.getInt(colorTagKey));
    }

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        tag.putInt(colorTagKey, this.color);
        tag.putInt(radiusTagKey, this.radius);
    }

}
