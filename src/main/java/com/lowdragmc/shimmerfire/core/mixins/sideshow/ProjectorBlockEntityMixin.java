package com.lowdragmc.shimmerfire.core.mixins.sideshow;

import com.lowdragmc.shimmerfire.core.IBloomProjector;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.teacon.slides.projector.ProjectorBlockEntity;
import org.teacon.slides.projector.ProjectorScreen;

/**
 * @author KilaBash
 * @date 2022/6/22
 * @implNote ProjectorScreenMixin
 */
@Mixin(ProjectorBlockEntity.class)
public abstract class ProjectorBlockEntityMixin implements IBloomProjector {

    boolean bloom;

    @Override
    public boolean isBloom() {
        return bloom;
    }

    @Override
    public void setBloom(boolean bloom) {
        this.bloom = bloom;
    }

    @Inject(
            method = {"writeCustomTag"},
            at = @At("RETURN"),
            remap = false
    )
    private void injectWriteCustomTag(CompoundTag tag, CallbackInfo ci) {
        tag.putBoolean("bloom", bloom);
    }

    @Inject(
            method = {"readCustomTag"},
            at = @At("RETURN"),
            remap = false
    )
    private void injectReadCustomTag(CompoundTag tag, CallbackInfo ci) {
        bloom = tag.getBoolean("bloom");
    }

}
