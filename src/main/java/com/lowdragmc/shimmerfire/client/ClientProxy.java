package com.lowdragmc.shimmerfire.client;

import com.lowdragmc.shimmer.CommonProxy;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.jetbrains.annotations.NotNull;

/**
 * @author KilaBash
 * @date: 2022/05/02
 * @implNote com.lowdragmc.shimmer.client.ClientProxy
 */
public class ClientProxy extends CommonProxy implements ResourceManagerReloadListener {

    @SubscribeEvent
    public void shaderRegistry(RegisterShadersEvent event) {

    }

    @SubscribeEvent
    public void clientSetup(FMLClientSetupEvent e) {
//        e.enqueueWork(()->{
//            ((ReloadableResourceManager)Minecraft.getInstance().getResourceManager()).registerReloadListener(this);
//            ItemBlockRenderTypes.setRenderLayer(CommonProxy.PISTON_BLOCK, ShimmerRenderTypes.bloom());
//            for (ColoredFireBlock fireBlock : FIRE_BLOCKS) {
//                ItemBlockRenderTypes.setRenderLayer(fireBlock, ShimmerRenderTypes.bloom());
//            }
//        });
    }

    @SubscribeEvent
    public void modelBaked(ModelBakeEvent event) {

    }

    @Override
    public void onResourceManagerReload(@NotNull ResourceManager resourceManager) {
//        for (Bloom bloom : Bloom.values()) {
//            bloom.onResourceManagerReload(resourceManager);
//        }
    }
}
