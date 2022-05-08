package com.lowdragmc.shimmerfire.client;

import com.lowdragmc.shimmer.client.ShimmerRenderTypes;
import com.lowdragmc.shimmer.client.light.ColorPointLight;
import com.lowdragmc.shimmer.client.light.LightManager;
import com.lowdragmc.shimmerfire.CommonProxy;
import com.lowdragmc.shimmerfire.block.ColoredFireBlock;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import static com.lowdragmc.shimmerfire.block.ColoredFireBlock.FIRE_COLOR;

/**
 * @author KilaBash
 * @date: 2022/05/02
 * @implNote com.lowdragmc.shimmer.client.ClientProxy
 */
public class ClientProxy extends CommonProxy {

    @SubscribeEvent
    public void shaderRegistry(RegisterShadersEvent event) {

    }

    @SubscribeEvent
    public void clientSetup(FMLClientSetupEvent e) {
        e.enqueueWork(()->{
            ItemBlockRenderTypes.setRenderLayer(FIRE_BLOCK, ShimmerRenderTypes.bloom());
            ItemBlockRenderTypes.setRenderLayer(CAMPFIRE_BLOCK, renderType -> renderType == ShimmerRenderTypes.bloom() || renderType == RenderType.cutout());
            LightManager.INSTANCE.registerBlockLight(FIRE_BLOCK, state -> {
                ColoredFireBlock.FireColor color = state.getValue(FIRE_COLOR);
                return new ColorPointLight.Template(color.radius, color.colorVale);
            });
            LightManager.INSTANCE.registerBlockLight(CAMPFIRE_BLOCK, state -> {
                if (state.getValue(CampfireBlock.LIT)) {
                    ColoredFireBlock.FireColor color = state.getValue(FIRE_COLOR);
                    return new ColorPointLight.Template(color.radius, color.colorVale);
                }
                return null;
            });
        });
    }

    @SubscribeEvent
    public void modelBaked(ModelBakeEvent event) {

    }

}
