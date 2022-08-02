package com.lowdragmc.shimmerfire.core.mixins;

import com.lowdragmc.shimmerfire.client.ClientProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.CubicSampler;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientLevel.class)
public class ClientLevelMixin {

    @Shadow @Final private Minecraft minecraft;

    @Shadow private int skyFlashTime;

    @Inject(method = "getSkyDarken", at = @At(value = "HEAD"), cancellable = true)
    private void injectGetSkyDarken(float pPartialTick, CallbackInfoReturnable<Float> cir){
        if (ClientProxy.isWearingGlasses()) {
            Level level = ((Level)(Object)this);
            pPartialTick = 0;
            float f1 = 1.0F - (Mth.cos(0.5f * ((float)Math.PI * 2F)) * 2.0F + 0.2F);
            f1 = Mth.clamp(f1, 0.0F, 1.0F);
            f1 = 1.0F - f1;
            f1 *= 1.0F - level.getRainLevel(pPartialTick) * 5.0F / 16.0F;
            f1 *= 1.0F - level.getThunderLevel(pPartialTick) * 5.0F / 16.0F;
            cir.setReturnValue(f1 * 0.8F + 0.2F);
        }
    }

    @Inject(method = "getSkyColor", at = @At(value = "HEAD"), cancellable = true)
    private void injectGetSkyColor(Vec3 pPos, float pPartialTick, CallbackInfoReturnable<Vec3> cir){
        if (ClientProxy.isWearingGlasses()) {
            Level level = ((Level)(Object)this);
            pPartialTick = 0;
            float f = 0.5f;
            Vec3 vec3 = pPos.subtract(2.0D, 2.0D, 2.0D).scale(0.25D);
            BiomeManager biomemanager = level.getBiomeManager();
            Vec3 vec31 = CubicSampler.gaussianSampleVec3(vec3, (p_194161_, p_194162_, p_194163_) -> Vec3.fromRGB24(biomemanager.getNoiseBiomeAtQuart(p_194161_, p_194162_, p_194163_).value().getSkyColor()));
            float f1 = Mth.cos(f * ((float)Math.PI * 2F)) * 2.0F + 0.5F;
            f1 = Mth.clamp(f1, 0.0F, 1.0F);
            float f2 = (float)vec31.x * f1;
            float f3 = (float)vec31.y * f1;
            float f4 = (float)vec31.z * f1;
            float f5 = level.getRainLevel(pPartialTick);
            if (f5 > 0.0F) {
                float f6 = (f2 * 0.3F + f3 * 0.59F + f4 * 0.11F) * 0.6F;
                float f7 = 1.0F - f5 * 0.75F;
                f2 = f2 * f7 + f6 * (1.0F - f7);
                f3 = f3 * f7 + f6 * (1.0F - f7);
                f4 = f4 * f7 + f6 * (1.0F - f7);
            }

            float f9 = level.getThunderLevel(pPartialTick);
            if (f9 > 0.0F) {
                float f10 = (f2 * 0.3F + f3 * 0.59F + f4 * 0.11F) * 0.2F;
                float f8 = 1.0F - f9 * 0.75F;
                f2 = f2 * f8 + f10 * (1.0F - f8);
                f3 = f3 * f8 + f10 * (1.0F - f8);
                f4 = f4 * f8 + f10 * (1.0F - f8);
            }

            if (!this.minecraft.options.hideLightningFlashes && this.skyFlashTime > 0) {
                float f11 = (float)this.skyFlashTime - pPartialTick;
                if (f11 > 1.0F) {
                    f11 = 1.0F;
                }

                f11 *= 0.45F;
                f2 = f2 * (1.0F - f11) + 0.8F * f11;
                f3 = f3 * (1.0F - f11) + 0.8F * f11;
                f4 = f4 * (1.0F - f11) + 1.0F * f11;
            }

            cir.setReturnValue(new Vec3(f2, f3, f4));
        }
    }


    @Inject(method = "getCloudColor", at = @At(value = "HEAD"), cancellable = true)
    private void injectGetCloudColor(float pPartialTick, CallbackInfoReturnable<Vec3> cir){
        if (ClientProxy.isWearingGlasses()) {
            Level level = ((Level)(Object)this);
            pPartialTick = 0;
            float f = 0.5f;
            float f1 = Mth.cos(f * ((float)Math.PI * 2F)) * 2.0F + 0.5F;
            f1 = Mth.clamp(f1, 0.0F, 1.0F);
            float f2 = 1.0F;
            float f3 = 1.0F;
            float f4 = 1.0F;
            float f5 = level.getRainLevel(pPartialTick);
            if (f5 > 0.0F) {
                float f6 = (f2 * 0.3F + f3 * 0.59F + f4 * 0.11F) * 0.6F;
                float f7 = 1.0F - f5 * 0.95F;
                f2 = f2 * f7 + f6 * (1.0F - f7);
                f3 = f3 * f7 + f6 * (1.0F - f7);
                f4 = f4 * f7 + f6 * (1.0F - f7);
            }

            f2 *= f1 * 0.9F + 0.1F;
            f3 *= f1 * 0.9F + 0.1F;
            f4 *= f1 * 0.85F + 0.15F;
            float f9 = level.getThunderLevel(pPartialTick);
            if (f9 > 0.0F) {
                float f10 = (f2 * 0.3F + f3 * 0.59F + f4 * 0.11F) * 0.2F;
                float f8 = 1.0F - f9 * 0.95F;
                f2 = f2 * f8 + f10 * (1.0F - f8);
                f3 = f3 * f8 + f10 * (1.0F - f8);
                f4 = f4 * f8 + f10 * (1.0F - f8);
            }

            cir.setReturnValue(new Vec3(f2, f3, f4));
        }
    }

    @Inject(method = "getStarBrightness", at = @At(value = "HEAD"), cancellable = true)
    private void injectGetStarBrightness(float pPartialTick, CallbackInfoReturnable<Float> cir){
        if (ClientProxy.isWearingGlasses()) {
            float f = 0.5f;
            float f1 = 1.0F - (Mth.cos(f * ((float)Math.PI * 2F)) * 2.0F + 0.25F);
            f1 = Mth.clamp(f1, 0.0F, 1.0F);
            cir.setReturnValue(f1 * f1 * 0.5F);
        }
    }
}
