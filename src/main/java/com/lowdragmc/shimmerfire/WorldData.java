package com.lowdragmc.shimmerfire;

import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import com.lowdragmc.lowdraglib.utils.DummyWorld;
import com.lowdragmc.shimmerfire.blockentity.FirePedestalBlockEntity;
import com.lowdragmc.shimmerfire.blockentity.multiblocked.HexGateBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import org.apache.commons.compress.utils.Lists;

import javax.annotation.Nonnull;
import java.lang.ref.WeakReference;
import java.util.*;

/**
 * @author KilaBash
 * @date 2022/6/24
 * @implNote WorldData
 */
public class WorldData extends SavedData {

    public final Table<ChunkPos, BlockPos, FirePedestalBlockEntity> LOADED_PEDESTAL = Tables.newCustomTable(new HashMap<>(), HashMap::new);
    public final Map<BlockPos, String> LOADED_HEX_GATE = new HashMap<>();

    private static final WorldData DUMMY = new WorldData(){
        @Override
        public void addPedestal(FirePedestalBlockEntity pedestal) {

        }

        @Override
        public void removePedestal(FirePedestalBlockEntity pedestal) {
        }

        @Override
        public void addGate(HexGateBlockEntity hexGateBlockEntity) {

        }

        @Override
        public void removeGate(HexGateBlockEntity hexGateBlockEntity) {

        }

        @Override
        public Collection<FirePedestalBlockEntity> getAroundPedestals(BlockPos pos, int radius) {
            return Collections.emptyList();
        }
    };

    private static WeakReference<Level> worldRef;


    public static WorldData getOrCreate(Level world) {
        if (world == null || world instanceof DummyWorld) {
            return DUMMY;
        }
        if (world instanceof ServerLevel) {
            worldRef = new WeakReference<>(world);
            WorldData worldData = ((ServerLevel) world).getDataStorage().computeIfAbsent(WorldData::new, WorldData::new, "ShimmerFire");
            worldRef = null;
            return worldData;
        }
        return DUMMY;
    }

    public WorldData() {
    }

    public WorldData(CompoundTag nbt) {
        this();
        if (nbt.contains("gates")) {
            ListTag list = nbt.getList("gates", Tag.TAG_COMPOUND);
            for (Tag tag : list) {
                CompoundTag compoundTag = (CompoundTag) tag;
                LOADED_HEX_GATE.put(NbtUtils.readBlockPos(compoundTag), compoundTag.getString("name"));
            }
        }
    }

    @Nonnull
    @Override
    public CompoundTag save(@Nonnull CompoundTag compound) {
        ListTag list = new ListTag();
        LOADED_HEX_GATE.forEach((pos, name) -> {
            CompoundTag tag = NbtUtils.writeBlockPos(pos);
            tag.putString("name", name);
            list.add(tag);
        });
        compound.put("gates", list);
        return compound;
    }

    public void addPedestal(FirePedestalBlockEntity pedestal) {
        ChunkPos chunkPos = new ChunkPos(pedestal.getBlockPos());
        LOADED_PEDESTAL.put(chunkPos, pedestal.getBlockPos(), pedestal);
    }

    public void removePedestal(FirePedestalBlockEntity pedestal) {
        ChunkPos chunkPos = new ChunkPos(pedestal.getBlockPos());
        LOADED_PEDESTAL.remove(chunkPos, pedestal.getBlockPos());
    }

    public Collection<FirePedestalBlockEntity> getAroundPedestals(BlockPos pos, int radius) {
        ChunkPos nMax = new ChunkPos(pos.relative(Direction.NORTH, radius));
        ChunkPos sMax = new ChunkPos(pos.relative(Direction.SOUTH, radius));
        ChunkPos eMax = new ChunkPos(pos.relative(Direction.EAST, radius));
        ChunkPos wMax = new ChunkPos(pos.relative(Direction.WEST, radius));
        List<FirePedestalBlockEntity> found = Lists.newArrayList();
        ChunkPos.rangeClosed(new ChunkPos(wMax.x, nMax.z), new ChunkPos(eMax.x, sMax.z)).forEach(chunkPos ->
                LOADED_PEDESTAL.row(chunkPos).forEach((pos1, entity) -> {
                    if (pos.closerThan(pos1, radius)) {
                        found.add(entity);
                    }
        }));
        return found;
    }

    public void removeGate(HexGateBlockEntity hexGateBlockEntity) {
        LOADED_HEX_GATE.remove(hexGateBlockEntity.getBlockPos());
        setDirty();
    }

    public void addGate(HexGateBlockEntity hexGateBlockEntity) {
        LOADED_HEX_GATE.put(hexGateBlockEntity.getBlockPos(), hexGateBlockEntity.gateName);
        setDirty();
    }
}
