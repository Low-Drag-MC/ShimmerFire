package com.lowdragmc.shimmerfire.item;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

/**
 * @author KilaBash
 * @date 2022/8/1
 * @implNote SunGlassesItem
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CyberpunkGlassesItem extends ArmorItem {

    public CyberpunkGlassesItem(Properties pProperties) {
        super(new ArmorMaterial(){

            @Override
            public int getDurabilityForSlot(EquipmentSlot pSlot) {
                return 15 * 15;
            }

            @Override
            public int getDefenseForSlot(EquipmentSlot pSlot) {
                return 2;
            }

            @Override
            public int getEnchantmentValue() {
                return 9;
            }

            @Override
            public SoundEvent getEquipSound() {
                return SoundEvents.ARMOR_EQUIP_IRON;
            }

            @Override
            public Ingredient getRepairIngredient() {
                return Ingredient.of(Items.IRON_INGOT);
            }

            @Override
            public String getName() {
                return "cyberpunk_glasses";
            }

            @Override
            public float getToughness() {
                return 0;
            }

            @Override
            public float getKnockbackResistance() {
                return 0;
            }

        }, EquipmentSlot.HEAD, pProperties);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        pTooltipComponents.add(new TranslatableComponent("item.shimmerfire.cyberpunk_glasses.tooltip"));
    }
}
