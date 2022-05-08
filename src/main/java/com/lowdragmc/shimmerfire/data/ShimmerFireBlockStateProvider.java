package com.lowdragmc.shimmerfire.data;

import com.lowdragmc.shimmer.client.ShimmerRenderTypes;
import com.lowdragmc.shimmerfire.CommonProxy;
import com.lowdragmc.shimmerfire.ShimmerFireMod;
import com.lowdragmc.shimmerfire.block.ColoredFireBlock;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.client.model.generators.MultiPartBlockStateBuilder;
import net.minecraftforge.client.model.generators.loaders.MultiLayerModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.lowdragmc.shimmerfire.block.ColoredFireBlock.FIRE_COLOR;
import static net.minecraft.world.level.block.CampfireBlock.FACING;
import static net.minecraft.world.level.block.CampfireBlock.SIGNAL_FIRE;
import static net.minecraft.world.level.block.CampfireBlock.WATERLOGGED;
import static net.minecraft.world.level.block.FireBlock.EAST;
import static net.minecraft.world.level.block.FireBlock.NORTH;
import static net.minecraft.world.level.block.FireBlock.SOUTH;
import static net.minecraft.world.level.block.FireBlock.UP;
import static net.minecraft.world.level.block.FireBlock.WEST;

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
    }

    private void createColoredCampfire() {
        ModelFile onModel = models().getExistingFile(new ResourceLocation(ShimmerFireMod.MODID, "block/template_campfire"));
        ModelFile fireModel = models().getExistingFile(new ResourceLocation(ShimmerFireMod.MODID, "block/template_campfire_fire"));
        ModelFile offModel = ((this.models().withExistingParent("block/campfire/off", this.mcLoc("block/block")))
                .customLoader(MultiLayerModelBuilder::begin))
                .submodel(RenderType.cutout(), new BlockModelBuilder(new ResourceLocation("dummy"), models().existingFileHelper)
                        .parent(models().getExistingFile(ModelLocationUtils.decorateBlockModelLocation("campfire_off"))))
                .end();
        getVariantBuilder(CommonProxy.CAMPFIRE_BLOCK).forAllStatesExcept(state -> {
            ColoredFireBlock.FireColor fireColor = state.getValue(FIRE_COLOR);
            ModelFile modelFile;
            if (state.getValue(BlockStateProperties.LIT)) {
                modelFile = campfireLit(fireColor, onModel, fireModel);
            } else {
                modelFile = offModel;
            }
            return ConfiguredModel.builder()
                    .modelFile(modelFile)
                    .rotationY((int) state.getValue(FACING).toYRot())
                    .build();
        }, SIGNAL_FIRE, WATERLOGGED);
    }


    private ModelFile campfireLit(ColoredFireBlock.FireColor color, ModelFile baseModel, ModelFile fireModel) {
        return ((this.models().withExistingParent("block/campfire/" + color.colorName, this.mcLoc("block/block")))
                .customLoader(MultiLayerModelBuilder::begin))
                .submodel(RenderType.cutout(), new BlockModelBuilder(new ResourceLocation("dummy"), models().existingFileHelper)
                        .parent(baseModel))
                .submodel(ShimmerRenderTypes.bloom(), new BlockModelBuilder(new ResourceLocation("dummy"), models().existingFileHelper)
                        .parent(fireModel)
                        .texture("fire", "shimmerfire:blocks/campfire/campfire_fire_" + color.colorName))
                .end();
    }

    private void createColoredFire() {
        MultiPartBlockStateBuilder partBuilder = getMultipartBuilder(CommonProxy.FIRE_BLOCK);
        for (ColoredFireBlock.FireColor color : ColoredFireBlock.FireColor.values()) {
            fireDir(partBuilder, color);
        }
    }

    private void fireDir(MultiPartBlockStateBuilder builder, ColoredFireBlock.FireColor color) {
        ModelFile fire_floor_0 = models().withExistingParent(ModelProvider.BLOCK_FOLDER + "/fire/" + color.colorName + "/fire_floor_0", "minecraft:block/template_fire_floor").texture("fire", "shimmerfire:blocks/fire/fire_0_" + color.colorName);
        ModelFile fire_floor_1 = models().withExistingParent(ModelProvider.BLOCK_FOLDER + "/fire/" + color.colorName + "/fire_floor_1", "minecraft:block/template_fire_floor").texture("fire", "shimmerfire:blocks/fire/fire_1_" + color.colorName);
        ModelFile fire_side_0 = models().withExistingParent(ModelProvider.BLOCK_FOLDER + "/fire/" + color.colorName + "/fire_side_0", "minecraft:block/template_fire_side").texture("fire", "shimmerfire:blocks/fire/fire_0_" + color.colorName);
        ModelFile fire_side_1 = models().withExistingParent(ModelProvider.BLOCK_FOLDER + "/fire/" + color.colorName + "/fire_side_1", "minecraft:block/template_fire_side").texture("fire", "shimmerfire:blocks/fire/fire_1_" + color.colorName);
        ModelFile fire_side_alt_0 = models().withExistingParent(ModelProvider.BLOCK_FOLDER + "/fire/" + color.colorName + "/fire_side_alt_0", "minecraft:block/template_fire_side_alt").texture("fire", "shimmerfire:blocks/fire/fire_0_" + color.colorName);
        ModelFile fire_side_alt_1 = models().withExistingParent(ModelProvider.BLOCK_FOLDER + "/fire/" + color.colorName + "/fire_side_alt_1", "minecraft:block/template_fire_side_alt").texture("fire", "shimmerfire:blocks/fire/fire_1_" + color.colorName);
        ModelFile fire_up_0 = models().withExistingParent(ModelProvider.BLOCK_FOLDER + "/fire/" + color.colorName + "/fire_up_0", "minecraft:block/template_fire_up").texture("fire", "shimmerfire:blocks/fire/fire_0_" + color.colorName);
        ModelFile fire_up_1 = models().withExistingParent(ModelProvider.BLOCK_FOLDER + "/fire/" + color.colorName + "/fire_up_1", "minecraft:block/template_fire_up").texture("fire", "shimmerfire:blocks/fire/fire_1_" + color.colorName);
        ModelFile fire_up_alt_0 = models().withExistingParent(ModelProvider.BLOCK_FOLDER + "/fire/" + color.colorName + "/fire_up_alt_0", "minecraft:block/template_fire_up_alt").texture("fire", "shimmerfire:blocks/fire/fire_0_" + color.colorName);
        ModelFile fire_up_alt_1 = models().withExistingParent(ModelProvider.BLOCK_FOLDER + "/fire/" + color.colorName + "/fire_up_alt_1", "minecraft:block/template_fire_up_alt").texture("fire", "shimmerfire:blocks/fire/fire_1_" + color.colorName);
        builder.part().modelFile(fire_floor_0).nextModel().modelFile(fire_floor_1).addModel()
                .condition(FIRE_COLOR, color).condition(UP, false).condition(WEST, false).condition(NORTH, false).condition(EAST, false).condition(SOUTH, false).end()

                .part().modelFile(fire_side_0).nextModel().modelFile(fire_side_1).nextModel().modelFile(fire_side_alt_0).nextModel().modelFile(fire_side_alt_1).addModel()
                .useOr()
                .nestedGroup()
                .condition(FIRE_COLOR, color).condition(NORTH,true)
                .end()
                .nestedGroup().condition(FIRE_COLOR, color).condition(UP, false).condition(WEST, false).condition(NORTH, false).condition(EAST, false).condition(SOUTH, false)
                .end()
                .end()

                .part().modelFile(fire_side_0).rotationY(90).nextModel().modelFile(fire_side_1).rotationY(90).nextModel().modelFile(fire_side_alt_0).rotationY(90).nextModel().modelFile(fire_side_alt_1).rotationY(90).addModel()
                .useOr()
                .nestedGroup()
                .condition(FIRE_COLOR, color).condition(EAST,true)
                .end()
                .nestedGroup().condition(FIRE_COLOR, color).condition(UP, false).condition(WEST, false).condition(NORTH, false).condition(EAST, false).condition(SOUTH, false)
                .end()
                .end()

                .part().modelFile(fire_side_0).rotationY(180).nextModel().modelFile(fire_side_1).rotationY(180).nextModel().modelFile(fire_side_alt_0).rotationY(180).nextModel().modelFile(fire_side_alt_1).rotationY(180).addModel()
                .useOr()
                .nestedGroup()
                .condition(FIRE_COLOR, color).condition(SOUTH,true)
                .end()
                .nestedGroup().condition(FIRE_COLOR, color).condition(UP, false).condition(WEST, false).condition(NORTH, false).condition(EAST, false).condition(SOUTH, false)
                .end()
                .end()

                .part().modelFile(fire_side_0).rotationY(270).nextModel().modelFile(fire_side_1).rotationY(270).nextModel().modelFile(fire_side_alt_0).rotationY(270).nextModel().modelFile(fire_side_alt_1).rotationY(270).addModel()
                .useOr()
                .nestedGroup()
                .condition(FIRE_COLOR, color).condition(WEST,true)
                .end()
                .nestedGroup().condition(FIRE_COLOR, color).condition(UP, false).condition(WEST, false).condition(NORTH, false).condition(EAST, false).condition(SOUTH, false)
                .end()
                .end()

                .part().modelFile(fire_up_0).nextModel().modelFile(fire_up_1).nextModel().modelFile(fire_up_alt_0).nextModel().modelFile(fire_up_alt_1).addModel()
                .condition(FIRE_COLOR, color).condition(UP,true)
                .end();
    }

}