package com.lowdragmc.shimmerfire.client;

import com.lowdragmc.shimmer.client.light.ColorPointLight;
import com.lowdragmc.shimmer.client.light.LightManager;
import com.lowdragmc.shimmerfire.CommonProxy;
import com.lowdragmc.shimmerfire.ShimmerFireMod;
import com.lowdragmc.shimmerfire.api.RawFire;
import com.lowdragmc.shimmerfire.block.FireJarBlock;
import com.lowdragmc.shimmerfire.client.particle.SparkParticle;
import com.lowdragmc.shimmerfire.client.renderer.ColoredCampfireRenderer;
import com.lowdragmc.shimmerfire.client.renderer.FireContainerRenderer;
import com.lowdragmc.shimmerfire.client.renderer.FireSpiritRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import static com.lowdragmc.shimmerfire.block.ColoredFireBlock.FIRE;

/**
 * @author KilaBash
 * @date: 2022/05/02
 * @implNote com.lowdragmc.shimmer.client.ClientProxy
 */
public class ClientProxy extends CommonProxy {

    @SubscribeEvent
    public void onParticleFactoryRegister(ParticleFactoryRegisterEvent event) {
        Minecraft.getInstance().particleEngine.register(FIRE_SPARK.get(), SparkParticle.Provider::new);
    }

    @SubscribeEvent
    public void onRegisterRenderer(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(COLORED_CAMPFIRE.get(), ColoredCampfireRenderer::new);
        event.registerBlockEntityRenderer(FIRE_CONTAINER.get(), FireContainerRenderer::new);
        event.registerEntityRenderer(FIRE_SPIRIT.get(), FireSpiritRenderer::new);
    }

    @SubscribeEvent
    public void clientSetup(FMLClientSetupEvent e) {
        e.enqueueWork(()->{
            ItemBlockRenderTypes.setRenderLayer(FIRE_BLOCK.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(CAMPFIRE_BLOCK.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(FIRE_CONTAINER_BLOCK.get(), renderType -> renderType == RenderType.translucent() || renderType == RenderType.solid());
            ItemBlockRenderTypes.setRenderLayer(FIRE_JAR_BLOCK.get(), renderType -> renderType == RenderType.translucent() || renderType == RenderType.cutout());
            LightManager.INSTANCE.registerBlockLight(FIRE_BLOCK.get(), (state, pos) -> {
                RawFire fire = state.getValue(FIRE);
                return new ColorPointLight.Template(fire.radius, fire.colorVale);
            });
            LightManager.INSTANCE.registerBlockLight(CAMPFIRE_BLOCK.get(), (state, pos) -> {
                if (state.getValue(CampfireBlock.LIT)) {
                    RawFire fire = state.getValue(FIRE);
                    return new ColorPointLight.Template(fire.radius, fire.colorVale);
                }
                return null;
            });
            LightManager.INSTANCE.registerBlockLight(Blocks.SOUL_LANTERN, (state, pos) -> new ColorPointLight.Template(8, 0xff74F1F5));
            LightManager.INSTANCE.registerBlockLight(FIRE_JAR_BLOCK.get(), (blockState, pos) -> {
                if (!blockState.getValue(FireJarBlock.EMPTY)) {
                    RawFire fire = blockState.getValue(FIRE);
                    return new ColorPointLight.Template(8, fire.colorVale);
                }
                return null;
            });
            ItemProperties.register(FIRE_JAR_ITEM.get(),
                    new ResourceLocation(ShimmerFireMod.MODID, "fire"),
                    (itemStack, clientWorld, entity, seed) -> itemStack.getDamageValue());
        });
    }

}
