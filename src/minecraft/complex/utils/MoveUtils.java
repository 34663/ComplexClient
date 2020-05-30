package complex.utils;

import complex.Complex;
import complex.command.Command;
import complex.event.impl.EventMove;
import complex.module.impl.combat.Aura;
import complex.module.impl.movement.TargetStrafe;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.potion.Potion;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class MoveUtils implements MCUtil {
    public static boolean tarst;
    private static int uz;

    public static int getSpeedEffect() {
        if (mc.thePlayer.isPotionActive(Potion.getPotionById(1)))
            return mc.thePlayer.getActivePotionEffect(Potion.getPotionById(1)).getAmplifier() + 1;
        else
            return 0;
    }

    public static int getJumpEffect() {
        if (mc.thePlayer.isPotionActive(Potion.getPotionById(8)))
            return mc.thePlayer.getActivePotionEffect(Potion.getPotionById(8)).getAmplifier() + 1;
        else
            return 0;
    }

    public static boolean isOnGround(double height) {
        return !mc.theWorld.getEntitiesWithinAABBExcludingEntity(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(0.0D, -height, 0.0D)).isEmpty();
    }

    public static double getBaseMoveSpeed() {
        double baseSpeed = 0.2873;
        if (Minecraft.getMinecraft().thePlayer.isPotionActive(Potion.getPotionById(0))) {
            final int amplifier = Minecraft.getMinecraft().thePlayer.getActivePotionEffect(Potion.getPotionById(0)).getAmplifier();
            baseSpeed *= 1.0 + 0.2 * (amplifier + 1);
        }
        return baseSpeed;
    }

    public static void strafe() {
        strafe(getSpeed());
    }

    public static float getSpeed() {
        return (float) Math.sqrt(mc.thePlayer.motionX * mc.thePlayer.motionX + mc.thePlayer.motionZ * mc.thePlayer.motionZ);
    }

    public static void strafe(final float speed) {
        if (!isMoving())
            return;

        final double yaw = getDirection();
        mc.thePlayer.motionX = -Math.sin(yaw) * speed;
        mc.thePlayer.motionZ = Math.cos(yaw) * speed;
    }

    public static boolean isMoving() {
        return mc.thePlayer != null && (mc.thePlayer.movementInput.moveForward != 0F || mc.thePlayer.movementInput.moveStrafe != 0F);
    }

    public static boolean c() {
        for (int i = (int) Math.ceil(mc.thePlayer.posY); i >= 0; --i) {
            if (mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, i, mc.thePlayer.posZ)).getBlock() != Blocks.AIR) {
                return false;
            }
        }
        return true;
    }

    public static double getDirection() {
        float rotationYaw = mc.thePlayer.rotationYaw;

        if (mc.thePlayer.moveForward < 0F)
            rotationYaw += 180F;

        float forward = 1F;
        if (mc.thePlayer.moveForward < 0F)
            forward = -0.5F;
        else if (mc.thePlayer.moveForward > 0F)
            forward = 0.5F;

        if (mc.thePlayer.moveStrafing > 0F)
            rotationYaw -= 90F * forward;

        if (mc.thePlayer.moveStrafing < 0F)
            rotationYaw += 90F * forward;

        return Math.toRadians(rotationYaw);
    }

    public static void motion(final double n) {
        mc.thePlayer.motionX = -MathHelper.sin(a()) * n;
        mc.thePlayer.motionZ = MathHelper.cos(a()) * n;
    }

    public static float a() {
        final float rotationYaw = mc.thePlayer.rotationYaw;
        final float moveForward = mc.thePlayer.moveForward;
        final float moveStrafing = mc.thePlayer.moveStrafing;
        float n = rotationYaw + ((moveForward < 0.0f) ? 180 : 0);
        if (moveStrafing < 0.0f) {
            n += ((moveForward < 0.0f) ? -45 : ((moveForward == 0.0f) ? 90 : 45));
        }
        if (moveStrafing > 0.0f) {
            n -= ((moveForward < 0.0f) ? -45 : ((moveForward == 0.0f) ? 90 : 45));
        }
        return n * 0.017453292f;
    }

    public static void setMotion(double speed) {
        ++uz;
        if (Aura.target != null && c() && uz > 4) {
            uz = 0;
            tarst = !tarst;
        }
        final boolean targetStrafe = Complex.getModuleManager().isEnabled("TargetStrafe") && TargetStrafe.vec3d != null && TargetStrafe.vn != null;
        double forward = targetStrafe ? ((double) ((Math.abs(MovementInput.moveForward) > 0.0f || Math.abs(MovementInput.moveStrafe) > 0.0f) ? 1 : 0)) : MovementInput.moveForward;
        double strafe = targetStrafe ? 0.0D : MovementInput.moveStrafe;
        float yaw = targetStrafe ? a(TargetStrafe.vec3d.xCoord, TargetStrafe.vec3d.zCoord) : mc.thePlayer.rotationYaw;

        if (forward == 0.0 && strafe == 0.0) {
            mc.thePlayer.motionX = 0.0;
            mc.thePlayer.motionZ = 0.0;
        } else {
            if (forward != 0.0) {
                if (strafe > 0.0) {
                    yaw += ((forward > 0.0) ? -45 : 45);
                } else if (strafe < 0.0) {
                    yaw += ((forward > 0.0) ? 45 : -45);
                }
                strafe = 0.0;
                if (forward > 0.0) {
                    forward = 1.0;
                } else if (forward < 0.0) {
                    forward = -1.0;
                }
            }
            mc.thePlayer.motionX = forward * speed * -Math.sin(Math.toRadians(yaw)) + strafe * speed * Math.cos(Math.toRadians(yaw));
            mc.thePlayer.motionZ = forward * speed * Math.cos(Math.toRadians(yaw)) - strafe * speed * -Math.sin(Math.toRadians(yaw));
        }
    }

    public static void setMotion(EventMove em, double speed) {
        ++uz;
        if (Aura.target != null && c() && uz > 4) {
            uz = 0;
            tarst = !tarst;
        }
        final boolean targetStrafe = Complex.getModuleManager().isEnabled("TargetStrafe") && TargetStrafe.vec3d != null && TargetStrafe.vn != null;
        double forward = targetStrafe ? ((double) ((Math.abs(MovementInput.moveForward) > 0.0f || Math.abs(MovementInput.moveStrafe) > 0.0f) ? 1 : 0)) : MovementInput.moveForward;
        double strafe = targetStrafe ? 0.0D : MovementInput.moveStrafe;
        float yaw = targetStrafe ? a(TargetStrafe.vec3d.xCoord, TargetStrafe.vec3d.zCoord) : mc.thePlayer.rotationYaw;
        if ((forward == 0.0D) && (strafe == 0.0D)) {
            em.setX(0.0D);
            em.setZ(0.0D);
        } else {
            if (forward != 0.0D) {
                if (strafe > 0.0D) {
                    yaw += (forward > 0.0D ? -45 : 45);
                } else if (strafe < 0.0D) {
                    yaw += (forward > 0.0D ? 45 : -45);
                }
                strafe = 0.0D;
                if (forward > 0.0D) {
                    forward = 1;
                } else if (forward < 0.0D) {
                    forward = -1;
                }
            }
            em.setX(forward * speed * Math.cos(Math.toRadians(yaw + 90.0F)) + strafe * speed * Math.sin(Math.toRadians(yaw + 90.0F)));
            em.setZ(forward * speed * Math.sin(Math.toRadians(yaw + 90.0F)) - strafe * speed * Math.cos(Math.toRadians(yaw + 90.0F)));
        }
    }

    public static float a(final double n, final double n2) {
        return (float) (Math.atan2(n2 - mc.thePlayer.posZ, n - mc.thePlayer.posX) * 180.0 / 3.141592653589793) - 90.0f;
    }

    public static Block getBlockUnderPlayer(EntityPlayer inPlayer, double height) {
        return Minecraft.getMinecraft().theWorld.getBlockState(new BlockPos(inPlayer.posX, inPlayer.posY - height, inPlayer.posZ)).getBlock();
    }
}
