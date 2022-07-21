package com.lowdragmc.shimmerfire.blockentity.multiblocked;

import com.lowdragmc.lowdraglib.client.particle.impl.TextureBeamParticle;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.utils.Vector3;
import com.lowdragmc.multiblocked.api.definition.ControllerDefinition;
import com.lowdragmc.multiblocked.api.recipe.RecipeLogic;
import com.lowdragmc.multiblocked.api.tile.ControllerTileEntity;
import com.lowdragmc.shimmerfire.ShimmerFireMod;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author Baka943, KilaBash
 * @date 2022/7/16
 * @implNote FuWenJiTanBlockEntity, implementation from bak943's script
 */
public class FuWenJiTanBlockEntity extends ControllerTileEntity {

    public FuWenJiTanBlockEntity(ControllerDefinition definition, BlockPos pos, BlockState state) {
        super(definition, pos, state);
    }

    @Override
    public ModularUI createUI(Player entityPlayer) {
        return null;
    }

    @Override
    public void updateFormed() {
        super.updateFormed();
        if (recipeLogic == null) return;
        if (recipeLogic.isWorking() && getTimer() % 15 == 0) {
            writeCustomData(14, buf -> {});
        } else if (recipeLogic.isIdle() && level != null) {
            var entities = level.getEntities(null, new AABB(getBlockPos(), getBlockPos().offset(1, 2, 1)));
            for (Entity entity : entities) {
                if (entity.isAlive() && entity instanceof ItemEntity itemEntity) {
                    if (itemEntity.getItem().getItem() == Items.GLASS_PANE) {
                        itemEntity.getItem().shrink(1);
                        recipeLogic.lastRecipe = definition.recipeMap.allRecipes().get(0);
                        recipeLogic.setStatus(RecipeLogic.Status.WORKING);
                        recipeLogic.progress = 0;
                        recipeLogic.duration = recipeLogic.lastRecipe.duration;
                        recipeLogic.markDirty();
                        if (itemEntity.getItem().isEmpty()) {
                            entity.kill();
                        }
                    }
                }
            }
        }
    }

    @Override
    public void receiveCustomData(int dataId, FriendlyByteBuf buf) {
        if (dataId == 14) {

        } else {
            super.receiveCustomData(dataId, buf);
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void emitLaser(int x, int z) {

    }

    public static class FuWenJiTanDefinition extends ControllerDefinition {
        public FuWenJiTanDefinition() {
            super(null, FuWenJiTanBlockEntity.class);
        }
    }

}
