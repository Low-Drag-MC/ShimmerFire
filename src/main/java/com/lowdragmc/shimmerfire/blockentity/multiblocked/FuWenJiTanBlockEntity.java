package com.lowdragmc.shimmerfire.blockentity.multiblocked;

import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.multiblocked.api.capability.IO;
import com.lowdragmc.multiblocked.api.definition.ControllerDefinition;
import com.lowdragmc.multiblocked.api.recipe.Recipe;
import com.lowdragmc.multiblocked.api.recipe.RecipeLogic;
import com.lowdragmc.multiblocked.api.recipe.RecipeMap;
import com.lowdragmc.multiblocked.api.registry.MbdCapabilities;
import com.lowdragmc.multiblocked.api.tile.ControllerTileEntity;
import com.lowdragmc.multiblocked.common.tile.PedestalTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Baka943, KilaBash
 * @date 2022/7/16
 * @implNote FuWenJiTanBlockEntity, implementation from bak943's script
 */
public class FuWenJiTanBlockEntity extends ControllerTileEntity {
    int isFoad = 0;

    public FuWenJiTanBlockEntity(ControllerDefinition definition, BlockPos pos, BlockState state) {
        super(definition, pos, state);
    }

    @Override
    public RecipeLogic createRecipeLogic() {
        return new FuWenJiTanRecipeLogic(this);
    }

    @Override
    public ModularUI createUI(Player entityPlayer) {
        return null;
    }

    @Override
    public void updateFormed() {
        super.updateFormed();
        if (recipeLogic == null) {
            return;
        }
        var controllerPos = getBlockPos().immutable();
        var centerPos = controllerPos.below(2);
        var world = getLevel();

        AtomicInteger instability = new AtomicInteger();

        var recipeMap = getDefinition().recipeMap;

        var entity = world.getBlockEntity(centerPos);

        ItemStack input;

        if(recipeLogic.isIdle() && isFoad == 1 && entity instanceof PedestalTileEntity pedestal) {
            for (Recipe recipe : recipeMap.allRecipes()) {
                if (recipe.matchRecipe(IO.IN, this, recipe.inputs)) {
                    input = ItemStack.of((CompoundTag) recipe.data.get("item"));
                    if (ItemStack.isSame(input, pedestal.getItemStack())) {
                        recipeLogic.lastRecipe = recipe;
                        recipeLogic.setStatus(RecipeLogic.Status.WORKING);
                        recipeLogic.progress = 0;
                        recipeLogic.duration = recipe.duration;
                        recipeLogic.markDirty();
                        break;
                    }
                }
            }
        }


        isFoad = 0;


        if(recipeLogic.isWorking() && entity instanceof PedestalTileEntity pedestal && level != null) {
            var recipe = recipeLogic.lastRecipe;
            input = ItemStack.of((CompoundTag) recipe.data.get("item"));
            boolean explode = false;
            if(recipe.matchRecipe(IO.IN, this, recipe.inputs)) {
                if (!ItemStack.isSame(input, pedestal.getItemStack())) {
                    explode = true;
                }
            } else {
                explode = true;
            }
            if (explode) {
                recipeLogic.lastRecipe = null;
                recipeLogic.setStatus(RecipeLogic.Status.IDLE);
                recipeLogic.markDirty();
                var e = level.explode(null,
                        controllerPos.getX() + 0.5,
                        controllerPos.getY() + 0.5,
                        controllerPos.getZ() + 0.5, 2,
                        Explosion.BlockInteraction.NONE);
            }
        }

        if (recipeLogic.isWorking() && getTimer() % 20 ==0) {
            List<PedestalTileEntity> pedestals = new ArrayList<>();
            BlockPos.betweenClosedStream(centerPos.offset(-12, -2, 0), centerPos.offset(12, 2, 12)).forEach(blockPos -> {
                var pos = blockPos.immutable();
                if(pos.equals(centerPos)) return;

                var block = world.getBlockState(pos);
                var ipos = centerPos.atY(pos.getY()).multiply(2).subtract(pos);
                var iblock = world.getBlockState(ipos);

                if (block == iblock) {
                    if (block.getBlock() instanceof CandleBlock) {
                        instability.addAndGet(block.getValue(CandleBlock.LIT) ? -2 : -1);
                    }
                } else {
                    instability.addAndGet(1);
                }

                if (world.getBlockEntity(pos) instanceof PedestalTileEntity pedestal) {
                    if (!pedestal.getItemStack().isEmpty()) {
                        pedestals.add(pedestal);
                    }

                    if (pedestal.getItemStack().isEmpty()) {
                        instability.addAndGet(1);
                    } else {
                        instability.addAndGet(2);
                    }

                    if (world.getBlockEntity(ipos) instanceof PedestalTileEntity ipedestal) {
                        if (!ipedestal.getItemStack().isEmpty()) {
                            pedestals.add(ipedestal);
                        }
                        if (!ItemStack.isSame(pedestal.getItemStack(), ipedestal.getItemStack())) {
                            instability.addAndGet(1);
                        }
                    } else {
                        instability.addAndGet(1);
                    }
                }
            });

            if(recipeLogic != null && recipeLogic.isWorking()) {
                var req = recipeLogic.lastRecipe.data.getInt("recipelnstability");
                if(instability.get() + req >= world.random.nextInt(200)) {
                    recipeLogic.setStatus(RecipeLogic.Status.IDLE);
                    isFoad = 0;
                    markAsDirty();

                    for(var pedestal : pedestals) {
                        if(!pedestal.getItemStack().isEmpty() && world.random.nextBoolean()) {
                            var x = pedestal.getBlockPos().getX() + 0.5;
                            var y = pedestal.getBlockPos().getY() + 1.5;
                            var z = pedestal.getBlockPos().getZ() + 0.5;

                            if(world.random.nextBoolean()) {
                                level.explode(null,
                                        controllerPos.getX() + 0.5,
                                        controllerPos.getY() + 1.5,
                                        controllerPos.getZ() + 0.5, 1,
                                        Explosion.BlockInteraction.NONE);
                            } else {
                                spawnLightning(x, y, z, true);
                            }

                            var stack = pedestal.getItemStack().getItem().getRegistryName();

                            MinecraftServer server = level.getServer();
                            server.getCommands().performCommand(server.createCommandSourceStack().withSuppressedOutput(),
                                    "particle minecraft:item %s %f %f %f 0.1 0.1 0.1 0.1 8 normal".formatted(stack.toString(), x, y, z));

                            pedestal.setItemStack(ItemStack.EMPTY);
                        }
                    }
                }
            }
        }
    }

    public void spawnLightning(double x, double y, double z, boolean effectOnly) {
        if (this.level instanceof ServerLevel) {
            LightningBolt e = EntityType.LIGHTNING_BOLT.create(this.level);
            if (e != null) {
                e.moveTo(x, y, z);
                e.setVisualOnly(effectOnly);
                this.level.addFreshEntity(e);
            }
        }

    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        if (level != null && level.getServer() != null) {
            var x = getBlockPos().getX() + 0.5;
            var y = getBlockPos().getY() + 0.5;
            var z = getBlockPos().getZ() + 0.5;
            MinecraftServer server = level.getServer();
            server.getCommands().performCommand(server.createCommandSourceStack().withSuppressedOutput(),
                    "particle minecraft:happy_villager %f %f %f 0.4 0.3 0.4 0 24 normal".formatted(x, y, z));
        }
    }

    @Override
    public InteractionResult use(Player player, InteractionHand hand, BlockHitResult hit) {
        if(isFormed() && player.getItemInHand(hand).getItem() == Items.STICK) {
            isFoad = 1;
        }
        return super.use(player, hand, hit);
    }

    public static class FuWenJiTanRecipeLogic extends RecipeLogic {

        public FuWenJiTanRecipeLogic(ControllerTileEntity controller) {
            super(controller);
        }

        @Override
        public void setupRecipe(Recipe recipe) {
        }

        @Override
        public void onRecipeFinish() {
            var world = controller.getLevel();
            var input = ItemStack.of((CompoundTag) lastRecipe.data.get("item"));
               if(lastRecipe.handleRecipeIO(IO.IN, controller)) {
                setStatus(Status.IDLE);
                progress = 0;
                duration = 0;
                markDirty();

                var entity = world.getBlockEntity(controller.getBlockPos().immutable().below(2));
                var list = lastRecipe.getOutputContents(MbdCapabilities.get("item"));
                var item = ((Ingredient)list.get(0).getContent()).getItems()[0];

                var stack = item.copy();

                if(entity instanceof PedestalTileEntity pedestal) {
                    if(ItemStack.isSame(input, pedestal.getItemStack())) {
                        pedestal.setItemStack(stack);
                    }
                }
            }
        }
    }


    public static class FuWenJiTanDefinition extends ControllerDefinition {
        public FuWenJiTanDefinition() {
            super(null, FuWenJiTanBlockEntity.class);
        }
    }


    public static void registerRecipeMap() {
        RecipeMap recipeMap = new RecipeMap("kaka");
        ItemStack output = Items.ENCHANTED_BOOK.getDefaultInstance();
        CompoundTag data = new CompoundTag();
        data.putInt("recipelnstability", 20);
        data.put("item", Items.BOOK.getDefaultInstance().save(new CompoundTag()));
        EnchantedBookItem.addEnchantment(output, new EnchantmentInstance(Registry.ENCHANTMENT.get(new ResourceLocation("minecraft:efficiency")), 5));
        recipeMap.start()
                .name("kaka_demo_recipe")
                .inputItems(Ingredient.of(Items.AMETHYST_SHARD), Ingredient.of(Items.AMETHYST_SHARD), Ingredient.of(Items.AMETHYST_SHARD))
                .inputFluids(new FluidStack(Fluids.LAVA, 1000), new FluidStack(Fluids.WATER, 1000))
                .outputItems(output)
                .data(data)
                .duration(100)
                .buildAndRegister();

        RecipeMap.register(recipeMap);
    }

}
