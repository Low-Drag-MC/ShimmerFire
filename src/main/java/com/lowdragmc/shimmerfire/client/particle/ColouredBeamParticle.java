package com.lowdragmc.shimmerfire.client.particle;

import com.lowdragmc.lowdraglib.client.particle.impl.TextureBeamParticle;
import com.lowdragmc.lowdraglib.utils.Vector3;
import com.lowdragmc.shimmerfire.ShimmerFireMod;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;


/**
 * @author KilaBash
 * @date 2022/6/25
 * @implNote BeamParticle
 */
public class ColouredBeamParticle extends TextureBeamParticle {

    public ColouredBeamParticle(ClientLevel level, Vector3 from, Vector3 end) {
        super(level, from, end);
        setTexture(new ResourceLocation(ShimmerFireMod.MODID, "textures/blocks/white.png"));
        setLifetime(50);
        setLight(0xf000f0);
    }
}
