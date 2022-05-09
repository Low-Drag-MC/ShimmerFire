package com.lowdragmc.shimmerfire.client;

import com.lowdragmc.shimmer.client.ShimmerRenderTypes;
import com.lowdragmc.shimmer.client.light.ColorPointLight;
import com.lowdragmc.shimmer.client.light.LightManager;
import com.lowdragmc.shimmerfire.CommonProxy;
import com.lowdragmc.shimmerfire.block.ColoredFireBlock;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraftforge.client.event.EntityRenderersEvent;
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
    public void onRegisterRenderer(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(COLORED_CAMPFIRE.get(), ColoredCampfireRenderer::new);
    }

    @SubscribeEvent
    public void clientSetup(FMLClientSetupEvent e) {
        e.enqueueWork(()->{
            ItemBlockRenderTypes.setRenderLayer(FIRE_BLOCK.get(), ShimmerRenderTypes.bloom());
            ItemBlockRenderTypes.setRenderLayer(CAMPFIRE_BLOCK.get(), renderType -> renderType == ShimmerRenderTypes.bloom() || renderType == RenderType.cutout());
            LightManager.INSTANCE.registerBlockLight(FIRE_BLOCK.get(), state -> {
                ColoredFireBlock.FireColor color = state.getValue(FIRE_COLOR);
                return new ColorPointLight.Template(color.radius, color.colorVale);
            });
            LightManager.INSTANCE.registerBlockLight(CAMPFIRE_BLOCK.get(), state -> {
                if (state.getValue(CampfireBlock.LIT)) {
                    ColoredFireBlock.FireColor color = state.getValue(FIRE_COLOR);
                    return new ColorPointLight.Template(color.radius, color.colorVale);
                }
                return null;
            });
        });
    }

}
