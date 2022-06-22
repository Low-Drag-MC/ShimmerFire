package com.lowdragmc.shimmerfire.blockentity;

import com.lowdragmc.lowdraglib.client.particle.impl.TextureParticle;
import com.lowdragmc.shimmerfire.CommonProxy;
import com.lowdragmc.shimmerfire.ShimmerFireMod;
import com.lowdragmc.shimmerfire.api.Capabilities;
import com.lowdragmc.shimmerfire.api.IFireContainer;
import com.lowdragmc.shimmerfire.api.RawFire;
import com.lowdragmc.shimmerfire.block.FireContainerBlock;
import com.lowdragmc.shimmerfire.item.FireJarItem;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

/**
 * @author KilaBash
 * @date 2022/05/12
 * @implNote FireContainerBlockEntity
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class FireContainerBlockEntity extends SyncedBlockEntity implements IFireContainer {
    private FireContainerBlockEntity core;
    private int stored;
    private RawFire rawFire;

    public FireContainerBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(CommonProxy.FIRE_CONTAINER.get(), pWorldPosition, pBlockState);
    }

    public boolean isCore() {
        return getBlockState().getValue(FireContainerBlock.HALF) == DoubleBlockHalf.LOWER;
    }

    @Nullable
    public FireContainerBlockEntity getCore() {
        if (core != null) return core;
        if (isCore()) return core = this;
        BlockEntity blockEntity = getLevel().getBlockEntity(getBlockPos().below());
        if (blockEntity instanceof FireContainerBlockEntity) {
            return core = (FireContainerBlockEntity) blockEntity;
        }
        getLevel().removeBlock(getBlockPos(), false);
        return null;
    }

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        if (rawFire != null) {
            tag.putString("f", rawFire.name());
            tag.putInt("s", stored);
        }
    }

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        if (tag.contains("f")) {
            rawFire = RawFire.valueOf(tag.getString("f"));
            stored = tag.getInt("s");
        }
    }

    @Override
    public int getStored() {
        if (!isCore() && getCore() != null) {
            return getCore().getStored();
        }
        return rawFire == null ? 0 : stored;
    }

    @Override
    public int getCapacity() {
        return 5000;
    }

    @Override
    public int extract(@Nullable RawFire fire, int heat, boolean simulate) {
        if (!isCore() && getCore() != null) {
            return getCore().extract(fire, heat, simulate);
        }
        if (rawFire == null) return 0;
        if (fire != null && getFireType() != fire) return 0;
        int extracted = Math.min(heat, stored);
        if (!simulate) {
            stored -= extracted;
            notifyUpdate();
        }
        return extracted;
    }

    @Override
    public int insert(@Nullable RawFire fire, int heat, boolean simulate) {
        if (!isCore() && getCore() != null) {
            return getCore().insert(fire, heat, simulate);
        }
        if (rawFire == null) return 0;
        if (fire != null && fire != getFireType()) {
            heat /= 2;
        }
        int inserted = Math.min(getCapacity(), stored + heat) - stored;
        if (!simulate) {
            stored += inserted;
            notifyUpdate();
        }
        return (fire != null && fire != getFireType()) ? inserted * 2 : inserted;
    }

    @Override
    @Nullable
    public RawFire getFireType() {
        if (!isCore() && getCore() != null) {
            return getCore().getFireType();
        }
        return rawFire;
    }

    @Override
    public void setFireType(RawFire fire) {
        if (!isCore() && getCore() != null) {
            getCore().setFireType(fire);
            return;
        }
        this.rawFire = fire;
        notifyUpdate();
    }

    public InteractionResult use(Player pPlayer, InteractionHand pHand) {
        ItemStack itemStack = pPlayer.getItemInHand(pHand);
        if (FireJarItem.isJarItem(itemStack)) {
            RawFire fire = FireJarItem.getFireType(itemStack);
            if (getFireType() == null) {
                if (fire != null && !level.isClientSide) {
                    setFireType(fire);
                }
            } else {
                if (fire == null && getStored() >= 2000) {
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
                particle.setFullLight(0xf000f0);
                particle.setTexture(new ResourceLocation(ShimmerFireMod.MODID, "textures/particle/fire_spark_2.png"));
                particle.setLifetime(random.nextInt(40) + 20);
                particle.setColor((fire.colorVale >> 16 & 0xff)/256f,(fire.colorVale >> 8 & 0xff)/256f,(fire.colorVale & 0xff)/256f);
                particle.addParticle();
            }
        }
    }
}
