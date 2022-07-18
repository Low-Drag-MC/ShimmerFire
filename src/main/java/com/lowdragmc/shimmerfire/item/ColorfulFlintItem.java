package com.lowdragmc.shimmerfire.item;

import com.lowdragmc.shimmerfire.CommonProxy;
import com.lowdragmc.shimmerfire.ShimmerFireMod;
import com.lowdragmc.shimmerfire.block.ColorfulCampfireBlock;
import com.lowdragmc.shimmerfire.block.ColorfulFireBlock;
import com.lowdragmc.shimmerfire.blockentity.ColoredCampfireBlockEntity;
import com.lowdragmc.shimmerfire.blockentity.ColorfulCampfireBlockEntity;
import com.lowdragmc.shimmerfire.blockentity.ColorfulFireBlockEntity;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FlintAndSteelItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.CandleCakeBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.GameEvent;

import javax.annotation.Nonnull;

import java.util.Random;

import static net.minecraft.world.level.block.CampfireBlock.LIT;
import static net.minecraft.world.level.block.CampfireBlock.WATERLOGGED;

/**
 * @author KilaBash
 * @date 2022/5/5
 * @implNote ColorfulFlintItem
 */
public class ColorfulFlintItem extends FlintAndSteelItem {

    public static void setColor(ItemStack itemStack, int color) {
        itemStack.getTag().putInt("color", color);
    }

    public static int getColor(ItemStack itemStack) {
        int color = itemStack.getTag().getInt("color");
        if (color==0){
            return new Random().nextInt(0xffffff) + (0xff << 24);
        }else {
            return color;
        }
    }

    public static void setRadius(ItemStack itemStack, int radius) {
        itemStack.getTag().putInt("radius", radius);
    }

    public static int getRadius(ItemStack itemStack) {
        int radius= itemStack.getTag().getInt("radius");
        if (radius==0){
            return 8;
        }else {
            return radius;
        }
    }


    public ColorfulFlintItem() {
        super(new Item.Properties().durability(64).tab(CommonProxy.TAB_ITEMS));
        setRegistryName(new ResourceLocation(ShimmerFireMod.MODID, "colorful_flint_fire"));
    }

    public static boolean canLight(BlockState pState) {
        return pState.is(BlockTags.CAMPFIRES, (stateBase) -> stateBase.hasProperty(WATERLOGGED)
                && stateBase.hasProperty(LIT))
                && !pState.getValue(WATERLOGGED);
    }

    @Override
    @Nonnull
    public InteractionResult useOn(@Nonnull UseOnContext context) {
        Player player = context.getPlayer();
        Level level = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        BlockState blockstate = level.getBlockState(blockpos);
        ItemStack itemstack = context.getItemInHand();
        int color = getColor(itemstack);
        int radius = getRadius(itemstack);
        if (!canLight(blockstate) && !CandleBlock.canLight(blockstate) && !CandleCakeBlock.canLight(blockstate)) {
            BlockPos blockpos1 = blockpos.relative(context.getClickedFace());
            if (BaseFireBlock.canBePlacedAt(level, blockpos1, context.getHorizontalDirection())) {
                level.playSound(player, blockpos1, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.4F + 0.8F);
                level.setBlock(blockpos1, CommonProxy.COLORFUL_FIRE_BLOCK.get().defaultBlockState(), Block.UPDATE_ALL_IMMEDIATE);

                ColorfulFireBlockEntity blockEntity = (ColorfulFireBlockEntity)level.getBlockEntity(blockpos1);
                if (!level.isClientSide){
                    blockEntity.setColor(color);
                    blockEntity.setRadius(radius);
                    blockEntity.notifyUpdate();
                }

                level.getChunkSource().getLightEngine().checkBlock(blockpos1);

                level.gameEvent(player, GameEvent.BLOCK_PLACE, blockpos);
                if (player instanceof ServerPlayer) {
                    CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer) player, blockpos1, itemstack);
                    itemstack.hurtAndBreak(1, player, (p_41300_) -> {
                        p_41300_.broadcastBreakEvent(context.getHand());
                    });
                }

                return InteractionResult.sidedSuccess(level.isClientSide());
            } else {
                return InteractionResult.FAIL;
            }
        } else {
            blockstate = blockstate.setValue(BlockStateProperties.LIT, Boolean.TRUE);
            level.playSound(player, blockpos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.4F + 0.8F);
            level.setBlock(blockpos, blockstate, Block.UPDATE_ALL_IMMEDIATE);
            if (blockstate.getBlock() == CommonProxy.COLORFUL_CAMPFIRE_BLOCK.get()){
                ColorfulCampfireBlockEntity blockEntity = (ColorfulCampfireBlockEntity)level.getBlockEntity(blockpos);
                if (!level.isClientSide){
                    blockEntity.setRadius(radius);
                    blockEntity.setColor(color);
                    blockEntity.notifyUpdate();
                }
            }
            level.gameEvent(player, GameEvent.BLOCK_PLACE, blockpos);
            if (player != null) {
                context.getItemInHand().hurtAndBreak(1, player, p -> p.broadcastBreakEvent(context.getHand()));
            }

            return InteractionResult.sidedSuccess(level.isClientSide());
        }
    }
}
