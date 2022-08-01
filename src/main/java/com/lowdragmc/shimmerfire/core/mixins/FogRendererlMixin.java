package com.lowdragmc.shimmerfire.core.mixins;

import com.lowdragmc.shimmerfire.client.ClientProxy;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FogRenderer.class)
public class FogRendererlMixin {


    @Redirect(method = "setupColor", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;getTimeOfDay(F)F"))
    private static float getTimeOfDay(ClientLevel level, float partialTicks){
        if (ClientProxy.isWearingGlasses()) {
            return 0.5f;
        }
        return level.getTimeOfDay(partialTicks);
    }

}
