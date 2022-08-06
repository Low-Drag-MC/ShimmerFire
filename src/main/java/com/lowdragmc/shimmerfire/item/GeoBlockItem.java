package com.lowdragmc.shimmerfire.item;

import com.lowdragmc.shimmerfire.client.renderer.GeoBlockItemRenderer;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.IItemRenderProperties;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Consumer;

/**
 * @author KilaBash
 * @date 2022/6/27
 * @implNote GeoBlockItem
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class GeoBlockItem extends BlockItem implements IAnimatable {

    private final AnimationFactory factory = new AnimationFactory(this);
    private String model;
    private final String controllerName;

    public GeoBlockItem(Block block, Item.Properties properties) {
        super(block, properties);
        setRegistryName(block.getRegistryName());
        controllerName = "controller_" + block.getRegistryName().getPath();
        model = block.getRegistryName().getPath();
    }

    private <P extends Item & IAnimatable> PlayState predicate(AnimationEvent<P> event) {
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, controllerName, 20, this::predicate));
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    public GeoBlockItem setModel(String model) {
        this.model = model;
        return this;
    }

    public String getModelName() {
        return model;
    }

    @Override
    public void initializeClient(Consumer<IItemRenderProperties> consumer) {
        consumer.accept(new IItemRenderProperties() {
            public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
                return GeoBlockItemRenderer.INSTANCE;
            }
        });
    }
}
