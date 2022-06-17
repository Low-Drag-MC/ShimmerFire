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
            Block.box(6, 3, 9, 9, 5, 10),
            Block.box(7, 3, 6, 10, 5, 7),
            Block.box(9, 3, 7, 10, 5, 10),
            Block.box(6, 3, 6, 7, 5, 9),
            Block.box(5, 2, 5, 11, 3, 11),
            Block.box(4, 2, 10, 5, 4, 12),
            Block.box(4, 2, 4, 5, 4, 6),
            Block.box(11, 2, 10, 12, 4, 12),
            Block.box(11, 2, 5, 12, 4, 6),
            Block.box(5, 2, 11, 6, 4, 12),
            Block.box(10, 2, 11, 11, 4, 12),
            Block.box(5, 2, 4, 6, 4, 5),
            Block.box(10, 2, 4, 12, 4, 5),
            Block.box(7, 2, 7, 9, 4, 9),
            Block.box(4, 1, 4, 12, 2, 12),
            Block.box(5, 0, 5, 11, 1, 11)
    );

    public FireReceiverBlock() {
        super(SHAPE);
    }
}
