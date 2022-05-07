package com.lowdragmc.shimmerfire.client;

import com.lowdragmc.shimmer.client.ShimmerRenderTypes;
import com.lowdragmc.shimmer.client.light.LightManager;
import com.lowdragmc.shimmerfire.CommonProxy;
import com.lowdragmc.shimmerfire.block.ColoredFireBlock;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

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
            for (ColoredFireBlock fireBlock : FIRE_BLOCKS) {
                ItemBlockRenderTypes.setRenderLayer(fireBlock, ShimmerRenderTypes.bloom());
                LightManager.INSTANCE.registerBlockLight(fireBlock, fireBlock.color, 8);
            }
        });
    }

    @SubscribeEvent
    public void modelBaked(ModelBakeEvent event) {

    }

}
