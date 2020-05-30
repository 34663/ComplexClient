package complex.module.impl.movement;

import complex.Complex;
import complex.event.EventTarget;
import complex.event.impl.EventMove;
import complex.event.impl.EventUpdate;
import complex.module.Category;
import complex.module.Module;
import complex.module.data.Setting;
import complex.utils.MoveUtils;
import net.minecraft.util.MovementInput;

import java.util.ArrayList;

public class Glide extends Module {
    private String mode;
    private int counter;

    public Glide() {
        super("Glide", null, 0, 0, Category.Movement);
    }

    @Override
    public void setup() {
        ArrayList<String> options = new ArrayList<>();
        options.add("Shotbow");
        options.add("Dev");
        Complex.getSettingsManager().rSetting(new Setting("Glide Mode", this, "Dev", options));
    }

    @Override
    public void onEnable() {
        mode = Complex.getSettingsManager().getSettingByName("Glide Mode").getValString();
        if (mode.equalsIgnoreCase("Shotbow")) {
            MoveUtils.setMotion(0.3 + MoveUtils.getSpeedEffect() * 0.05f);
            if (mc.thePlayer.onGround)
                mc.thePlayer.motionY = 0.41999998688698f + MoveUtils.getJumpEffect() * 0.1;
            mc.thePlayer.onGround = false;
        }
        super.onEnable();
    }

    @EventTarget
    public void onUpdate(EventUpdate update) {
        mode = Complex.getSettingsManager().getSettingByName("Glide Mode").getValString();

        if (mode.equalsIgnoreCase("Shotbow")) {
            this.setDisplayName("Glide §7Shotbow");
            mc.thePlayer.cameraYaw = 0.15384614F;
            ++counter;
            if (MovementInput.moveForward == 0.0F && MovementInput.moveStrafe == 0.0F) {
                mc.thePlayer.setPosition(mc.thePlayer.posX + 1.0D, mc.thePlayer.posY + 1.0D, mc.thePlayer.posZ + 1.0D);
                mc.thePlayer.setPosition(mc.thePlayer.prevPosX, mc.thePlayer.prevPosY, mc.thePlayer.prevPosZ);
                mc.thePlayer.motionX = 0.0D;
                mc.thePlayer.motionZ = 0.0D;
            }
            mc.thePlayer.motionY = 0.0D;
            if (counter == 2 && !mc.thePlayer.onGround) {
                mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + -1.0E-10D, mc.thePlayer.posZ);
                counter = 0;
            }
            mc.thePlayer.onGround = false;
        } else if (mode.equalsIgnoreCase("Dev")) {
            this.setDisplayName("Glide §7Dev");
            mc.thePlayer.motionY = 0.0D;
            if (update.isPost() || !mc.thePlayer.isMoving())
                return;

            if (!mc.thePlayer.isCollidedVertically) {
                mc.thePlayer.setSprinting(false);
                mc.thePlayer.cameraYaw = 0.15384614F;
                if (mc.thePlayer.ticksExisted % 2 == 0) {
                    mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 1.28E-9D, mc.thePlayer.posZ);
                }
                if (mc.thePlayer.ticksExisted % 25 == 0 && !mc.thePlayer.onGround && !mc.thePlayer.isSwingInProgress) {
                    mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 1.28E-10D, mc.thePlayer.posZ);
                }
            }
        }
    }
    @EventTarget
    public void onMove(EventMove move) {
        if (mode.equalsIgnoreCase("Dev") || mode.equalsIgnoreCase("Shotbow")) {
            if (!mc.thePlayer.isMoving())
                return;
            MoveUtils.setMotion(mc.thePlayer.getBaseSpeed());
        }
    }
}