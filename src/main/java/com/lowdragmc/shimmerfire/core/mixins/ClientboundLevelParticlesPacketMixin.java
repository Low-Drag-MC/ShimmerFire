package com.lowdragmc.shimmerfire.core.mixins;

import com.lowdragmc.shimmerfire.core.IBloomParticle;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author KilaBash
 * @date 2022/7/22
 * @implNote ClientboundLevelParticlesPacketMixin
 */
@Mixin(ClientboundLevelParticlesPacket.class)
public class ClientboundLevelParticlesPacketMixin implements IBloomParticle {
    boolean bloom;

    @Override
    public boolean isBloom() {
        return bloom;
    }

    @Override
    public void setBloom() {
        bloom = true;
    }

    @Inject(method = "<init>(Lnet/minecraft/network/FriendlyByteBuf;)V", at = @At("TAIL"))
    public void injectInit(FriendlyByteBuf pBuffer, CallbackInfo ci) {
       bloom = pBuffer.readBoolean();
    }

    @Inject(method = "write", at = @At("TAIL"))
    public void injectWrite(FriendlyByteBuf pBuffer, CallbackInfo ci) {
        pBuffer.writeBoolean(bloom);
    }

}
