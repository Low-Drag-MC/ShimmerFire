package com.lowdragmc.shimmerfire.data;

import com.lowdragmc.shimmerfire.CommonProxy;
import com.lowdragmc.shimmerfire.ShimmerFireMod;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * @author KilaBash
 * @date 2022/05/08
 * @implNote ColoredFireBlockStateProvider
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ShimmerFireBlockTagsProvider extends BlockTagsProvider {

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        generator.addProvider(new ShimmerFireBlockTagsProvider(generator, event.getExistingFileHelper()));
    }

    private ShimmerFireBlockTagsProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, ShimmerFireMod.MODID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        this.tag(BlockTags.CAMPFIRES).add(CommonProxy.CAMPFIRE_BLOCK.get());
    }

    @Override
    public String getName() {
        return "Shimmer Tag";
    }
}