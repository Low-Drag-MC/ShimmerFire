package com.lowdragmc.shimmerfire.blockentity;

import com.lowdragmc.shimmerfire.CommonProxy;
import com.lowdragmc.shimmerfire.block.MimicDissolveBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class MimicDissolveBlockEntity extends SyncedBlockEntity {

    public long time = System.currentTimeMillis();
    public BlockState mimicBlockState = Blocks.GRASS_BLOCK.defaultBlockState();
    private float progress = 0f;
    private static final float animationTotalTime = 5000f;

    private static final String TIME_TAG_KEY = "time";
    private static final String MIMIC_BLOCK_STATE_KEY = "mimic_blockstate";

    public Boolean isSelfDestroy() {
        return this.getBlockState().getValue(MimicDissolveBlock.MIMIC_SELF_DESTROY_STATE);
    }

    public void resetProgress() {
        if (!isSelfDestroy()) {
            progress = 0f;
        } else {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * called both from sever
     *
     * @return ture if continue rendering , else for to be removed
     * @see MimicDissolveBlock.MimicDissolveBlockTicker#tick(Level, BlockPos, BlockState, MimicDissolveBlockEntity)
     * <p>
     * from client
     * @see com.lowdragmc.shimmerfire.client.renderer.MimicDissolveRender#render(MimicDissolveBlockEntity, float, PoseStack, MultiBufferSource, int, int)
     */
    public boolean updateProgress() {
        progress = (System.currentTimeMillis() - time) / animationTotalTime;
        if (progress >= 1) {
            if (isSelfDestroy()) {
                return false;
            } else {
                progress -= Math.floor(progress);
            }
        }
        return true;
    }

    public float getProgress() {
        return progress;
    }

    public MimicDissolveBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(CommonProxy.MIMIC_DISSOLVE.get(), pWorldPosition, pBlockState);
    }

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        this.mimicBlockState = NbtUtils.readBlockState(tag.getCompound(MIMIC_BLOCK_STATE_KEY));
        this.time = tag.getLong(TIME_TAG_KEY);
    }

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        tag.put(MIMIC_BLOCK_STATE_KEY, NbtUtils.writeBlockState(mimicBlockState));
        tag.putLong(TIME_TAG_KEY, time);
    }

}
