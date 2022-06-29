package com.lowdragmc.shimmerfire.data;

import com.lowdragmc.shimmerfire.CommonProxy;
import com.mojang.datafixers.util.Pair;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author KilaBash
 * @date 2022/05/08
 * @implNote ColoredFireBlockStateProvider
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ShimmerFireLootTablesProvider extends LootTableProvider {

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        generator.addProvider(new ShimmerFireLootTablesProvider(generator));
    }

    private ShimmerFireLootTablesProvider(DataGenerator generator) {
        super(generator);
    }
    protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> getTables() {
        return List.of(Pair.of(ShimmerFireBlockLoot::new, LootContextParamSets.BLOCK));
    }

    protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext context) {
        map.forEach((key, value) -> LootTables.validate(context, key, value));
    }

    private static class ShimmerFireBlockLoot extends BlockLoot {
        private ShimmerFireBlockLoot() {
        }

        protected Iterable<Block> getKnownBlocks() {
            return CommonProxy.BLOCKS.getEntries().stream().filter(obj -> obj.get() != CommonProxy.FIRE_BLOCK.get()).map(RegistryObject::get).toList();
        }

        protected void addTables() {
            for (RegistryObject<Block> entry : CommonProxy.BLOCKS.getEntries()) {
                if (entry.get() != CommonProxy.FIRE_BLOCK.get()) {
                    dropSelf(entry.get());
                }
            }
        }
    }

}