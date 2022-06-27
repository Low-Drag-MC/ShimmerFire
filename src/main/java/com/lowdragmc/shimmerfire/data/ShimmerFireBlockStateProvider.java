package com.lowdragmc.shimmerfire.data;

import com.lowdragmc.shimmerfire.CommonProxy;
import com.lowdragmc.shimmerfire.ShimmerFireMod;
import com.lowdragmc.shimmerfire.block.FireCultureTankBlock;
import com.lowdragmc.shimmerfire.block.FireJarBlock;
import com.lowdragmc.shimmerfire.block.FirePortBlock;
import com.lowdragmc.shimmerfire.api.RawFire;
import com.lowdragmc.shimmerfire.block.decorated.ColoredDecorationBlock;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.client.model.ForgeModelBakery;
import net.minecraftforge.client.model.generators.*;
import net.minecraftforge.client.model.generators.loaders.MultiLayerModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.ParametersAreNonnullByDefault;

import java.util.function.Function;

import static com.lowdragmc.shimmerfire.block.ColoredFireBlock.FIRE;
import static net.minecraft.world.level.block.CampfireBlock.*;
import static net.minecraft.world.level.block.FireBlock.*;

/**
 * @author KilaBash
 * @date 2022/05/08
 * @implNote ColoredFireBlockStateProvider
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ShimmerFireBlockStateProvider extends BlockStateProvider {

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        generator.addProvider(new ShimmerFireBlockStateProvider(generator, event.getExistingFileHelper()));
    }

    private ShimmerFireBlockStateProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, ShimmerFireMod.MODID, existingFileHelper);
    }

    protected void registerStatesAndModels() {
        createColoredCampfire();
        createColoredFire();
        createFireContainer();
        createFireJar();
        createFirePort(CommonProxy.FIRE_EMITTER_BLOCK.get(), "block/fire_emitter");
        createFirePort(CommonProxy.FIRE_RECEIVER_BLOCK.get(), "block/fire_receiver");

        createFlintModels();
        createSimpleBlock(CommonProxy.FIRE_PEDESTAL_BLOCK.get());
        for (RegistryObject<ColoredDecorationBlock> coloredBloomBlock : CommonProxy.COLORED_BLOOM_BLOCKS) {
            createSimpleBlock(coloredBloomBlock.get(), new ResourceLocation(ShimmerFireMod.MODID, "block/colored_bloom_block"));
        }
    }

    private void createSimpleBlock(Block block) {
        createSimpleBlock(block, new ResourceLocation(ShimmerFireMod.MODID, "block/" + block.getRegistryName().getPath()));
    }

    private void createSimpleBlock(Block block, ResourceLocation model) {
        Property<?>[] properties = block.getStateDefinition().getProperties().toArray(Property<?>[]::new);
        getVariantBuilder(block).forAllStatesExcept(blockState -> ConfiguredModel.builder().modelFile(models().getExistingFile(model)).build(), properties);
        simpleBlockItem(block, models().getExistingFile(model));
    }

    private void createFlintModels() {
        for (RawFire fire : RawFire.values()) {
            itemModels().withExistingParent("flint_fire_" + fire.fireName, "minecraft:item/generated")
                    .texture("layer0", "minecraft:item/flint_and_steel");
        }
    }

    private void createFireContainer() {
        getVariantBuilder(CommonProxy.FIRE_CULTURE_TANK_BLOCK.get()).forAllStatesExcept(state -> ConfiguredModel.builder()
                .modelFile(models().getExistingFile(new ResourceLocation(ShimmerFireMod.MODID, "block/fire_culture_tank")))
                .build(), FireCultureTankBlock.HALF, FireCultureTankBlock.CHARGING);
        getVariantBuilder(CommonProxy.CREATIVE_FIRE_CULTURE_TANK_BLOCK.get()).forAllStatesExcept(state -> ConfiguredModel.builder()
                .modelFile(models().getExistingFile(new ResourceLocation(ShimmerFireMod.MODID, "block/fire_culture_tank")))
                .build(), FireCultureTankBlock.HALF, FireCultureTankBlock.CHARGING);

    }

    private void createFirePort(FirePortBlock block, String model) {
        getVariantBuilder(block).forAllStates(state -> {
            Direction dir = state.getValue(FirePortBlock.FACING);
            return ConfiguredModel.builder()
                    .modelFile(models().getExistingFile(new ResourceLocation(ShimmerFireMod.MODID, model)))
                    .rotationX(dir == Direction.DOWN ? 180 : dir.getAxis().isHorizontal() ? 90 : 0)
                    .rotationY(dir.getAxis().isVertical() ? 0 : (((int) dir.toYRot()) + 180) % 360)
                    .build();
        });
        simpleBlockItem(block, models().getExistingFile(new ResourceLocation(ShimmerFireMod.MODID, model)));
    }

    private void createFireJar() {
        ModelFile fireJarModel = models().getExistingFile(new ResourceLocation(ShimmerFireMod.MODID, "block/fire_jar"));
        ModelFile emptyModel = models().getExistingFile(new ResourceLocation(ShimmerFireMod.MODID, "block/fire_empty_jar"));
        ModelFile translucentModel = models().getExistingFile(new ResourceLocation(ShimmerFireMod.MODID, "block/fire_jar_translucent"));
        ModelFile cutoutModel = models().getExistingFile(new ResourceLocation(ShimmerFireMod.MODID, "block/fire_jar_cutout"));
        Function<RawFire, ModelFile> fireModelMap = fire -> {
            String name = "block/jar/" + fire.fireName;
            return models().getBuilder(name).customLoader(MultiLayerModelBuilder::begin)
                    .submodel(RenderType.translucent(), new BlockModelBuilder(fireJarModel.getLocation(), models().existingFileHelper).parent(translucentModel))
                    .submodel(RenderType.cutout(), new BlockModelBuilder(fireJarModel.getLocation(), models().existingFileHelper).parent(cutoutModel)
                            .texture("fire", "shimmerfire:blocks/fire/fire_0_" + fire.fireName)
                            .texture("fire2", "shimmerfire:blocks/fire/fire_1_" + fire.fireName))
                    .end()
                    .parent(fireJarModel)
                    .texture("fire", "shimmerfire:blocks/fire/fire_0_" + fire.fireName)
                    .texture("fire2", "shimmerfire:blocks/fire/fire_1_" + fire.fireName);
        };
        getVariantBuilder(CommonProxy.FIRE_JAR_BLOCK.get()).forAllStatesExcept(state -> {
            RawFire fire = state.getValue(FIRE);
            ModelFile modelFile;
            if (state.getValue(FireJarBlock.EMPTY)) {
                modelFile = emptyModel;
            } else {
                modelFile = fireModelMap.apply(fire);
            }
            return ConfiguredModel.builder().modelFile(modelFile).build();
        });
        int i = 0;
        ResourceLocation fireKey = new ResourceLocation(ShimmerFireMod.MODID, "fire");
        var itemModel = itemModels()
                .getBuilder(CommonProxy.FIRE_JAR_BLOCK.get().getRegistryName().getPath())
                .override().predicate(fireKey, i++).model(emptyModel).end();
        for (RawFire fire : RawFire.values()) {
            itemModel.override().predicate(fireKey, i++).model(fireModelMap.apply(fire)).end();
        }
    }

    private void createColoredCampfire() {
        ResourceLocation fireModel = new ResourceLocation(ShimmerFireMod.MODID, "block/campfire_fire_lit");
        ModelFile offModel = (this.models().withExistingParent("block/campfire/off", ModelLocationUtils.decorateBlockModelLocation("campfire_off")))
                .texture("particle", "minecraft:block/campfire_log");
        getVariantBuilder(CommonProxy.CAMPFIRE_BLOCK.get()).forAllStatesExcept(state -> {
            RawFire fireColor = state.getValue(FIRE);
            ModelFile modelFile;
            if (state.getValue(BlockStateProperties.LIT)) {
                modelFile = campfireLit(fireColor, fireModel);
            } else {
                modelFile = offModel;
            }
            return ConfiguredModel.builder()
                    .modelFile(modelFile)
                    .rotationY((int) state.getValue(FACING).toYRot())
                    .build();
        }, SIGNAL_FIRE, WATERLOGGED);
        simpleBlockItem(CommonProxy.CAMPFIRE_BLOCK.get(), offModel);
    }


    private ModelFile campfireLit(RawFire fire, ResourceLocation fireModel) {
        return this.models().withExistingParent("block/campfire/" + fire.fireName, fireModel)
                .texture("fire", "shimmerfire:blocks/campfire/campfire_fire_" + fire.fireName);
    }

    private void createColoredFire() {
        MultiPartBlockStateBuilder partBuilder = getMultipartBuilder(CommonProxy.FIRE_BLOCK.get());
        for (RawFire color : RawFire.values()) {
            fireDir(partBuilder, color);
        }
    }

    private void fireDir(MultiPartBlockStateBuilder builder, RawFire fire) {
        ModelFile fire_floor_0 = models().withExistingParent(ModelProvider.BLOCK_FOLDER + "/fire/" + fire.fireName + "/fire_floor_0", "minecraft:block/template_fire_floor").texture("fire", "shimmerfire:blocks/fire/fire_0_" + fire.fireName);
        ModelFile fire_floor_1 = models().withExistingParent(ModelProvider.BLOCK_FOLDER + "/fire/" + fire.fireName + "/fire_floor_1", "minecraft:block/template_fire_floor").texture("fire", "shimmerfire:blocks/fire/fire_1_" + fire.fireName);
        ModelFile fire_side_0 = models().withExistingParent(ModelProvider.BLOCK_FOLDER + "/fire/" + fire.fireName + "/fire_side_0", "minecraft:block/template_fire_side").texture("fire", "shimmerfire:blocks/fire/fire_0_" + fire.fireName);
        ModelFile fire_side_1 = models().withExistingParent(ModelProvider.BLOCK_FOLDER + "/fire/" + fire.fireName + "/fire_side_1", "minecraft:block/template_fire_side").texture("fire", "shimmerfire:blocks/fire/fire_1_" + fire.fireName);
        ModelFile fire_side_alt_0 = models().withExistingParent(ModelProvider.BLOCK_FOLDER + "/fire/" + fire.fireName + "/fire_side_alt_0", "minecraft:block/template_fire_side_alt").texture("fire", "shimmerfire:blocks/fire/fire_0_" + fire.fireName);
        ModelFile fire_side_alt_1 = models().withExistingParent(ModelProvider.BLOCK_FOLDER + "/fire/" + fire.fireName + "/fire_side_alt_1", "minecraft:block/template_fire_side_alt").texture("fire", "shimmerfire:blocks/fire/fire_1_" + fire.fireName);
        ModelFile fire_up_0 = models().withExistingParent(ModelProvider.BLOCK_FOLDER + "/fire/" + fire.fireName + "/fire_up_0", "minecraft:block/template_fire_up").texture("fire", "shimmerfire:blocks/fire/fire_0_" + fire.fireName);
        ModelFile fire_up_1 = models().withExistingParent(ModelProvider.BLOCK_FOLDER + "/fire/" + fire.fireName + "/fire_up_1", "minecraft:block/template_fire_up").texture("fire", "shimmerfire:blocks/fire/fire_1_" + fire.fireName);
        ModelFile fire_up_alt_0 = models().withExistingParent(ModelProvider.BLOCK_FOLDER + "/fire/" + fire.fireName + "/fire_up_alt_0", "minecraft:block/template_fire_up_alt").texture("fire", "shimmerfire:blocks/fire/fire_0_" + fire.fireName);
        ModelFile fire_up_alt_1 = models().withExistingParent(ModelProvider.BLOCK_FOLDER + "/fire/" + fire.fireName + "/fire_up_alt_1", "minecraft:block/template_fire_up_alt").texture("fire", "shimmerfire:blocks/fire/fire_1_" + fire.fireName);
        builder.part().modelFile(fire_floor_0).nextModel().modelFile(fire_floor_1).addModel()
                .condition(FIRE, fire).condition(UP, false).condition(WEST, false).condition(NORTH, false).condition(EAST, false).condition(SOUTH, false).end()

                .part().modelFile(fire_side_0).nextModel().modelFile(fire_side_1).nextModel().modelFile(fire_side_alt_0).nextModel().modelFile(fire_side_alt_1).addModel()
                .useOr()
                .nestedGroup()
                .condition(FIRE, fire).condition(NORTH,true)
                .end()
                .nestedGroup().condition(FIRE, fire).condition(UP, false).condition(WEST, false).condition(NORTH, false).condition(EAST, false).condition(SOUTH, false)
                .end()
                .end()

                .part().modelFile(fire_side_0).rotationY(90).nextModel().modelFile(fire_side_1).rotationY(90).nextModel().modelFile(fire_side_alt_0).rotationY(90).nextModel().modelFile(fire_side_alt_1).rotationY(90).addModel()
                .useOr()
                .nestedGroup()
                .condition(FIRE, fire).condition(EAST,true)
                .end()
                .nestedGroup().condition(FIRE, fire).condition(UP, false).condition(WEST, false).condition(NORTH, false).condition(EAST, false).condition(SOUTH, false)
                .end()
                .end()

                .part().modelFile(fire_side_0).rotationY(180).nextModel().modelFile(fire_side_1).rotationY(180).nextModel().modelFile(fire_side_alt_0).rotationY(180).nextModel().modelFile(fire_side_alt_1).rotationY(180).addModel()
                .useOr()
                .nestedGroup()
                .condition(FIRE, fire).condition(SOUTH,true)
                .end()
                .nestedGroup().condition(FIRE, fire).condition(UP, false).condition(WEST, false).condition(NORTH, false).condition(EAST, false).condition(SOUTH, false)
                .end()
                .end()

                .part().modelFile(fire_side_0).rotationY(270).nextModel().modelFile(fire_side_1).rotationY(270).nextModel().modelFile(fire_side_alt_0).rotationY(270).nextModel().modelFile(fire_side_alt_1).rotationY(270).addModel()
                .useOr()
                .nestedGroup()
                .condition(FIRE, fire).condition(WEST,true)
                .end()
                .nestedGroup().condition(FIRE, fire).condition(UP, false).condition(WEST, false).condition(NORTH, false).condition(EAST, false).condition(SOUTH, false)
                .end()
                .end()

                .part().modelFile(fire_up_0).nextModel().modelFile(fire_up_1).nextModel().modelFile(fire_up_alt_0).nextModel().modelFile(fire_up_alt_1).addModel()
                .condition(FIRE, fire).condition(UP,true)
                .end();
    }

}