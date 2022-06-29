package com.lowdragmc.shimmerfire.blockentity;

import com.lowdragmc.shimmer.client.postprocessing.PostProcessing;
import com.lowdragmc.shimmerfire.CommonProxy;
import com.lowdragmc.shimmerfire.WorldData;
import com.lowdragmc.shimmerfire.api.RawFire;
import com.lowdragmc.shimmerfire.client.particle.FireSpiritParticle;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import javax.annotation.Nonnull;

/**
 * @author KilaBash
 * @date 2022/6/23
 * @implNote FirePedestalBlockEntity
 */
public class FirePedestalBlockEntity extends FireContainer implements IAnimatable {

    @OnlyIn(Dist.CLIENT)
    public FireSpiritParticle fireParticle;

    public FirePedestalBlockEntity(BlockPos pos, BlockState state) {
        super(CommonProxy.FIRE_PEDESTAL.get(), pos, state);
    }

    @Override
    public void clearRemoved() {
        super.clearRemoved();
        WorldData.getOrCreate(level).addPedestal(this);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        WorldData.getOrCreate(level).removePedestal(this);
    }

    @Override
    public int getCapacity() {
        return 50000;
    }

    @Override
    public int extract(@Nullable RawFire fire, int heat, boolean simulate) {
        return 0;
    }

    public int extractInner(@Nullable RawFire fire, int heat, boolean simulate) {
        return super.extract(fire, heat, simulate);
    }

    @Override
    protected void read(@Nonnull CompoundTag tag, boolean clientPacket) {
        RawFire lastFire = rawFire;
        super.read(tag, clientPacket);
        if (lastFire != rawFire) {
            scheduleChunkForRenderUpdate();
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void clientTick() {
        if (level instanceof ClientLevel level) {
            RawFire fire = getFireType();
            if ((fireParticle == null || !fireParticle.isAlive()) && fire != null) {
                BlockPos pos = getBlockPos();
                fireParticle = new FireSpiritParticle(level, pos.getX() + 0.5, pos.getY() + 1.5f, pos.getZ() + 0.5);
                fireParticle.setAliveCondition(particle -> {
                    if (particle.getLevel() != null && particle.getLevel().getBlockEntity(pos) instanceof FirePedestalBlockEntity pedestal) {
                        return pedestal.fireParticle == particle;
                    }
                    return false;
                });
                fireParticle.setLight(0xf000f0);
                fireParticle.setLifetime(-1);
                fireParticle.setCull(false);
                PostProcessing.BLOOM_UNREAL.postParticle(fireParticle);
            } else if (fireParticle != null && fire == null) {
                fireParticle.remove();
                fireParticle = null;
            }
            if (fireParticle != null) {
                float scale = getStored() * 1f / getCapacity();
                fireParticle.scale(0.2f + 0.4f * scale);
                fireParticle.setColor(
                        (fire.colorVale >> 16 & 0xff)/256f,
                        (fire.colorVale >> 8 & 0xff)/256f,
                        (fire.colorVale & 0xff)/256f);
            }
        }
    }

    private final AnimationFactory factory = new AnimationFactory(this);

    private PlayState predicate(AnimationEvent<FirePedestalBlockEntity> event) {
        AnimationController controller = event.getController();
        if (getFireType() != null) {
            controller.setAnimation(new AnimationBuilder().addAnimation("working"));
        } else {
            controller.setAnimation(new AnimationBuilder().addAnimation("idle"));
        }
        return PlayState.CONTINUE;

    }

    @Override
    public void registerControllers(AnimationData data) {
        AnimationController<FirePedestalBlockEntity> controller = new AnimationController<>(this, "controller", 0, this::predicate);
        controller.transitionLengthTicks = 20;
        data.addAnimationController(controller);
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }
}
