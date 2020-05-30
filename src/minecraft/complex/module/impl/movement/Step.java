package complex.module.impl.movement;

import complex.Complex;
import complex.command.Command;
import complex.event.EventTarget;
import complex.event.impl.EventJump;
import complex.event.impl.EventStep;
import complex.event.impl.EventTick;
import complex.event.impl.EventUpdate;
import complex.module.Category;
import complex.module.Module;
import complex.module.data.Setting;
import complex.utils.BlockUtils;
import complex.utils.PlayerUtil;
import complex.utils.timer.Timer;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Step extends Module {
    private final double[][] offsets = new double[][] { { 0.42D, 0.753D }, { 0.42D, 0.75D, 1.0D, 1.16D, 1.23D, 1.2D }, { 0.42D, 0.78D, 0.63D, 0.51D, 0.9D, 1.21D, 1.45D, 1.43D } };
    public static Timer lastStep = new Timer();
    public static Timer time = new Timer();
    private String mode;
    private int tick = 0;
    boolean resetTimer;
    private boolean jumped;

    public Step() {
        super("Step", null, 0, 0, Category.Movement);
    }

    @Override
    public void setup() {
        ArrayList<String> options = new ArrayList<>();
        options.add("Motion");
        options.add("NCP");
        options.add("Cube");
        options.add("Vanilla");
        Complex.getSettingsManager().rSetting(new Setting("Step Mode", this, "NCP", options));
        Complex.getSettingsManager().rSetting(new Setting("StepHeight", this, 1.5, 0, 10, false));
        Complex.getSettingsManager().rSetting(new Setting("StepDelay", this, 0.15, 0.0, 5.0, false));
        Complex.getSettingsManager().rSetting(new Setting("Reverse", this, false));
    }

    @Override
    public void onEnable() {
        resetTimer = false;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        mc.thePlayer.stepHeight = 0.625f;
        super.onDisable();
    }

    @EventTarget
    public void onUpdate(EventUpdate em) {
        mode = Complex.getSettingsManager().getSettingByName("Step Mode").getValString();

        if (mode.equalsIgnoreCase("Motion")) {
            this.setDisplayName("Step §7Motion");
        } else if (mode.equalsIgnoreCase("NCP")) {
            this.setDisplayName("Step §7NCP");
        } else if (mode.equalsIgnoreCase("Cube")) {
            this.setDisplayName("Step §7Cube");
        } else if (mode.equalsIgnoreCase("Vanilla")) {
            this.setDisplayName("Step §7Vanilla");
        }

        if (Complex.getSettingsManager().getSettingByName("Reverse").getValBoolean()) {
            if (mc.thePlayer.onGround)
                jumped = false;
            if (mc.thePlayer.motionY > 0)
                jumped = true;
            if (BlockUtils.collideBlock(mc.thePlayer.getEntityBoundingBox(), block -> block instanceof BlockLiquid) || BlockUtils.collideBlock(new AxisAlignedBB(mc.thePlayer.getEntityBoundingBox().maxX, mc.thePlayer.getEntityBoundingBox().maxY, mc.thePlayer.getEntityBoundingBox().maxZ, mc.thePlayer.getEntityBoundingBox().minX, mc.thePlayer.getEntityBoundingBox().minY - 0.01D, mc.thePlayer.getEntityBoundingBox().minZ), block -> block instanceof BlockLiquid))
                return;

            if (!mc.gameSettings.keyBindJump.isKeyDown() && !mc.thePlayer.onGround && !mc.thePlayer.movementInput.jump && mc.thePlayer.motionY <= 0D && mc.thePlayer.fallDistance <= 1F && !jumped)
                mc.thePlayer.motionY = -1.0F;
        }
    }
    @EventTarget
    public void onJump(EventJump ej) {
        if (ej.isPre()) {
            if (!lastStep.delay(60) && mode.equalsIgnoreCase("Cubecraft")) {
                ej.setCancelled(true);
            }
            jumped = true;
        }
    }
    @EventTarget
    public void onStep(EventStep step) {
        double stepValue = Complex.getSettingsManager().getSettingByName("StepHeight").getValDouble();
        final float delay = (float) Complex.getSettingsManager().getSettingByName("StepDelay").getValDouble() * 1000;
        final float timer = 0.37F;

        if (resetTimer) {
            resetTimer = !resetTimer;
            mc.timer.timerSpeed = 1;
        }
        if (!PlayerUtil.isInLiquid()) {
            if (step.isPre()) {
                if (mc.thePlayer.isCollidedVertically && !mc.gameSettings.keyBindJump.isPressed() && time.delay(delay)) {
                    step.setStepHeight(stepValue);
                    step.setActive(true);
                }
            } else {
                double rheight = mc.thePlayer.getEntityBoundingBox().minY - mc.thePlayer.posY;
                boolean canStep = rheight >= 0.625;
                if (canStep) {
                    lastStep.reset();
                    time.reset();
                }
                if (mode.equalsIgnoreCase("Motion")) {
                    if (canStep) {
                        mc.timer.timerSpeed = timer - (rheight >= 1 ? Math.abs(1-(float)rheight)*((float)timer*0.55f) : 0);
                        if(mc.timer.timerSpeed <= 0.05f){
                            mc.timer.timerSpeed = 0.05f;
                        }
                        resetTimer = true;
                        ncpStep(rheight);
                    }
                } else if (mode.equalsIgnoreCase("NCP")) {
                    if (canStep) {
                        mc.timer.timerSpeed = rheight < 2 ? 0.6f : 0.3f;
                        resetTimer = true;
                        ncpStep(rheight);
                    }
                } else if (mode.equalsIgnoreCase("Cube")) {
                    if (canStep) {
                        cubeStep(rheight);
                        resetTimer = true;
                        mc.timer.timerSpeed = rheight < 2 ? 0.6f : 0.3f;
                    }
                }
            }
        }
    }

    void cubeStep(double height) {
        double posX = mc.thePlayer.posX;
        double posZ = mc.thePlayer.posZ;
        double y = mc.thePlayer.posY;
        double first = 0.42;
        double second = 0.75;
        mc.thePlayer.connection.sendPacket(new CPacketPlayer.Position(posX, y + first, posZ, false));
    }
    void ncpStep(double height) {
        List<Double> offset = Arrays.asList(0.42, 0.333, 0.248, 0.083, -0.078);
        double posX = mc.thePlayer.posX;
        double posZ = mc.thePlayer.posZ;
        double y = mc.thePlayer.posY;
        if (height < 1.1) {
            double first = 0.42;
            double second = 0.75;
            if (height != 1) {
                first *= height;
                second *= height;
                if (first > 0.425) {
                    first = 0.425;
                }
                if (second > 0.78) {
                    second = 0.78;
                }
                if (second < 0.49) {
                    second = 0.49;
                }
            }
            if (first == 0.42)
                first = 0.41999998688698;
            mc.thePlayer.connection.sendPacket(new CPacketPlayer.Position(posX, y + first, posZ, false));
            if (y + second < y + height)
                mc.thePlayer.connection.sendPacket(new CPacketPlayer.Position(posX, y + second, posZ, false));
            return;
        } else if (height < 1.6) {
            for (int i = 0; i < offset.size(); i++) {
                double off = offset.get(i);
                y += off;
                mc.thePlayer.connection.sendPacket(new CPacketPlayer.Position(posX, y, posZ, false));
            }
        } else if (height < 2.1) {
            double[] heights = {0.425, 0.821, 0.699, 0.599, 1.022, 1.372, 1.652, 1.869};
            for (double off : heights) {
                mc.thePlayer.connection.sendPacket(new CPacketPlayer.Position(posX, y + off, posZ, false));
            }
        } else {
            double[] heights = {0.425, 0.821, 0.699, 0.599, 1.022, 1.372, 1.652, 1.869, 2.019, 1.907};
            for (double off : heights) {
                mc.thePlayer.connection.sendPacket(new CPacketPlayer.Position(posX, y + off, posZ, false));
            }
        }
    }
}
