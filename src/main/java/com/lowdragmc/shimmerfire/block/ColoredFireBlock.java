package com.lowdragmc.shimmerfire.block;

import com.lowdragmc.shimmerfire.api.RawFire;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

import java.util.Random;

/**
 * @author KilaBash
 * @date 2022/5/5
 * @implNote ColoredFireBlock
 */
public class ColoredFireBlock extends FireBlock {
    public static final EnumProperty<RawFire> FIRE = EnumProperty.create("fire", RawFire.class, RawFire.values());

    public ColoredFireBlock() {
        super(Properties.of(Material.FIRE, MaterialColor.FIRE).noCollission().instabreak().sound(SoundType.WOOL));
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(AGE, 0)
                .setValue(NORTH, Boolean.FALSE)
                .setValue(EAST, Boolean.FALSE)
                .setValue(SOUTH, Boolean.FALSE)
                .setValue(WEST, Boolean.FALSE)
                .setValue(UP, Boolean.FALSE)
                .setValue(FIRE, RawFire.DESTROY));

    }
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(AGE, NORTH, EAST, SOUTH, WEST, UP, FIRE);
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        return 4;
    }

    @Override
    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
        return this.canSurvive(pState, pLevel, pCurrentPos) ? pState : Blocks.AIR.defaultBlockState();
    }

    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, Random pRand) {
        pLevel.scheduleTick(pPos, this, getFireTickDelay(pLevel.random));
        if (pLevel.getGameRules().getBoolean(GameRules.RULE_DOFIRETICK)) {
            if (!pState.canSurvive(pLevel, pPos)) {
                pLevel.removeBlock(pPos, false);
            }

            BlockState blockstate = pLevel.getBlockState(pPos.below());
            boolean flag = blockstate.isFireSource(pLevel, pPos, Direction.UP);
            int i = pState.getValue(AGE);
            if (!flag && pLevel.isRaining() && this.isNearRain(pLevel, pPos) && pRand.nextFloat() < 0.2F + (float)i * 0.03F) {
                pLevel.removeBlock(pPos, false);
            } else {
                int j = Math.min(15, i + pRand.nextInt(3) / 2);
                if (i != j) {
                    pState = pState.setValue(AGE, Integer.valueOf(j));
                    pLevel.setBlock(pPos, pState, 4);
                }

                if (!flag) {
                    if (!this.isValidFireLocation(pLevel, pPos)) {
                        BlockPos blockpos = pPos.below();
                        if (!pLevel.getBlockState(blockpos).isFaceSturdy(pLevel, blockpos, Direction.UP) || i > 3) {
                            pLevel.removeBlock(pPos, false);
                        }

                        return;
                    }

                    if (i == 15 && pRand.nextInt(4) == 0 && !this.canCatchFire(pLevel, pPos.below(), Direction.UP)) {
                        pLevel.removeBlock(pPos, false);
                        return;
                    }
                }

                boolean flag1 = pLevel.isHumidAt(pPos);
                int k = flag1 ? -50 : 0;
                this.tryCatchFire(pLevel, pPos.east(), 300 + k, pRand, i, Direction.WEST);
                this.tryCatchFire(pLevel, pPos.west(), 300 + k, pRand, i, Direction.EAST);
                this.tryCatchFire(pLevel, pPos.below(), 250 + k, pRand, i, Direction.UP);
                this.tryCatchFire(pLevel, pPos.above(), 250 + k, pRand, i, Direction.DOWN);
                this.tryCatchFire(pLevel, pPos.north(), 300 + k, pRand, i, Direction.SOUTH);
                this.tryCatchFire(pLevel, pPos.south(), 300 + k, pRand, i, Direction.NORTH);
                BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

                for(int l = -1; l <= 1; ++l) {
                    for(int i1 = -1; i1 <= 1; ++i1) {
                        for(int j1 = -1; j1 <= 4; ++j1) {
                            if (l != 0 || j1 != 0 || i1 != 0) {
                                int k1 = 100;
                                if (j1 > 1) {
                                    k1 += (j1 - 1) * 100;
                                }

                                blockpos$mutableblockpos.setWithOffset(pPos, l, j1, i1);
                                int l1 = this.getFireOdds(pLevel, blockpos$mutableblockpos);
                                if (l1 > 0) {
                                    int i2 = (l1 + 40 + pLevel.getDifficulty().getId() * 7) / (i + 30);
                                    if (flag1) {
                                        i2 /= 2;
                                    }

                                    if (i2 > 0 && pRand.nextInt(k1) <= i2 && (!pLevel.isRaining() || !this.isNearRain(pLevel, blockpos$mutableblockpos))) {
                                        int j2 = Math.min(15, i + pRand.nextInt(5) / 4);
                                        pLevel.setBlock(blockpos$mutableblockpos, this.getStateWithAge(pLevel, blockpos$mutableblockpos, j2), 3);
                                    }
                                }
                            }
                        }
                    }
                }

            }
        }
    }

    private static int getFireTickDelay(Random pRandom) {
        return 30 + pRandom.nextInt(10);
    }

    private void tryCatchFire(Level pLevel, BlockPos pPos, int pChance, Random pRandom, int pAge, Direction face) {
        int i = pLevel.getBlockState(pPos).getFlammability(pLevel, pPos, face);
        if (pRandom.nextInt(pChance) < i) {
            BlockState blockstate = pLevel.getBlockState(pPos);
            if (pRandom.nextInt(pAge + 10) < 5 && !pLevel.isRainingAt(pPos)) {
                int j = Math.min(pAge + pRandom.nextInt(5) / 4, 15);
                pLevel.setBlock(pPos, this.getStateWithAge(pLevel, pPos, j), 3);
            } else {
                pLevel.removeBlock(pPos, false);
            }

            blockstate.onCaughtFire(pLevel, pPos, face, null);
        }

    }

    private BlockState getStateWithAge(LevelAccessor pLevel, BlockPos pPos, int pAge) {
        BlockState blockstate = getState(pLevel, pPos);
        return blockstate.is(Blocks.FIRE) ? blockstate.setValue(AGE, Integer.valueOf(pAge)) : blockstate;
    }

    private boolean isValidFireLocation(BlockGetter pLevel, BlockPos pPos) {
        for(Direction direction : Direction.values()) {
            if (this.canCatchFire(pLevel, pPos.relative(direction), direction.getOpposite())) {
                return true;
            }
        }

        return false;
    }

    private int getFireOdds(LevelReader pLevel, BlockPos pPos) {
        if (!pLevel.isEmptyBlock(pPos)) {
            return 0;
        } else {
            int i = 0;

            for(Direction direction : Direction.values()) {
                BlockState blockstate = pLevel.getBlockState(pPos.relative(direction));
                i = Math.max(blockstate.getFireSpreadSpeed(pLevel, pPos.relative(direction), direction.getOpposite()), i);
            }

            return i;
        }
    }
}
