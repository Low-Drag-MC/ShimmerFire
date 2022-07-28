package com.lowdragmc.shimmerfire.item;

import com.lowdragmc.shimmerfire.entity.FireSpiritEntity;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * @author KilaBash
 * @date 2022/7/18
 * @implNote FireSpiritEgg
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class FireSpiritItem extends Item {
    Supplier<? extends EntityType<?>> type;

    public FireSpiritItem(Supplier<? extends EntityType<?>> type, Properties props) {
        super(props);
        this.type = type;
    }

    public InteractionResult useOn(UseOnContext pContext) {
        Level level = pContext.getLevel();
        if (!(level instanceof ServerLevel)) {
            return InteractionResult.SUCCESS;
        } else {
            Player player = pContext.getPlayer();
            if (player != null ) {
                for (Entity entity : level.getEntities(player, new AABB(
                        player.getOnPos().offset(-4, -4, -4),
                        player.getOnPos().offset(4, 4, 4)
                ))) {
                    if (entity instanceof FireSpiritEntity fireSpiritEntity && fireSpiritEntity.getPlayer() == player) {
                        fireSpiritEntity.kill();
                        return InteractionResult.SUCCESS;
                    }
                }
            }
            ItemStack itemstack = pContext.getItemInHand();
            BlockPos blockpos = pContext.getClickedPos();
            Direction direction = pContext.getClickedFace();
            BlockState blockstate = level.getBlockState(blockpos);
            if (blockstate.is(Blocks.SPAWNER)) {
                BlockEntity blockentity = level.getBlockEntity(blockpos);
                if (blockentity instanceof SpawnerBlockEntity) {
                    BaseSpawner basespawner = ((SpawnerBlockEntity)blockentity).getSpawner();
                    EntityType<?> entitytype1 = type.get();
                    basespawner.setEntityId(entitytype1);
                    blockentity.setChanged();
                    level.sendBlockUpdated(blockpos, blockstate, blockstate, 3);
                    return InteractionResult.SUCCESS;
                }
            }

            BlockPos blockpos1;
            if (blockstate.getCollisionShape(level, blockpos).isEmpty()) {
                blockpos1 = blockpos;
            } else {
                blockpos1 = blockpos.relative(direction);
            }

            EntityType<?> entitytype = type.get();
            Entity entity = entitytype.spawn((ServerLevel)level, itemstack, pContext.getPlayer(), blockpos1, MobSpawnType.SPAWN_EGG, true, !Objects.equals(blockpos, blockpos1) && direction == Direction.UP);
            if (entity instanceof FireSpiritEntity fireSpiritEntity) {
                fireSpiritEntity.setMaster(pContext.getPlayer());
            }

            return InteractionResult.SUCCESS;
        }
    }

    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
        for (Entity entity : pLevel.getEntities(pPlayer, new AABB(
                pPlayer.getOnPos().offset(-4, -4, -4),
                pPlayer.getOnPos().offset(4, 4, 4)
        ))) {
            if (entity instanceof FireSpiritEntity fireSpiritEntity && fireSpiritEntity.getPlayer() == pPlayer) {
                fireSpiritEntity.kill();
            }
        }
        return super.use(pLevel, pPlayer, pHand);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        if (pIsAdvanced == TooltipFlag.Default.ADVANCED) {
            pTooltipComponents.add(new TranslatableComponent("item.shimmerfire.fire_spirit.shift"));
        }
    }

}
