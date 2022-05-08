package com.lowdragmc.shimmerfire;


import com.lowdragmc.shimmerfire.block.ColoredCampfireBlock;
import com.lowdragmc.shimmerfire.block.ColoredFireBlock;
import com.lowdragmc.shimmerfire.item.ColoredFlintItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistry;


/**
 * @author KilaBash
 * @date 2022/05/02
 * @implNote com.lowdragmc.shimmer.CommonProxy
 */
public class CommonProxy {
    public static ColoredFireBlock FIRE_BLOCK;
    public static ColoredCampfireBlock CAMPFIRE_BLOCK;
    public static Item CAMPFIRE_ITEM;
    public CommonProxy() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        eventBus.register(this);
    }

    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> event) {
        IForgeRegistry<Block> registry = event.getRegistry();
        registry.register(FIRE_BLOCK = new ColoredFireBlock());
        registry.register(CAMPFIRE_BLOCK = new ColoredCampfireBlock());
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();
        registry.register(CAMPFIRE_ITEM = new BlockItem(CAMPFIRE_BLOCK, new Item.Properties().tab(CreativeModeTab.TAB_REDSTONE)).setRegistryName(CAMPFIRE_BLOCK.getRegistryName()));
        for (ColoredFireBlock.FireColor color : ColoredFireBlock.FireColor.values()) {
            registry.register(new ColoredFlintItem(color));
        }
    }
}
