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
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ambient.AmbientCreature;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * @author KilaBash
 * @date 2022/5/10
 * @implNote FireSpirit
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class FireSpiritEntity extends AmbientCreature {
    private static final EntityDataAccessor<Integer> FIRE_COLOR = SynchedEntityData.defineId(FireSpiritEntity.class, EntityDataSerializers.INT);
    @OnlyIn(Dist.CLIENT)
    private ColorPointLight colorPointLight;
    @OnlyIn(Dist.CLIENT)
    private VividFireParticle vividFireParticle;

    public FireSpiritEntity(EntityType<? extends FireSpiritEntity> entityType, Level level) {
        super(entityType, level);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(FIRE_COLOR, RawFire.DESTROY.ordinal());
    }

    /**
     * Returns the volume for the sounds this mob makes.
     */
    protected float getSoundVolume() {
        return 0.1F;
    }

    /**
     * Gets the pitch of living sounds in living entities.
     */
    public float getVoicePitch() {
        return super.getVoicePitch() * 0.95F;
    }

    protected SoundEvent getHurtSound(@Nonnull DamageSource pDamageSource) {
        return SoundEvents.BAT_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.BAT_DEATH;
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

    protected void doPush(@Nonnull Entity pEntity) {
        // TODO BURNING
    }

    protected void pushEntities() {
        // TODO BURNING
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 6.0D);
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
        super.tick();
    }


    protected void customServerAiStep() {
        super.customServerAiStep();

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

    /**
     * Called when the entity is attacked.
     */
    public boolean hurt(@Nonnull DamageSource pSource, float pAmount) {
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
        super.readAdditionalSaveData(pCompound);
        this.entityData.set(FIRE_COLOR, pCompound.getInt("Color"));
    }

    public void addAdditionalSaveData(@Nonnull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("Color", this.entityData.get(FIRE_COLOR));
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @org.jetbrains.annotations.Nullable SpawnGroupData pSpawnData, @org.jetbrains.annotations.Nullable CompoundTag pDataTag) {
        setColor(RawFire.values()[random.nextInt(RawFire.values().length)]);
        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }

    protected float getStandingEyeHeight(@Nonnull Pose pPose, EntityDimensions pSize) {
        return pSize.height / 2.0F;
    }

}
