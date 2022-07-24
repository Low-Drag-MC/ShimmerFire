package com.lowdragmc.shimmerfire;

import com.google.common.collect.Lists;
import com.lowdragmc.shimmerfire.item.ColorfulFlintItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.List;

public class FlintDyeRecipe extends CustomRecipe {

    public FlintDyeRecipe(ResourceLocation pId) {
        super(pId);
    }


    @Override
    public boolean matches(CraftingContainer pContainer, Level pLevel) {
        ItemStack flintItem = ItemStack.EMPTY;
        List<ItemStack> dyeList = Lists.newArrayList();

        for (int i = 0; i < pContainer.getContainerSize(); ++i) {
            ItemStack containerItem = pContainer.getItem(i);
            if (!containerItem.isEmpty()) {
                if (containerItem.getItem() instanceof ColorfulFlintItem) {
                    if (!flintItem.isEmpty()) {
                        return false;
                    }

                    flintItem = containerItem;
                } else {
                    if (!(containerItem.getItem() instanceof DyeItem)) {
                        return false;
                    }

                    dyeList.add(containerItem);
                }
            }
        }

        return !flintItem.isEmpty() && !dyeList.isEmpty();
    }

    @Override
    public ItemStack assemble(CraftingContainer pContainer) {
        List<DyeItem> dyeList = Lists.newArrayList();
        ItemStack flintItem = ItemStack.EMPTY;

        for (int i = 0; i < pContainer.getContainerSize(); ++i) {
            ItemStack containerItem = pContainer.getItem(i);
            if (!containerItem.isEmpty()) {
                Item item = containerItem.getItem();
                if (item instanceof ColorfulFlintItem) {
                    if (!flintItem.isEmpty()) {
                        return ItemStack.EMPTY;
                    }

                    flintItem = containerItem.copy();
                } else {
                    if (!(item instanceof DyeItem)) {
                        return ItemStack.EMPTY;
                    }

                    dyeList.add((DyeItem) item);
                }
            }
        }

        return !flintItem.isEmpty() && !dyeList.isEmpty() ? ColorfulFlintItem.dyeFlint(flintItem, dyeList) : ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return CommonProxy.FLINT_DYE_RECIPE.get();
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeType.CRAFTING;
    }
}
