package com.lowdragmc.shimmerfire.core.mixins;

import com.lowdragmc.lowdraglib.gui.texture.ResourceTexture;
import com.lowdragmc.lowdraglib.gui.widget.ImageWidget;
import com.lowdragmc.multiblocked.api.capability.IO;
import com.lowdragmc.multiblocked.api.gui.recipe.RecipeWidget;
import com.lowdragmc.multiblocked.api.recipe.Content;
import com.lowdragmc.multiblocked.api.recipe.Recipe;
import com.lowdragmc.multiblocked.common.capability.FluidMultiblockCapability;
import com.lowdragmc.multiblocked.common.capability.ItemMultiblockCapability;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author KilaBash
 * @date 2022/7/27
 * @implNote RecipeWidgetMixin
 */
@Mixin(RecipeWidget.class)
public class RecipeWidgetMixin {

    @Shadow @Final public Recipe recipe;

    @Inject(method = "<init>(Lcom/lowdragmc/multiblocked/api/recipe/Recipe;Ljava/util/function/DoubleSupplier;Lcom/lowdragmc/lowdraglib/gui/texture/ResourceTexture;Lcom/lowdragmc/lowdraglib/gui/texture/IGuiTexture;)V",
            at = @At("TAIL"))
    public void injectPInit(CallbackInfo ci) {
        RecipeWidget recipeWidget = (RecipeWidget) (Object) this;
        if (recipeWidget.recipe.uid.equals("kaka_demo_recipe")) {
            recipeWidget.clearAllWidgets();

            var background = new ImageWidget(-1, 0, 178, 84, new ResourceTexture("multiblocked:textures/gui/fuwenjitan.png"));
            recipeWidget.addWidget(background);

            var recipe = recipeWidget.recipe;
            var itemCapability = ItemMultiblockCapability.CAP;

            var inputs = recipe.getInputContents(itemCapability);
            var angle = 2 * 3.141596 / inputs.size();
            var i = 0;

            for (Content input : inputs) {
                var itemIn = itemCapability.createContentWidget();
                itemIn.setContent(IO.IN, input, false);
                itemIn.setSelfPosition((int) (32 + Math.sin(angle * i) * 32), (int) (32 + Math.cos(angle * i) * 32));
                i++;
                recipeWidget.addWidget(itemIn);
            }

            var fluidCapability = FluidMultiblockCapability.CAP;
            var fnputs = recipe.getInputContents(fluidCapability);
            var j = 0;

            for(Content fnput : fnputs) {
                var fluid = (FluidStack)fnput.content;
                var key = fluid.getTranslationKey();
                String texture;
                if (fluid.getFluid() == Fluids.WATER) {
                    texture = "multiblocked:textures/gui/aqueous_spirit.png";
                } else if (fluid.getFluid() == Fluids.LAVA){
                    texture = "multiblocked:textures/gui/infernal_spirit.png";
                } else {
                    texture = "multiblocked:textures/gui/wicked_spirit.png";
                }

                var spirit = new ImageWidget(139 + 16 * (j % 2 == 0 ? 0 : 1), 6 + 16 * (j / 2), 12, 12, new ResourceTexture(texture));

                spirit.setHoverTooltips(new TranslatableComponent(key), new TextComponent(fluid.getAmount() + "mB"));
                recipeWidget.addWidget(spirit);

                j++;
            }

            var input = ItemStack.of((CompoundTag) recipe.data.get("item"));

            var center = itemCapability.createContentWidget();

            center.setContent(IO.IN, Ingredient.of(input), 1, false);
            center.setSelfPosition(32, 32);
            recipeWidget.addWidget(center);

            var output = recipe.getOutputContents(itemCapability);
            var itemOut = itemCapability.createContentWidget();

            itemOut.setContent(IO.OUT, output.get(0), false);
            itemOut.setSelfPosition(100, 32);
            recipeWidget.addWidget(itemOut);

            var lns = recipe.data.getInt("recipelnstability");
            var dd = "negligible";

            if(lns >= 1 && lns < 3.5) {
                dd = "minor";
            } else if(lns >= 3.5 && lns < 5) {
                dd = "moderate";
            } else if(lns >= 5 && lns < 6.5) {
                dd = "high";
            } else if(lns >= 6.5 && lns < 8) {
                dd = "very_high";
            } else if(lns >= 8) {
                dd = "dangerous";
            }

            var instability = new ImageWidget(69, 65, 74, 12, new ResourceTexture("multiblocked:textures/gui/" + dd +".png"));
            instability.setHoverTooltips(new TranslatableComponent("kubejs.kaka.inst"), new TranslatableComponent("kubejs.kaka.inst." + dd));
            recipeWidget.addWidget(instability);
        }
    }
}
