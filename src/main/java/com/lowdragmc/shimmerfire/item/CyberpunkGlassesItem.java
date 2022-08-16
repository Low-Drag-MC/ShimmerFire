package com.lowdragmc.shimmerfire.item;

import com.lowdragmc.shimmerfire.ShimmerFireMod;
import com.lowdragmc.shimmerfire.client.GlassAtlas;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.item.GeoArmorItem;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

/**
 * @author KilaBash
 * @date 2022/8/1
 * @implNote SunGlassesItem
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CyberpunkGlassesItem extends GeoArmorItem implements IAnimatable {

    private static String name = "cyberpunk_glasses";
    private AnimationFactory factory = new AnimationFactory(this);

    public CyberpunkGlassesItem(Properties pProperties) {
        super(new ArmorMaterial() {

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

    private <P extends IAnimatable> PlayState predicate(AnimationEvent<P> event) {
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController(this,
                name + "_controller", 20, this::predicate));
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    public static class CyberpunkGlassesRender extends GeoArmorRenderer<CyberpunkGlassesItem> {

        public CyberpunkGlassesRender() {
            super(new CyberpunkGlassesModel());
        }
    }

    static class CyberpunkGlassesModel extends AnimatedGeoModel {

        @Override
        public void setLivingAnimations(Object entity, Integer uniqueID, AnimationEvent customPredicate) {

        }

        @Override
        public ResourceLocation getModelLocation(Object object) {
            return ShimmerFireMod.rl("geo/cyberpunk_glasses.geo.json");
        }

        @Override
        public ResourceLocation getTextureLocation(Object object) {
            return GlassAtlas.location;
        }

        @Override
        public ResourceLocation getAnimationFileLocation(Object animatable) {
            return null;
        }


    }

}
