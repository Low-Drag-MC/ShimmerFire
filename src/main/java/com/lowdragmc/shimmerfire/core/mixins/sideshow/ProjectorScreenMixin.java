package com.lowdragmc.shimmerfire.core.mixins.sideshow;

import com.lowdragmc.shimmerfire.core.IShimmerEffectProjector;
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

import javax.annotation.Nonnull;

/**
 * @author KilaBash
 * @date 2022/6/22
 * @implNote ProjectorScreenMixin
 */
@Mixin(ProjectorScreen.class)
public abstract class ProjectorScreenMixin extends AbstractContainerScreen {

    String effect;

    @Shadow(remap = false) @Final private ProjectorBlockEntity mEntity;

    public ProjectorScreenMixin(AbstractContainerMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }


    @Inject(
            method = {"init"},
            at = @At("RETURN")
    )
    private void injectInit(CallbackInfo ci) {
        if (this.mEntity != null) {
            effect = ((IShimmerEffectProjector)(Object)this.mEntity).getEffect();
            String[] effects = {
                    "bloom_unreal",
                    "warp",
                    "vhs",
                    "flicker",
                    "halftone",
                    "dot_screen"
            };
            for (int i = 0; i < effects.length; i++) {
                final String e = effects[i];
                this.addRenderableWidget(new AbstractButton(this.leftPos + 28 + i * 20, this.topPos + 212, 18, 19, new TranslatableComponent("gui.slide_show.rotate")) {
                    @Override
                    public void updateNarration(NarrationElementOutput pNarrationElementOutput) {

                    }

                    @Override
                    public void onPress() {
                        if (e.equals(effect)) {
                            effect = null;
                        } else {
                            effect = e;
                        }
                    }

                    @Override
                    public void renderButton(@Nonnull PoseStack stack, int pMouseX, int pMouseY, float pPartialTick) {
                        RenderSystem.enableBlend();
                        RenderSystem.defaultBlendFunc();
                        RenderSystem.setShaderTexture(0, new ResourceLocation("shimmerfire", "textures/gui/" + e + "_button.png"));
                        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
                        if (e.equals(effect)) {
                            blit(stack, this.x, this.y, 0, 19, this.width, this.height, 18, 38);
                        } else {
                            blit(stack, this.x, this.y, 0, 0, this.width, this.height, 18, 38);
                        }
                    }
                });
            }

        }
    }

    @Inject(
            method = {"removed"},
            at = @At("HEAD")
    )
    private void injectRemove(CallbackInfo ci) {
        if (this.mEntity != null) {
            ((IShimmerEffectProjector)(Object)this.mEntity).setEffect(effect);
        }
    }

    @Inject(
            method = {"renderLabels"},
            at = @At("RETURN"),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void injectRenderLabels(PoseStack stack, int mouseX, int mouseY, CallbackInfo ci, int alpha, int offsetX, int offsetY) {
        String[] effects = {
                "bloom_unreal",
                "warp",
                "vhs",
                "flicker",
                "halftone",
                "dot_screen"
        };
        for (int i = 0; i < effects.length; i++) {
            final String e = effects[i];
            if (offsetX >= 28 + i * 20 && offsetY >= 212 && offsetX < 28 + 18 + i * 20 && offsetY < 212 + 19) {
                this.renderTooltip(stack, new TranslatableComponent("gui.slide_show." + e), offsetX, offsetY);
            }
        }
    }

}
