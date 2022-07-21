package com.lowdragmc.shimmerfire.entity;

import com.lowdragmc.shimmer.client.light.ColorPointLight;
import com.lowdragmc.shimmer.client.light.LightManager;
import com.lowdragmc.shimmerfire.api.RawFire;
import com.lowdragmc.shimmerfire.client.particle.VividFireParticle;
import com.mojang.math.Vector3f;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.ref.WeakReference;

/**
 * @author KilaBash
 * @date 2022/5/10
 * @implNote FireSpirit
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class FireSpiritEntity extends Entity {
    private static final EntityDataAccessor<Integer> FIRE_COLOR = SynchedEntityData.defineId(FireSpiritEntity.class, EntityDataSerializers.INT);
    @OnlyIn(Dist.CLIENT)
    private ColorPointLight colorPointLight;
    @OnlyIn(Dist.CLIENT)
    private VividFireParticle vividFireParticle;

    private WeakReference<Player> master;

    public FireSpiritEntity(EntityType<? extends FireSpiritEntity> entityType, Level level) {
        super(entityType, level);
        setNoGravity(true);
    }

    protected void defineSynchedData() {
        this.entityData.define(FIRE_COLOR, RawFire.DESTROY.ordinal());
    }

    @Nullable
    public Player getPlayer() {
        if (master == null) return null;
        Player player = master.get();
        return player == null ? null : player.isAddedToWorld() && player.level == this.level ? player : null;
    }

    public void setMaster(Player master) {
        this.master = new WeakReference<>(master);
    }

    @OnlyIn(Dist.CLIENT)
    @Nullable
    public VividFireParticle getOrCreateFire() {
        if (vividFireParticle == null || !vividFireParticle.isAlive()) {
            Vec3 position = getPosition(0);
            if (level instanceof ClientLevel clientLevel) {
                vividFireParticle = new VividFireParticle(clientLevel, position.x, position.y, position.z, getColor().colorVale, 0.3f, true);
                vividFireParticle.addParticle();
            }
        }
        return vividFireParticle;
    }

    @OnlyIn(Dist.CLIENT)
    @Nullable
    public ColorPointLight getOrCreateLight() {
        if (colorPointLight == null) {
            colorPointLight = LightManager.INSTANCE.addLight(new Vector3f(0,0,0), getColor().colorVale, 10);
        }
        return colorPointLight;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void onClientRemoval() {
        super.onClientRemoval();
        if (colorPointLight != null) {
            colorPointLight.remove();
        }
        if (vividFireParticle != null) {
            vividFireParticle.remove();
        }
    }

    /**
     * Returns true if this entity should push and be pushed by other entities when colliding.
     */
    public boolean isPushable() {
        return false;
    }

    public void setColor(RawFire color) {
        this.entityData.set(FIRE_COLOR, color.ordinal());
    }

    public RawFire getColor() {
        return RawFire.values()[this.entityData.get(FIRE_COLOR)];
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void tick() {
        if (!level.isClientSide) {
            Player player = getPlayer();
            if (player == null) {
                remove(RemovalReason.DISCARDED);
            } else {
                Vec3 eyePosition = player.getEyePosition();
                float f = 0;
                float f1 = -(player.getYRot() + 45) * ((float)Math.PI / 180F);
                float f2 = Mth.cos(f1);
                float f3 = Mth.sin(f1);
                float f4 = Mth.cos(f);
                float f5 = Mth.sin(f);
                Vec3 angle = new Vec3(f3 * f4, (-f5), (f2 * f4));
                eyePosition = eyePosition.add(angle);
                moveTo(eyePosition);
            }
        }
        super.tick();
    }

    @Nonnull
    protected Entity.MovementEmission getMovementEmission() {
        return Entity.MovementEmission.EVENTS;
    }

    public boolean causeFallDamage(float pFallDistance, float pMultiplier, @Nonnull DamageSource pSource) {
        return false;
    }

    @ParametersAreNonnullByDefault
    protected void checkFallDamage(double pY, boolean pOnGround, BlockState pState, BlockPos pPos) {
    }

    /**
     * Return whether this entity should NOT trigger a pressure plate or a tripwire.
     */
    public boolean isIgnoringBlockTriggers() {
        return true;
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean hurt(@Nonnull DamageSource pSource, float pAmount) {
        if (pSource.getEntity() instanceof Player player && player == getPlayer()) {
            remove(RemovalReason.DISCARDED);
        }
        if (this.isInvulnerableTo(pSource)) {
            return false;
        } else {
            return super.hurt(pSource, pAmount);
        }
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readAdditionalSaveData(@Nonnull CompoundTag pCompound) {
        this.entityData.set(FIRE_COLOR, pCompound.getInt("Color"));
    }

    public void addAdditionalSaveData(@Nonnull CompoundTag pCompound) {
        pCompound.putInt("Color", this.entityData.get(FIRE_COLOR));
    }

    protected float getStandingEyeHeight(@Nonnull Pose pPose, EntityDimensions pSize) {
        return pSize.height / 2.0F;
    }

}
