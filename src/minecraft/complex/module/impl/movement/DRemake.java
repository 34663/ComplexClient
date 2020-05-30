package complex.module.impl.movement;

import complex.event.EventTarget;
import complex.event.impl.EventUpdate;
import complex.module.Category;
import complex.module.Module;
import net.minecraft.util.MovementInput;

public class DRemake extends Module {
    int counter;

    public DRemake() {
        super("DRemake", null, 0, 0, Category.Movement);
    }

    @Override
    public void onEnable() {
        this.setDisplayName("DRemake");
        super.onEnable();
    }

    @EventTarget
    public void onUpdate(EventUpdate eventUpdate) {
        if (eventUpdate.isPre()) {
            mc.thePlayer.onGround = true;
            mc.thePlayer.cameraYaw = 0.1F;

            ++counter;
            if (MovementInput.moveForward == 0.0F && MovementInput.moveStrafe == 0.0F) {
                mc.thePlayer.setPosition(mc.thePlayer.posX + 1.0D, mc.thePlayer.posY + 1.0D, mc.thePlayer.posZ + 1.0D);
                mc.thePlayer.setPosition(mc.thePlayer.prevPosX, mc.thePlayer.prevPosY, mc.thePlayer.prevPosZ);
                mc.thePlayer.motionX = 0.0D;
                mc.thePlayer.motionZ = 0.0D;
            }
            if (counter == 2) {
                mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + -1.0E-10D, mc.thePlayer.posZ);
                counter = 0;
            }
            setSpeed(0.25);
        }
    }


    public void setSpeed(double speed) {
        MovementInput movementInput = mc.thePlayer.movementInput;
        float forward = movementInput.moveForward;
        float strafe = movementInput.moveStrafe;
        float yaw = mc.thePlayer.rotationYaw;
        if (forward == 0.0F && strafe == 0.0F) {
            mc.thePlayer.motionX = 0.0D;
            mc.thePlayer.motionZ = 0.0D;
        } else if (forward != 0.0F) {
            if (strafe >= 1.0F) {
                yaw += ((forward > 0.0F) ? -45 : 45);
                strafe = 0.0F;
            } else if (strafe <= -1.0F) {
                yaw += ((forward > 0.0F) ? 45 : -45);
                strafe = 0.0F;
            }
            if (forward > 0.0F) {
                forward = 1.0F;
            } else if (forward < 0.0F) {
                forward = -1.0F;
            }
        }
        double mx = Math.cos(Math.toRadians((yaw + 90.0F)));
        double mz = Math.sin(Math.toRadians((yaw + 90.0F)));
        double motionX = forward * speed * mx + strafe * speed * mz;
        double motionZ = forward * speed * mz - strafe * speed * mx;
        mc.thePlayer.motionX = motionX;
        mc.thePlayer.motionZ = motionZ;
    }
}
