package com.lowdragmc.shimmerfire.core.mixins;

import com.mojang.blaze3d.vertex.VertexFormatElement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.Coerce;

@Mixin(VertexFormatElement.Usage.class)
public interface VertexFormatElementUsageAccessor{
        @Invoker("<init>")
        static VertexFormatElement.Usage constructor(String enumName, int enumIndex, String name, VertexFormatElement.Usage.SetupState steup, @Coerce VertexFormatElement.Usage.ClearState clear){
            return null;
        }
}
