package com.lowdragmc.shimmerfire.api;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;

/**
 * @author KilaBash
 * @date 2022/6/22
 * @implNote Capabilities
 */
public class Capabilities {

    public static final Capability<IFireContainer> FIRE_CONTAINER_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

    public static void register(RegisterCapabilitiesEvent event) {
        event.register(IFireContainer.class);
    }
}
