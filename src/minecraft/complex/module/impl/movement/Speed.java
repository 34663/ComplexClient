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
import complex.utils.misc.MiscUtils;
import complex.utils.timer.Timer3;
import complex.utils.timer.TimerUtil;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.Blocks;
import net.minecraft.potion.Potion;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.input.Keyboard;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class Speed extends Module {
    private TimerUtil time = new TimerUtil();
    private double[] values = new double[]{0.08D, 0.09316090325960147D, 1.35D, 2.149D, 0.66D};
    private int level = 1;
    private String mode;
    private double moveSpeed;
    private double achoQueEle;
    private double lastDist;
    private int stage;
    private double speed;
    private Timer3 groundTimer = new Timer3();
    private int uq;
    private double uu;
    private int amp;
    private boolean addTimer, addSpeed;
    private int ticks;
    private double protick;
    private double prop;
    private int counter = 0;

    public Speed() {
        super("Speed", null, Keyboard.KEY_M, 0, Category.Movement);
    }

    @Override
    public void setup() {
        ArrayList<String> options = new ArrayList<>();
        options.add("Hypixel");
        options.add("Hypixel2");
        options.add("NCP");
        options.add("NCP2");
        options.add("NCP3");
        options.add("NCP4");
        options.add("NCPYPort");
        options.add("OnGround");
        options.add("Cube1");
        options.add("Cube2");
        options.add("CubeTest");
        options.add("Mineplex");
        options.add("Bhop");
        options.add("Dev");
        Complex.getSettingsManager().rSetting(new Setting("Speed Mode", this, "Hypixel", options));
    }

    @Override
    public void onEnable() {
        mode = Complex.getSettingsManager().getSettingByName("Speed Mode").getValString();
        lastDist = 0;
        level = 1;
        moveSpeed = MoveUtils.getBaseMoveSpeed() * 2.145;
        stage = 1;
        uq = 0;

        if (mode.equalsIgnoreCase("NCPFHop")) {
            mc.timer.timerSpeed = 1.0866F;
        }
        super.onEnable();
    }

    @Override
    public void onDisable() {
        mc.thePlayer.speedInAir = 0.02F;
        mc.timer.timerSpeed = 1.0f;
        stage = 1;
        this.moveSpeed = MoveUtils.getBaseMoveSpeed();
        super.onDisable();
    }

    @EventTarget
    public void onUpdate(EventUpdate em) {
        mode = Complex.getSettingsManager().getSettingByName("Speed Mode").getValString();
        final boolean b = this.mc.thePlayer.ticksExisted % 2 == 0;

        if (mode.equalsIgnoreCase("Hypixel")) {
            this.setDisplayName("Speed §7Hypixel");
        } else if (mode.equalsIgnoreCase("NCP")) {
            this.setDisplayName("Speed §7NCP");
        } else if (mode.equalsIgnoreCase("NCP2")) {
            this.setDisplayName("Speed §7NCP2");
        } else if (mode.equalsIgnoreCase("OnGround")) {
            this.setDisplayName("Speed §7OnGround");
            if (em.isPre() && !Complex.getModuleManager().isEnabled("Fly")) {
                mc.timer.timerSpeed = 1.085f;
                double forward = mc.thePlayer.movementInput.moveForward;
                double strafe = mc.thePlayer.movementInput.moveStrafe;
                if ((forward != 0 || strafe != 0) && !mc.thePlayer.isJumping && !mc.thePlayer.isInWater() && !mc.thePlayer.isOnLadder() && (!mc.thePlayer.isCollidedHorizontally)) {
                    if (!mc.theWorld.getEntitiesWithinAABBExcludingEntity(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(0.0D, 0.4d, 0.0D)).isEmpty()) {
                        em.setY(mc.thePlayer.posY + (mc.thePlayer.ticksExisted % 2 != 0 ? 0.2 : 0));
                    } else {
                        em.setY(mc.thePlayer.posY + (mc.thePlayer.ticksExisted % 2 != 0 ? 0.4198 : 0));
                    }
                }
                speed = Math.max(mc.thePlayer.ticksExisted % 2 == 0 ? 2.1 : 1.3, MoveUtils.getBaseMoveSpeed());
                setSpeed(speed);
            }
        } else if (em.isPre() && mode.equalsIgnoreCase("CubeTest")) {
            this.setDisplayName("Speed §7CubeTest");
            this.mc.thePlayer.cameraYaw = 0.0f;
            if (em.isPre() && PlayerUtil.isMoving()) {
                if (this.mc.thePlayer.onGround && !b) {
                    em.setY(em.getY() + 0.41248);
                }
                if (b && !this.mc.thePlayer.onGround) {
                    this.mc.thePlayer.motionY = -1.02345234623;
                }
                this.uu *= (b ? 2.12542 : 0.905);
            }
        } else if (mode.equalsIgnoreCase("Bhop")) {
            this.setDisplayName("Speed §7Bhop");
        } else if (mode.equalsIgnoreCase("NCP3")) {
            this.setDisplayName("Speed §7NCP3");
            if (!mc.thePlayer.onGround) {
                mc.thePlayer.setSprinting(true);  // this doesn't rly work i don't know why
            }
            if (mc.thePlayer.isPotionActive(Potion.getPotionById(1))) {
                amp = mc.thePlayer.getActivePotionEffect(Potion.getPotionById(1)).getAmplifier();
            }
            if (mc.thePlayer.movementInput.moveForward != 0 || mc.thePlayer.movementInput.moveStrafe != 0) {
                mc.timer.timerSpeed = 1.08F;
                if (mc.thePlayer.onGround) {
                    mc.thePlayer.jump();
                    mc.timer.timerSpeed = 1.16F;
                }
            } else {
                mc.timer.timerSpeed = 1;
            }
            double moveSpeed = (MathHelper.sqrt_double(mc.thePlayer.motionX * mc.thePlayer.motionX + mc.thePlayer.motionZ * mc.thePlayer.motionZ));
            if (addSpeed) {
                switch (amp) { // is above 5 necessary? I'll try to do it sometime anyway but this is a workaround for now and should cover most speed effects you'll be seeing on servers
                    case 0:
                        moveSpeed = 0.31; //0.31 +6 +6 +
                        break;
                    case 1:
                        moveSpeed = 0.37; // 0.37 - previous value
                        break;
                    case 2:
                        moveSpeed = 0.43; // 0.41
                        break;
                    case 3:
                        moveSpeed = 0.49; // 0.45
                        break;
                    case 4:
                        moveSpeed = 0.54; // 0.49
                        break;
                    case 5:
                        moveSpeed = 0.60; // 0.53
                        break;
                    default:
                        //everything i do here flags ncp when turning Y_Y
                }
            }
            double forward = mc.thePlayer.movementInput.moveForward;
            double strafe = mc.thePlayer.movementInput.moveStrafe;
            double yaw = mc.thePlayer.rotationYaw;
            if (forward == 0 && strafe == 0) {
                mc.thePlayer.motionX = 0;
                mc.thePlayer.motionZ = 0;
            } else if (forward != 0) {
                if (strafe >= 1.0) {
                    yaw = mc.thePlayer.rotationYaw + ((forward > 0.0) ? -45 : 45);
                    strafe = 0;
                } else if (strafe <= -1.0) {
                    yaw = mc.thePlayer.rotationYaw + ((forward > 0.0) ? 45 : -45);
                    strafe = 0;
                }
            }

            if (forward > 0) {
                forward = 1;
            } else if (forward < 0) {
                forward = -1;
            }
            double mx = Math.cos(Math.toRadians(yaw + 90));
            double mz = Math.sin(Math.toRadians(yaw + 90));
            mc.thePlayer.jumpMovementFactor = 0.029F;
            mc.thePlayer.motionX = forward * moveSpeed * mx + strafe * moveSpeed * mz;
            mc.thePlayer.motionZ = forward * moveSpeed * mz - strafe * moveSpeed * mx;
            if (mc.thePlayer.motionY >= 0.32) {
                mc.thePlayer.motionY = 0.4;
                addTimer = false;
            }
        } else if (mode.equalsIgnoreCase("NCP4")) {
            this.setDisplayName("Speed §7NCP4");
            this.NCPBhop();
        } else if (mode.equalsIgnoreCase("NCPYPort")) {
            this.setDisplayName("Speed §7NCPYPort");
            this.NCPYPort();
        } else if (mode.equalsIgnoreCase("Cube2")) {
            this.setDisplayName("Speed §7Cube2");
            this.mc.gameSettings.keyBindJump.pressed = false;
            if (this.mc.thePlayer.onGround) {
                if (this.counter < 2) {
                    if (this.counter == 1) {
                        setSpeed(0.001D);
                    } else {
                        setSpeed(1.5D);
                    }
                    this.counter++;
                } else {
                    this.counter = 0;
                }
            }
        } else if (mode.equalsIgnoreCase("Mineplex")) {
            this.setDisplayName("Speed §7Mineplex");
        } else if (mode.equalsIgnoreCase("Dev")) {
            this.setDisplayName("Speed §7Dev");
        }
        if (em.isPre()) {
            double xDist = mc.thePlayer.posX - mc.thePlayer.lastTickPosX;
            double zDist = mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ;
            this.lastDist = Math.sqrt(xDist * xDist + zDist * zDist) * 0.99309375D;
        }
    }
    @EventTarget
    public void onMove(EventMove eventMove) {
        if (mode.equalsIgnoreCase("Hypixel")) {
            if (!this.mc.gameSettings.keyBindJump.isKeyDown()) {
                if (this.stage == 3) {
                    double difference = 0.66D * (this.lastDist - MoveUtils.getBaseMoveSpeed());
                    this.moveSpeed = this.lastDist - difference;
                } else {
                    this.moveSpeed = this.lastDist - this.lastDist / 159.0D;
                }

                if (PlayerUtil.isMoving() && this.mc.thePlayer.onGround && this.stage > 1) {
                    if (this.groundTimer.hasReached(60L)) {

                        double motionY = 0.4085;
                        if (this.mc.thePlayer.isPotionActive(Potion.getPotionById(8))) {
                            motionY += (double) ((float) (this.mc.thePlayer.getActivePotionEffect(Potion.getPotionById(8)).getAmplifier() + 1)) * 0.1D;
                        }
                        this.mc.thePlayer.motionY = motionY;
                        eventMove.setY(this.mc.thePlayer.motionY);

                        this.groundTimer.reset();
                        this.moveSpeed *= 2.149D;
                        this.stage = 2;
                    } else {
                        this.moveSpeed = 0D;
                    }
                } else {
                    this.groundTimer.reset();
                }
            }

            this.moveSpeed = Math.max(moveSpeed, MoveUtils.getBaseMoveSpeed());
            MoveUtils.setMotion(eventMove, moveSpeed);
            stage++;
        } else if (mode.equalsIgnoreCase("Hypixel2")) {
            if (time.delay(2000)) {
                Command.sendChatMessageInfo("ゴミモジュール");
            }
            if (!mc.thePlayer.isMoving())
                return;
            this.prop = 0.0D;
            this.prop = mc.thePlayer.getBaseSpeed() * 2.25D;
            if (this.protick == 1.0D) {
                this.prop = this.lastDist - 0.66D * (this.lastDist - mc.thePlayer.getBaseSpeed());
            } else {
                this.prop = this.lastDist - mc.thePlayer.getBaseSpeed() / 144.0D;
                MoveUtils.setMotion(Math.max(this.prop, mc.thePlayer.getBaseSpeed()));
                if (mc.thePlayer.onGround)
                    eventMove.setY((float) (mc.thePlayer.motionY = 0.41999998688697815D));
                this.protick = 0.0D;
            }
            MoveUtils.setMotion(eventMove, Math.max(this.prop, mc.thePlayer.getBaseSpeed()));
            this.protick++;
        } else if (mode.equalsIgnoreCase("NCP")) {
            if (stage == 3) {
                double difference = 0.72D * (this.lastDist - MoveUtils.getBaseMoveSpeed());
                this.moveSpeed = this.lastDist - difference;
            } else {
                this.moveSpeed = this.lastDist - this.lastDist / 159.0D;
            }

            if (PlayerUtil.isMoving() && mc.thePlayer.onGround) {
                mc.thePlayer.motionY = 0.42D;
                eventMove.setY(mc.thePlayer.motionY);
                groundTimer.reset();
                this.moveSpeed *= 2.149D;
                stage = 2;
            }

            this.moveSpeed = Math.max(moveSpeed, MoveUtils.getBaseMoveSpeed());
            MoveUtils.setMotion(eventMove, moveSpeed);
            stage++;
        } else if (mode.equalsIgnoreCase("NCP2")) {
            if (mc.thePlayer.isSneaking()) {
                return;
            }
            if (mc.thePlayer.onGround) {
                this.level = 2;
            }

            if (round(mc.thePlayer.posY - (double) ((int) mc.thePlayer.posY), 3) == round(0.138D, 3)) {
                EntityPlayerSP var10000 = mc.thePlayer;
                var10000.motionY -= this.values[0];
                eventMove.y -= this.values[1];
                var10000 = mc.thePlayer;
                var10000.posY -= this.values[1];
            }

            if (this.level != 1 || mc.thePlayer.moveForward == 0.0F && mc.thePlayer.moveStrafing == 0.0F) {
                if (this.level == 2) {
                    this.level = 3;
                    if (mc.thePlayer.moveForward != 0.1F || mc.thePlayer.moveStrafing != 0.1F) {
                        mc.thePlayer.motionY = 0.4D;
                        eventMove.y = 0.4D;
                        moveSpeed *= this.values[3];
                    }
                } else if (this.level == 3) {
                    this.level = 4;
                    double difference = this.values[4] * (this.lastDist - MoveUtils.getBaseMoveSpeed());
                    moveSpeed = this.lastDist - difference;
                } else {
                    if (mc.thePlayer.onGround && (mc.theWorld.getEntitiesWithinAABBExcludingEntity(mc.thePlayer, mc.thePlayer.boundingBox.offset(0.0D, mc.thePlayer.motionY, 0.0D)).size() > 0 || mc.thePlayer.isCollidedVertically)) {
                        this.level = 1;
                    }

                    moveSpeed = this.lastDist - this.lastDist / 159.0D;
                }
            } else {
                this.level = 2;
                moveSpeed = this.values[2] * MoveUtils.getBaseMoveSpeed() - 0.01D;
            }

            moveSpeed = Math.max(moveSpeed, MoveUtils.getBaseMoveSpeed());
            MoveUtils.setMotion(eventMove, moveSpeed);
        } else if (mode.equalsIgnoreCase("Mineplex")) {
            double n5 = 0.0;
            this.mc.timer.timerSpeed = 1.0f;
            ++this.uq;
            if (this.mc.thePlayer.isCollidedHorizontally) {
                this.uq = 50;
            }
            if (this.mc.thePlayer.onGround && (this.mc.thePlayer.moveForward != 0.1f || this.mc.thePlayer.moveStrafing != 0.1f)) {
                this.mc.timer.timerSpeed = 3.0f;
                this.mc.thePlayer.jump();
                eventMove.setY(this.mc.thePlayer.motionY = 0.42);
                this.uq = 0;
                n5 = 0.0;
            }
            if (!this.mc.thePlayer.onGround) {
                if (this.mc.thePlayer.motionY > -0.38) {
                    final EntityPlayerSP dzl = this.mc.thePlayer;
                    dzl.motionY += 0.023;
                } else {
                    final EntityPlayerSP dzl2 = this.mc.thePlayer;
                    dzl2.motionY += 0.01;
                }
                n5 = 0.8 - this.uq * 0.006;
                if (n5 < 0.0) {
                    n5 = 0.0;
                }
            }
            MoveUtils.setMotion(eventMove, n5);
        } else if (mode.equalsIgnoreCase("CubeTest")) {
            if ((this.mc.thePlayer.moveForward != 0.1f || this.mc.thePlayer.moveStrafing != 0.1f) && (!this.mc.thePlayer.onGround || !this.mc.thePlayer.isCollidedVertically)) {
                if (MiscUtils.c(this.mc.thePlayer.posY - (int) this.mc.thePlayer.posY, 3) == MiscUtils.c(0.4, 3)) {
                    eventMove.setY(this.mc.thePlayer.motionY = 0.0352);
                    MoveUtils.setMotion(eventMove, 1.95);
                }
                if (MiscUtils.c(this.mc.thePlayer.posY - (int) this.mc.thePlayer.posY, 3) == MiscUtils.c(0.6, 3)) {
                    eventMove.setY(this.mc.thePlayer.motionY = -0.0362);
                    MoveUtils.setMotion(eventMove, 1.65);
                }
                if (MiscUtils.c(this.mc.thePlayer.posY - (int) this.mc.thePlayer.posY, 3) == MiscUtils.c(0.4, 3)) {
                    eventMove.setY(this.mc.thePlayer.motionY = -0.0363);
                    MoveUtils.setMotion(eventMove, 1.95);
                    return;
                }
            } else {
                if (this.mc.thePlayer.moveForward != 0.1f || this.mc.thePlayer.moveStrafing != 0.1f) {
                    float n6 = 0.4001f;
                    if (this.mc.thePlayer.onGround) {
                        if (this.mc.thePlayer.isPotionActive(Potion.getPotionById(8))) {
                            n6 += (this.mc.thePlayer.getActivePotionEffect(Potion.getPotionById(8)).getAmplifier() + 1) * 0.099f;
                        }
                        eventMove.setY(this.mc.thePlayer.motionY = n6);
                    }
                    MoveUtils.setMotion(eventMove, 0.1576);
                    return;
                }
            }
        } else if (mode.equalsIgnoreCase("Cube1")) {
            MoveUtils.setMotion(eventMove, 2.2);
            this.mc.timer.timerSpeed = 0.32f;
        } else if (mode.equalsIgnoreCase("Bhop")) {
            if (MovementInput.moveForward == 0.0f && MovementInput.moveStrafe == 0.0f) {
                speed = MoveUtils.getBaseMoveSpeed();
            }
            if (stage == 1 && PlayerUtil.isMoving()) {
                speed = 1.35 + MoveUtils.getBaseMoveSpeed() - 0.01;
            }
            if (!PlayerUtil.isInLiquid() && stage >= 2 && mc.thePlayer.isCollidedVertically && mc.thePlayer.onGround && PlayerUtil.isMoving()) {
                if (mc.thePlayer.isPotionActive(Potion.getPotionById(8))) {
                    eventMove.setY(mc.thePlayer.motionY = 0.41999998688698 + (mc.thePlayer.getActivePotionEffect(Potion.getPotionById(8)).getAmplifier() + 1) * 0.1);
                } else {
                    eventMove.setY(mc.thePlayer.motionY = 0.41999998688698);
                }
                mc.thePlayer.jump();
                speed *= 2.149D;
            } else if (stage >= 3) {
                final double difference = 0.66 * (lastDist - MoveUtils.getBaseMoveSpeed());
                speed = lastDist - difference;
            } else {
                final List collidingList = mc.theWorld.getEntitiesWithinAABBExcludingEntity(mc.thePlayer, mc.thePlayer.boundingBox.offset(0.0, mc.thePlayer.motionY, 0.0));
                if ((collidingList.size() > 0 || mc.thePlayer.isCollidedVertically) && stage > 0) {
                    stage = ((MovementInput.moveForward != 0.0F || MovementInput.moveStrafe != 0.0F) ? 1 : 0);
                }
                speed = lastDist - lastDist / 159.0;
            }
            speed = Math.max(speed, MoveUtils.getBaseMoveSpeed());

            if (stage > 0) {
                if (PlayerUtil.isInLiquid())
                    speed = 0.1;
                MoveUtils.setMotion(eventMove, speed);
            }
            if (PlayerUtil.isMoving()) {
                ++stage;
            }
        }
    }

    public float a(final double n, final double n2) {
        return (float)(Math.atan2(n2 - this.mc.thePlayer.posZ, n - this.mc.thePlayer.posX) * 180.0 / 3.141592653589793) - 90.0f;
    }
    public boolean c() {
        for (int i = (int)Math.ceil(this.mc.thePlayer.posY); i >= 0; --i) {
            if (this.mc.theWorld.getBlockState(new BlockPos(this.mc.thePlayer.posX, i, this.mc.thePlayer.posZ)).getBlock() != Blocks.AIR) {
                return false;
            }
        }
        return true;
    }
    private double d() {
        double n = 0.2873;
        if (this.mc.thePlayer.isPotionActive(Potion.getPotionById(1))) {
            n *= 1.0 + 0.2 * this.mc.thePlayer.getActivePotionEffect(Potion.getPotionById(1)).getAmplifier();
        }
        return n;
    }
    public static double round(double value, int places) {
        if(places < 0) {
            throw new IllegalArgumentException();
        } else {
            BigDecimal bd = new BigDecimal(value);
            bd = bd.setScale(places, RoundingMode.HALF_UP);
            return bd.doubleValue();
        }
    }

    public void NCPYPort() {
        ++this.ticks;
        if (PlayerUtil.isMoving()) {
            if ((Speed.mc.thePlayer.moveForward != 0.1f || Speed.mc.thePlayer.moveStrafing != 0.1f) && !Speed.mc.thePlayer.onGround && !Speed.mc.thePlayer.isInWater() && !Speed.mc.thePlayer.isInWeb && !Speed.mc.thePlayer.isCollidedHorizontally) {
                final EntityPlayerSP thePlayer = Speed.mc.thePlayer;
                thePlayer.posY -= 0.4000000059604645;
                Speed.mc.thePlayer.motionY = -10.0;
                Speed.mc.timer.timerSpeed = 1.07f;
            }
            if (Speed.mc.thePlayer.onGround && (Speed.mc.thePlayer.moveForward != 0.1f || Speed.mc.thePlayer.moveStrafing != 0.1f)) {
                Speed.mc.thePlayer.jump();
                Speed.mc.thePlayer.motionY = 0.4000000059604645;
                final EntityPlayerSP thePlayer2 = Speed.mc.thePlayer;
                thePlayer2.motionX *= 0.75;
                final EntityPlayerSP thePlayer3 = Speed.mc.thePlayer;
                thePlayer3.motionZ *= 0.75;
            }
        }
    }
    public void NCPBhop() {
        if (PlayerUtil.isMoving()) {
            if (Speed.mc.thePlayer.moveForward != 0.1f || Speed.mc.thePlayer.moveStrafing != 0.1f) {
                if (!Speed.mc.thePlayer.onGround) {
                    float forward = 0.0f;
                    float strafe = 0.0f;
                    final double speed = 0.25 + Speed.mc.thePlayer.jumpMovementFactor;
                    final float var5 = MathHelper.sin(Speed.mc.thePlayer.rotationYaw * 3.1415927f / 180.0f);
                    final float var6 = MathHelper.cos(Speed.mc.thePlayer.rotationYaw * 3.1415927f / 180.0f);
                    MoveUtils.setMotion(speed);
                }
                if (Speed.mc.thePlayer.onGround && (Speed.mc.thePlayer.moveForward != 0.1f || Speed.mc.thePlayer.moveStrafing != 0.1f)) {
                    if (Speed.mc.thePlayer.hurtResistantTime == 0) {
                        Speed.mc.thePlayer.jump();
                        Speed.mc.thePlayer.motionY = 0.4000000059604645;
                    }
                    Speed.mc.timer.timerSpeed = 1.07f;
                }
            }
        }
    }
    public float getDirection() {
        float var1 = this.mc.thePlayer.rotationYaw;
        if (this.mc.thePlayer.moveForward < 0.0F)
            var1 += 180.0F;
        float forward = 1.0F;
        if (this.mc.thePlayer.moveForward < 0.0F) {
            forward = -0.5F;
        } else if (this.mc.thePlayer.moveForward > 0.0F) {
            forward = 0.5F;
        }
        if (this.mc.thePlayer.moveStrafing > 0.0F)
            var1 -= 90.0F * forward;
        if (this.mc.thePlayer.moveStrafing < 0.0F)
            var1 += 90.0F * forward;
        var1 *= 0.017453292F;
        return var1;
    }
    public static void setSpeed(double speed) {
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
