package com.lowdragmc.shimmerfire.block.decorated;


import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author KilaBash
 * @date 2022/6/22
 * @implNote DecorationBlock
 */
public class ColoredDecorationBlock extends DecorationBlock {

    public static final EnumProperty<Color> COLOR = EnumProperty.create("color", Color.class);

    public ColoredDecorationBlock() {
        this.registerDefaultState(this.stateDefinition.any().setValue(COLOR, Color.WHITE));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(COLOR);
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
        ItemStack itemStack = new ItemStack(this);
        itemStack.setDamageValue(state.getValue(COLOR).ordinal());
        return itemStack;
    }

    @Override
    public void fillItemCategory(CreativeModeTab pTab, NonNullList<ItemStack> pItems) {
        for (Color color : Color.values()) {
            ItemStack itemStack = new ItemStack(this);
            itemStack.setDamageValue(color.ordinal());
            pItems.add(itemStack);
        }
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable BlockGetter pLevel, List<Component> pTooltip, TooltipFlag pFlag) {
        super.appendHoverText(pStack, pLevel, pTooltip, pFlag);
        pTooltip.add(new TranslatableComponent("shimmerfire.color").append(" %s".formatted(Color.values()[pStack.getDamageValue()].name)));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return defaultBlockState().setValue(COLOR, Color.values()[pContext.getItemInHand().getDamageValue()]);
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
