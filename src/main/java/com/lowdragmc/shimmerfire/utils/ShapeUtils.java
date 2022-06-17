package com.lowdragmc.shimmerfire.utils;

import com.lowdragmc.lowdraglib.utils.Vector3;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * @author KilaBash
 * @date 2022/6/17
 * @implNote ShapeUtils
 */
public class ShapeUtils {

    public static AABB rotate(AABB aaBB, Direction facing) {
        switch (facing) {
            case SOUTH -> {
                return rotate(aaBB, new Vector3(0, 1, 0), 180);
            }
            case EAST -> {
                return rotate(aaBB, new Vector3(0, 1, 0), -90);
            }
            case WEST -> {
                return rotate(aaBB, new Vector3(0, 1, 0), 90);
            }
            case UP -> {
                return rotate(aaBB, new Vector3(0, 0, 1), 90);
            }
            case DOWN -> {
                return rotate(aaBB, new Vector3(0, 0, 1), -90);
            }
        }
        return aaBB;
    }

    public static AABB rotate(AABB aaBB, Vector3 axis, double degree) {
        Vector3 min = new Vector3(aaBB.minX, aaBB.minY, aaBB.minZ).subtract(0.5);
        Vector3 max = new Vector3(aaBB.maxX, aaBB.maxY, aaBB.maxZ).subtract(0.5);
        double radians = Math.toRadians(degree);
        min.rotate(radians, axis);
        max.rotate(radians, axis);
        min.add(0.5);
        max.add(0.5);
        return new AABB(min.x, min.y, min.z, max.x, max.y, max.z);
    }

    public static VoxelShape rotate(VoxelShape shape, Direction facing) {
        return shape.toAabbs().stream().map(aabb -> Shapes.create(rotate(aabb, facing))).reduce(Shapes::or).orElse(Shapes.block());
    }

    public static VoxelShape rotate(VoxelShape shape, Vector3 axis, double degree) {
        return shape.toAabbs().stream().map(aabb -> Shapes.create(rotate(aabb, axis, degree))).reduce(Shapes::or).orElse(Shapes.block());
    }
}
