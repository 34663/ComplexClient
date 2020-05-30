package complex.module.impl.movement;

import complex.Complex;
import complex.command.Command;
import complex.event.EventTarget;
import complex.event.impl.*;
import complex.module.Category;
import complex.module.Module;
import complex.module.data.Setting;
import complex.utils.MoveUtils;
import complex.utils.PlayerUtil;
import complex.utils.timer.Timer3;
import complex.utils.timer.TimerUtil;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketInput;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.*;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;

public class Fly extends Module {
    TimerUtil timer = new TimerUtil();
    private String mode;
    private double moveSpeed, lastDist;
    private int counter, offsetcounter, stage, level;
    private boolean b2;
    private int sh;
    private double sf;
    private double sg;
    double postStage = 0;
    private double mineplexSpeed;
    private boolean back;
    private boolean mineplexRape;
    private boolean done;
    private double x, y, z;
    private double tickwait, tickwait2, tick, tick2;
    private Integer land;
    private final Timer3 timerTimer = new Timer3();

    public Fly() {
        super("Fly", null, Keyboard.KEY_F, 0, Category.Movement);
    }

    @Override
    public void setup() {
        ArrayList<String> options = new ArrayList<>();
        options.add("Hypixel");
        options.add("Float");
        options.add("NCP");
        options.add("Cube");
        options.add("ShotbowFast");
        options.add("ShotbowTest");
        options.add("ComuGamers");
        options.add("BruhMineplex");
        options.add("Vanilla");
        Complex.getSettingsManager().rSetting(new Setting("Fly Mode", this, "Hypixel", options));
        Complex.getSettingsManager().rSetting(new Setting("FlyBoost", this, true));
        Complex.getSettingsManager().rSetting(new Setting("onGround", this, false));
        Complex.getSettingsManager().rSetting(new Setting("FlySpeed", this, 5.0, 1.0, 10.0, false));
        Complex.getSettingsManager().rSetting(new Setting("FlyTimer", this, 1.0, 0.1, 3.0, false));
    }

    @Override
    public void onEnable() {
        mode = Complex.getSettingsManager().getSettingByName("Fly Mode").getValString();
        this.timerTimer.reset();
        counter = 0;
        offsetcounter = 0;
        stage = 1;
        lastDist = 0;
        mc.thePlayer.stepHeight = 0F;
        sg = 0;
        sf = getBaseMoveSpeed();
        sh = 0;
        postStage = 0;
        this.mineplexSpeed = 0.0D;
        this.mineplexRape = false;
        this.x = mc.thePlayer.posX;
        this.y = mc.thePlayer.posY;
        this.z = mc.thePlayer.posZ;
        this.done = false;
        this.back = true;

        if (mc.thePlayer.onGround && Complex.getSettingsManager().getSettingByName("FlyBoost").getValBoolean()) {
            if (mode.equalsIgnoreCase("Hypixel")) {
                mc.thePlayer.motionZ = 0;
                mc.thePlayer.motionX = 0;

                for (int index = 0; index < 49; index++) {
                    mc.getConnection().sendPacketNoEvent(new CPacketPlayer.Position(mc.thePlayer.posX, mc.thePlayer.posY + 0.06249D, mc.thePlayer.posZ, false));
                    mc.getConnection().sendPacketNoEvent(new CPacketPlayer.Position(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
                }
                mc.getConnection().sendPacketNoEvent(new CPacketPlayer.Position(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));
                MoveUtils.setMotion(0.3 + MoveUtils.getSpeedEffect() * 0.05f);
                ;
                mc.thePlayer.motionY = 0.41999998688698f + MoveUtils.getJumpEffect() * 0.1;
                mc.thePlayer.jumpMovementFactor = 0;
            } else if (mode.equalsIgnoreCase("Float")) {
                mc.thePlayer.motionZ = 0;
                mc.thePlayer.motionX = 0;
                MoveUtils.setMotion(0);
                mc.thePlayer.motionY = 0.41999998688698f + MoveUtils.getJumpEffect() * 0.1;
                mc.thePlayer.jumpMovementFactor = 0;
            } else if (mode.equalsIgnoreCase("ShotbowFast")) {
                if (mc.thePlayer == null || mc.theWorld == null) {
                    return;
                }
                level = 1;
                moveSpeed = 0.1D;
                b2 = true;
                lastDist = 0.0D;

                damagePlayer(2);
                MoveUtils.setMotion(0);
                mc.thePlayer.motionY = 0.41999998688698f + MoveUtils.getJumpEffect() * 0.1;
                mc.thePlayer.jumpMovementFactor = 0;
                timer.reset();
            } else if (mode.equalsIgnoreCase("ShotbowTest")) {
                if (mc.thePlayer == null || mc.theWorld == null) {
                    return;
                }

                level = 1;
                moveSpeed = 0.1D;
                b2 = true;
                lastDist = 0.0D;
                damagePlayer(2);
                MoveUtils.setMotion(0);
                mc.thePlayer.motionY = 0.41999998688698f + MoveUtils.getJumpEffect() * 0.1;
                mc.thePlayer.jumpMovementFactor = 0;
            }
        }
        if (mode.equalsIgnoreCase("NCP")) {
            double x = mc.thePlayer.posX;
            double y = mc.thePlayer.posY;
            double z = mc.thePlayer.posZ;

            if(!mc.thePlayer.onGround)
                return;

            for(int i = 0; i < 65; ++i) {
                mc.getConnection().sendPacket(new CPacketPlayer.Position(x, y + 0.049D, z, false));
                mc.getConnection().sendPacket(new CPacketPlayer.Position(x, y, z, false));
            }
            mc.getConnection().sendPacket(new CPacketPlayer.Position(x, y + 0.1D, z, true));
            mc.thePlayer.swingArm(EnumHand.MAIN_HAND);
        } else if (mode.equalsIgnoreCase("Cube")) {
            damagePlayer(2);
            mc.timer.timerSpeed = 0.3F;
            mc.thePlayer.speedInAir = 0.026F;
            this.tickwait = 6;
            this.tickwait2 = 6;
            this.tick = 0;
            this.tick2 = 0;
            this.land = 0;
        }
        super.onEnable();
    }

    @Override
    public void onDisable() {
        counter = 0;
        offsetcounter = 0;
        stage = 1;
        lastDist = 0;
        level = 1;
        moveSpeed = 0.1D;
        b2 = true;
        mc.timer.timerSpeed = 1.0F;
        MoveUtils.setMotion(mc.thePlayer.getBaseSpeed());
        mc.thePlayer.stepHeight = 0.625F;
        timer.reset();

        if (mode.equalsIgnoreCase("Cube")) {
            mc.thePlayer.motionY = -0.3;
            mc.thePlayer.motionX = 0.0;
            mc.thePlayer.motionZ = 0.0;
            mc.timer.timerSpeed = 1.0F;
            mc.thePlayer.speedInAir = 0.02F;
        }
        super.onDisable();
    }

    @EventTarget
    public void onUpdate(EventUpdate em) {
        mode = Complex.getSettingsManager().getSettingByName("Fly Mode").getValString();

        if (mode.equalsIgnoreCase("Hypixel")) {
            this.setDisplayName("Fly §7Hypixel");
        } else if (mode.equalsIgnoreCase("NCP")) {
            this.setDisplayName("Fly §7NCP");
        } else if (mode.equalsIgnoreCase("Vanilla")) {
            this.setDisplayName("Fly §7Vanilla");
        } else if (mode.equalsIgnoreCase("Cube")) {
            this.setDisplayName("Fly §7Cube");
        } else if (mode.equalsIgnoreCase("ShotbowFast")) {
            this.setDisplayName("Fly §7ShotbowFast");
        } else if (mode.equalsIgnoreCase("ShotbowTest")) {
            this.setDisplayName("Fly §7ShotbowTest");
        } else if (mode.equalsIgnoreCase("Float")) {
            this.setDisplayName("Fly §7Float");
        } else if (mode.equalsIgnoreCase("ComuGamers")) {
            this.setDisplayName("Fly §7ComuGamers");
        }
        if (!em.isPre() && Complex.getSettingsManager().getSettingByName("FlyBoost").getValBoolean()) {
            final double n = this.mc.thePlayer.posX - this.mc.thePlayer.prevPosX;
            final double n2 = this.mc.thePlayer.posZ - this.mc.thePlayer.prevPosZ;
            this.sg = Math.sqrt(n * n + n2 * n2);
        }
    }
    @EventTarget
    public void onMove(EventMove event) {
        if (mode.equalsIgnoreCase("Hypixel") || mode.equalsIgnoreCase("Float")) {
            if (Complex.getSettingsManager().getSettingByName("FlyBoost").getValBoolean()) {
                List<Entity> collidingList = mc.theWorld.getEntitiesWithinAABBExcludingEntity(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(0, 0, 0));
                double boost = mc.thePlayer.isPotionActive(Potion.getPotionById(0)) ? 1.85D : 2.12;
                switch (stage) {
                    case 1:
                        moveSpeed = boost * MoveUtils.getBaseMoveSpeed();
                        break;
                    case 2:
                        moveSpeed *= boost;
                        break;
                    default:
                        if (collidingList.size() > 0 || mc.thePlayer.isCollidedVertically) {
                            stage = 1;
                        }
                        moveSpeed = lastDist;
                        break;
                }
                setMoveSpeed(event, Math.max(MoveUtils.getBaseMoveSpeed(), moveSpeed));
            } else {
                setMoveSpeed(event, getBaseMoveSpeed());
            }
        } else if (mode.equalsIgnoreCase("Vanilla")) {
            MoveUtils.setMotion(Complex.getSettingsManager().getSettingByName("FlySpeed").getValDouble());
        } else if (mode.equalsIgnoreCase("ShotbowFast") || mode.equalsIgnoreCase("ShotbowTest")) {
            if (Complex.getSettingsManager().getSettingByName("FlyBoost").getValBoolean()) {
                float forward = MovementInput.moveForward;
                float strafe = MovementInput.moveStrafe;
                float yaw = mc.thePlayer.rotationYaw;
                double mx = Math.cos(Math.toRadians((double) (yaw + 90.0F)));
                double mz = Math.sin(Math.toRadians((double) (yaw + 90.0F)));

                if (b2) {
                    if (level != 1 || MovementInput.moveForward == 0.0F && MovementInput.moveStrafe == 0.0F) {
                        if (level == 2) {
                            level = 3;
                            moveSpeed *= 2.1499999D;
                            mc.timer.timerSpeed = 1.0f;
                        } else if (level == 3) {
                            level = 4;
                            double difference = (mc.thePlayer.ticksExisted % 2 == 0 ? 0.0103D : 0.0123D) * (lastDist - getBaseMoveSpeed());
                            moveSpeed = lastDist - difference;
                        } else {
                            moveSpeed = lastDist - lastDist / 159.0D;
                            if (moveSpeed <= 0.600 && moveSpeed >= 0.400) {
                                mc.timer.elapsedPartialTicks = 0.22F;
                            }
                        }
                    } else {
                        level = 2;
                        double boost = mc.thePlayer.isPotionActive(Potion.getPotionById(1)) ? 1.85D : 2.12;
                        moveSpeed = boost * getBaseMoveSpeed();
                    }
                    moveSpeed = Math.max(moveSpeed, getBaseMoveSpeed());
                    setMoveSpeed(event, moveSpeed);
                    if (forward == 0.0F && strafe == 0.0F) {
                        setMoveSpeed(event, 0.0F);
                    }
                }
            } else {
                setMoveSpeed(event, getBaseMoveSpeed());
            }
        } else if (mode.equalsIgnoreCase("ComuGamers")) {
            if (!this.mineplexRape && this.mc.thePlayer.onGround && PlayerUtil.isMoving()) {
                this.mc.timer.timerSpeed = (float) 1.0D;
                MoveUtils.setMotion(this.back ? -this.mineplexSpeed : this.mineplexSpeed);
                this.mineplexSpeed += 0.046D;
                this.back = !this.back;
                if (this.mineplexSpeed >= 2.5D) {
                    MoveUtils.setMotion(0.0D);
                    event.setY(this.mc.thePlayer.motionY = 0.425D);
                    this.mineplexRape = true;
                }
            } else {
                this.mc.timer.timerSpeed = (float) 0.30000001192092896D;
                this.mineplexSpeed *= 0.98D;
                event.setY(this.mc.thePlayer.motionY += 0.036D);
                if (this.mc.thePlayer.fallDistance < 1.3D) {
                    event.setY(this.mc.thePlayer.motionY += 0.0255D);
                }
                MoveUtils.setMotion(this.mineplexSpeed);
            }
        } else if (mode.equalsIgnoreCase("BruhMineplex")) {
            if (PlayerUtil.airSlot() == -10) {
                return;
            }
            if (!this.done && mc.thePlayer.onGround && PlayerUtil.isMoving()) {
                mc.timer.timerSpeed = 1.0F;
                mc.getConnection().sendPacket(new CPacketHeldItemChange(PlayerUtil.airSlot()));
                BlockPos blockPos = new BlockPos(mc.thePlayer.posX, (mc.thePlayer.getEntityBoundingBox()).minY - 1.0D, mc.thePlayer.posZ);
                Vec3d vec = (new Vec3d(blockPos)).addVector(0.4000000059604645D, 0.4000000059604645D, 0.4000000059604645D);
                mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, blockPos, EnumFacing.UP, new Vec3d(vec.xCoord * 0.4000000059604645D, vec.yCoord * 0.4000000059604645D, vec.zCoord * 0.4000000059604645D));
                if (mc.thePlayer.ticksExisted % 2 == 0) {
                    this.mineplexSpeed += 0.115D;
                }
                MoveUtils.setMotion(this.back ? -this.mineplexSpeed : this.mineplexSpeed);
                this.back = !this.back;
                if (this.mineplexSpeed >= 2.5D) {
                    Command.sendChatMessage((new CPacketInput()).toString());
                    Command.sendChatMessage("sent packet");
                    this.mineplexSpeed += 0.2D;
                    MoveUtils.setMotion(0.0D);
                    event.setY(mc.thePlayer.motionY = 0.41999998688697815D);
                    this.done = true;
                }
            } else {
                mc.timer.timerSpeed = 0.3F;
                mc.getConnection().sendPacket(new CPacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                this.mineplexSpeed *= 0.98D;
                event.setY(mc.thePlayer.motionY += 0.038D);
                if (mc.thePlayer.fallDistance < 1.4D) {
                    event.setY(mc.thePlayer.motionY += 0.032D);
                }
                MoveUtils.setMotion(this.mineplexSpeed);
            }
        }
    }
    @EventTarget
    public void onMotion(EventUpdate em) {
        if (isEnabled()) {
            if (mode.equalsIgnoreCase("Hypixel")) {
                mc.thePlayer.motionY = 0F;
                if (em.isPre()) {
                    if (Complex.getSettingsManager().getSettingByName("FlyBoost").getValBoolean()) {
                        if (stage == 1) {
                            double motionY = 0.4045;
                            if (mc.thePlayer.isPotionActive(Potion.getPotionById(8))) {
                                motionY += ((float) (mc.thePlayer.getActivePotionEffect(Potion.getPotionById(8)).getAmplifier() + 1) * 0.1D);
                            }
                            em.setY(em.getY() + motionY);
                        }
                        lastDist = Math.hypot(mc.thePlayer.posX - mc.thePlayer.prevPosX, mc.thePlayer.posZ - mc.thePlayer.prevPosZ) * 0.993075;
                    }
                    counter++;
                    switch (counter) {
                        case 1:
                            this.mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 1E-10F, mc.thePlayer.posZ);
                            break;
                        case 2:
                            this.mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + -1E-10F, mc.thePlayer.posZ);
                            counter = 0;
                            break;
                    }
                    stage++;
                }
            } else if (mode.equalsIgnoreCase("NCP")) {
                mc.timer.timerSpeed = 1.0f;
                mc.thePlayer.cameraYaw = 0.1f;
                mc.thePlayer.motionY = -0.0f;
                MoveUtils.setMotion(MoveUtils.getBaseMoveSpeed());
            } else if (mode.equalsIgnoreCase("Vanilla")) {
                mc.timer.timerSpeed = (float) Complex.getSettingsManager().getSettingByName("FlyTimer").getValDouble();
                if (mc.gameSettings.keyBindJump.pressed) {
                    mc.thePlayer.motionY = 1F;
                } else if (mc.gameSettings.keyBindSneak.pressed) {
                    mc.thePlayer.motionY = -1F;
                } else {
                    mc.thePlayer.motionY = 0F;
                }
            } else if (mode.equalsIgnoreCase("Cube")) {
                tick += 1;
                tick2 += 1;
                land += 1;

                if (tick >= tickwait) {
                    forward(2.05);
                    tick = 0;
                    if (tick2 >= tickwait2) {
                        mc.thePlayer.motionY = 0.04f;
                        tick2 = 0;
                    }
                }
                mc.thePlayer.onGround = false;
            } else if (mode.equalsIgnoreCase("ShotbowFast")) {
//                mc.thePlayer.cameraYaw = 0.025f;
                if (Complex.getSettingsManager().getSettingByName("onGround").getValBoolean()) {
                    em.setOnGround(true);
                } else {
                    mc.thePlayer.onGround = false;
                }

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
                } else {
                    double xDist = mc.thePlayer.posX - mc.thePlayer.prevPosX;
                    double zDist = mc.thePlayer.posZ - mc.thePlayer.prevPosZ;
                    lastDist = Math.sqrt(xDist * xDist + zDist * zDist);
                }
            } else if (mode.equalsIgnoreCase("ShotbowTest")) {
//                mc.thePlayer.cameraYaw = 0.1F;
                mc.thePlayer.onGround = false;
                ++counter;

                if (MovementInput.moveForward == 0.0F && MovementInput.moveStrafe == 0.0F) {
                    mc.thePlayer.setPosition(mc.thePlayer.posX + 1.0D, mc.thePlayer.posY + 1.0D, mc.thePlayer.posZ + 1.0D);
                    mc.thePlayer.setPosition(mc.thePlayer.prevPosX, mc.thePlayer.prevPosY, mc.thePlayer.prevPosZ);
                    mc.thePlayer.motionX = 0.0D;
                    mc.thePlayer.motionZ = 0.0D;
                }
                mc.thePlayer.motionY = 0.0D;
                if (counter == 1) {
                    mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 1.0E-10D, mc.thePlayer.posZ);

                    double xDist = mc.thePlayer.posX - mc.thePlayer.prevPosX;
                    double zDist = mc.thePlayer.posZ - mc.thePlayer.prevPosZ;
                    lastDist = Math.sqrt(xDist * xDist + zDist * zDist);
                } else if (counter == 2) {
                    mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 1.0E-10D, mc.thePlayer.posZ);
                    counter = 0;
                }
            } else if (mode.equalsIgnoreCase("Float")) {
                mc.thePlayer.cameraYaw = 0.1f;
                mc.thePlayer.setVelocity(0, 0, 0); // cringe moon x code by ohare
                if (Complex.getSettingsManager().getSettingByName("onGround").getValBoolean()) {
                    em.setOnGround(true);
                } else {
                    mc.thePlayer.onGround = false;
                }

                ++counter;
                if (MovementInput.moveForward == 0.0F && MovementInput.moveStrafe == 0.0F) {
                    mc.thePlayer.setPosition(mc.thePlayer.posX + 1.0D, mc.thePlayer.posY + 1.0D, mc.thePlayer.posZ + 1.0D);
                    mc.thePlayer.setPosition(mc.thePlayer.prevPosX, mc.thePlayer.prevPosY, mc.thePlayer.prevPosZ);
                    mc.thePlayer.motionX = 0.0D;
                    mc.thePlayer.motionZ = 0.0D;
                }
                mc.thePlayer.motionY = 0.0D;
                if (counter == 2 && !mc.thePlayer.onGround) {
                    mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 1.0E-10D, mc.thePlayer.posZ);
                    counter = 0;
                } else {
                    double xDist = mc.thePlayer.posX - mc.thePlayer.prevPosX;
                    double zDist = mc.thePlayer.posZ - mc.thePlayer.prevPosZ;
                    lastDist = Math.sqrt(xDist * xDist + zDist * zDist);
                }
                if (this.mc.gameSettings.keyBindJump.isKeyDown()) {
                    this.mc.thePlayer.motionY = 0.5;
                } else if (this.mc.gameSettings.keyBindSneak.isKeyDown()) {
                    this.mc.thePlayer.motionY = -0.5;
                }
            } else if (mode.equalsIgnoreCase("BruhMineplex")) {
                this.setDisplayName("BruhMineplex");
                if (em.isPre()) {
                    if (!this.done) {
                        mc.thePlayer.posX = this.x;
                        mc.thePlayer.posZ = this.z;
                    }
                    mc.thePlayer.posY = this.y;
                }
            }
        }
    }

    public void damagePlayer(int damage) {
        if (damage < 1)
            damage = 1;
        if (damage > MathHelper.floor_double(mc.thePlayer.getMaxHealth()))
            damage = MathHelper.floor_double(mc.thePlayer.getMaxHealth());

        double offset = 0.0625;
        if (mc.thePlayer != null && mc.getConnection() != null && mc.thePlayer.onGround) {
            for (int i = 0; (double) i < 29.2D; ++i) {
                mc.thePlayer.connection.sendPacket(new CPacketPlayer.Position(mc.thePlayer.posX, mc.thePlayer.posY + 0.0525D, mc.thePlayer.posZ, false));
                mc.thePlayer.connection.sendPacket(new CPacketPlayer.Position(mc.thePlayer.posX, mc.thePlayer.posY - 0.0525D, mc.thePlayer.posZ, false));
            }
            mc.thePlayer.connection.sendPacket(new CPacketPlayer.Position(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));
        }
    }
    public static double getBaseMoveSpeed() {
        double n = 0.2873;
        if (mc.thePlayer.isPotionActive(Potion.getPotionById(1))) {
            n *= 1.0 + 0.2 * (mc.thePlayer.getActivePotionEffect(Potion.getPotionById(1)).getAmplifier() + 1);
        }
        return n;
    }
    public void setMoveSpeed(final EventMove event, final double speed) {
        final MovementInput movementInput = this.mc.thePlayer.movementInput;
        double forward = MovementInput.moveForward;
        final MovementInput movementInput2 = this.mc.thePlayer.movementInput;
        double strafe = MovementInput.moveStrafe;
        float yaw = this.mc.thePlayer.rotationYaw;
        if (forward == 0.0 && strafe == 0.0) {
            event.setX(0.0);
            event.setZ(0.0);
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
            event.setX(forward * speed * Math.cos(Math.toRadians(yaw + 90.0f)) + strafe * speed * Math.sin(Math.toRadians(yaw + 90.0f)));
            event.setZ(forward * speed * Math.sin(Math.toRadians(yaw + 90.0f)) - strafe * speed * Math.cos(Math.toRadians(yaw + 90.0f)));
        }
    }
    public static void forward(final double length) {
        final double yaw = Math.toRadians(mc.thePlayer.rotationYaw);
        mc.thePlayer.setPosition(mc.thePlayer.posX + (-Math.sin(yaw) * length), mc.thePlayer.posY, mc.thePlayer.posZ + (Math.cos(yaw) * length));
    }
}
