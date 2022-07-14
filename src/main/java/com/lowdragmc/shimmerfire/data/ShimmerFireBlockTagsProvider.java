package com.lowdragmc.shimmerfire.data;

import com.lowdragmc.shimmerfire.CommonProxy;
import com.lowdragmc.shimmerfire.ShimmerFireMod;
import com.lowdragmc.shimmerfire.block.decorated.ColoredDecorationBlock;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import net.minecraftforge.registries.RegistryObject;

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
        this.tag(BlockTags.CAMPFIRES).add(CommonProxy.CAMPFIRE_BLOCK.get(),CommonProxy.COLORFUL_CAMPFIRE_BLOCK.get());
        this.tag(BlockTags.MINEABLE_WITH_AXE).add(CommonProxy.CAMPFIRE_BLOCK.get(),CommonProxy.COLORFUL_CAMPFIRE_BLOCK.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(
                CommonProxy.FIRE_JAR_BLOCK.get(),
                CommonProxy.FIRE_CULTURE_TANK_BLOCK.get(),
                CommonProxy.CREATIVE_FIRE_CULTURE_TANK_BLOCK.get(),
                CommonProxy.FIRE_PEDESTAL_BLOCK.get(),
                CommonProxy.FIRE_EMITTER_BLOCK.get(),
                CommonProxy.FIRE_RECEIVER_BLOCK.get());
        for (RegistryObject<ColoredDecorationBlock> coloredBloomBlock : CommonProxy.COLORED_BLOOM_BLOCKS) {
            this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(coloredBloomBlock.get());
        }
    }

    @Override
    public String getName() {
        return "Shimmer Tag";
    }
}