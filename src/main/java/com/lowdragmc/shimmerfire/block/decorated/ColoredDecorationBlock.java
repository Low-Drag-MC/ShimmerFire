package com.lowdragmc.shimmerfire.block.decorated;


import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

/**
 * @author KilaBash
 * @date 2022/6/22
 * @implNote DecorationBlock
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ColoredDecorationBlock extends DecorationBlock {

    public final Color color;

    // I want to use the blockstate, but the framedblock only support the defaultstate.
//    public static final EnumProperty<Color> COLOR = EnumProperty.create("color", Color.class);

    public ColoredDecorationBlock(Color color) {
        this.color = color;
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable BlockGetter pLevel, List<Component> pTooltip, TooltipFlag pFlag) {
        super.appendHoverText(pStack, pLevel, pTooltip, pFlag);
        pTooltip.add(new TranslatableComponent("shimmerfire.color").append(" %s".formatted(color.name)));
    }

    public enum Color implements StringRepresentable {
        WHITE("white", 16383998),
        ORANGE("orange", 16351261),
        MAGENTA("magenta", 13061821),
        LIGHT_BLUE("light_blue", 3847130),
        YELLOW("yellow", 16701501),
        LIME("lime", 8439583),
        PINK("pink", 0xFF5E9E),
        GRAY("gray", 4673362),
        LIGHT_GRAY("light_gray", 10329495),
        CYAN("cyan", 1481884),
        PURPLE("purple", 8991416),
        BLUE("blue", 3949738),
        BROWN("brown", 8606770),
        GREEN("green", 6192150),
        RED("red", 11546150);

        public final String name;
        public final int color;

        Color(String name, int color) {
            this.name = name;
            this.color = color;
        }

        @Override
        public @Nonnull String getSerializedName() {
            return name;
        }
    }
}
