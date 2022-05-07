package com.lowdragmc.shimmerfire;


import com.lowdragmc.shimmerfire.block.ColoredFireBlock;
import com.lowdragmc.shimmerfire.item.ColoredFlintItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
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
    public static ColoredFireBlock[] FIRE_BLOCKS;
    public CommonProxy() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        eventBus.register(this);
    }

    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> event) {
        IForgeRegistry<Block> registry = event.getRegistry();
        FIRE_BLOCKS = new ColoredFireBlock[]{
                new ColoredFireBlock("orange", 0xFFA500),
                new ColoredFireBlock("cyan", 0xff00FFFF),
                new ColoredFireBlock("green", 0xff008000),
                new ColoredFireBlock("purple", 0xff800080),
        };
        for (ColoredFireBlock block : FIRE_BLOCKS) {
            registry.register(block);
        }
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();
        for (ColoredFireBlock fireBlock : FIRE_BLOCKS) {
            registry.register(new ColoredFlintItem(fireBlock));
        }
    }
}
