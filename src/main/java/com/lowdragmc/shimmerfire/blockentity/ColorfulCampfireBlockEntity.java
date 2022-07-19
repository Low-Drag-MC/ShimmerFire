package com.lowdragmc.shimmerfire.blockentity;

import com.lowdragmc.shimmerfire.CommonProxy;
import com.lowdragmc.shimmerfire.utils.ShimmerFireUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Clearable;
import net.minecraft.world.level.block.state.BlockState;

import static com.lowdragmc.shimmerfire.blockentity.ColorfulFireBlockEntity.colorTagKey;
import static com.lowdragmc.shimmerfire.blockentity.ColorfulFireBlockEntity.radiusTagKey;

/**
 * @author KilaBash
 * @date 2022/05/09
 * @implNote ColorfulCampfireBlockEntity copy from {@link net.minecraft.world.level.block.entity.CampfireBlockEntity}
 */
public class ColorfulCampfireBlockEntity extends ColoredCampfireBlockEntity implements Clearable {
    private int color;
    private int radius;

    public ColorfulCampfireBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(CommonProxy.COLORFUL_CAMPFIRE.get(), pWorldPosition, pBlockState);
    }

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        setRadius(tag.getInt(radiusTagKey));
        setColor(tag.getInt(colorTagKey));
    }

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.putInt(colorTagKey, this.color);
        tag.putInt(radiusTagKey, this.radius);
    }

    public void setColor(int color) {
        this.color = color;
        if (level != null && level.isClientSide){
            ShimmerFireUtils.setSingleBlocksDirty(worldPosition);
        }
    }

    public void setRadius(int radius) {
        this.radius = radius;
        if (level != null && level.isClientSide){
            ShimmerFireUtils.setSingleBlocksDirty(worldPosition);
        }
    }

    public int getColor() {
        return color;
    }

    public int getRadius() {
        return radius;
    }
}
