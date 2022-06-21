package com.lowdragmc.shimmerfire.item;

import com.lowdragmc.shimmerfire.CommonProxy;
import com.lowdragmc.shimmerfire.api.RawFire;
import com.lowdragmc.shimmerfire.block.FireJarBlock;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootTables;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

/**
 * @author KilaBash
 * @date 2022/6/20
 * @implNote FireGlassJarItem
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class FireJarItem extends BlockItem {

    public FireJarItem(FireJarBlock block, Properties properties) {
        super(block, properties);
    }



    @Override
    public void fillItemCategory(CreativeModeTab pGroup, NonNullList<ItemStack> pItems) {
        if (this.allowdedIn(pGroup)) {
            this.getBlock().fillItemCategory(pGroup, pItems);
            for (RawFire fire : RawFire.values()) {
                pItems.add(getStack(fire));
            }
        }
    }

    public static boolean isJarItem(ItemStack itemStack) {
        return itemStack.getItem() == CommonProxy.FIRE_JAR_ITEM.get();
    }

    @Nullable
    public static RawFire getFireType(ItemStack itemStack) {
        if (isJarItem(itemStack)) {
            int damage = itemStack.getDamageValue();
            if (damage > 0 && damage <= RawFire.values().length) {
                return RawFire.values()[damage - 1];
            }
        }
        return null;
    }

    public static void setFireType(ItemStack itemStack, @Nullable RawFire fireType) {
        if (isJarItem(itemStack)) {
            itemStack.setDamageValue(fireType == null ? 0 : fireType.ordinal() + 1);
        }
    }

    public static ItemStack getStack(@Nullable RawFire fire) {
        ItemStack stack = new ItemStack(CommonProxy.FIRE_JAR_ITEM.get());
        if (fire != null) {
            stack.setDamageValue(fire.ordinal() + 1);
        }
        return stack;
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltip, TooltipFlag pFlag) {
        super.appendHoverText(pStack, pLevel, pTooltip, pFlag);
        RawFire fire = getFireType(pStack);
        if (fire == null) {
            pTooltip.add(new TranslatableComponent("shimmerfire.fire_jar.hover.empty"));
        } else {
            pTooltip.add(new TranslatableComponent("shimmerfire.fire_jar.hover.fire").append(fire.getTranslatableComponent()));
        }
    }
}
