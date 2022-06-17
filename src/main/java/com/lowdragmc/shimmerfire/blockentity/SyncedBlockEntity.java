package com.lowdragmc.shimmerfire.blockentity;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * @author KilaBash
 * @date 2022/6/17
 * @implNote SyncedBlockEntity
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class SyncedBlockEntity extends BlockEntity {

    public SyncedBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public final CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        write(tag, true);
        return tag;
    }

    public final ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public final void saveAdditional(@Nonnull CompoundTag tag) {
        this.write(tag, false);
    }

    public final void load(CompoundTag tag) {
        this.read(tag, false);
    }

    public final void handleUpdateTag(CompoundTag tag) {
        this.read(tag, true);
    }

    public final void onDataPacket(Connection connection, ClientboundBlockEntityDataPacket packet) {
        CompoundTag tag = packet.getTag();
        this.read(tag == null ? new CompoundTag() : tag, true);
    }

    public void sendData() {
        if (this.level != null && !this.level.isClientSide) {
            this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 22);
        }

    }

    public void causeBlockUpdate() {
        if (this.level != null) {
            this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 8);
        }

    }

    public void notifyUpdate() {
        this.setChanged();
        this.sendData();
    }

    protected void write(CompoundTag tag, boolean clientPacket) {

    }

    protected void read(CompoundTag tag, boolean clientPacket) {

    }
}
