package complex.utils;

import complex.command.Command;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class PlayerUtil implements MCUtil {
    public static boolean isOnLiquid(AxisAlignedBB boundingBox) {
        AxisAlignedBB bb = new AxisAlignedBB(boundingBox.minX + 0.305, boundingBox.minY - 0.1, boundingBox.minZ + 0.305, boundingBox.maxX - 0.305, boundingBox.maxY, boundingBox.maxZ - 0.305);
        boolean onLiquid = false;
        Block block = getBlock(new BlockPos(bb.minX, bb.minY, bb.minZ));

        if (!(block instanceof BlockLiquid)) {
            return false;
        }
        onLiquid = true;
        return onLiquid;
    }

    public static boolean isOnLiquid() {
        final AxisAlignedBB par1AxisAlignedBB = mc.thePlayer.boundingBox.offset(0.0, -0.01, 0.0).contract(0.001);
        final int var4 = MathHelper.floor_double(par1AxisAlignedBB.minX);
        final int var5 = MathHelper.floor_double(par1AxisAlignedBB.maxX + 1.0);
        final int var6 = MathHelper.floor_double(par1AxisAlignedBB.minY);
        final int var7 = MathHelper.floor_double(par1AxisAlignedBB.maxY + 1.0);
        final int var8 = MathHelper.floor_double(par1AxisAlignedBB.minZ);
        final int var9 = MathHelper.floor_double(par1AxisAlignedBB.maxZ + 1.0);
        final Vec3d var10 = new Vec3d(0.0, 0.0, 0.0);
        for (int var11 = var4; var11 < var5; ++var11) {
            for (int var12 = var6; var12 < var7; ++var12) {
                for (int var13 = var8; var13 < var9; ++var13) {
                    final Block var14 = mc.theWorld.getBlock(var11, var12, var13);
                    if (!(var14 instanceof BlockAir) && !(var14 instanceof BlockLiquid)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public static boolean isInLiquid2() {
        final AxisAlignedBB par1AxisAlignedBB = mc.thePlayer.boundingBox.contract(0.001);
        final int var4 = MathHelper.floor_double(par1AxisAlignedBB.minX);
        final int var5 = MathHelper.floor_double(par1AxisAlignedBB.maxX + 1.0);
        final int var6 = MathHelper.floor_double(par1AxisAlignedBB.minY);
        final int var7 = MathHelper.floor_double(par1AxisAlignedBB.maxY + 1.0);
        final int var8 = MathHelper.floor_double(par1AxisAlignedBB.minZ);
        final int var9 = MathHelper.floor_double(par1AxisAlignedBB.maxZ + 1.0);
        final Vec3d var10 = new Vec3d(0.0, 0.0, 0.0);
        for (int var11 = var4; var11 < var5; ++var11) {
            for (int var12 = var6; var12 < var7; ++var12) {
                for (int var13 = var8; var13 < var9; ++var13) {
                    final Block var14 = mc.theWorld.getBlock(var11, var12, var13);
                    if (var14 instanceof BlockLiquid) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static double[] moveLooking( float yawOffset) {
        float dir = mc.thePlayer.rotationYaw + yawOffset;
        if (mc.thePlayer.moveForward < 0.0F) {
            dir += 180.0F;
        }
        if (mc.thePlayer.moveStrafing > 0.0F) {
            dir -= 90.0F * (mc.thePlayer.moveForward < 0.0F ? -0.5F : mc.thePlayer.moveForward > 0.0F ? 0.5F : 1.0F);
        }
        if (mc.thePlayer.moveStrafing < 0.0F) {
            dir += 90.0F * (mc.thePlayer.moveForward < 0.0F ? -0.5F : mc.thePlayer.moveForward > 0.0F ? 0.5F : 1.0F);
        }

        float xD = (float)Math.cos((dir + 90.0F) * Math.PI / 180.0D);
        float zD = (float)Math.sin((dir + 90.0F) * Math.PI / 180.0D); // sin(x + 90) = cos(x) ? Explain?
        return new double[] { xD, zD };
    }

    public static Block getBlockUnderPlayer(EntityPlayer inPlayer) {
        return getBlock(new BlockPos(inPlayer.posX, inPlayer.posY - 0.5, inPlayer.posZ));
    }

    public static Block getBlock(BlockPos pos) {
        return mc.theWorld.getBlockState(pos).getBlock();
    }

    public static boolean isInLiquid() {
        if (mc.thePlayer == null) {
            return false;
        }
        for (int x = MathHelper.floor_double(mc.thePlayer.boundingBox.minX); x < MathHelper.floor_double(mc.thePlayer.boundingBox.maxX) + 1; x++) {
            for (int z = MathHelper.floor_double(mc.thePlayer.boundingBox.minZ); z < MathHelper.floor_double(mc.thePlayer.boundingBox.maxZ) + 1; z++) {
                BlockPos pos = new BlockPos(x, (int) mc.thePlayer.boundingBox.minY, z);
                Block block = mc.theWorld.getBlockState(pos).getBlock();
                if ((block != null) && (!(block instanceof BlockAir))) {
                    return block instanceof BlockLiquid;
                }
            }
        }
        return false;
    }

    public static BlockPos getHypixelBlockpos(String str){
        int val = 89;
        if(str != null && str.length() > 1){
            char[] chs = str.toCharArray();

            int lenght = chs.length;
            for(int i = 0; i < lenght; i++)
                val += (int)chs[i] * str.length()* str.length() + (int)str.charAt(0) + (int)str.charAt(1);
            val/=str.length();
        }
        return new BlockPos(val, -val%255, val);
    }

    public static boolean isMoving() {
        if ((!mc.thePlayer.isCollidedHorizontally) && (!mc.thePlayer.isSneaking())) {
            return (MovementInput.moveForward != 0.0F || MovementInput.moveStrafe != 0.0F);
        }
        return false;
    }

    public static boolean isMoving2() {
        return ((mc.thePlayer.moveForward != 0.0F || mc.thePlayer.moveStrafing != 0.0F));
    }

    public static float getCooldown() {
        return mc.thePlayer.getCooledAttackStrength(0);
    }

    public static boolean isBlockUnder() {
        for (int offset = 0; offset < mc.thePlayer.posY + mc.thePlayer.getEyeHeight(); offset += 2) {
            AxisAlignedBB boundingBox = mc.thePlayer.getEntityBoundingBox().offset(0, -offset, 0);

            if (!mc.theWorld.getEntitiesWithinAABBExcludingEntity(mc.thePlayer, boundingBox).isEmpty()) {
                return true;
            }
        }

        return false;
    }

    public static int airSlot() {
        for (int j = 0; j < 8; j++) {
            if (mc.thePlayer.inventory.mainInventory.get(j).getItem().getContainerItem() == null)
                return j;
        }
        Command.sendChatMessage("Clear a hotbar slot.");
        return -10;
    }
}
