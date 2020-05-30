package complex.event.impl;

import complex.event.Event;
import net.minecraft.util.MovementInput;

public class EventMove extends Event {
    public double x;
    public double y;
    public double z;

    public EventMove(final double x, final double y, final double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public void setX(final double x) {
        this.x = x;
    }

    public void setY(final double y) {
        this.y = y;
    }

    public void setZ(final double z) {
        this.z = z;
    }

//    public void setSpeed(double speed) {
//        double forward = MovementInput.moveForward;
//        double strafe = MovementInput.moveStrafe;
//        float yaw = mc.thePlayer.rotationYaw;
//        if (forward == 0.0D && strafe == 0.0D) {
//            setX(0.0D);
//            setZ(0.0D);
//        } else {
//            if (forward != 0.0D) {
//                if (strafe > 0.0D) {
//                    yaw += ((forward > 0.0D) ? -45 : 45);
//                } else if (strafe < 0.0D) {
//                    yaw += ((forward > 0.0D) ? 45 : -45);
//                }
//                strafe = 0.0D;
//                if (forward > 0.0D) {
//                    forward = 1.0D;
//                } else {
//                    forward = -1.0D;
//                }
//            }
//            setX(forward * speed * Math.cos(Math.toRadians((yaw + 90.0F))) + strafe * speed * Math.sin(Math.toRadians((yaw + 90.0F))));
//            setZ(forward * speed * Math.sin(Math.toRadians((yaw + 90.0F))) - strafe * speed * Math.cos(Math.toRadians((yaw + 90.0F))));
//        }
//    }
}
