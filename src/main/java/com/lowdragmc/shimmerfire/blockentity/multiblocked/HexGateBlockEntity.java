package com.lowdragmc.shimmerfire.blockentity.multiblocked;

import com.lowdragmc.lowdraglib.client.particle.impl.TextureBeamParticle;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.utils.Vector3;
import com.lowdragmc.multiblocked.Multiblocked;
import com.lowdragmc.multiblocked.api.definition.ControllerDefinition;
import com.lowdragmc.multiblocked.api.pattern.FactoryBlockPattern;
import com.lowdragmc.multiblocked.api.pattern.Predicates;
import com.lowdragmc.multiblocked.api.pattern.util.RelativeDirection;
import com.lowdragmc.multiblocked.api.registry.MbdComponents;
import com.lowdragmc.multiblocked.api.tile.ControllerTileEntity;
import com.lowdragmc.multiblocked.client.renderer.impl.MBDIModelRenderer;
import com.lowdragmc.shimmer.client.postprocessing.PostProcessing;
import com.lowdragmc.shimmerfire.ShimmerFireMod;
import com.lowdragmc.shimmerfire.WorldData;
import com.lowdragmc.shimmerfire.api.RawFire;
import com.lowdragmc.shimmerfire.blockentity.FirePedestalBlockEntity;
import com.lowdragmc.shimmerfire.client.particle.ColouredBeamParticle;
import com.lowdragmc.shimmerfire.client.particle.FireSpiritParticle;
import com.lowdragmc.shimmerfire.client.particle.FireTailParticle;
import com.lowdragmc.shimmerfire.client.renderer.HexGateRenderer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author KilaBash
 * @date 2022/6/23
 * @implNote HexGateComponent
 */
public class HexGateBlockEntity extends ControllerTileEntity {

    private static final int COLD_START_ENERGY = 100000;

    @OnlyIn(Dist.CLIENT)
    public FireSpiritParticle fireParticle;

    public HexGateBlockEntity(ControllerDefinition definition, BlockPos pos, BlockState state) {
        super(definition, pos, state);
    }

    public int preLeft;

    public int workingStage;

    @Override
    public void updateFormed() {
        if (isPreWorking()) {
            if (getTimer() % 10 == 0) {
                int lastLeft = preLeft;
                List<BlockPos> emitters = new ArrayList<>();
                for (FirePedestalBlockEntity pedestal : WorldData.getOrCreate(level).getAroundPedestals(getBlockPos(), 32)) {
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
            if (workingStage == 59) {
                workingStage = 0;
                setStatus("idle");
            }
        } else if (isIdle() && workingStage < 0){
            workingStage--;
            if (workingStage == -60) {
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
        if (dataId == 20) {
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
                ColouredBeamParticle beamParticle = new ColouredBeamParticle(clientLevel,
                        new Vector3(from).add(0.5),
                        new Vector3(from.relative(getFrontFacing(), 40)).add(0.5));
                beamParticle.setLifetime(50);
                beamParticle.setAlpha(0.6f);
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
    }

    @Override
    public ModularUI createUI(Player entityPlayer) {
       return null;
    }


    @Override
    public InteractionResult use(Player player, InteractionHand hand, BlockHitResult hit) {
        if (!isRemote() && isFormed() && isIdle() && workingStage >= 0) {
            workingStage = -1;
        }
        return super.use(player, hand, hit);
    }

    @NotNull
    @Override
    public AABB getRenderBoundingBox() {
        BlockPos pos = getBlockPos();
        Direction dir = getFrontFacing().getClockWise();
        return new AABB(
                pos.relative(dir, 4).relative(Direction.UP, 4),
                pos.relative(dir.getOpposite(), 4).relative(Direction.UP, -4).relative(getFrontFacing(), 3));
    }

    public final static ControllerDefinition HEX_GATE_DEFINITION = new ControllerDefinition(new ResourceLocation(ShimmerFireMod.MODID, "hex_gate_controller"), HexGateBlockEntity::new);

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

        HEX_GATE_DEFINITION.baseRenderer = new MBDIModelRenderer(new ResourceLocation(Multiblocked.MODID,"block/blueprint_table_controller"));
        HEX_GATE_DEFINITION.formedRenderer = new HexGateRenderer();
        HEX_GATE_DEFINITION.properties.isOpaque = false;

        MbdComponents.registerComponent(HEX_GATE_DEFINITION);
    }

}
