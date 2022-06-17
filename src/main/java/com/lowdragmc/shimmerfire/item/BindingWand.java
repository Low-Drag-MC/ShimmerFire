package com.lowdragmc.shimmerfire.item;

import com.lowdragmc.shimmerfire.CommonProxy;
import com.lowdragmc.shimmerfire.block.FirePortBlock;
import com.lowdragmc.shimmerfire.blockentity.FirePortBlockEntity;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nullable;

/**
 * @author KilaBash
 * @date 2022/6/17
 * @implNote BindingWand
 */
public class BindingWand extends Item {

    public BindingWand() {
        super(new Item.Properties().tab(CommonProxy.TAB_ITEMS));
    }

    public static boolean isBindingWand(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof BindingWand;
    }

    public static void setLastPos(ItemStack stack, BlockPos pos) {
        if (isBindingWand(stack)) {
            if (pos == null) {
                stack.removeTagKey("last");
            } else {
                stack.getOrCreateTag().put("last", NbtUtils.writeBlockPos(pos));
            }
        }
    }

    @Nullable
    public static BlockPos getLastPos(ItemStack stack) {
        if (isBindingWand(stack)) {
            if (stack.hasTag() && stack.getTag().contains("last")) {
                return NbtUtils.readBlockPos(stack.getOrCreateTagElement("last"));
            }
        }
        return null;
    }


    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        Player player = context.getPlayer();
        if (player != null && !player.level.isClientSide) {
            if (isBindingWand(stack)) {
                BlockEntity selected = context.getLevel().getBlockEntity(context.getClickedPos());
                BlockPos pos = getLastPos(stack);
                if (selected instanceof FirePortBlockEntity firePortSelected) {
                    if (pos != null) {
                        BlockEntity last = context.getLevel().getBlockEntity(pos);
                        if (last instanceof FirePortBlockEntity firePortLast) {
                            if (firePortLast.isEmitter() != firePortSelected.isEmitter()) {
                                FirePortBlockEntity emitter = firePortLast.isEmitter() ? firePortLast : firePortSelected;
                                FirePortBlockEntity receiver = firePortLast.isReceiver() ? firePortLast : firePortSelected;
                                Boolean result = FirePortBlockEntity.bindConnect(emitter, receiver);
                                if (result != null) {
                                    setLastPos(stack, null);
                                    player.sendMessage(new TranslatableComponent(result ? "shimmerfire.binding.bind" : "shimmerfire.binding.unbind"), Util.NIL_UUID);
                                    return InteractionResult.SUCCESS;
                                }
                            }
                        }
                    }
                    setLastPos(stack, context.getClickedPos());
                    player.sendMessage(new TranslatableComponent("shimmerfire.binding.select"), Util.NIL_UUID);
                    return InteractionResult.SUCCESS;
                } else {
                    player.sendMessage(new TranslatableComponent("shimmerfire.binding.error"), Util.NIL_UUID);
                }
            }
        }
        return InteractionResult.PASS;
    }
}
