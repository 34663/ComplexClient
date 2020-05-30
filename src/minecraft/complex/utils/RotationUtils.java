package complex.utils;

import java.util.List;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class RotationUtils {
    static Minecraft mc = Minecraft.getMinecraft();
    public static Rotation serverRotation;

    public static double isInFov(final float n, final float n2, final double n3, final double n4, final double n5) {
        return Math.abs(MathHelper.wrapDegrees(new Vec3d(n, n2, 0.0).xCoord - getAngleBetweenVecs(new Vec3d(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ), new Vec3d(n3, n4, n5))[0])) * 2.0;
    }

    public static float[] getAngleBetweenVecs(final Vec3d vec3, final Vec3d vec4) {
        final double n = vec4.xCoord - vec3.xCoord;
        final double n2 = vec4.yCoord - vec3.yCoord;
        final double n3 = vec4.zCoord - vec3.zCoord;
        return new float[] { (float)(Math.atan2(n3, n) * 180.0 / 3.141592653589793) - 90.0f, (float)(-(Math.atan2(n2, Math.sqrt(n * n + n3 * n3)) * 180.0 / 3.141592653589793)) };
    }

    public static boolean isValidToRotate(final double n, final double n2) {
        if (mc.thePlayer == null || mc.theWorld == null || mc.thePlayer.getEntityWorld() == null) {
            return false;
        }
        for (final Entity entity : mc.thePlayer.getEntityWorld().loadedEntityList) {
            if (entity instanceof EntityPlayer && entity != mc.thePlayer && mc.thePlayer.getDistanceToEntity(entity) < n && isInFov(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, entity.posX, entity.posY, entity.posZ) < n2) {
                return true;
            }
        }
        return false;
    }

    public static float[] getRotations(EntityLivingBase ent) {
        double x = ent.posX;
        double z = ent.posZ;
        double y = ent.posY + ent.getEyeHeight() / 2.0F;
        return getRotationFromPosition(x, z, y);
    }

    public static float[] getPredictedRotations(EntityLivingBase ent) {
        double x = ent.posX + (ent.posX - ent.lastTickPosX);
        double z = ent.posZ + (ent.posZ - ent.lastTickPosZ);
        double y = ent.posY + ent.getEyeHeight() / 2.0F;
        return getRotationFromPosition(x, z, y);
    }

    public static float[] getAverageRotations(List<EntityLivingBase> targetList) {
        double posX = 0.0D;
        double posY = 0.0D;
        double posZ = 0.0D;
        for (Entity ent : targetList) {
            posX += ent.posX;
            posY += ent.boundingBox.maxY - 2.0D;
            posZ += ent.posZ;
        }
        posX /= targetList.size();
        posY /= targetList.size();
        posZ /= targetList.size();

        return new float[]{getRotationFromPosition(posX, posZ, posY)[0], getRotationFromPosition(posX, posZ, posY)[1]};
    }

    public static float getStraitYaw() {
        float YAW = MathHelper.wrapDegrees(mc.thePlayer.rotationYaw);
        if (YAW < 45 && YAW > -45) {
            YAW = 0;
        } else if (YAW > 45 && YAW < 135) {
            YAW = 90f;
        } else if (YAW > 135 || YAW < -135) {
            YAW = 180;
        } else {
            YAW = -90f;
        }
        return YAW;
    }

    public static float[] getBowAngles(final Entity entity) {
        final double xDelta = (entity.posX - entity.lastTickPosX) * 0.4;
        final double zDelta = (entity.posZ - entity.lastTickPosZ) * 0.4;
        double d = Minecraft.getMinecraft().thePlayer.getDistanceToEntity(entity);
        d -= d % 0.8;
        double xMulti = 1.0;
        double zMulti = 1.0;
        final boolean sprint = entity.isSprinting();
        xMulti = d / 0.8 * xDelta * (sprint ? 1.25 : 1.0);
        zMulti = d / 0.8 * zDelta * (sprint ? 1.25 : 1.0);
        final double x = entity.posX + xMulti - Minecraft.getMinecraft().thePlayer.posX;
        final double z = entity.posZ + zMulti - Minecraft.getMinecraft().thePlayer.posZ;
        final double y = Minecraft.getMinecraft().thePlayer.posY + Minecraft.getMinecraft().thePlayer.getEyeHeight() - (entity.posY + entity.getEyeHeight());
        final double dist = Minecraft.getMinecraft().thePlayer.getDistanceToEntity(entity);
        final float yaw = (float) Math.toDegrees(Math.atan2(z, x)) - 90.0f;
        double d1 = MathHelper.sqrt_double(x * x + z * z);
        final float pitch = (float) -(Math.atan2(y, d1) * 180.0D / Math.PI) + (float) dist * 0.11f;

        return new float[]{yaw, -pitch};
    }

    public static float[] getRotationFromPosition(double x, double z, double y) {
        double xDiff = x - Minecraft.getMinecraft().thePlayer.posX;
        double zDiff = z - Minecraft.getMinecraft().thePlayer.posZ;
        double yDiff = y - Minecraft.getMinecraft().thePlayer.posY - 1.2;

        double dist = MathHelper.sqrt_double(xDiff * xDiff + zDiff * zDiff);
        float yaw = (float) (Math.atan2(zDiff, xDiff) * 180.0D / 3.141592653589793D) - 90.0F;
        float pitch = (float) -(Math.atan2(yDiff, dist) * 180.0D / 3.141592653589793D);
        return new float[]{yaw, pitch};
    }

    public static float getTrajAngleSolutionLow(float d3, float d1, float velocity) {
        float g = 0.006F;
        float sqrt = velocity * velocity * velocity * velocity - g * (g * (d3 * d3) + 2.0F * d1 * (velocity * velocity));
        return (float) Math.toDegrees(Math.atan((velocity * velocity - Math.sqrt(sqrt)) / (g * d3)));
    }

    public static float getYawChange(float yaw, double posX, double posZ) {
        double deltaX = posX - Minecraft.getMinecraft().thePlayer.posX;
        double deltaZ = posZ - Minecraft.getMinecraft().thePlayer.posZ;
        double yawToEntity = 0;
        if ((deltaZ < 0.0D) && (deltaX < 0.0D)) {
            if (deltaX != 0)
                yawToEntity = 90.0D + Math.toDegrees(Math.atan(deltaZ / deltaX));
        } else if ((deltaZ < 0.0D) && (deltaX > 0.0D)) {
            if (deltaX != 0)
                yawToEntity = -90.0D + Math.toDegrees(Math.atan(deltaZ / deltaX));
        } else {
            if (deltaZ != 0)
                yawToEntity = Math.toDegrees(-Math.atan(deltaX / deltaZ));
        }

        return MathHelper.wrapDegrees(-(yaw - (float) yawToEntity));
    }

    public static float getPitchChange(float pitch, Entity entity, double posY) {
        double deltaX = entity.posX - Minecraft.getMinecraft().thePlayer.posX;
        double deltaZ = entity.posZ - Minecraft.getMinecraft().thePlayer.posZ;
        double deltaY = posY - 2.2D + entity.getEyeHeight() - Minecraft.getMinecraft().thePlayer.posY;
        double distanceXZ = MathHelper.sqrt_double(deltaX * deltaX + deltaZ * deltaZ);
        double pitchToEntity = -Math.toDegrees(Math.atan(deltaY / distanceXZ));
        return -MathHelper.wrapDegrees(pitch - (float) pitchToEntity) - 2.5F;
    }


    public static float getNewAngle(float angle) {
        angle %= 360.0F;
        if (angle >= 180.0F) {
            angle -= 360.0F;
        }
        if (angle < -180.0F) {
            angle += 360.0F;
        }
        return angle;
    }

    public static boolean canEntityBeSeen(Entity e) {
        Vec3d vec1 = new Vec3d(mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);

        AxisAlignedBB box = e.getEntityBoundingBox();
        Vec3d vec2 = new Vec3d(e.posX, e.posY + (e.getEyeHeight() / 1.32F), e.posZ);
        double minx = e.posX - 0.25;
        double maxx = e.posX + 0.25;
        double miny = e.posY;
        double maxy = e.posY + Math.abs(e.posY - box.maxY);
        double minz = e.posZ - 0.25;
        double maxz = e.posZ + 0.25;
        boolean see = mc.theWorld.rayTraceBlocks(vec1, vec2) == null ? true : false;
        if (see)
            return true;
        vec2 = new Vec3d(maxx, miny, minz);
        see = mc.theWorld.rayTraceBlocks(vec1, vec2) == null ? true : false;
        if (see)
            return true;
        vec2 = new Vec3d(minx, miny, minz);
        see = mc.theWorld.rayTraceBlocks(vec1, vec2) == null ? true : false;

        if (see)
            return true;
        vec2 = new Vec3d(minx, miny, maxz);
        see = mc.theWorld.rayTraceBlocks(vec1, vec2) == null ? true : false;
        if (see)
            return true;
        vec2 = new Vec3d(maxx, miny, maxz);
        see = mc.theWorld.rayTraceBlocks(vec1, vec2) == null ? true : false;
        if (see)
            return true;

        vec2 = new Vec3d(maxx, maxy, minz);
        see = mc.theWorld.rayTraceBlocks(vec1, vec2) == null ? true : false;

        if (see)
            return true;
        vec2 = new Vec3d(minx, maxy, minz);

        see = mc.theWorld.rayTraceBlocks(vec1, vec2) == null ? true : false;
        if (see)
            return true;
        vec2 = new Vec3d(minx, maxy, maxz - 0.1);
        see = mc.theWorld.rayTraceBlocks(vec1, vec2) == null ? true : false;
        if (see)
            return true;
        vec2 = new Vec3d(maxx, maxy, maxz);
        see = mc.theWorld.rayTraceBlocks(vec1, vec2) == null ? true : false;
        if (see)
            return true;


        return false;
    }

    public static float getDistanceBetweenAngles(float angle1, float angle2) {
        float angle = Math.abs(angle1 - angle2) % 360.0F;
        if (angle > 180.0F) {
            angle = 360.0F - angle;
        }
        return angle;
    }

    public static float[] getBlockRotations(Vec3d vec) {
        Entity temp = new net.minecraft.entity.projectile.EntitySnowball(Minecraft.getMinecraft().theWorld);
        Vec3d eyesPos = getEyesPos();
        temp.posX = (vec.xCoord - eyesPos.xCoord + 0.5D);
        temp.posY = (vec.yCoord - eyesPos.yCoord + 0.5D);
        temp.posZ = (vec.zCoord - eyesPos.zCoord + 0.5D);
        return getAngles(temp);
    }

    public static float[] getBlockRotations(final double x, final double y, final double z) {
        final double var4 = x - mc.thePlayer.posX + 0.5;
        final double var5 = z - mc.thePlayer.posZ + 0.5;
        final double var6 = y - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight() - 1.0);
        final double var7 = MathHelper.sqrt_double(var4 * var4 + var5 * var5);
        final float var8 = (float)(Math.atan2(var5, var4) * 180.0 / 3.141592653589793) - 90.0f;
        return new float[] { var8, (float)(-(Math.atan2(var6, var7) * 180.0 / 3.141592653589793)) };
    }

    public static Vec3d getEyesPos() {
        Minecraft.getMinecraft();
        Minecraft.getMinecraft();
        Minecraft.getMinecraft();
        Minecraft.getMinecraft();
        return new Vec3d(Minecraft.getMinecraft().thePlayer.posX, Minecraft.getMinecraft().thePlayer.posY + Minecraft.getMinecraft().thePlayer.getEyeHeight(), Minecraft.getMinecraft().thePlayer.posZ);
    }

    public static float[] getAngles(Entity e) {
        return new float[]{getYawChangeToEntity(e) + Minecraft.getMinecraft().thePlayer.rotationYaw, getPitchChangeToEntity(e) + Minecraft.getMinecraft().thePlayer.rotationPitch};
    }

    public static float getPitchChangeToEntity(Entity entity) {
        double deltaX = entity.posX - Minecraft.getMinecraft().thePlayer.posX;
        double deltaZ = entity.posZ - Minecraft.getMinecraft().thePlayer.posZ;
        double deltaY = entity.posY - 1.6D + entity.getEyeHeight() - 0.4D - Minecraft.getMinecraft().thePlayer.posY;
        double distanceXZ = MathHelper.sqrt_double(deltaX * deltaX + deltaZ * deltaZ);
        double pitchToEntity = -Math.toDegrees(Math.atan(deltaY / distanceXZ));
        return -MathHelper.wrapDegrees(Minecraft.getMinecraft().thePlayer.rotationPitch - (float) pitchToEntity);
    }

    public static float getYawChangeToEntity(Entity entity) {
        double deltaX = entity.posX - Minecraft.getMinecraft().thePlayer.posX;
        double deltaZ = entity.posZ - Minecraft.getMinecraft().thePlayer.posZ;
        double yawToEntity;
        if ((deltaZ < 0.0D) && (deltaX < 0.0D)) {
            yawToEntity = 90.0D + Math.toDegrees(Math.atan(deltaZ / deltaX));
        } else {
            if ((deltaZ < 0.0D) && (deltaX > 0.0D)) {
                yawToEntity = -90.0D + Math.toDegrees(Math.atan(deltaZ / deltaX));
            } else
                yawToEntity = Math.toDegrees(-Math.atan(deltaX / deltaZ));
        }
        return MathHelper.wrapDegrees(-(Minecraft.getMinecraft().thePlayer.rotationYaw - (float) yawToEntity));
    }

    public static float[] getNeededRotations(Vec3d vec) {
        Vec3d eyesPos = getEyesPos();
        double diffX = vec.xCoord - eyesPos.xCoord + 0.5D;
        double diffY = vec.yCoord - eyesPos.yCoord + 0.5D;
        double diffZ = vec.zCoord - eyesPos.zCoord + 0.5D;
        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
        float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0F;
        float pitch = (float) -(Math.atan2(diffY, diffXZ) * 180.0D / 3.141592653589793D);
        return new float[]{MathHelper.wrapDegrees(yaw), Minecraft.getMinecraft().gameSettings.keyBindJump.pressed ? 90.0F : MathHelper.wrapDegrees(pitch)};
    }
}

