package com.lowdragmc.shimmerfire.block.decorated;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

/**
 * @author KilaBash
 * @date 2022/6/22
 * @implNote DecorationBlock
 */
public class DecorationBlock extends Block {
    public DecorationBlock() {
        super(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.STONE).requiresCorrectToolForDrops().strength(1.5F, 6.0F));
    }

    public DecorationBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }
}
