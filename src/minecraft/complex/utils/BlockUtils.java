package complex.utils;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class BlockUtils implements MCUtil {
    public static boolean isOnLiquid() {
        if (mc.thePlayer == null) return false;
        boolean onLiquid = false;
        final int y = (int) mc.thePlayer.getEntityBoundingBox().offset(0.0D, -0.01D, 0.0D).minY;
        for (int x = MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().minX); x < MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().maxX) + 1; x++) {
            for (int z = MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().minZ); z < MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().maxZ) + 1; z++) {
                final Block block = mc.theWorld.getBlockState(new BlockPos(x, y, z)).getBlock();
                if (block != null && block.getMaterial() != Material.AIR) {
                    if (!(block instanceof BlockLiquid)) return false;
                    onLiquid = true;
                }
            }
        }
        return onLiquid;
    }

    public static boolean isOnLiquid(double profondeur) {
        boolean onLiquid = false;

        if (mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - profondeur, mc.thePlayer.posZ)).getBlock().getMaterial().isLiquid()) {
            onLiquid = true;
        }
        return onLiquid;
    }

    public static boolean isTotalOnLiquid(double profondeur) {
        for (double x = mc.thePlayer.boundingBox.minX; x < mc.thePlayer.boundingBox.maxX; x += 0.01f) {
            for (double z = mc.thePlayer.boundingBox.minZ; z < mc.thePlayer.boundingBox.maxZ; z += 0.01f) {
                Block block = mc.theWorld.getBlockState(new BlockPos(x, mc.thePlayer.posY - profondeur, z)).getBlock();
                if (!(block instanceof BlockLiquid) && !(block instanceof BlockAir)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean collideBlock(AxisAlignedBB axisAlignedBB, Collidable collide) {
        for (int x = MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().minX); x < MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().maxX) + 1; x++) {
            for (int z = MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().minZ); z < MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().maxZ) + 1; z++) {
                Block block = getBlock(new BlockPos(x, axisAlignedBB.minY, z));

                if (!collide.collideBlock(block))
                    return false;
            }
        }
        return true;
    }

    public static float[] aimAtBlock(BlockPos pos) {
        EnumFacing[] arrenumFacing = EnumFacing.values();
        int n = arrenumFacing.length;
        int n2 = 0;
        float yaw = 1.0F;
        float pitch = 1.0F;
        if (n2 <= n) {
            EnumFacing side = arrenumFacing[n2];
            BlockPos neighbor = pos.offset(side);
            EnumFacing side2 = side.getOpposite();
            Vec3d hitVec = new Vec3d(neighbor).addVector(0.5D, 0.5D, 0.5D).add(new Vec3d(side2.getDirectionVec()).scale(0.5D).normalize());

            yaw = RotationUtils.getNeededRotations(hitVec)[0];
            pitch = RotationUtils.getNeededRotations(hitVec)[1];
            if (canBeClicked(neighbor)) {
                return new float[]{yaw, pitch};
            }
            hitVec = new Vec3d(pos).addVector(0.5D, 0.5D, 0.5D).add(new Vec3d(side.getDirectionVec()).scale(0.5D).normalize());
            yaw = RotationUtils.getNeededRotations(hitVec)[0];
            pitch = RotationUtils.getNeededRotations(hitVec)[1];
            return new float[]{yaw, pitch};
        }
        return new float[]{1.0F, 1.0F};
    }

    public static boolean canBeClicked(BlockPos pos) {
        return getBlock(pos).canCollideCheck(getState(pos), false);
    }

    public static IBlockState getState(BlockPos pos) {
        return mc.theWorld.getBlockState(pos);
    }

    public static Block getBlock(BlockPos pos) {
        return getState(pos).getBlock();
    }

    public interface Collidable {
        boolean collideBlock(Block block);
    }
}
