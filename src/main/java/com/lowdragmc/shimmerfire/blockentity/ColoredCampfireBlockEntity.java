package com.lowdragmc.shimmerfire.blockentity;

import com.lowdragmc.shimmerfire.CommonProxy;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.Clearable;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;
import java.util.Random;

/**
 * @author KilaBash
 * @date 2022/05/09
 * @implNote ColoredCampfireBlockEntity copy from {@link net.minecraft.world.level.block.entity.CampfireBlockEntity}
 */
public class ColoredCampfireBlockEntity extends SyncedBlockEntity implements Clearable {
    protected final NonNullList<ItemStack> items = NonNullList.withSize(4, ItemStack.EMPTY);
    protected final int[] cookingProgress = new int[4];
    protected final int[] cookingTime = new int[4];

    public <T extends ColoredCampfireBlockEntity> ColoredCampfireBlockEntity(BlockEntityType<T> type,BlockPos pWorldPosition, BlockState pBlockState) {
        super(type, pWorldPosition, pBlockState);
    }

    public ColoredCampfireBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(CommonProxy.COLORED_CAMPFIRE.get(), pWorldPosition, pBlockState);
    }

    public static void cookTick(Level pLevel, BlockPos pPos, BlockState pState, ColoredCampfireBlockEntity pBlockEntity) {
        boolean flag = false;

        for(int i = 0; i < pBlockEntity.items.size(); ++i) {
            ItemStack itemstack = pBlockEntity.items.get(i);
            if (!itemstack.isEmpty()) {
                flag = true;
                pBlockEntity.cookingProgress[i]++;
                if (pBlockEntity.cookingProgress[i] >= pBlockEntity.cookingTime[i]) {
                    Container container = new SimpleContainer(itemstack);
                    ItemStack itemstack1 = pLevel.getRecipeManager().getRecipeFor(RecipeType.CAMPFIRE_COOKING, container, pLevel).map((p_155305_) -> p_155305_.assemble(container)).orElse(itemstack);
                    Containers.dropItemStack(pLevel, pPos.getX(), pPos.getY(),
                            pPos.getZ(), itemstack1);
                    pBlockEntity.items.set(i, ItemStack.EMPTY);
                    pBlockEntity.notifyUpdate();
                }
            }
        }

        if (flag) {
            setChanged(pLevel, pPos, pState);
        }

    }

    public static void cooldownTick(Level pLevel, BlockPos pPos, BlockState pState, ColoredCampfireBlockEntity pBlockEntity) {
        boolean flag = false;

        for(int i = 0; i < pBlockEntity.items.size(); ++i) {
            if (pBlockEntity.cookingProgress[i] > 0) {
                flag = true;
                pBlockEntity.cookingProgress[i] = Mth.clamp(pBlockEntity.cookingProgress[i] - 2, 0, pBlockEntity.cookingTime[i]);
            }
        }

        if (flag) {
            setChanged(pLevel, pPos, pState);
        }

    }

    public static void particleTick(Level pLevel, BlockPos pPos, BlockState pState, ColoredCampfireBlockEntity pBlockEntity) {
        Random random = pLevel.random;
        if (random.nextFloat() < 0.11F) {
            for(int i = 0; i < random.nextInt(2) + 2; ++i) {
                CampfireBlock.makeParticles(pLevel, pPos, pState.getValue(CampfireBlock.SIGNAL_FIRE), false);
            }
        }

        int l = pState.getValue(CampfireBlock.FACING).get2DDataValue();

        for(int j = 0; j < pBlockEntity.items.size(); ++j) {
            if (!pBlockEntity.items.get(j).isEmpty() && random.nextFloat() < 0.2F) {
                Direction
                        direction = Direction.from2DDataValue(Math.floorMod(j + l, 4));
                float f = 0.3125F;
                double d0 = (double)pPos.getX() + 0.5D - (double)((float)direction.getStepX() * 0.3125F) + (double)((float)direction.getClockWise().getStepX() * 0.3125F);
                double d1 = (double)pPos.getY() + 0.5D;
                double d2 = (double)pPos.getZ() + 0.5D - (double)((float)direction.getStepZ() * 0.3125F) + (double)((float)direction.getClockWise().getStepZ() * 0.3125F);

                for(int k = 0; k < 4; ++k) {
                    pLevel.addParticle(ParticleTypes.SMOKE, d0, d1, d2, 0.0D, 5.0E-4D, 0.0D);
                }
            }
        }

    }

    /**
     * @return the items currently held in this campfire
     */
    public NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        this.items.clear();
        ContainerHelper.loadAllItems(tag,this.items);
        if (tag.contains("CookingTimes", 11)) {
            int[] aint = tag.getIntArray("CookingTimes");
            System.arraycopy(aint, 0, this.cookingProgress, 0, Math.min(this.cookingTime.length, aint.length));
        }

        if (tag.contains("CookingTotalTimes", 11)) {
            int[] aint1 = tag.getIntArray("CookingTotalTimes");
            System.arraycopy(aint1, 0, this.cookingTime, 0, Math.min(this.cookingTime.length, aint1.length));
        }
    }

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        ContainerHelper.saveAllItems(tag, this.items, true);
        tag.putIntArray("CookingTimes", this.cookingProgress);
        tag.putIntArray("CookingTotalTimes", this.cookingTime);
    }

    public Optional<CampfireCookingRecipe> getCookableRecipe(ItemStack pStack) {
        return this.items.stream().noneMatch(ItemStack::isEmpty) ? Optional.empty() : this.level.getRecipeManager().getRecipeFor(RecipeType.CAMPFIRE_COOKING, new SimpleContainer(pStack), this.level);
    }

    public boolean placeFood(ItemStack pStack, int pCookTime) {
        for(int i = 0; i < this.items.size(); ++i) {
            ItemStack itemstack = this.items.get(i);
            if (itemstack.isEmpty()) {
                this.cookingTime[i] = pCookTime;
                this.cookingProgress[i] = 0;
                this.items.set(i, pStack.split(1));
                this.notifyUpdate();
                return true;
            }
        }

        return false;
    }

    public void clearContent() {
        this.items.clear();
    }

    public void dowse() {
        if (this.level != null) {
            this.notifyUpdate();
        }
    }
}
