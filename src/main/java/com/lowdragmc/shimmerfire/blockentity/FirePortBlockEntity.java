package com.lowdragmc.shimmerfire.blockentity;

import com.lowdragmc.shimmerfire.CommonProxy;
import com.lowdragmc.shimmerfire.block.FireEmitterBlock;
import com.lowdragmc.shimmerfire.block.FireReceiverBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * @author KilaBash
 * @date 2022/05/12
 * @implNote FireContainerBlockEntity
 */
public class FirePortBlockEntity extends SyncedBlockEntity {

    private final Map<BlockPos, FirePortBlockEntity> connected = new HashMap<>();

    public FirePortBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(CommonProxy.FIRE_PORT.get(), pWorldPosition, pBlockState);
    }

    public boolean isEmitter() {
        return getBlockState().getBlock() instanceof FireEmitterBlock;
    }

    public boolean isReceiver() {
        return getBlockState().getBlock() instanceof FireReceiverBlock;
    }

    public FirePortBlockEntity getConnect(BlockPos pos) {
        FirePortBlockEntity connect = connected.get(pos);
        if (connect != null && connect.isRemoved()) {
            connect = null;
            connected.put(pos, null);
        }
        if (connect == null) {
            if (getLevel().isLoaded(pos)) {
                BlockEntity blockEntity = getLevel().getBlockEntity(pos);
                if (blockEntity instanceof FirePortBlockEntity firePort && firePort.connected.containsKey(this.getBlockPos())) {
                    connected.put(pos, connect = firePort);
                } else {
                    connected.remove(pos);
                    notifyUpdate();
                }
            }
        }
        return connect;
    }

    public static Boolean bindConnect(FirePortBlockEntity emitter, FirePortBlockEntity receiver) {
        if (emitter.isEmitter() && receiver.isReceiver()) {
            if (!emitter.addConnect(receiver)) {
                emitter.removeConnect(receiver);
                return false;
            }
            return true;
        }
        return null;
    }

    public boolean addConnect(FirePortBlockEntity connect) {
        if ((connect.isReceiver() && isReceiver()) || (connect.isEmitter() && isEmitter())) return false;
        if (!connected.containsKey(connect.getBlockPos())) {
            connected.put(connect.getBlockPos(), connect);
            connect.connected.put(this.getBlockPos(), this);
            notifyUpdate();
            connect.notifyUpdate();
            return true;
        }
        return false;
    }

    public boolean removeConnect(FirePortBlockEntity connect) {
        if ((connect.isReceiver() && isReceiver()) || (connect.isEmitter() && isEmitter())) return false;
        if (connected.containsKey(connect.getBlockPos())) {
            connected.remove(connect.getBlockPos());
            connect.connected.remove(this.getBlockPos());
            notifyUpdate();
            connect.notifyUpdate();
            return true;
        }
        return false;
    }

    @Override
    protected void write(@Nonnull CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        ListTag listTag = new ListTag();
        for (BlockPos pos : connected.keySet()) {
            listTag.add(NbtUtils.writeBlockPos(pos));
        }
        compound.put("connected", listTag);
    }

    @Override
    protected void read(@Nonnull CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        ListTag listTag = compound.getList("connected", Tag.TAG_COMPOUND);
        for (Tag tag : listTag) {
            connected.put(NbtUtils.readBlockPos((CompoundTag) tag), null);
        }
    }

    public static void emitTick(Level level, BlockPos pos, BlockState blockState, FirePortBlockEntity emitter) {

    }
}
