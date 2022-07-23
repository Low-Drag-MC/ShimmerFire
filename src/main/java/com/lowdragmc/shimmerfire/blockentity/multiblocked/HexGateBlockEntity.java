package com.lowdragmc.shimmerfire.blockentity.multiblocked;

import com.lowdragmc.lowdraglib.client.particle.impl.TextureBeamParticle;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.utils.Vector3;
import com.lowdragmc.multiblocked.api.definition.ControllerDefinition;
import com.lowdragmc.multiblocked.api.pattern.FactoryBlockPattern;
import com.lowdragmc.multiblocked.api.pattern.Predicates;
import com.lowdragmc.multiblocked.api.pattern.util.RelativeDirection;
import com.lowdragmc.multiblocked.api.registry.MbdComponents;
import com.lowdragmc.multiblocked.api.tile.ControllerTileEntity;
import com.lowdragmc.multiblocked.client.renderer.impl.MBDBlockStateRenderer;
import com.lowdragmc.shimmer.client.postprocessing.PostProcessing;
import com.lowdragmc.shimmerfire.ShimmerFireMod;
import com.lowdragmc.shimmerfire.WorldData;
import com.lowdragmc.shimmerfire.api.RawFire;
import com.lowdragmc.shimmerfire.blockentity.FirePedestalBlockEntity;
import com.lowdragmc.shimmerfire.client.particle.FireSpiritParticle;
import com.lowdragmc.shimmerfire.client.particle.FireTailParticle;
import com.lowdragmc.shimmerfire.client.renderer.HexGateRenderer;
import com.lowdragmc.shimmerfire.gui.HexGateWidget;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author KilaBash
 * @date 2022/6/23
 * @implNote HexGateComponent
 */
public class HexGateBlockEntity extends ControllerTileEntity {

    private static final int COLD_START_ENERGY = 30000;

    @OnlyIn(Dist.CLIENT)
    public FireSpiritParticle fireParticle;

    public int preLeft;
    public int workingStage;
    @Nonnull
    public String gateName;
    @Nullable
    public BlockPos defaultDestination;
    @Nullable
    public BlockPos destination;

    public HexGateBlockEntity(ControllerDefinition definition, BlockPos pos, BlockState state) {
        super(definition, pos, state);
        gateName = "(%s)".formatted(pos.toShortString());
    }

    protected void writeGateInfo(FriendlyByteBuf buffer) {
        buffer.writeUtf(gateName);
        buffer.writeBoolean(defaultDestination != null);
        if (defaultDestination != null) {
            buffer.writeBlockPos(defaultDestination);
        }
    }

    protected void readGateInfo(FriendlyByteBuf buffer) {
        gateName = buffer.readUtf();
        defaultDestination = null;
        if (buffer.readBoolean()) {
            defaultDestination = buffer.readBlockPos();
        }
    }

    @Override
    public void writeInitialSyncData(FriendlyByteBuf buf) {
        super.writeInitialSyncData(buf);
        writeGateInfo(buf);
    }

    @Override
    public void receiveInitialSyncData(FriendlyByteBuf buf) {
        super.receiveInitialSyncData(buf);
        readGateInfo(buf);
    }

    @Override
    public void load(@NotNull CompoundTag compound) {
        super.load(compound);
        gateName = compound.contains("gateName") ? compound.getString("gateName") : gateName;
        defaultDestination = null;
        if (compound.contains("destination")) {
            defaultDestination = NbtUtils.readBlockPos(compound.getCompound("destination"));
        }
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag compound) {
        super.saveAdditional(compound);
        compound.putString("gateName", gateName);
        if (defaultDestination != null) {
            compound.put("destination", NbtUtils.writeBlockPos(defaultDestination));
        }
    }

    public void setGateInfo(String gateName, BlockPos destination) {
        if (!isRemote()) {
            this.gateName = gateName;
            this.defaultDestination = destination;
            markAsDirty();
            writeCustomData(21, this::writeGateInfo);
            if (isFormed()) {
                WorldData.getOrCreate(level).addGate(this);
            }
        }
    }

    @Override
    public void updateFormed() {
        if (isPreWorking()) {
            if (getTimer() % 10 == 0) {
                int lastLeft = preLeft;
                List<BlockPos> emitters = new ArrayList<>();
                for (FirePedestalBlockEntity pedestal : WorldData.getOrCreate(level).getAroundPedestals(getBlockPos(), 16)) {
                    int extracted = pedestal.extractInner(RawFire.ARCANE, Math.min(500, preLeft), false);
                    if (extracted > 0) {
                        preLeft -= extracted;
                        emitters.add(pedestal.getBlockPos());
                        if (preLeft == 0) {
                            setStatus("working");
                            workingStage = 0;
                            sendWorkingStage();
                            break;
                        }
                    }
                }
                emitClient(emitters);
                if (preLeft == lastLeft) {
                    workingStage = 0;
                    setStatus("idle");
                }
            }
        } else if (isWorking()) {
            workingStage++;
            if (workingStage % 10 == 0) {
                sendWorkingStage();
            }
            if (workingStage == 59) {
                workingStage = 0;
                setStatus("post_working");
                sendWorkingStage();
            }
        } else if (isPostWorking()) {
            workingStage++;
            if (workingStage >= 10 && workingStage % 10 == 0 && destination != null && getLevel().getBlockEntity(destination) instanceof HexGateBlockEntity destHexGate) {
                Direction front = getFrontFacing();
                BlockPos from = getBlockPos().relative(front, 2);
                List<Entity> entities = level.getEntities(null, new AABB(
                        Vec3.atCenterOf(from.above().relative(front.getClockWise())),
                        Vec3.atCenterOf(from.below().relative(front.getCounterClockWise()).relative(front, 256)))
                );
                float rotate = 0;
                Direction toFront = destHexGate.getFrontFacing();
                while (toFront != front) {
                    toFront = toFront.getCounterClockWise();
                    rotate += Math.PI / 2;
                }
                for (Entity entity : entities) {
                    if (entity.isAlive()) {
                        Vector3 vec = new Vector3(entity.position()).subtract(new Vector3(getBlockPos()).add(0.5));
                        vec.rotate(-rotate, Vector3.Y);
                        vec = new Vector3(destHexGate.getBlockPos()).add(0.5).add(vec);
                        if (!entity.isPassenger()) {
                            entity.moveTo(vec.vec3());
                        }
                        this.level.playSound(null, vec.x, vec.y, vec.z, SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, 1.0F);
                    }
                }
            }
            if (workingStage == 59) {
                workingStage = 0;
                setStatus("idle");
            }
        } else if (isIdle() && workingStage < 0){
            workingStage--;
            if (workingStage == -30) {
                setStatus("pre_working");
                workingStage = 0;
                preLeft = COLD_START_ENERGY;
            }
        }
    }

    public void sendWorkingStage() {
        writeCustomData(20, buffer -> buffer.writeVarInt(workingStage));
    }

    public void emitClient(Collection<BlockPos> poses) {
        if (poses.size() == 0) return;
        writeCustomData(19, buffer -> {
            buffer.writeVarInt(poses.size());
            for (BlockPos pos : poses) {
                buffer.writeBlockPos(pos);
            }
            buffer.writeVarInt(preLeft);
        });
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void receiveCustomData(int dataId, FriendlyByteBuf buf) {
        if (dataId == 21) {
            readGateInfo(buf);
        } else if (dataId == 20) {
            workingStage = buf.readVarInt();
        } else if (dataId == 19) {
            BlockPos to = getBlockPos().relative(getFrontFacing(), 3);
            Direction toFace = getFrontFacing();
            for (int i = buf.readVarInt(); i > 0; i--) {
                BlockPos from = buf.readBlockPos().relative(Direction.UP, 2);
                if (level instanceof ClientLevel clientLevel) {
                    new FireTailParticle(clientLevel, new Vector3(from).add(0.5), new Vector3(to).add(0.5), Direction.UP, toFace, 50, RawFire.ARCANE).addParticle();
                }
            }
            preLeft = buf.readVarInt();
            updateParticle();
        } else {
            super.receiveCustomData(dataId, buf);
        }
        if (dataId == 1) {
            if (isPreWorking()) {
                preLeft = COLD_START_ENERGY;
            }
            if (isPostWorking() && level instanceof ClientLevel clientLevel) {
                BlockPos from = getBlockPos().relative(getFrontFacing(), 2);
                TextureBeamParticle beamParticle = new TextureBeamParticle(clientLevel,
                        new Vector3(from).add(0.5),
                        new Vector3(from.relative(getFrontFacing(), 256)).add(0.5));
                beamParticle.setTexture(new ResourceLocation(ShimmerFireMod.MODID, "textures/particle/laser.png"));
                beamParticle.setFullLight();
                beamParticle.setLifetime(50);
                beamParticle.setEmit(0.3F);
                beamParticle.setColor(
                        (RawFire.ARCANE.colorVale >> 16 & 0xff)/256f,
                        (RawFire.ARCANE.colorVale >> 8 & 0xff)/256f,
                        (RawFire.ARCANE.colorVale & 0xff)/256f);
                PostProcessing.BLOOM_UNREAL.postParticle(beamParticle);
            }
            updateParticle();
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void updateParticle() {
        if (isPreWorking() || isWorking() || isPostWorking()) {
            if (level instanceof ClientLevel clientLevel) {
                if (fireParticle == null || !fireParticle.isAlive()) {
                    BlockPos pos = getBlockPos();
                    BlockPos from = pos.relative(getFrontFacing(), 2);
                    fireParticle = new FireSpiritParticle(clientLevel, from.getX() + 0.5, from.getY() + 0.5f, from.getZ() + 0.5);
                    fireParticle.setAliveCondition(particle -> {
                        if (particle.getLevel() != null && particle.getLevel().getBlockEntity(pos) instanceof HexGateBlockEntity hexGate) {
                            return hexGate.fireParticle == particle;
                        }
                        return false;
                    });
                    fireParticle.setLight(0xf000f0);
                    fireParticle.setColor(
                            (RawFire.ARCANE.colorVale >> 16 & 0xff)/256f,
                            (RawFire.ARCANE.colorVale >> 8 & 0xff)/256f,
                            (RawFire.ARCANE.colorVale & 0xff)/256f);
                    fireParticle.setLifetime(-1);
                    PostProcessing.BLOOM_UNREAL.postParticle(fireParticle);
                }
                if (fireParticle != null) {
                    float scale = preLeft * 1f / COLD_START_ENERGY;
                    fireParticle.scale(1.5f * (1 - scale));
                }
            }
        } else {
            if (fireParticle != null) {
                fireParticle.remove();
                fireParticle = null;
            }
        }
    }

    public boolean isPreWorking() {
        return getStatus().equals("pre_working");
    }

    public boolean isPostWorking() {
        return getStatus().equals("post_working");
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
        WorldData.getOrCreate(level).addGate(this);
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        WorldData.getOrCreate(level).removeGate(this);
    }

    @Override
    public ModularUI createUI(Player entityPlayer) {
        if (isFormed()) {
            if (isIdle()) {
                return new ModularUI(200, 200, this, entityPlayer).widget(new HexGateWidget(this));
            } else {
                return null;
            }
        } else {
            return super.createUI(entityPlayer);
        }
    }

    @Override
    public void onNeighborChange() {
        super.onNeighborChange();
        if (!isRemote() && isFormed() && isIdle() && workingStage >= 0) {
            if (level != null && level.hasNeighborSignal(getBlockPos())) {
                if (defaultDestination != null ) {
                    if (!go(defaultDestination)) {
                        setGateInfo(gateName, null);
                    }
                }
            }
        }
    }

    public boolean go(BlockPos destination) {
        if (destination != null && isIdle() && workingStage >= 0) {
            if (level != null && level.getBlockEntity(destination) instanceof HexGateBlockEntity) {
                workingStage = -1;
                this.destination = destination;
                return true;
            }
        }
        return false;
    }

    @NotNull
    @Override
    public AABB getRenderBoundingBox() {
        BlockPos pos = getBlockPos();
        Direction dir = getFrontFacing().getClockWise();
        return new AABB(
                pos.relative(dir, 5).relative(Direction.UP, 5),
                pos.relative(dir.getOpposite(), 5).relative(Direction.DOWN, 5).relative(getFrontFacing(), isIdle() ? 2 : 20));
    }

    public final static ControllerDefinition HEX_GATE_DEFINITION = new ControllerDefinition(new ResourceLocation(ShimmerFireMod.MODID, "hex_gate_controller"), HexGateBlockEntity.class);

    public static void registerHexGate() {

        HEX_GATE_DEFINITION.basePattern = FactoryBlockPattern.start(RelativeDirection.DOWN, RelativeDirection.BACK, RelativeDirection.RIGHT)
                .aisle("  AAAAA  ")
                .aisle(" ABBBBBA ")
                .aisle("ABBBBBBBA")
                .aisle("ABBCCCBBA")
                .aisle("ABBC@CBBA")
                .aisle("ABBCCCBBA")
                .aisle("ABBBBBBBA")
                .aisle(" ABBBBBA ")
                .aisle("  AAAAA  ")
                .where(' ', Predicates.any())
                .where('@', Predicates.component(HEX_GATE_DEFINITION))
                .where('A', Predicates.blocks(Blocks.ORANGE_TERRACOTTA).disableRenderFormed())
                .where('B', Predicates.blocks(Blocks.BLUE_STAINED_GLASS_PANE).disableRenderFormed())
                .where('C', Predicates.blocks(Blocks.POLISHED_BLACKSTONE).disableRenderFormed())
                .build();

        HEX_GATE_DEFINITION.baseRenderer = new MBDBlockStateRenderer(Blocks.RAW_GOLD_BLOCK.defaultBlockState());
        HEX_GATE_DEFINITION.formedRenderer = new HexGateRenderer();
        HEX_GATE_DEFINITION.properties.tabGroup = "shimmerfire.all";

        MbdComponents.registerComponent(HEX_GATE_DEFINITION);
    }

}
