package complex.module.impl.movement;

import complex.Complex;
import complex.command.Command;
import complex.event.EventTarget;
import complex.event.impl.EventMove;
import complex.event.impl.EventUpdate;
import complex.module.Category;
import complex.module.Module;
import complex.module.data.Setting;
import complex.utils.MoveUtils;
import complex.utils.PlayerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.MovementInput;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

public class LongJump extends Module {
    private double moveSpeed;
    private int stage, count;
    private double lastDist;
    private String mode;

    public LongJump() {
        super("LongJump", null, 0, 0, Category.Movement);
    }

    @Override
    public void setup() {
        ArrayList<String> options = new ArrayList<>();
        options.add("NCP");
        options.add("Cube");
        options.add("Cube2");
        options.add("AACDev");
        Complex.getSettingsManager().rSetting(new Setting("Jump Mode", this, "NCP", options));
        Complex.getSettingsManager().rSetting(new Setting("JumpDamage", this, true));
    }

    @Override
    public void onEnable() {
        mode = Complex.getSettingsManager().getSettingByName("Jump Mode").getValString();
        stage = 0;
        if (mc.thePlayer.onGround) {
            if (Complex.getSettingsManager().getSettingByName("JumpDamage").getValBoolean()) {
                mc.thePlayer.motionZ = 0;
                mc.thePlayer.motionX = 0;
                final double[] offsets = new double[]{0.06D, 0.0001D};

                for (int i = 0; i < 52; i++) {
                    mc.getConnection().getNetworkManager().sendPacketNoEvent(new CPacketPlayer.Position(mc.thePlayer.posX, mc.thePlayer.posY + offsets[0], mc.thePlayer.posZ, false));
                    mc.getConnection().getNetworkManager().sendPacketNoEvent(new CPacketPlayer.Position(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
                    mc.getConnection().getNetworkManager().sendPacketNoEvent(new CPacketPlayer.Position(mc.thePlayer.posX, mc.thePlayer.posY + offsets[1], mc.thePlayer.posZ, false));
                }
                mc.getConnection().getNetworkManager().sendPacketNoEvent(new CPacketPlayer.Position(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));
            }
            if (mode.equalsIgnoreCase("Cube")) {
                this.mc.thePlayer.motionY = 0.399399995803833;
            } else if (mode.equalsIgnoreCase("Cube2")) {
                Command.sendChatMessageInfo("Place a block before landing.");
            }
        }
        super.onEnable();
    }
    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1f;
        MoveUtils.setMotion(0.2);
        super.onDisable();
    }

    @EventTarget
    public void onUpdate(EventUpdate em) {
        mode = Complex.getSettingsManager().getSettingByName("Jump Mode").getValString();

        if (mode.equalsIgnoreCase("NCP")) {
            this.setDisplayName("LongJump §7NCP");
            final double xDist = this.mc.thePlayer.posX - this.mc.thePlayer.prevPosX;
            final double zDist = this.mc.thePlayer.posZ - this.mc.thePlayer.prevPosZ;
            this.lastDist = Math.sqrt(xDist * xDist + zDist * zDist);
        } else if (mode.equalsIgnoreCase("Cube")) {
            this.setDisplayName("LongJump §7Cube");
        } else if (mode.equalsIgnoreCase("Cube2")) {
            this.setDisplayName("LongJump §7Cube2");
            if (this.mc.thePlayer.onGround) {
                if (this.mc.gameSettings.keyBindForward.pressed) {
                    this.mc.thePlayer.jump();
                }
            } else if (!PlayerUtil.isMoving()) {
                this.mc.timer.timerSpeed = 1.0F;
                this.mc.thePlayer.motionX = 0.0D;
                this.mc.thePlayer.motionZ = 0.0D;
            } else {
                this.mc.timer.timerSpeed = 0.2F;
                MoveUtils.setMotion(2.0D);
            }
        } else if (mode.equalsIgnoreCase("AACDev")) {
            this.setDisplayName("LongJump §7AACDev");
            if (mc.thePlayer.onGround) {
                mc.timer.timerSpeed = 1F;
                mc.thePlayer.jump();
                mc.thePlayer.motionY = 0.5;
                mc.thePlayer.speedInAir = 0.02F;
                mc.thePlayer.jumpMovementFactor = 0.02F;
            } else {
                mc.thePlayer.jumpMovementFactor = 0.48F;
                this.move(mc.thePlayer.rotationYaw, (float) 0.2);
                mc.timer.timerSpeed = 0.6F;
            }
        }
    }
    @EventTarget
    public void onMove(EventMove event) {
        if (mode.equalsIgnoreCase("NCP")) {
            final MovementInput movementInput = this.mc.thePlayer.movementInput;
            float forward = MovementInput.moveForward;
            float strafe = MovementInput.moveStrafe;
            float yaw = this.mc.thePlayer.rotationYaw;
            final double round = round(this.mc.thePlayer.posY - (int) this.mc.thePlayer.posY, 3);
            if (this.stage == 1 && (MovementInput.moveForward != 0.0f || MovementInput.moveStrafe != 0.0f)) {
                this.stage = 2;
                this.moveSpeed = 1.38 * getBaseMoveSpeed() - 0.01;
            } else if (this.stage == 2) {
                this.stage = 3;
                this.mc.thePlayer.motionY = 0.399399995803833;
                event.y = 0.399399995803833;
                this.moveSpeed *= 2.149;
            } else if (this.stage == 3) {
                this.stage = 4;
                final double difference = 0.66 * (this.lastDist - getBaseMoveSpeed());
                this.moveSpeed = (this.lastDist - difference) * 2.5;
            } else {
                if (this.mc.theWorld.getEntitiesWithinAABBExcludingEntity(this.mc.thePlayer, this.mc.thePlayer.boundingBox.offset(0.0, this.mc.thePlayer.motionY, 0.0)).size() > 0 || this.mc.thePlayer.isCollidedVertically) {
                    this.stage = 1;
                }
                this.moveSpeed = this.lastDist - this.lastDist / 159.0;
            }
            this.moveSpeed = Math.max(this.moveSpeed, getBaseMoveSpeed());
            if (forward == 0.0f && strafe == 0.0f) {
                event.x = 0.0;
                event.z = 0.0;
            } else if (forward != 0.0f) {
                if (strafe >= 1.0f) {
                    yaw += ((forward > 0.0f) ? -45 : 45);
                    strafe = 0.0f;
                } else if (strafe <= -1.0f) {
                    yaw += ((forward > 0.0f) ? 45 : -45);
                    strafe = 0.0f;
                }
                if (forward > 0.0f) {
                    forward = 1.0f;
                } else if (forward < 0.0f) {
                    forward = -1.0f;
                }
            }
            final double mx = Math.cos(Math.toRadians(yaw + 90.0f));
            final double mz = Math.sin(Math.toRadians(yaw + 90.0f));
            event.x = forward * this.moveSpeed * mx + strafe * this.moveSpeed * mz;
            event.z = forward * this.moveSpeed * mz - strafe * this.moveSpeed * mx;
        } else if (mode.equalsIgnoreCase("Cube")) {
            mc.timer.timerSpeed = MoveUtils.isOnGround(0.001) ? 0.75f : 0.6f;
            if (PlayerUtil.isMoving()) {
                count++;
                if (count == 1) {
                    MoveUtils.setMotion(2.1);
                } else if (count == 2) {
                    MoveUtils.setMotion(0);
                    if (!MoveUtils.isOnGround(0.001)) {
                        count = 0;
                    } else {
                        mc.timer.timerSpeed = 1;
                    }
                } else if (count >= 3) {
                    MoveUtils.setMotion(0);
                    count = 0;
                }
            } else {
                count = 0;
                MoveUtils.setMotion(0);
            }
        }
    }

    public static double getBaseMoveSpeed() {
        double baseSpeed = 0.2873;
        if (Minecraft.getMinecraft().thePlayer.isPotionActive(Potion.getPotionById(0))) {
            final int amplifier = Minecraft.getMinecraft().thePlayer.getActivePotionEffect(Potion.getPotionById(0)).getAmplifier();
            baseSpeed *= 1.0 + 0.2 * (amplifier + 1);
        }
        return baseSpeed;
    }
    public double round(final double value, final int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
    public void move(float yaw, float multiplyer) {
        double moveX = -Math.sin(Math.toRadians((double) yaw)) * (double) multiplyer;
        double moveZ = Math.cos(Math.toRadians((double) yaw)) * (double) multiplyer;
        this.mc.thePlayer.motionX = moveX;
        this.mc.thePlayer.motionZ = moveZ;
    }
}
