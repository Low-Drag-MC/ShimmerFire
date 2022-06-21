package com.lowdragmc.shimmerfire.blockentity;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author KilaBash
 * @date 2022/6/17
 * @implNote SyncedBlockEntity
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class SyncedBlockEntity extends BlockEntity {
    private boolean syncAll;

    public SyncedBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
    public final CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        write(tag, true);
        return tag;
    }

    public final ClientboundBlockEntityDataPacket getUpdatePacket() {
        CompoundTag updateTag = new CompoundTag();
        if (syncAll) {
            syncAll = false;
            updateTag.put("s", getUpdateTag());
        }
        if (!updateEntries.isEmpty()) {
            ListTag tagList = new ListTag();
            for (UpdateEntry updateEntry : updateEntries) {
                CompoundTag entryTag = new CompoundTag();
                entryTag.putInt("i", updateEntry.discriminator);
                entryTag.putByteArray("d", updateEntry.updateData);
                tagList.add(entryTag);
            }
            this.updateEntries.clear();
            updateTag.put("d", tagList);
        }
        return ClientboundBlockEntityDataPacket.create(this, blockEntity -> updateTag);
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
        CompoundTag updateTag = packet.getTag();
        if (updateTag == null) return;
        if (updateTag.contains("d")) {
            ListTag tagList = updateTag.getList("d", Tag.TAG_COMPOUND);
            for (int i = 0; i < tagList.size(); i++) {
                CompoundTag entryTag = tagList.getCompound(i);
                int discriminator = entryTag.getInt("i");
                byte[] updateData = entryTag.getByteArray("d");
                ByteBuf backedBuffer = Unpooled.copiedBuffer(updateData);
                receiveCustomData(discriminator, new FriendlyByteBuf(backedBuffer));
            }
        }
        if (updateTag.contains("s")) {
            this.read(updateTag.getCompound("s"), true);
        }
    }

    public void syncData() {
        if (this.level != null && !this.level.isClientSide) {
            syncAll = true;
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
        this.syncData();
    }

    protected void write(CompoundTag tag, boolean clientPacket) {

    }

    protected void read(CompoundTag tag, boolean clientPacket) {

    }

    private static class UpdateEntry {
        private final int discriminator;
        private final byte[] updateData;

        public UpdateEntry(int discriminator, byte[] updateData) {
            this.discriminator = discriminator;
            this.updateData = updateData;
        }
    }

    protected final List<UpdateEntry> updateEntries = new ArrayList<>();

    protected void writeCustomData(int discriminator, Consumer<FriendlyByteBuf> dataWriter) {
        ByteBuf backedBuffer = Unpooled.buffer();
        dataWriter.accept(new FriendlyByteBuf(backedBuffer));
        byte[] updateData = Arrays.copyOfRange(backedBuffer.array(), 0, backedBuffer.writerIndex());
        updateEntries.add(new UpdateEntry(discriminator, updateData));
        BlockState state = getBlockState();
        if (level != null) {
            level.sendBlockUpdated(getBlockPos(), state, state, 8);
        }
    }

    @OnlyIn(Dist.CLIENT)
    protected void receiveCustomData(int dataId, FriendlyByteBuf buf) {

    }
}
