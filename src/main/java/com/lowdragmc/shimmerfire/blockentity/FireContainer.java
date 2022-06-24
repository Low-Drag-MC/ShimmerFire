package com.lowdragmc.shimmerfire.blockentity;

import com.lowdragmc.multiblocked.api.capability.IInnerCapabilityProvider;
import com.lowdragmc.shimmerfire.api.Capabilities;
import com.lowdragmc.shimmerfire.api.IFireContainer;
import com.lowdragmc.shimmerfire.api.RawFire;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * @author KilaBash
 * @date 2022/6/23
 * @implNote FireContainer
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public abstract class FireContainer extends SyncedBlockEntity implements IFireContainer, IInnerCapabilityProvider {
    protected int stored;
    protected RawFire rawFire;

    public FireContainer(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public int getStored() {
        return rawFire == null ? 0 : stored;
    }

    @Override
    public int extract(@Nullable RawFire fire, int heat, boolean simulate) {
        if (rawFire == null) return 0;
        if (fire != null && getFireType() != fire) return 0;
        int extracted = Math.min(heat, stored);
        if (!simulate) {
            stored -= extracted;
            if (stored == 0) {
                rawFire = null;
            }
            notifyUpdate();
        }
        return extracted;
    }

    @Override
    public int insert(@Nullable RawFire fire, int heat, boolean simulate) {
        if (rawFire == null) {
            if (fire == null) {
                return 0;
            } else {
                rawFire = fire;
                stored = 0;
            }
        }
        if (fire != null && fire != getFireType()) {
            heat /= 2;
        }
        int inserted = Math.min(getCapacity(), stored + heat) - stored;
        if (!simulate) {
            stored += inserted;
            notifyUpdate();
        }
        return (fire != null && fire != getFireType()) ? inserted * 2 : inserted;
    }

    @Override
    public @Nullable RawFire getFireType() {
        return rawFire;
    }

    @Override
    public void setFireType(RawFire fire) {
        this.rawFire = fire;
        notifyUpdate();
    }

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        if (rawFire != null) {
            tag.putString("f", rawFire.name());
            tag.putInt("s", stored);
        }
    }

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        rawFire = null;
        stored = 0;
        if (tag.contains("f")) {
            rawFire = RawFire.valueOf(tag.getString("f"));
            stored = tag.getInt("s");
        }
    }

    public void scheduleChunkForRenderUpdate() {
        if (level != null) {
            BlockState state = getBlockState();
            level.sendBlockUpdated(getBlockPos(), state, state, 1 << 3);
        }
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == Capabilities.FIRE_CONTAINER_CAPABILITY) {
            return Capabilities.FIRE_CONTAINER_CAPABILITY.orEmpty(cap, LazyOptional.of(() -> this));
        }
        return super.getCapability(cap, side);
    }

    @Override
    public <T> LazyOptional<T> getInnerCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == Capabilities.FIRE_CONTAINER_CAPABILITY) {
            return Capabilities.FIRE_CONTAINER_CAPABILITY.orEmpty(cap, LazyOptional.of(() -> this));
        }
        return getCapability(cap, side);
    }
}
