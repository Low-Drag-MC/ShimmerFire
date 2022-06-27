package com.lowdragmc.shimmerfire;

import com.lowdragmc.lowdraglib.ItemGroup.LDItemGroup;
import com.lowdragmc.shimmerfire.api.Capabilities;
import com.lowdragmc.shimmerfire.api.RawFire;
import com.lowdragmc.shimmerfire.block.*;
import com.lowdragmc.shimmerfire.block.decorated.ColoredDecorationBlock;
import com.lowdragmc.shimmerfire.blockentity.ColoredCampfireBlockEntity;
import com.lowdragmc.shimmerfire.blockentity.FireCultureTankBlockEntity;
import com.lowdragmc.shimmerfire.blockentity.FirePedestalBlockEntity;
import com.lowdragmc.shimmerfire.blockentity.FirePortBlockEntity;
import com.lowdragmc.shimmerfire.blockentity.multiblocked.HexGateBlockEntity;
import com.lowdragmc.shimmerfire.entity.FireSpiritEntity;
import com.lowdragmc.shimmerfire.item.BindingWand;
import com.lowdragmc.shimmerfire.item.ColoredFlintItem;
import com.lowdragmc.shimmerfire.item.GeoBlockItem;
import com.lowdragmc.shimmerfire.item.FireJarItem;
import net.minecraft.Util;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;

import java.util.Arrays;


/**
 * @author KilaBash
 * @date 2022/05/02
 * @implNote com.lowdragmc.shimmer.CommonProxy
 */
public class CommonProxy {
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ShimmerFireMod.MODID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ShimmerFireMod.MODID);
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, ShimmerFireMod.MODID);
    private static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, ShimmerFireMod.MODID);
    private static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, ShimmerFireMod.MODID);
    // blocks
    public static final RegistryObject<ColoredFireBlock> FIRE_BLOCK = BLOCKS.register("colored_fire", ColoredFireBlock::new);
    public static final RegistryObject<ColoredCampfireBlock> CAMPFIRE_BLOCK = BLOCKS.register("colored_campfire", ColoredCampfireBlock::new);
    public static final RegistryObject<FireJarBlock> FIRE_JAR_BLOCK = BLOCKS.register("fire_jar", FireJarBlock::new);
    public static final RegistryObject<FireCultureTankBlock> FIRE_CULTURE_TANK_BLOCK = BLOCKS.register("fire_culture_tank", FireCultureTankBlock::new);
    public static final RegistryObject<CreativeCultureTankBlock> CREATIVE_FIRE_CULTURE_TANK_BLOCK = BLOCKS.register("creative_fire_culture_tank", CreativeCultureTankBlock::new);
    public static final RegistryObject<FirePedestalBlock> FIRE_PEDESTAL_BLOCK = BLOCKS.register("fire_pedestal", FirePedestalBlock::new);
    public static final RegistryObject<FireEmitterBlock> FIRE_EMITTER_BLOCK = BLOCKS.register("fire_emitter", FireEmitterBlock::new);
    public static final RegistryObject<FireReceiverBlock> FIRE_RECEIVER_BLOCK = BLOCKS.register("fire_receiver", FireReceiverBlock::new);
    public static final RegistryObject<ColoredDecorationBlock>[] COLORED_BLOOM_BLOCKS = Util.make(() -> Arrays.stream(ColoredDecorationBlock.Color.values())
            .map(color -> BLOCKS.register("colored_bloom_block_" + color.name, () -> new ColoredDecorationBlock(color)))
            .toArray(RegistryObject[]::new));
    // block entities
    public static final RegistryObject<BlockEntityType<ColoredCampfireBlockEntity>> COLORED_CAMPFIRE = BLOCK_ENTITIES.register("campfire", () -> BlockEntityType.Builder.of(ColoredCampfireBlockEntity::new, CAMPFIRE_BLOCK.get()).build(null));
    public static final RegistryObject<BlockEntityType<FireCultureTankBlockEntity>> FIRE_CULTURE_TANK = BLOCK_ENTITIES.register("fire_culture_tank", () -> BlockEntityType.Builder.of(FireCultureTankBlockEntity::new, FIRE_CULTURE_TANK_BLOCK.get(), CREATIVE_FIRE_CULTURE_TANK_BLOCK.get()).build(null));
    public static final RegistryObject<BlockEntityType<FirePedestalBlockEntity>> FIRE_PEDESTAL = BLOCK_ENTITIES.register("fire_pedestal", () -> BlockEntityType.Builder.of(FirePedestalBlockEntity::new, FIRE_PEDESTAL_BLOCK.get()).build(null));
    public static final RegistryObject<BlockEntityType<FirePortBlockEntity>> FIRE_PORT = BLOCK_ENTITIES.register("fire_port", () -> BlockEntityType.Builder.of(FirePortBlockEntity::new, FIRE_EMITTER_BLOCK.get(), FIRE_RECEIVER_BLOCK.get()).build(null));
    // entities
    public static final RegistryObject<EntityType<FireSpiritEntity>> FIRE_SPIRIT = ENTITIES.register("fire_spirit", () -> EntityType.Builder.of(FireSpiritEntity::new, MobCategory.AMBIENT).sized(0.5F, 0.9F).build("fire_spirit"));
    // particles
    public static final RegistryObject<SimpleParticleType> FIRE_SPARK = PARTICLE_TYPES.register("fire_spark", () -> new SimpleParticleType(false));
    // Items
    public static final CreativeModeTab TAB_ITEMS = new LDItemGroup(ShimmerFireMod.MODID, "all", () -> new ItemStack(FIRE_EMITTER_BLOCK.get()));
    public static final RegistryObject<BindingWand> BINDING_WAND_ITEM = ITEMS.register("binding_wand", BindingWand::new);
    public static final RegistryObject<FireJarItem> FIRE_JAR_ITEM = ITEMS.register("fire_jar", ()->new FireJarItem(FIRE_JAR_BLOCK.get(), new Item.Properties().tab(TAB_ITEMS)));

    public CommonProxy() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        eventBus.register(this);
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);
        BLOCK_ENTITIES.register(eventBus);
        ENTITIES.register(eventBus);
        PARTICLE_TYPES.register(eventBus);
    }

    @SubscribeEvent
    public void registerEntityAttributeCreation(EntityAttributeCreationEvent event) {
        event.put(FIRE_SPIRIT.get(), FireSpiritEntity.createAttributes().build());
    }

    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> event) {
        HexGateBlockEntity.registerHexGate();
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();
        registry.register(new ForgeSpawnEggItem(FIRE_SPIRIT, 4996656, 986895, new Item.Properties().tab(CreativeModeTab.TAB_MISC)).setRegistryName(ShimmerFireMod.MODID, "fire_spirit_spawn_egg"));
        for (RawFire fire : RawFire.values()) {
            registry.register(new ColoredFlintItem(fire));
        }
        registry.register(new GeoBlockItem(FIRE_CULTURE_TANK_BLOCK.get(), new Item.Properties().tab(TAB_ITEMS)).setModel("culture_tank"));
        registry.register(new GeoBlockItem(CREATIVE_FIRE_CULTURE_TANK_BLOCK.get(), new Item.Properties().tab(TAB_ITEMS)).setModel("culture_tank"));
        registerSimpleItem(registry, CAMPFIRE_BLOCK.get());
        registerSimpleItem(registry, FIRE_PEDESTAL_BLOCK.get());
        registerSimpleItem(registry, FIRE_EMITTER_BLOCK.get());
        registerSimpleItem(registry, FIRE_RECEIVER_BLOCK.get());
        for (RegistryObject<ColoredDecorationBlock> block : COLORED_BLOOM_BLOCKS) {
            registerSimpleItem(registry, block.get());
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void registerSimpleItem(IForgeRegistry<Item> registry, Block block) {
        registry.register(new BlockItem(block, new Item.Properties().tab(TAB_ITEMS)).setRegistryName(block.getRegistryName()));
    }

    @SubscribeEvent
    public void registerCapabilities(RegisterCapabilitiesEvent event) {
        Capabilities.register(event);
    }
}
