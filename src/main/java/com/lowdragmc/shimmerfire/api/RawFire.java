package com.lowdragmc.shimmerfire.api;

import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.StringRepresentable;

import javax.annotation.Nonnull;

/**
 * @author KilaBash
 * @date 2022/6/20
 * @implNote RawFire
 */
public enum RawFire implements StringRepresentable {
    DESTROY("destroy", 0xFFFFA500, 9),
    ARCANE("arcane", 0xff00FFFF, 9),
    LIFE("life", 0xff4cff4c, 9),
    ROT("rot", 0xffc328b5, 9);

    public final String fireName;
    public final int colorVale;
    public final float radius;

    RawFire(String fireName, int colorVale, float radius) {
        this.fireName = fireName;
        this.colorVale = colorVale;
        this.radius = radius;
    }

    public TranslatableComponent getTranslatableComponent() {
        return new TranslatableComponent("shimmerfire.raw_fire." + fireName);
    }

    @Override
    @Nonnull
    public String getSerializedName() {
        return fireName;
    }
}
