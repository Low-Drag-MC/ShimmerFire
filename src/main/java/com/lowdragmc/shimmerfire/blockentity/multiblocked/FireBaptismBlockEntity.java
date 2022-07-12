package com.lowdragmc.shimmerfire.blockentity.multiblocked;

import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.utils.Vector3;
import com.lowdragmc.multiblocked.api.definition.ControllerDefinition;
import com.lowdragmc.multiblocked.api.pattern.FactoryBlockPattern;
import com.lowdragmc.multiblocked.api.pattern.Predicates;
import com.lowdragmc.multiblocked.api.registry.MbdComponents;
import com.lowdragmc.multiblocked.api.tile.ControllerTileEntity;
import com.lowdragmc.shimmerfire.ShimmerFireMod;
import com.lowdragmc.shimmerfire.api.Capabilities;
import com.lowdragmc.shimmerfire.api.IFireContainer;
import com.lowdragmc.shimmerfire.api.RawFire;
import com.lowdragmc.shimmerfire.client.particle.FireTailParticle;
import com.lowdragmc.shimmerfire.client.renderer.FireBaptismRenderer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SandBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * @author KilaBash
 * @date 2022/6/23
 * @implNote HexGateComponent
 */
public class FireBaptismBlockEntity extends ControllerTileEntity {

    public FireBaptismBlockEntity(ControllerDefinition definition, BlockPos pos, BlockState state) {
        super(definition, pos, state);
    }

    @Override
    public void writeInitialSyncData(FriendlyByteBuf buf) {
        super.writeInitialSyncData(buf);
    }

    @Override
    public void receiveInitialSyncData(FriendlyByteBuf buf) {
        super.receiveInitialSyncData(buf);
    }

    @Override
    public void load(@NotNull CompoundTag compound) {
        super.load(compound);
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag compound) {
        super.saveAdditional(compound);
    }

    @Override
    public void updateFormed() {
        if (getTimer() % 20 == 0) {
            var container = getContainer();
            if (container != null) {
                RawFire type = container.getFireType();
                if (type != null && container.extract(null, 100, false) >= 100) {
                    setStatus("working");
                    if (getTimer() % 40 == 0) {
                        working(type);
                    }
                    return;
                }
            }
            setStatus("idle");
        }
    }

    private void working(@Nonnull RawFire type) {
        int dur = 0;
        BlockPos blockPos = getBlockPos();
        while (dur <= 8) {
            dur++;
            blockPos = blockPos.below();
            if (!getLevel().isEmptyBlock(blockPos)) break;
        }
        AABB range =  new AABB(
                getBlockPos().relative(Direction.NORTH, 8).relative(Direction.EAST, 8),
                getBlockPos().relative(Direction.SOUTH, 8).relative(Direction.WEST, 8).below(dur)
        );
        if (type == RawFire.LIFE) {
            var entities = getLevel().getEntities((Entity) null, range, e -> e instanceof LivingEntity livingEntity && livingEntity.getHealth() != livingEntity.getMaxHealth());
            emitEntityParticle(type, entities);
            for (Entity entity : entities) {
                LivingEntity livingEntity = (LivingEntity) entity;
                livingEntity.clearFire();
                livingEntity.heal(8);
            }
        } else if (type == RawFire.DESTROY) {
            var entities = getLevel().getEntities((Entity) null, range, e -> e instanceof LivingEntity && !(e instanceof Player));
            emitEntityParticle(type, entities);
            for (Entity entity : entities) {
                LivingEntity livingEntity = (LivingEntity) entity;
                livingEntity.setSecondsOnFire(3);
                livingEntity.setHealth(livingEntity.getHealth() - 8);
            }
        } else if (type == RawFire.ARCANE && level instanceof ServerLevel serverLevel) {
            List<BlockPos> poses = new ArrayList<>();
            BlockPos.betweenClosedStream(range).forEach(pos->{
                BlockState blockState = level.getBlockState(pos);
                if (blockState.getBlock() instanceof CropBlock crop && !crop.isMaxAge(blockState) && serverLevel.random.nextFloat() < 0.3f) {
                    for (int i = 0; i < 10; i++) {
                        crop.randomTick(blockState, serverLevel, pos, serverLevel.random);
                    }
                    poses.add(pos.immutable());
                }
            });
            emitBlockParticle(type, poses);
        } else if (type == RawFire.ROT) {
            List<BlockPos> poses = new ArrayList<>();
            BlockPos.betweenClosedStream(range).forEach(pos->{
                BlockState blockState = level.getBlockState(pos);
                if (blockState.getBlock() instanceof SandBlock && level.random.nextFloat() < 0.3f) {
                    level.setBlockAndUpdate(pos, Blocks.GLASS.defaultBlockState());
                    poses.add(pos.immutable());
                }
            });
            emitBlockParticle(type, poses);
        }
    }

    public void emitBlockParticle(@Nonnull RawFire type, Collection<BlockPos> poses) {
        if (poses.size() == 0) return;
        writeCustomData(19, buffer -> {
            buffer.writeEnum(type);
            buffer.writeVarInt(poses.size());
            for (BlockPos pos : poses) {
                buffer.writeBlockPos(pos);
            }
        });
    }

    public void emitEntityParticle(@Nonnull RawFire type, Collection<Entity> entities) {
        if (entities.size() == 0) return;
        writeCustomData(20, buffer -> {
            buffer.writeEnum(type);
            buffer.writeVarInt(entities.size());
            for (Entity entity : entities) {
                buffer.writeVarInt(entity.getId());
            }
        });
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void receiveCustomData(int dataId, FriendlyByteBuf buf) {
        if (dataId == 20) {
            RawFire fire = buf.readEnum(RawFire.class);
            BlockPos from = getBlockPos();
            for (int i = buf.readVarInt(); i > 0; i--) {
                int id = buf.readVarInt();
                Entity entity = level.getEntity(id);
                if (level instanceof ClientLevel clientLevel && entity != null) {
                    new FireTailParticle(clientLevel, new Vector3(from).add(0.5), new Vector3(entity.position()).add(0, entity.getBbHeight() / 2, 0), Direction.DOWN, Direction.UP, 10, fire).setEntity(entity).addParticle();
                }
            }
        } else if (dataId == 19) {
            RawFire fire = buf.readEnum(RawFire.class);
            BlockPos from = getBlockPos();
            for (int i = buf.readVarInt(); i > 0; i--) {
                BlockPos to = buf.readBlockPos().below();
                if (level instanceof ClientLevel clientLevel) {
                    new FireTailParticle(clientLevel, new Vector3(from).add(0.5), new Vector3(to).add(0.5), Direction.DOWN, Direction.UP, 10, fire).addParticle();
                }
            }
        } else {
            super.receiveCustomData(dataId, buf);
        }
    }

    public boolean isIdle() {
        return getStatus().equals("idle");
    }

    public boolean isWorking() {
        return getStatus().equals("working");
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        recipeLogic = null;
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
    }

    @Override
    public ModularUI createUI(Player entityPlayer) {
        if (isFormed()) {
            return null;
        } else {
            return super.createUI(entityPlayer);
        }
    }

    public IFireContainer getContainer() {
        BlockEntity blockEntity = level.getBlockEntity(getBlockPos().above());
        if (blockEntity != null) {
            var cap = blockEntity.getCapability(Capabilities.FIRE_CONTAINER_CAPABILITY, Direction.DOWN);
            return cap.orElse(null);
        }
        return null;
    }

    public RawFire getFireType() {
        IFireContainer container = getContainer();
        return container == null ? null : container.getFireType();
    }

    @NotNull
    @Override
    public AABB getRenderBoundingBox() {
        return new AABB(
                getBlockPos().relative(Direction.NORTH, 8).relative(Direction.EAST, 8),
                getBlockPos().relative(Direction.SOUTH, 8).relative(Direction.WEST, 8).below(8)
        );
    }

    public final static ControllerDefinition FIRE_BAPTISM_DEFINITION = new ControllerDefinition(new ResourceLocation(ShimmerFireMod.MODID, "fire_baptism"), FireBaptismBlockEntity.class);

    public static void registerFireBaptism() {

        FIRE_BAPTISM_DEFINITION.basePattern = FactoryBlockPattern.start()
                .aisle("@")
                .where('@', Predicates.component(FIRE_BAPTISM_DEFINITION))
                .build();

        FIRE_BAPTISM_DEFINITION.properties.isOpaque = false;
        FIRE_BAPTISM_DEFINITION.baseRenderer = new FireBaptismRenderer();
        FIRE_BAPTISM_DEFINITION.properties.tabGroup = "shimmerfire.all";

        MbdComponents.registerComponent(FIRE_BAPTISM_DEFINITION);
    }

}
