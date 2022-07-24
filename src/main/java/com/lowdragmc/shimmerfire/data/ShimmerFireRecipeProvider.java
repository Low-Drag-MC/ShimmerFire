package com.lowdragmc.shimmerfire.data;

import com.lowdragmc.shimmerfire.CommonProxy;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SpecialRecipeBuilder;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Consumer;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ShimmerFireRecipeProvider extends RecipeProvider {
    private ShimmerFireRecipeProvider(DataGenerator pGenerator) {
        super(pGenerator);
    }

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        generator.addProvider(new ShimmerFireRecipeProvider(generator));
    }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> pFinishedRecipeConsumer) {
        SpecialRecipeBuilder.special(CommonProxy.FLINT_DYE_RECIPE.get())
                .save(pFinishedRecipeConsumer, "flint_dye");
    }
}
