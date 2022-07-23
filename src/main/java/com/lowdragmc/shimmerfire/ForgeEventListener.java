package com.lowdragmc.shimmerfire;

import com.lowdragmc.shimmer.client.shader.ReloadShaderManager;
import com.lowdragmc.shimmerfire.client.ClientProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.Commands;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * @author KilaBash
 * @date 2022/5/12
 * @implNote ForgeEventListener
 */
@Mod.EventBusSubscriber(modid = ShimmerFireMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
@OnlyIn(Dist.CLIENT)
public class ForgeEventListener {

    @SubscribeEvent
    public static void onRegisterClientCommands(RegisterClientCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("shimmerfire")
                .then(Commands.literal("leaves_bloom_on")
                        .executes(context -> {
                            ClientProxy.BLOOM_LEAVE = true;
                            return 1;
                        }))
                .then(Commands.literal("leaves_bloom_off")
                        .executes(context -> {
                            ClientProxy.BLOOM_LEAVE = false;
                            return 1;
                        }))
        );
    }
}
