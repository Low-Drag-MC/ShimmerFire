package com.lowdragmc.shimmerfire;

import com.lowdragmc.shimmerfire.client.ClientProxy;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.slf4j.Logger;

@Mod(ShimmerFireMod.MODID)
public class ShimmerFireMod {
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final String MODID = "shimmerfire";

    public ShimmerFireMod() {
        DistExecutor.unsafeRunForDist(() -> ClientProxy::new, () -> CommonProxy::new);
    }

    public static boolean isClient() {
        return FMLEnvironment.dist == Dist.CLIENT;
    }

    public static boolean isRemote() {
        if (isClient()) {
            return Minecraft.getInstance().isSameThread();
        }
        return false;
    }

    public static boolean isModLoaded(String mod) {
        return ModList.get().isLoaded(mod);
    }
}
