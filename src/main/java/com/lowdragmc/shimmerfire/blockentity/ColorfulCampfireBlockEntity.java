package com.lowdragmc.shimmerfire.blockentity;

import com.lowdragmc.shimmerfire.CommonProxy;
import com.lowdragmc.shimmerfire.utils.ShimmerFireUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Clearable;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;

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

    /**
     * Get an NBT compound to sync to the client with SPacketChunkData, used for initial loading of the chunk or when
     * many blocks change at once. This compound comes back to you clientside in
     */
    @Override
    @Nonnull
    public CompoundTag getUpdateTag() {
        CompoundTag compoundtag = super.getUpdateTag();
        compoundtag.putInt(colorTagKey,color);
        compoundtag.putInt(radiusTagKey,radius);
        return compoundtag;
    }

    public void setColor(int color) {
        if (this.color!=color){
            this.color = color;
            if (!level.isClientSide){
//                level.sendBlockUpdated(worldPosition,bloc);
            }else {
                ShimmerFireUtils.setSingleBlocksDirty(worldPosition);
            }
        }
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public int getColor() {
        return color;
    }

    public int getRadius() {
        return radius;
    }
}
