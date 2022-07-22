package com.lowdragmc.shimmerfire.core.mixins;

import com.lowdragmc.shimmer.client.postprocessing.PostProcessing;
import com.lowdragmc.shimmerfire.ShimmerFireMod;
import com.lowdragmc.shimmerfire.core.IBloomParticle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

/**
 * @author KilaBash
 * @date 2022/7/22
 * @implNote ClientPacketListenermIXIN
 */
@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {

    @Shadow @Final private Minecraft minecraft;

    @Shadow @Final private Random random;

    @Inject(method = "handleParticleEvent", at = @At(value = "HEAD"), cancellable = true)
    public void injectInit(ClientboundLevelParticlesPacket pPacket, CallbackInfo ci) {
        if (pPacket instanceof IBloomParticle bloomParticle && bloomParticle.isBloom()) {
            PacketUtils.ensureRunningOnSameThread(pPacket, ((ClientPacketListener)(Object)this), this.minecraft);
            if (pPacket.getCount() == 0) {
                double d0 = pPacket.getMaxSpeed() * pPacket.getXDist();
                double d2 = pPacket.getMaxSpeed() * pPacket.getYDist();
                double d4 = pPacket.getMaxSpeed() * pPacket.getZDist();
                try {
                    PostProcessing.BLOOM_UNREAL.postParticle(pPacket.getParticle(), pPacket.getX(), pPacket.getY(), pPacket.getZ(), d0, d2, d4);
                    ci.cancel();
                } catch (Throwable throwable1) {
                    ShimmerFireMod.LOGGER.warn("Could not spawn particle effect {}", pPacket.getParticle());
                }
            } else {
                for(int i = 0; i < pPacket.getCount(); ++i) {
                    double d1 = this.random.nextGaussian() * (double)pPacket.getXDist();
                    double d3 = this.random.nextGaussian() * (double)pPacket.getYDist();
                    double d5 = this.random.nextGaussian() * (double)pPacket.getZDist();
                    double d6 = this.random.nextGaussian() * (double)pPacket.getMaxSpeed();
                    double d7 = this.random.nextGaussian() * (double)pPacket.getMaxSpeed();
                    double d8 = this.random.nextGaussian() * (double)pPacket.getMaxSpeed();

                    try {
                        PostProcessing.BLOOM_UNREAL.postParticle(pPacket.getParticle(), pPacket.getX() + d1, pPacket.getY() + d3, pPacket.getZ() + d5, d6, d7, d8);
                        ci.cancel();
                    } catch (Throwable throwable) {
                        ShimmerFireMod.LOGGER.warn("Could not spawn particle effect {}", pPacket.getParticle());
                        return;
                    }
                }
            }
        }
    }
}
