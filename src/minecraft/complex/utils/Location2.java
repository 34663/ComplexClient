package complex.utils;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.BlockPos;

public class Location2 {
    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;

    public Location2(double x, double y, double z, float yaw, float pitch) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public Location2(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = 0.0F;
        this.pitch = 0.0F;
    }

    public Location2(BlockPos pos) {
        this.x = (double)pos.getX();
        this.y = (double)pos.getY();
        this.z = (double)pos.getZ();
        this.yaw = 0.0F;
        this.pitch = 0.0F;
    }

    public Location2(int x, int y, int z) {
        this.x = (double)x;
        this.y = (double)y;
        this.z = (double)z;
        this.yaw = 0.0F;
        this.pitch = 0.0F;
    }

    public Location2(EntityLivingBase entity) {
        this.x = entity.posX;
        this.y = entity.posY;
        this.z = entity.posZ;
        this.yaw = 0.0f;
        this.pitch = 0.0f;
    }

    public Location2 add(int x, int y, int z) {
        this.x += (double)x;
        this.y += (double)y;
        this.z += (double)z;
        return this;
    }

    public Location2 add(double x, double y, double z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }

    public Location2 subtract(int x, int y, int z) {
        this.x -= (double)x;
        this.y -= (double)y;
        this.z -= (double)z;
        return this;
    }

    public Location2 subtract(double x, double y, double z) {
        this.x -= x;
        this.y -= y;
        this.z -= z;
        return this;
    }

    public Block getBlock() {
        return Minecraft.getMinecraft().theWorld.getBlockState(this.toBlockPos()).getBlock();
    }

    public double getX() {
        return this.x;
    }

    public Location2 setX(double x) {
        this.x = x;
        return this;
    }

    public double getY() {
        return this.y;
    }

    public Location2 setY(double y) {
        this.y = y;
        return this;
    }

    public double getZ() {
        return this.z;
    }

    public Location2 setZ(double z) {
        this.z = z;
        return this;
    }

    public float getYaw() {
        return this.yaw;
    }

    public Location2 setYaw(float yaw) {
        this.yaw = yaw;
        return this;
    }

    public float getPitch() {
        return this.pitch;
    }

    public Location2 setPitch(float pitch) {
        this.pitch = pitch;
        return this;
    }

    public static Location2 fromBlockPos(BlockPos blockPos) {
        return new Location2(blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    public BlockPos toBlockPos() {
        return new BlockPos(this.getX(), this.getY(), this.getZ());
    }

    public double distanceTo(Location2 loc) {
        double dx = loc.x - this.x;
        double dz = loc.z - this.z;
        double dy = loc.y - this.y;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    public double distanceToY(Location2 loc) {
        double dy = loc.y - this.y;
        return Math.sqrt(dy * dy);
    }
}