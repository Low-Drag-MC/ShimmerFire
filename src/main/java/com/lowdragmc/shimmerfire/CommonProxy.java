package com.lowdragmc.shimmerfire;


import com.lowdragmc.shimmer.ShimmerMod;
import com.lowdragmc.shimmerfire.block.ColoredCampfireBlock;
import com.lowdragmc.shimmerfire.block.ColoredFireBlock;
import com.lowdragmc.shimmerfire.blockentity.ColoredCampfireBlockEntity;
import com.lowdragmc.shimmerfire.entity.FireSpirit;
import com.lowdragmc.shimmerfire.item.ColoredFlintItem;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;


/**
 * @author KilaBash
 * @date 2022/05/02
 * @implNote com.lowdragmc.shimmer.CommonProxy
 */
public class CommonProxy {
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ShimmerFireMod.MODID);
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, ShimmerFireMod.MODID);
    private static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, ShimmerFireMod.MODID);
    public static RegistryObject<ColoredFireBlock> FIRE_BLOCK = BLOCKS.register("colored_fire", ColoredFireBlock::new);
    public static RegistryObject<ColoredCampfireBlock> CAMPFIRE_BLOCK = BLOCKS.register("colored_campfire", ColoredCampfireBlock::new);
    public static RegistryObject<BlockEntityType<ColoredCampfireBlockEntity>> COLORED_CAMPFIRE = BLOCK_ENTITIES.register("campfire", () -> BlockEntityType.Builder.of(ColoredCampfireBlockEntity::new, CAMPFIRE_BLOCK.get()).build(null));
    public static RegistryObject<EntityType<FireSpirit>> FIRE_SPIRIT = ENTITIES.register("fire_spirit", () -> EntityType.Builder.of(FireSpirit::new, MobCategory.AMBIENT).sized(0.5F, 0.9F).build("fire_spirit"));
    public CommonProxy() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        eventBus.register(this);
        BLOCKS.register(eventBus);
        BLOCK_ENTITIES.register(eventBus);
        ENTITIES.register(eventBus);
    }

    @SubscribeEvent
    public void registerEntityAttributeCreation(EntityAttributeCreationEvent event) {
        event.put(FIRE_SPIRIT.get(), FireSpirit.createAttributes().build());
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();
        registry.register(new ForgeSpawnEggItem(FIRE_SPIRIT, 4996656, 986895, (new Item.Properties()).tab(CreativeModeTab.TAB_MISC)).setRegistryName(ShimmerMod.MODID, "fire_spirit_spawn_egg"));
        registry.register(new BlockItem(CAMPFIRE_BLOCK.get(), new Item.Properties().tab(CreativeModeTab.TAB_REDSTONE)).setRegistryName(CAMPFIRE_BLOCK.get().getRegistryName()));
        for (ColoredFireBlock.FireColor color : ColoredFireBlock.FireColor.values()) {
            registry.register(new ColoredFlintItem(color));
        }
    }
}
