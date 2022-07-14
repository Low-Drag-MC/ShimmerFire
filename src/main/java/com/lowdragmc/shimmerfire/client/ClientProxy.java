package com.lowdragmc.shimmerfire.client;

import com.lowdragmc.shimmer.client.light.ColorPointLight;
import com.lowdragmc.shimmer.client.light.LightManager;
import com.lowdragmc.shimmer.client.shader.ShaderInjection;
import com.lowdragmc.shimmerfire.CommonProxy;
import com.lowdragmc.shimmerfire.ShimmerFireMod;
import com.lowdragmc.shimmerfire.api.RawFire;
import com.lowdragmc.shimmerfire.block.FireJarBlock;
import com.lowdragmc.shimmerfire.block.decorated.ColoredDecorationBlock;
import com.lowdragmc.shimmerfire.blockentity.FirePedestalBlockEntity;
import com.lowdragmc.shimmerfire.client.particle.SparkParticle;
import com.lowdragmc.shimmerfire.client.renderer.ColoredCampfireRenderer;
import com.lowdragmc.shimmerfire.client.renderer.FireCultureTankRenderer;
import com.lowdragmc.shimmerfire.client.renderer.FirePedestalRenderer;
import com.lowdragmc.shimmerfire.client.renderer.FireSpiritRenderer;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.RegistryObject;

import static com.lowdragmc.shimmerfire.block.ColoredFireBlock.FIRE;

/**
 * @author KilaBash
 * @date: 2022/05/02
 * @implNote com.lowdragmc.shimmer.client.ClientProxy
 */
public class ClientProxy extends CommonProxy {

    public ClientProxy() {
        super();
        ShaderInjection.registerFSHInjection("ldlib:particle", s -> {
            s = s.replace("fragColor = linear_fog(color, vertexDistance, FogStart, FogEnd, FogColor);", """
                        fragColor = linear_fog(color, vertexDistance, FogStart, FogEnd, FogColor);
                    """);
            return s;
        });
    }

    @SubscribeEvent
    public void onParticleFactoryRegister(ParticleFactoryRegisterEvent event) {
        Minecraft.getInstance().particleEngine.register(FIRE_SPARK.get(), SparkParticle.Provider::new);
    }

    @SubscribeEvent
    public void onRegisterRenderer(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(COLORED_CAMPFIRE.get(), ColoredCampfireRenderer::new);
        event.registerBlockEntityRenderer(FIRE_CULTURE_TANK.get(), FireCultureTankRenderer::new);
        event.registerBlockEntityRenderer(FIRE_PEDESTAL.get(), FirePedestalRenderer::new);
        event.registerEntityRenderer(FIRE_SPIRIT.get(), FireSpiritRenderer::new);
    }

    @SubscribeEvent
    public void shaderRegistry(RegisterShadersEvent event) {
        ResourceManager resourceManager = event.getResourceManager();
        try {
            event.registerShader(new ShaderInstance(resourceManager, new ResourceLocation(ShimmerFireMod.MODID, "rendertype_cutout_no_cull"), DefaultVertexFormat.NEW_ENTITY), shaderInstance -> RenderTypes.EmissiveCutoutNoCullRenderType.emissiveCutoutNoCullShader = shaderInstance);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SubscribeEvent
    public void clientSetup(FMLClientSetupEvent e) {
        e.enqueueWork(()->{
            ItemBlockRenderTypes.setRenderLayer(FIRE_BLOCK.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(CAMPFIRE_BLOCK.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(FIRE_PEDESTAL_BLOCK.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(FIRE_CULTURE_TANK_BLOCK.get(), renderType -> renderType == RenderType.translucent() || renderType == RenderType.solid());
            ItemBlockRenderTypes.setRenderLayer(CREATIVE_FIRE_CULTURE_TANK_BLOCK.get(), renderType -> renderType == RenderType.translucent() || renderType == RenderType.solid());
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

    @SubscribeEvent
    public void registerColorHandle(ColorHandlerEvent.Item event) {
        for (RegistryObject<ColoredDecorationBlock> coloredBloomBlock : CommonProxy.COLORED_BLOOM_BLOCKS) {
            event.getBlockColors().register((state, level, pos, tintIndex) -> coloredBloomBlock.get().color.color, coloredBloomBlock.get());
            event.getItemColors().register((stack, tintIndex) -> coloredBloomBlock.get().color.color, coloredBloomBlock.get().asItem());
        }

        event.getBlockColors().register((state, level, pos, tintIndex) -> {
            if (level != null && pos != null && level.getBlockEntity(pos) instanceof FirePedestalBlockEntity entity) {
                RawFire fire = entity.getFireType();
                return fire == null ? -1 : fire.colorVale;
            }
            return -1;
        }, CommonProxy.FIRE_PEDESTAL_BLOCK.get());

    }

}
