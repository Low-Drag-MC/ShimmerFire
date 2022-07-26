package com.lowdragmc.shimmerfire;

import com.google.gson.JsonObject;
import com.lowdragmc.lowdraglib.ItemGroup.LDItemGroup;
import com.lowdragmc.lowdraglib.client.renderer.impl.BlockStateRenderer;
import com.lowdragmc.lowdraglib.utils.FileUtility;
import com.lowdragmc.multiblocked.Multiblocked;
import com.lowdragmc.multiblocked.api.definition.ComponentDefinition;
import com.lowdragmc.multiblocked.api.definition.ControllerDefinition;
import com.lowdragmc.multiblocked.api.definition.PartDefinition;
import com.lowdragmc.multiblocked.api.recipe.RecipeMap;
import com.lowdragmc.multiblocked.api.registry.MbdComponents;
import com.lowdragmc.multiblocked.client.renderer.IMultiblockedRenderer;
import com.lowdragmc.shimmerfire.api.Capabilities;
import com.lowdragmc.shimmerfire.api.RawFire;
import com.lowdragmc.shimmerfire.block.*;
import com.lowdragmc.shimmerfire.block.decorated.ColoredDecorationBlock;
import com.lowdragmc.shimmerfire.blockentity.*;
import com.lowdragmc.shimmerfire.blockentity.multiblocked.AssemblyBlockEntity;
import com.lowdragmc.shimmerfire.blockentity.multiblocked.FireBaptismBlockEntity;
import com.lowdragmc.shimmerfire.blockentity.multiblocked.HexGateBlockEntity;
import com.lowdragmc.shimmerfire.entity.FireSpiritEntity;
import com.lowdragmc.shimmerfire.item.*;
import net.minecraft.Util;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;


/**
 * @author KilaBash
 * @date 2022/05/02
 * @implNote com.lowdragmc.shimmer.CommonProxy
 */
public class CommonProxy {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ShimmerFireMod.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ShimmerFireMod.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, ShimmerFireMod.MODID);
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, ShimmerFireMod.MODID);
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, ShimmerFireMod.MODID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS,ShimmerFireMod.MODID);
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
    public static final RegistryObject<ColorfulFireBlock> COLORFUL_FIRE_BLOCK = BLOCKS.register("colorful_fire",ColorfulFireBlock::new);
    public static final RegistryObject<ColorfulCampfireBlock> COLORFUL_CAMPFIRE_BLOCK = BLOCKS.register("colorful_campfire",ColorfulCampfireBlock::new);
    public static final RegistryObject<MimicDissolveBlock> MIMIC_DISSOLVE_BLOCK = BLOCKS.register("mimic_dissolve",MimicDissolveBlock::new);
    // block entities
    public static final RegistryObject<BlockEntityType<ColoredCampfireBlockEntity>> COLORED_CAMPFIRE = BLOCK_ENTITIES.register("campfire", () -> BlockEntityType.Builder.of(ColoredCampfireBlockEntity::new, CAMPFIRE_BLOCK.get()).build(null));
    public static final RegistryObject<BlockEntityType<FireCultureTankBlockEntity>> FIRE_CULTURE_TANK = BLOCK_ENTITIES.register("fire_culture_tank", () -> BlockEntityType.Builder.of(FireCultureTankBlockEntity::new, FIRE_CULTURE_TANK_BLOCK.get(), CREATIVE_FIRE_CULTURE_TANK_BLOCK.get()).build(null));
    public static final RegistryObject<BlockEntityType<FirePedestalBlockEntity>> FIRE_PEDESTAL = BLOCK_ENTITIES.register("fire_pedestal", () -> BlockEntityType.Builder.of(FirePedestalBlockEntity::new, FIRE_PEDESTAL_BLOCK.get()).build(null));
    public static final RegistryObject<BlockEntityType<FirePortBlockEntity>> FIRE_PORT = BLOCK_ENTITIES.register("fire_port", () -> BlockEntityType.Builder.of(FirePortBlockEntity::new, FIRE_EMITTER_BLOCK.get(), FIRE_RECEIVER_BLOCK.get()).build(null));
    public static final RegistryObject<BlockEntityType<ColorfulFireBlockEntity>> COLORFUL_FIRE = BLOCK_ENTITIES.register("colorful_fire",() -> BlockEntityType.Builder.of(ColorfulFireBlockEntity::new,COLORFUL_FIRE_BLOCK.get()).build(null));
    public static final RegistryObject<BlockEntityType<ColorfulCampfireBlockEntity>> COLORFUL_CAMPFIRE = BLOCK_ENTITIES.register("colorful_campfire_fire",() -> BlockEntityType.Builder.of(ColorfulCampfireBlockEntity::new,COLORFUL_CAMPFIRE_BLOCK.get()).build(null));
    public static final RegistryObject<BlockEntityType<MimicDissolveBlockEntity>> MIMIC_DISSOLVE = BLOCK_ENTITIES.register("mimic_dissolve",()->BlockEntityType.Builder.of(MimicDissolveBlockEntity::new,MIMIC_DISSOLVE_BLOCK.get()).build(null));
    // entities
    public static final RegistryObject<EntityType<FireSpiritEntity>> FIRE_SPIRIT = ENTITIES.register("fire_spirit", () -> EntityType.Builder.of(FireSpiritEntity::new, MobCategory.MISC).sized(0.5F, 0.9F).build("fire_spirit"));
    // particles
    public static final RegistryObject<SimpleParticleType> FIRE_SPARK = PARTICLE_TYPES.register("fire_spark", () -> new SimpleParticleType(false));
    // Items
    public static final CreativeModeTab TAB_ITEMS = new LDItemGroup(ShimmerFireMod.MODID, "all", () -> new ItemStack(FIRE_EMITTER_BLOCK.get()));
    public static final RegistryObject<BindingWand> BINDING_WAND_ITEM = ITEMS.register("binding_wand", BindingWand::new);
    public static final RegistryObject<FireJarItem> FIRE_JAR_ITEM = ITEMS.register("fire_jar", ()->new FireJarItem(FIRE_JAR_BLOCK.get(), new Item.Properties().tab(TAB_ITEMS)));
    public static final RegistryObject<ColorfulFlintItem> COLORFUL_FLINT_ITEM = ITEMS.register("colorful_flint_fire",ColorfulFlintItem::new);
    // recipe
    public static final RegistryObject<SimpleRecipeSerializer<FlintDyeRecipe>> FLINT_DYE_RECIPE = RECIPE.register("flint_dye",()-> new SimpleRecipeSerializer<>(FlintDyeRecipe::new));
    public CommonProxy() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        eventBus.register(this);
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);
        BLOCK_ENTITIES.register(eventBus);
        ENTITIES.register(eventBus);
        PARTICLE_TYPES.register(eventBus);
        RECIPE.register(eventBus);
        eventBus.addListener(CommonProxy::loadCompleteEvent);
    }

    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> event) {
        HexGateBlockEntity.registerHexGate();
        FireBaptismBlockEntity.registerFireBaptism();

        MbdComponents.registerComponentFromResource(ShimmerFireMod.class, Multiblocked.GSON, new ResourceLocation(Multiblocked.MODID, "part/mbd_coil"), PartDefinition.class, CommonProxy::componentPost);
        MbdComponents.registerComponentFromResource(ShimmerFireMod.class, Multiblocked.GSON, new ResourceLocation(Multiblocked.MODID, "part/mbd_ebf_casing"), PartDefinition.class, CommonProxy::componentPost);
        MbdComponents.registerComponentFromResource(ShimmerFireMod.class, Multiblocked.GSON, new ResourceLocation(Multiblocked.MODID, "part/multiblocked_base"), PartDefinition.class, CommonProxy::componentPost);
        MbdComponents.registerComponentFromResource(ShimmerFireMod.class, Multiblocked.GSON, new ResourceLocation(Multiblocked.MODID, "part/multiblocked_ebf_casing"), PartDefinition.class, CommonProxy::componentPost);
        MbdComponents.registerComponentFromResource(ShimmerFireMod.class, Multiblocked.GSON, new ResourceLocation(Multiblocked.MODID, "part/multiblocked_energy_hatch"), PartDefinition.class, CommonProxy::componentPost);
        MbdComponents.registerComponentFromResource(ShimmerFireMod.class, Multiblocked.GSON, new ResourceLocation(Multiblocked.MODID, "part/multiblocked_item_hatch"), PartDefinition.class, CommonProxy::componentPost);
        MbdComponents.registerComponentFromResource(ShimmerFireMod.class, Multiblocked.GSON, new ResourceLocation(Multiblocked.MODID, "part/multiblocked_sun"), PartDefinition.class, CommonProxy::componentPost);
        MbdComponents.registerComponentFromResource(ShimmerFireMod.class, Multiblocked.GSON, new ResourceLocation(Multiblocked.MODID, "part/multiblocked_atesla"), PartDefinition.class, CommonProxy::componentPost);

        MbdComponents.registerComponentFromResource(ShimmerFireMod.class, Multiblocked.GSON, new ResourceLocation(Multiblocked.MODID, "controller/multiblocked_ebf"), ControllerDefinition.class, com.lowdragmc.multiblocked.CommonProxy::controllerPost);
        MbdComponents.registerComponentFromResource(ShimmerFireMod.class, Multiblocked.GSON, new ResourceLocation(Multiblocked.MODID, "controller/multiblocked_alsm"), ControllerDefinition.class, com.lowdragmc.multiblocked.CommonProxy::controllerPost);
        MbdComponents.registerComponentFromResource(ShimmerFireMod.class, Multiblocked.GSON, new ResourceLocation(Multiblocked.MODID, "controller/multiblocked_art_sun_controller"), ControllerDefinition.class, com.lowdragmc.multiblocked.CommonProxy::controllerPost);
        MbdComponents.registerComponentFromResource(ShimmerFireMod.class, Multiblocked.GSON, new ResourceLocation(Multiblocked.MODID, "controller/multiblocked_assembly"), AssemblyBlockEntity.AssemblyDefinition.class, com.lowdragmc.multiblocked.CommonProxy::controllerPost);
        MbdComponents.registerComponentFromResource(ShimmerFireMod.class, Multiblocked.GSON, new ResourceLocation(Multiblocked.MODID, "controller/kaka_fuwenjitan"), ControllerDefinition.class, com.lowdragmc.multiblocked.CommonProxy::controllerPost);
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();
        registry.register(new FireSpiritItem(FIRE_SPIRIT, new Item.Properties().tab(TAB_ITEMS)).setRegistryName(ShimmerFireMod.MODID, "fire_spirit"));
        for (RawFire fire : RawFire.values()) {
            registry.register(new ColoredFlintItem(fire));
        }
        registry.register(new GeoBlockItem(FIRE_CULTURE_TANK_BLOCK.get(), new Item.Properties().tab(TAB_ITEMS)).setModel("culture_tank"));
        registry.register(new GeoBlockItem(CREATIVE_FIRE_CULTURE_TANK_BLOCK.get(), new Item.Properties().tab(TAB_ITEMS)).setModel("culture_tank"));
        registry.register(new GeoBlockItem(FIRE_PEDESTAL_BLOCK.get(), new Item.Properties().tab(TAB_ITEMS)).setModel("fire_pedestal"));
        registerSimpleItem(registry, CAMPFIRE_BLOCK.get());
        registerSimpleItem(registry, FIRE_EMITTER_BLOCK.get());
        registerSimpleItem(registry, FIRE_RECEIVER_BLOCK.get());
        for (RegistryObject<ColoredDecorationBlock> block : COLORED_BLOOM_BLOCKS) {
            registerSimpleItem(registry, block.get());
        }
        registerSimpleItem(registry,MIMIC_DISSOLVE_BLOCK.get());
        registerSimpleItem(registry,COLORFUL_CAMPFIRE_BLOCK.get());
    }

    @SuppressWarnings("ConstantConditions")
    private void registerSimpleItem(IForgeRegistry<Item> registry, Block block) {
        registry.register(new BlockItem(block, new Item.Properties().tab(TAB_ITEMS)).setRegistryName(block.getRegistryName()));
    }

    @SubscribeEvent
    public void registerCapabilities(RegisterCapabilitiesEvent event) {
        Capabilities.register(event);
    }

    @SubscribeEvent
    public void commonSetup(FMLCommonSetupEvent e) {
        e.enqueueWork(() -> {
            registerRecipeFromResource(new ResourceLocation(Multiblocked.MODID, "alsm"));
            registerRecipeFromResource(new ResourceLocation(Multiblocked.MODID, "ebf"));
            registerRecipeFromResource(new ResourceLocation(Multiblocked.MODID, "sun"));
            registerRecipeFromResource(new ResourceLocation(Multiblocked.MODID, "asembly"));
        });

    }

    private static void componentPost(ComponentDefinition definition, JsonObject config) {
        if (definition.baseRenderer instanceof BlockStateRenderer) {
            definition.baseRenderer = Multiblocked.GSON.fromJson(config.get("baseRenderer"), IMultiblockedRenderer.class);
        }
        if (definition.formedRenderer instanceof BlockStateRenderer) {
            definition.formedRenderer = Multiblocked.GSON.fromJson(config.get("formedRenderer"), IMultiblockedRenderer.class);
        }
        if (definition.workingRenderer instanceof BlockStateRenderer) {
            definition.workingRenderer = Multiblocked.GSON.fromJson(config.get("workingRenderer"), IMultiblockedRenderer.class);
        }
    }

    public static void registerRecipeFromResource(ResourceLocation location) {
        try {
            InputStream inputstream = ShimmerFireMod.class.getResourceAsStream(String.format("/assets/%s/recipe_map/%s.json", location.getNamespace(), location.getPath()));
            JsonObject config = FileUtility.jsonParser.parse(new InputStreamReader(inputstream)).getAsJsonObject();
            RecipeMap recipeMap = Multiblocked.GSON.fromJson(config, RecipeMap.class);
            if (recipeMap != null && !recipeMap.name.equals("empty")) {
                RecipeMap.register(recipeMap);
            }
        } catch (Exception e) {
            Multiblocked.LOGGER.error("error while loading the definition resource {}", location.toString());
        }
    }

    @SubscribeEvent
    public static void loadCompleteEvent(FMLLoadCompleteEvent event){
        event.enqueueWork(()->{
            CauldronInteraction.WATER.put(COLORFUL_FLINT_ITEM.get(), (pBlockState, pLevel, pBlockPos, pPlayer, pHand, pStack) -> {
                Item item = pStack.getItem();
                if (!(item instanceof ColorfulFlintItem) || !pStack.getTag().contains("color")){
                    return InteractionResult.PASS;
                }
                if (!pLevel.isClientSide){
                    pStack.getTag().remove("color");
                    LayeredCauldronBlock.lowerFillLevel(pBlockState,pLevel,pBlockPos);
                }
                return InteractionResult.sidedSuccess(pLevel.isClientSide);
            });
        });
    }
}
