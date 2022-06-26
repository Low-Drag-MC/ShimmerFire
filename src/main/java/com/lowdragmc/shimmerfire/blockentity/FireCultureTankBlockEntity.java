package com.lowdragmc.shimmerfire.blockentity;

import com.lowdragmc.lowdraglib.client.particle.impl.TextureParticle;
import com.lowdragmc.shimmerfire.CommonProxy;
import com.lowdragmc.shimmerfire.ShimmerFireMod;
import com.lowdragmc.shimmerfire.api.Capabilities;
import com.lowdragmc.shimmerfire.api.RawFire;
import com.lowdragmc.shimmerfire.block.CreativeCultureTankBlock;
import com.lowdragmc.shimmerfire.block.FireCultureTankBlock;
import com.lowdragmc.shimmerfire.item.FireJarItem;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

import static com.lowdragmc.shimmerfire.block.FireCultureTankBlock.CHARGING;

/**
 * @author KilaBash
 * @date 2022/05/12
 * @implNote FireContainerBlockEntity
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class FireCultureTankBlockEntity extends FireContainer implements IAnimatable {
    private FireCultureTankBlockEntity core;
    private final boolean isCreative;

    public FireCultureTankBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(CommonProxy.FIRE_CULTURE_TANK.get(), pWorldPosition, pBlockState);
        isCreative = pBlockState.getBlock() instanceof CreativeCultureTankBlock;
    }

    public boolean isCore() {
        return getBlockState().getValue(FireCultureTankBlock.HALF) == DoubleBlockHalf.LOWER;
    }

    @Nullable
    public FireCultureTankBlockEntity getCore() {
        if (core != null) return core;
        if (isCore()) return core = this;
        BlockEntity blockEntity = getLevel().getBlockEntity(getBlockPos().below());
        if (blockEntity instanceof FireCultureTankBlockEntity) {
            return core = (FireCultureTankBlockEntity) blockEntity;
        }
        getLevel().removeBlock(getBlockPos(), false);
        return null;
    }

    public boolean isCreative() {
        return isCreative;
    }

    @Override
    public int getStored() {
        if (!isCore() && getCore() != null) {
            return getCore().getStored();
        }
        if (isCreative) return Integer.MAX_VALUE;
        return super.getStored();
    }

    @Override
    public int getCapacity() {
        if (isCreative) return Integer.MAX_VALUE;
        return 5000;
    }

    @Override
    public int extract(@Nullable RawFire fire, int heat, boolean simulate) {
        if (!isCore() && getCore() != null) {
            return getCore().extract(fire, heat, simulate);
        }
        if (isCreative) return heat;
        return super.extract(fire, heat, simulate);
    }

    @Override
    public int insert(@Nullable RawFire fire, int heat, boolean simulate) {
        if (!isCore() && getCore() != null) {
            return getCore().insert(fire, heat, simulate);
        }
        if (isCreative) return heat;
        if (rawFire == null) return 0;
        return super.insert(fire, heat, simulate);
    }

    @Override
    @Nullable
    public RawFire getFireType() {
        if (!isCore() && getCore() != null) {
            return getCore().getFireType();
        }
        return super.getFireType();
    }

    @Override
    public void setFireType(RawFire fire) {
        if (!isCore() && getCore() != null) {
            getCore().setFireType(fire);
            return;
        }
        super.setFireType(fire);
    }

    private boolean isCharging() {
        return getBlockState().getValue(CHARGING);
    }

    public InteractionResult use(Player pPlayer, InteractionHand pHand) {
        ItemStack itemStack = pPlayer.getItemInHand(pHand);
        if (FireJarItem.isJarItem(itemStack)) {
            RawFire fire = FireJarItem.getFireType(itemStack);
            if (getFireType() == null) {
                if (fire != null && !level.isClientSide) {
                    setFireType(fire);
                    insert(fire, 1, false);
                }
            } else {
                if (fire != null && getCore() != null && getCore().isCreative()) {
                    if (!level.isClientSide) {
                        setFireType(fire);
                    }
                } else if (fire == null && getStored() > 2000) {
                    if (!level.isClientSide) {
                        extract(null, 2000, false);
                        notifyUpdate();
                    }
                    FireJarItem.setFireType(itemStack, getFireType());
                }
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == Capabilities.FIRE_CONTAINER_CAPABILITY) {
            if (isCore()) {
                if (side == Direction.DOWN) {
                    return Capabilities.FIRE_CONTAINER_CAPABILITY.orEmpty(cap, LazyOptional.of(() -> this));
                }
            } else {
                if (side == Direction.UP) {
                    return Capabilities.FIRE_CONTAINER_CAPABILITY.orEmpty(cap, LazyOptional.of(() -> this));
                }
            }
        }
        return super.getCapability(cap, side);
    }

    public void chargingTick() {
        if (getFireType() != null) {
            if (level.isClientSide) {
                creatChargingParticle();
            } else {
                insert(getFireType(), 10, false);
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void creatChargingParticle() {
        // emit particles
        if (level instanceof ClientLevel clientLevel) {
            RawFire fire = getFireType();
            BlockPos pos = getBlockPos();
            if (fire == null) return;
            Random random = clientLevel.random;
            for (int i = 0; i < 3; i++) {
                TextureParticle particle = new TextureParticle(clientLevel, pos.getX() + random.nextFloat(), pos.getY() + 0.5 + random.nextFloat(), pos.getZ() + random.nextFloat()) {
                    @Override
                    protected void update() {
                        this.alpha -= 1f / this.lifetime;
                    }

                    @Override
                    public void render(@NotNull VertexConsumer pBuffer, Camera pRenderInfo, float pPartialTicks) {
                        float lastAlpha = this.alpha;
                        this.alpha -= pPartialTicks / this.lifetime;
                        super.render(pBuffer, pRenderInfo, pPartialTicks);
                        this.alpha = lastAlpha;
                    }
                };

                particle.scale(0.02f);
                particle.setLight(0xf000f0);
                particle.setTexture(new ResourceLocation(ShimmerFireMod.MODID, "textures/particle/fire_spark_2.png"));
                particle.setLifetime(random.nextInt(40) + 20);
                particle.setColor((fire.colorVale >> 16 & 0xff)/256f,(fire.colorVale >> 8 & 0xff)/256f,(fire.colorVale & 0xff)/256f);
                particle.addParticle();
            }
        }
    }

    @Override
    public AABB getRenderBoundingBox() {
        return new AABB(getBlockPos(), getBlockPos().offset(1, 2, 1));
    }

    private final AnimationFactory factory = new AnimationFactory(this);

    private PlayState predicate(AnimationEvent<FireCultureTankBlockEntity> event) {
        if (isCore()) {
            AnimationController controller = event.getController();
            if (getFireType() == null) {
                controller.setAnimation(new AnimationBuilder().addAnimation("empty"));
                controller.transitionLengthTicks = 20;
            } else {
                if (isCharging()) {
                    controller.setAnimation(new AnimationBuilder().addAnimation("charging"));
                    controller.transitionLengthTicks = 20;
                } else {
                    controller.setAnimation(new AnimationBuilder().addAnimation("idle"));
                    controller.transitionLengthTicks = 20;
                }
            }
            return PlayState.CONTINUE;
        } else {
            return PlayState.STOP;
        }
    }

    @Override
    public void registerControllers(AnimationData data) {
        AnimationController<FireCultureTankBlockEntity> controller = new AnimationController<>(this, "controller", 0, this::predicate);
        controller.transitionLengthTicks = 20;
        data.addAnimationController(controller);
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }
}
