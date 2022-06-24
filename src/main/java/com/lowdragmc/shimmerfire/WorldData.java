package com.lowdragmc.shimmerfire;

import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import com.lowdragmc.lowdraglib.utils.DummyWorld;
import com.lowdragmc.shimmerfire.blockentity.FirePedestalBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import org.apache.commons.compress.utils.Lists;

import javax.annotation.Nonnull;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * @author KilaBash
 * @date 2022/6/24
 * @implNote WorldData
 */
public class WorldData extends SavedData {

    public final Table<ChunkPos, BlockPos, FirePedestalBlockEntity> LOADED_PEDESTAL = Tables.newCustomTable(new HashMap<>(), HashMap::new);

    private static final WorldData DUMMY = new WorldData(){
        @Override
        public void addPedestal(FirePedestalBlockEntity pedestal) {

        }

        @Override
        public void removePedestal(FirePedestalBlockEntity pedestal) {
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
    }

    @Nonnull
    @Override
    public CompoundTag save(@Nonnull CompoundTag compound) {
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
}
