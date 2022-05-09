package com.lowdragmc.shimmerfire.item;

import com.lowdragmc.shimmerfire.CommonProxy;
import com.lowdragmc.shimmerfire.ShimmerFireMod;
import com.lowdragmc.shimmerfire.block.ColoredFireBlock;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.FlintAndSteelItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.CandleCakeBlock;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.GameEvent;

import javax.annotation.Nonnull;

import static com.lowdragmc.shimmerfire.block.ColoredFireBlock.FIRE_COLOR;
import static net.minecraft.world.level.block.CampfireBlock.LIT;
import static net.minecraft.world.level.block.CampfireBlock.WATERLOGGED;

/**
 * @author KilaBash
 * @date 2022/5/5
 * @implNote ColoredFlintItem
 */
public class ColoredFlintItem extends FlintAndSteelItem {
    public final ColoredFireBlock.FireColor color;
    public ColoredFlintItem(ColoredFireBlock.FireColor color) {
        super(new Item.Properties().durability(64).tab(CreativeModeTab.TAB_TOOLS));
        setRegistryName(new ResourceLocation(ShimmerFireMod.MODID, "flint_fire_" + color.colorName));
        this.color = color;
    }

    public BlockState getFireState() {
        return CommonProxy.FIRE_BLOCK.get().defaultBlockState().setValue(FIRE_COLOR, color);
    }

    public static boolean canLight(BlockState pState) {
        return pState.is(BlockTags.CAMPFIRES, (stateBase) -> stateBase.hasProperty(WATERLOGGED)
                && stateBase.hasProperty(LIT))
                && !pState.getValue(WATERLOGGED)
                && (!pState.getValue(LIT) || pState.hasProperty(FIRE_COLOR));
    }

    @Override
    @Nonnull
    public InteractionResult useOn(@Nonnull UseOnContext context) {
        Player player = context.getPlayer();
        Level level = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        BlockState blockstate = level.getBlockState(blockpos);
        if (!canLight(blockstate) && !CandleBlock.canLight(blockstate) && !CandleCakeBlock.canLight(blockstate)) {
            BlockPos blockpos1 = blockpos.relative(context.getClickedFace());
            if (BaseFireBlock.canBePlacedAt(level, blockpos1, context.getHorizontalDirection())) {
                level.playSound(player, blockpos1, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.4F + 0.8F);
                BlockState blockstate1 = getFireState();
                level.setBlock(blockpos1, blockstate1, 11);
                level.gameEvent(player, GameEvent.BLOCK_PLACE, blockpos);
                ItemStack itemstack = context.getItemInHand();
                if (player instanceof ServerPlayer) {
                    CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer)player, blockpos1, itemstack);
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
            if (blockstate.hasProperty(FIRE_COLOR)) {
                blockstate = blockstate.setValue(FIRE_COLOR, color);
            }
            level.playSound(player, blockpos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.4F + 0.8F);
            level.setBlock(blockpos, blockstate, 11);
            level.gameEvent(player, GameEvent.BLOCK_PLACE, blockpos);
            if (player != null) {
                context.getItemInHand().hurtAndBreak(1, player, p -> p.broadcastBreakEvent(context.getHand()));
            }

            return InteractionResult.sidedSuccess(level.isClientSide());
        }
    }
}
