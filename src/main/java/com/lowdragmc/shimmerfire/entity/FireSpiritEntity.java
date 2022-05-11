package com.lowdragmc.shimmerfire.entity;

import com.lowdragmc.shimmer.client.light.ColorPointLight;
import com.lowdragmc.shimmer.client.light.LightManager;
import com.lowdragmc.shimmerfire.CommonProxy;
import com.lowdragmc.shimmerfire.block.ColoredFireBlock;
import com.mojang.math.Vector3f;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.ambient.AmbientCreature;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.Random;

/**
 * @author KilaBash
 * @date 2022/5/10
 * @implNote FireSpirit
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class FireSpiritEntity extends AmbientCreature {
    private static final EntityDataAccessor<Integer> FIRE_COLOR = SynchedEntityData.defineId(FireSpiritEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Byte> DATA_ID_FLAGS = SynchedEntityData.defineId(FireSpiritEntity.class, EntityDataSerializers.BYTE);
    private static final TargetingConditions BAT_RESTING_TARGETING = TargetingConditions.forNonCombat().range(4.0D);
    @Nullable
    private BlockPos targetPosition;
    @OnlyIn(Dist.CLIENT)
    private ColorPointLight colorPointLight;

    public FireSpiritEntity(EntityType<? extends FireSpiritEntity> entityType, Level level) {
        super(entityType, level);
        this.setResting(true);
    }

    public boolean isFlapping() {
        return !this.isResting() && this.tickCount % 3 == 0;
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_ID_FLAGS, (byte)0);
        this.entityData.define(FIRE_COLOR, ColoredFireBlock.FireColor.ORANGE.ordinal());
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

    @Nullable
    public SoundEvent getAmbientSound() {
        return this.isResting() && this.random.nextInt(4) != 0 ? null : SoundEvents.BAT_AMBIENT;
    }

    protected SoundEvent getHurtSound(@Nonnull DamageSource pDamageSource) {
        return SoundEvents.BAT_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.BAT_DEATH;
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

    public boolean isResting() {
        return (this.entityData.get(DATA_ID_FLAGS) & 1) != 0;
    }

    public void setResting(boolean pIsHanging) {
        byte b0 = this.entityData.get(DATA_ID_FLAGS);
        if (pIsHanging) {
            this.entityData.set(DATA_ID_FLAGS, (byte)(b0 | 1));
        } else {
            this.entityData.set(DATA_ID_FLAGS, (byte)(b0 & -2));
        }
    }

    public void setColor(ColoredFireBlock.FireColor color) {
        this.entityData.set(FIRE_COLOR, color.ordinal());
    }

    public ColoredFireBlock.FireColor getColor() {
        return ColoredFireBlock.FireColor.values()[this.entityData.get(FIRE_COLOR)];
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void tick() {
        super.tick();
        if (this.isResting()) {
            this.setDeltaMovement(Vec3.ZERO);
            this.setPosRaw(this.getX(), (double)Mth.floor(this.getY()) + 1.0D - (double)this.getBbHeight(), this.getZ());
        } else {
            this.setDeltaMovement(this.getDeltaMovement().multiply(.5D, 0.3D, .5D));
        }
        if (level.isClientSide) {
            Random random = level.random;
            Vec3 pos = getPosition(0);
            if (random.nextInt(5) == 0) {
                for(int i = 0; i < random.nextInt(1) + 1; ++i) {
                    Particle particle = Minecraft.getInstance().particleEngine.createParticle(CommonProxy.FIRE_SPARK.get(), pos.x + 0.5D, pos.y + 0.5D, pos.z + 0.5D,
                            random.nextFloat() / 2.0F, 5.0E-5D, random.nextFloat() / 2.0F);
                    if (particle != null) {
                        int color = getColor().colorVale;
                        particle.setColor((color >> 16) & 0xff, (color >> 8) & 0xff, (color) & 0xff);
                    }
                }
            }
        }
    }



    protected void customServerAiStep() {
        super.customServerAiStep();
        BlockPos blockpos = this.blockPosition();
        BlockPos abovePos = blockpos.above();
        if (this.isResting()) {
            boolean flag = this.isSilent();
            if (this.level.getBlockState(abovePos).isRedstoneConductor(this.level, blockpos)) {
                if (this.random.nextInt(200) == 0) {
                    this.yHeadRot = (float)this.random.nextInt(360);
                }

                if (this.level.getNearestPlayer(BAT_RESTING_TARGETING, this) != null) {
                    this.setResting(false);
                    if (!flag) {
                        this.level.levelEvent(null, 1025, blockpos, 0);
                    }
                }
            } else {
                this.setResting(false);
                if (!flag) {
                    this.level.levelEvent(null, 1025, blockpos, 0);
                }
            }
        } else {
            if (this.targetPosition != null && (!this.level.isEmptyBlock(this.targetPosition) || this.targetPosition.getY() <= this.level.getMinBuildHeight())) {
                this.targetPosition = null;
            }

            if (this.targetPosition == null || this.random.nextInt(30) == 0 || this.targetPosition.closerToCenterThan(this.position(), 2.0D)) {
                this.targetPosition = new BlockPos(this.getX() + (double)this.random.nextInt(7) - (double)this.random.nextInt(7), this.getY() + (double)this.random.nextInt(6) - 2.0D, this.getZ() + (double)this.random.nextInt(7) - (double)this.random.nextInt(7));
            }

            double d2 = (double)this.targetPosition.getX() + 0.5D - this.getX();
            double d0 = (double)this.targetPosition.getY() + 0.1D - this.getY();
            double d1 = (double)this.targetPosition.getZ() + 0.5D - this.getZ();
            Vec3 vec3 = this.getDeltaMovement();
            Vec3 vec31 = vec3.add((Math.signum(d2) * 0.5D - vec3.x) * (double)0.1F, (Math.signum(d0) * (double)0.7F - vec3.y) * (double)0.1F, (Math.signum(d1) * 0.5D - vec3.z) * (double)0.1F);
            this.setDeltaMovement(vec31);
            float f = (float)(Mth.atan2(vec31.z, vec31.x) * (double)(180F / (float)Math.PI)) - 90.0F;
            float f1 = Mth.wrapDegrees(f - this.getYRot());
            this.zza = 0.5F;
            this.setYRot(this.getYRot() + f1);
            if (this.random.nextInt(100) == 0 && this.level.getBlockState(abovePos).isRedstoneConductor(this.level, abovePos)) {
                this.setResting(true);
            }
        }

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
            if (!this.level.isClientSide && this.isResting()) {
                this.setResting(false);
            }

            return super.hurt(pSource, pAmount);
        }
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readAdditionalSaveData(@Nonnull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.entityData.set(DATA_ID_FLAGS, pCompound.getByte("Flags"));
        this.entityData.set(FIRE_COLOR, pCompound.getInt("Color"));
    }

    public void addAdditionalSaveData(@Nonnull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putByte("Flags", this.entityData.get(DATA_ID_FLAGS));
        pCompound.putInt("Color", this.entityData.get(FIRE_COLOR));
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @org.jetbrains.annotations.Nullable SpawnGroupData pSpawnData, @org.jetbrains.annotations.Nullable CompoundTag pDataTag) {
        setColor(ColoredFireBlock.FireColor.values()[random.nextInt(ColoredFireBlock.FireColor.values().length)]);
        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }

    public static boolean checkBatSpawnRules(EntityType<Bat> pBat, LevelAccessor pLevel, MobSpawnType pReason, BlockPos pPos, Random pRandom) {
        int i = pLevel.getMaxLocalRawBrightness(pPos);
        int j = 4;
        if (isHalloween()) {
            j = 7;
        } else if (pRandom.nextBoolean()) {
            return false;
        }

        return i <= pRandom.nextInt(j) && checkMobSpawnRules(pBat, pLevel, pReason, pPos, pRandom);
    }

    private static boolean isHalloween() {
        LocalDate localdate = LocalDate.now();
        int i = localdate.get(ChronoField.DAY_OF_MONTH);
        int j = localdate.get(ChronoField.MONTH_OF_YEAR);
        return j == 10 && i >= 20 || j == 11 && i <= 3;
    }

    protected float getStandingEyeHeight(@Nonnull Pose pPose, EntityDimensions pSize) {
        return pSize.height / 2.0F;
    }

}
