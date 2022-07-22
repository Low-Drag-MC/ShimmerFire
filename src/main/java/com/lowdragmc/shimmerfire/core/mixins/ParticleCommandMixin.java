package com.lowdragmc.shimmerfire.core.mixins;

import com.lowdragmc.shimmerfire.core.IBloomParticle;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ParticleArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.server.commands.ParticleCommand;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;

/**
 * @author KilaBash
 * @date 2022/7/22
 * @implNote ParticleCommandMixin
 */
@Mixin(ParticleCommand.class)
public abstract class ParticleCommandMixin {

    @Shadow @Final private static SimpleCommandExceptionType ERROR_FAILED;

    @Inject(method = "register", at = @At(value = "HEAD"))
    private static void injectTryCatchFire(CommandDispatcher<CommandSourceStack> pDispatcher, CallbackInfo ci) {
        pDispatcher.register(Commands.literal("bloom_particle").requires((p_138127_) -> p_138127_.hasPermission(2)).then(Commands.argument("name", ParticleArgument.particle()).executes((p_138148_) -> {
            return sendBloomParticles(p_138148_.getSource(), ParticleArgument.getParticle(p_138148_, "name"), p_138148_.getSource().getPosition(), Vec3.ZERO, 0.0F, 0, false, p_138148_.getSource().getServer().getPlayerList().getPlayers());
        }).then(Commands.argument("pos", Vec3Argument.vec3()).executes((p_138146_) -> {
            return sendBloomParticles(p_138146_.getSource(), ParticleArgument.getParticle(p_138146_, "name"), Vec3Argument.getVec3(p_138146_, "pos"), Vec3.ZERO, 0.0F, 0, false, p_138146_.getSource().getServer().getPlayerList().getPlayers());
        }).then(Commands.argument("delta", Vec3Argument.vec3(false)).then(Commands.argument("speed", FloatArgumentType.floatArg(0.0F)).then(Commands.argument("count", IntegerArgumentType.integer(0)).executes((p_138144_) -> {
            return sendBloomParticles(p_138144_.getSource(), ParticleArgument.getParticle(p_138144_, "name"), Vec3Argument.getVec3(p_138144_, "pos"), Vec3Argument.getVec3(p_138144_, "delta"), FloatArgumentType.getFloat(p_138144_, "speed"), IntegerArgumentType.getInteger(p_138144_, "count"), false, p_138144_.getSource().getServer().getPlayerList().getPlayers());
        }).then(Commands.literal("force").executes((p_138142_) -> {
            return sendBloomParticles(p_138142_.getSource(), ParticleArgument.getParticle(p_138142_, "name"), Vec3Argument.getVec3(p_138142_, "pos"), Vec3Argument.getVec3(p_138142_, "delta"), FloatArgumentType.getFloat(p_138142_, "speed"), IntegerArgumentType.getInteger(p_138142_, "count"), true, p_138142_.getSource().getServer().getPlayerList().getPlayers());
        }).then(Commands.argument("viewers", EntityArgument.players()).executes((p_138140_) -> {
            return sendBloomParticles(p_138140_.getSource(), ParticleArgument.getParticle(p_138140_, "name"), Vec3Argument.getVec3(p_138140_, "pos"), Vec3Argument.getVec3(p_138140_, "delta"), FloatArgumentType.getFloat(p_138140_, "speed"), IntegerArgumentType.getInteger(p_138140_, "count"), true, EntityArgument.getPlayers(p_138140_, "viewers"));
        }))).then(Commands.literal("normal").executes((p_138138_) -> {
            return sendBloomParticles(p_138138_.getSource(), ParticleArgument.getParticle(p_138138_, "name"), Vec3Argument.getVec3(p_138138_, "pos"), Vec3Argument.getVec3(p_138138_, "delta"), FloatArgumentType.getFloat(p_138138_, "speed"), IntegerArgumentType.getInteger(p_138138_, "count"), false, p_138138_.getSource().getServer().getPlayerList().getPlayers());
        }).then(Commands.argument("viewers", EntityArgument.players()).executes((p_138125_) -> {
            return sendBloomParticles(p_138125_.getSource(), ParticleArgument.getParticle(p_138125_, "name"), Vec3Argument.getVec3(p_138125_, "pos"), Vec3Argument.getVec3(p_138125_, "delta"), FloatArgumentType.getFloat(p_138125_, "speed"), IntegerArgumentType.getInteger(p_138125_, "count"), false, EntityArgument.getPlayers(p_138125_, "viewers"));
        })))))))));
    }

    private static int sendBloomParticles(CommandSourceStack pSource, ParticleOptions pParticleData, Vec3 pPos, Vec3 pDelta, float pSpeed, int pCount, boolean pForce, Collection<ServerPlayer> pViewers) throws CommandSyntaxException {
        int i = 0;

        for(ServerPlayer serverplayer : pViewers) {
            if (sendBloomParticles(pSource.getLevel(), serverplayer, pParticleData, pForce, pPos.x, pPos.y, pPos.z, pCount, pDelta.x, pDelta.y, pDelta.z, (double)pSpeed)) {
                ++i;
            }
        }

        if (i == 0) {
            throw ERROR_FAILED.create();
        } else {
            pSource.sendSuccess(new TranslatableComponent("commands.particle.success", Registry.PARTICLE_TYPE.getKey(pParticleData.getType()).toString()), true);
            return i;
        }
    }

    private static <T extends ParticleOptions> boolean sendBloomParticles(Level level, ServerPlayer pPlayer, T pType, boolean pLongDistance, double pPosX, double pPosY, double pPosZ, int pParticleCount, double pXOffset, double pYOffset, double pZOffset, double pSpeed) {
        Packet<?> packet = new ClientboundLevelParticlesPacket(pType, pLongDistance, pPosX, pPosY, pPosZ, (float)pXOffset, (float)pYOffset, (float)pZOffset, (float)pSpeed, pParticleCount);
        if (packet instanceof IBloomParticle bloomParticle) {
            bloomParticle.setBloom();
        }
        return sendBloomParticles(level, pPlayer, pLongDistance, pPosX, pPosY, pPosZ, packet);
    }

    private static boolean sendBloomParticles(Level level, ServerPlayer pPlayer, boolean pLongDistance, double pPosX, double pPosY, double pPosZ, Packet<?> pPacket) {
        if (pPlayer.getLevel() != level) {
            return false;
        } else {
            BlockPos blockpos = pPlayer.blockPosition();
            if (blockpos.closerToCenterThan(new Vec3(pPosX, pPosY, pPosZ), pLongDistance ? 512.0D : 32.0D)) {
                pPlayer.connection.send(pPacket);
                return true;
            } else {
                return false;
            }
        }
    }
}
