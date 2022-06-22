package com.lowdragmc.shimmerfire.blockentity;

import com.lowdragmc.lowdraglib.LDLMod;
import com.lowdragmc.shimmerfire.CommonProxy;
import com.lowdragmc.shimmerfire.api.Capabilities;
import com.lowdragmc.shimmerfire.api.IFireContainer;
import com.lowdragmc.shimmerfire.api.RawFire;
import com.lowdragmc.shimmerfire.block.*;
import com.lowdragmc.shimmerfire.client.particle.FireTailParticle;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

/**
 * @author KilaBash
 * @date 2022/05/12
 * @implNote FireContainerBlockEntity
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class FirePortBlockEntity extends SyncedBlockEntity {

    private long timer = LDLMod.random.nextLong();

    private final Map<BlockPos, FirePortBlockEntity> connected = new HashMap<>();

    public FirePortBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(CommonProxy.FIRE_PORT.get(), pWorldPosition, pBlockState);
    }

    public Direction getFace() {
        return getBlockState().getValue(FirePortBlock.FACING);
    }

    public boolean isEmitter() {
        return getBlockState().getBlock() instanceof FireEmitterBlock;
    }

    public boolean isReceiver() {
        return getBlockState().getBlock() instanceof FireReceiverBlock;
    }

    @Nullable
    public IFireContainer getContainer() {
        BlockEntity blockEntity = level.getBlockEntity(getBlockPos().relative(getFace().getOpposite()));
        if (blockEntity != null) {
            return blockEntity.getCapability(Capabilities.FIRE_CONTAINER_CAPABILITY, getFace()).orElse(null);
        }
        return null;
    }

    @Nullable
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
                }
            }
        }
        return connect;
    }

    @Nullable
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
        if (getConnect(connect.getBlockPos()) != connect) {
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
    protected void write(CompoundTag compound, boolean clientPacket) {
        ListTag listTag = new ListTag();
        for (BlockPos pos : connected.keySet()) {
            listTag.add(NbtUtils.writeBlockPos(pos));
        }
        compound.put("con", listTag);
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        connected.clear();
        ListTag listTag = compound.getList("con", Tag.TAG_COMPOUND);
        for (Tag tag : listTag) {
            connected.put(NbtUtils.readBlockPos((CompoundTag) tag), null);
        }
    }

    public void emitClient(BlockPos pos, RawFire fire) {
        writeCustomData(0, buffer -> {
            buffer.writeBlockPos(pos);
            buffer.writeEnum(fire);
        });
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    protected void receiveCustomData(int dataId, FriendlyByteBuf buf) {
        if (dataId == 0) {
            BlockPos pos = buf.readBlockPos();
            RawFire fire = buf.readEnum(RawFire.class);
            BlockPos from = getBlockPos();
            Direction fromFace = getFace();
            FirePortBlockEntity to = getConnect(pos);
            if (to != null && level instanceof ClientLevel clientLevel) {
                new FireTailParticle(clientLevel, from, fromFace, to.getBlockPos(), to.getFace(), 50, fire).addParticle();
            }
        }
    }

    public void emitTick() {
        timer++;
        if (timer % 50 == 0) {
            int left = 500;
            IFireContainer source = getContainer();
            if (source != null && source.getFireType() != null) {
                RawFire fire = source.getFireType();
                for (BlockPos pos : connected.keySet()) {
                    FirePortBlockEntity to = getConnect(pos);
                    if (to != null && to.isReceiver()) {
                        IFireContainer destination = to.getContainer();
                        if (destination != null) {
                            int inserted = destination.insert(fire, source.extract(null, left, true), false);
                            source.extract(null, inserted, false);
                            left -= inserted;
                            if (inserted > 0) {
                                emitClient(pos, fire);
                            }
                        }
                    }
                    if (left <= 0) break;
                }
            }
        }
    }


    public void destroy() {
        if (!level.isClientSide) {
            for (BlockPos pos : connected.keySet()) {
                FirePortBlockEntity to = getConnect(pos);
                if (to != null) {
                    to.connected.remove(getBlockPos());
                    to.notifyUpdate();
                }
            }
        }
    }

}
