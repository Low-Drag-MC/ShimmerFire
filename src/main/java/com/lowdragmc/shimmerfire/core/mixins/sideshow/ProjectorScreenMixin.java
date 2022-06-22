package com.lowdragmc.shimmerfire.core.mixins.sideshow;

import com.lowdragmc.shimmerfire.core.IBloomProjector;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
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
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.teacon.slides.projector.ProjectorBlockEntity;
import org.teacon.slides.projector.ProjectorScreen;

/**
 * @author KilaBash
 * @date 2022/6/22
 * @implNote ProjectorScreenMixin
 */
@Mixin(ProjectorScreen.class)
public abstract class ProjectorScreenMixin extends AbstractContainerScreen {

    boolean bloom;

    @Shadow @Final private ProjectorBlockEntity mEntity;

    public ProjectorScreenMixin(AbstractContainerMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }


    @Inject(
            method = {"init"},
            at = @At("RETURN")
    )
    private void injectInit(CallbackInfo ci) {
        if (this.mEntity != null) {
            bloom = ((IBloomProjector)(Object)this.mEntity).isBloom();
            this.addRenderableWidget(new AbstractButton(this.leftPos + 142, this.topPos + 183, 18, 19, new TranslatableComponent("gui.slide_show.rotate")) {
                @Override
                public void updateNarration(NarrationElementOutput pNarrationElementOutput) {

                }

                @Override
                public void onPress() {
                    bloom = !bloom;
                }

                @Override
                public void renderButton(PoseStack stack, int pMouseX, int pMouseY, float pPartialTick) {
                    RenderSystem.enableBlend();
                    RenderSystem.defaultBlendFunc();
                    RenderSystem.setShaderTexture(0, new ResourceLocation("shimmerfire", "textures/gui/bloom_button.png"));
                    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
                    if (bloom) {
                        blit(stack, this.x, this.y, 0, 19, this.width, this.height, 18, 38);
                    } else {
                        blit(stack, this.x, this.y, 0, 0, this.width, this.height, 18, 38);
                    }
                }
            });
        }
    }

    @Inject(
            method = {"removed"},
            at = @At("HEAD")
    )
    private void injectRemove(CallbackInfo ci) {
        if (this.mEntity != null) {
            ((IBloomProjector)(Object)this.mEntity).setBloom(bloom);
        }
    }

    @Inject(
            method = {"renderLabels"},
            at = @At("RETURN"),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void injectRenderLabels(PoseStack stack, int mouseX, int mouseY, CallbackInfo ci, int alpha, int offsetX, int offsetY) {
        if (offsetX >= 142 && offsetY >= 183 && offsetX < 160 && offsetY < 202) {
            this.renderTooltip(stack, new TranslatableComponent("gui.slide_show.bloom"), offsetX, offsetY);
        }
    }

}
