package com.lowdragmc.shimmerfire.block;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.ParametersAreNonnullByDefault;


/**
 * @author KilaBash
 * @date 2022/06/17
 * @implNote FireReceiverBlock
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class FireReceiverBlock extends FirePortBlock {

    public static final VoxelShape SHAPE = Shapes.or(
            Block.box(4, 0, 4, 12, 4, 12)
    );

    public FireReceiverBlock() {
        super(SHAPE);
    }
}
