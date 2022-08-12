package com.lowdragmc.shimmerfire.core.mixins;

import com.lowdragmc.shimmerfire.CommonProxy;
import com.lowdragmc.shimmerfire.ForgeCommonEventListener;
import com.lowdragmc.shimmerfire.core.IBloomParticle;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Player.class)
public class PlayerMixin {
    @Redirect(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;sweepAttack()V"))
    private void redirectSweepAttack(Player player) {
        if (!player.getItemInHand(InteractionHand.MAIN_HAND).is(CommonProxy.LIGHTER_SWORD.get())) {
            player.sweepAttack();
        } else {
            double d0 = (-Mth.sin(player.getYRot() * ((float) Math.PI / 180F)));
            double d1 = Mth.cos(player.getYRot() * ((float) Math.PI / 180F));
            if (player.level instanceof ServerLevel severLevel) {
                var pPosX = player.getX() + d0;
                var pPosY = player.getY(0.5D);
                var pPosZ = player.getZ() + d1;
                var packet = new ClientboundLevelParticlesPacket(ParticleTypes.SWEEP_ATTACK, false,
                        pPosX, pPosY, pPosZ, (float) d0, (float) 0.0D, (float) d1, (float) 0.0, 0);
                if (packet instanceof IBloomParticle bloomParticle) {
                    bloomParticle.setBloom();
                }
                var players = severLevel.players();
                for (var receiver : players) {
                    ForgeCommonEventListener.sendBloomParticles(severLevel,receiver,false,pPosX,pPosY,pPosZ,packet);
                }
            }
        }
    }
}
