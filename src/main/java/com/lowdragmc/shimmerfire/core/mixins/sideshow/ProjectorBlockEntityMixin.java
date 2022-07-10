package com.lowdragmc.shimmerfire.core.mixins.sideshow;

import com.lowdragmc.shimmerfire.core.IShimmerEffectProjector;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.teacon.slides.projector.ProjectorBlockEntity;

/**
 * @author KilaBash
 * @date 2022/6/22
 * @implNote ProjectorScreenMixin
 */
@Mixin(ProjectorBlockEntity.class)
public abstract class ProjectorBlockEntityMixin implements IShimmerEffectProjector {

    String effect;

    @Override
    public String getEffect() {
        return effect;
    }

    @Override
    public void setEffect(String effect) {
        this.effect = effect;
    }

    @Inject(
            method = {"writeCustomTag"},
            at = @At("RETURN"),
            remap = false
    )
    private void injectWriteCustomTag(CompoundTag tag, CallbackInfo ci) {
        if (effect != null) {
            tag.putString("effect", effect);
        }
    }

    @Inject(
            method = {"readCustomTag"},
            at = @At("RETURN"),
            remap = false
    )
    private void injectReadCustomTag(CompoundTag tag, CallbackInfo ci) {
        effect = null;
        if (tag.contains("effect")) {
            effect = tag.getString("effect");
        } else if (tag.contains("bloom") && tag.getBoolean("bloom")){
            effect = "bloom_unreal";
        }
    }

}
