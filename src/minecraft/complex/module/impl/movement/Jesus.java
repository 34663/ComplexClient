package complex.module.impl.movement;

import complex.Complex;
import complex.command.Command;
import complex.event.EventTarget;
import complex.event.impl.*;
import complex.module.Category;
import complex.module.Module;
import complex.module.data.Setting;
import complex.utils.BlockUtils;
import complex.utils.MoveUtils;
import complex.utils.PlayerUtil;
import complex.utils.timer.Timer;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Jesus extends Module {
    private List<Packet> packets = new ArrayList<>();
    private boolean shouldStopLavaBlinking;
    private boolean onLiquid = false;
    private boolean getdown = false;
    Timer timer = new Timer();
    int stage, water;
    String mode;
    private int state;
    private double motion = 0;
    public static boolean shouldOffsetPacket;

    public Jesus() {
        super("Jesus", null, 0, 0, Category.Movement);
    }

    @Override
    public void setup() {
        ArrayList<String> options = new ArrayList<>();
        options.add("Dolphin");
        options.add("MiniJump");
        options.add("Bounce");
        Complex.getSettingsManager().rSetting(new Setting("Jesus Mode", this, "Dolphin", options));
    }

    @Override
    public void onEnable() {
        stage = 0;
        water = 0;
        state = 1;
        super.onEnable();
    }

    @EventTarget
    public void onUpdate(EventUpdate em) {
        mode = Complex.getSettingsManager().getSettingByName("Jesus Mode").getValString();

        if (mode.equalsIgnoreCase("Bounce")) {
            this.setDisplayName("Jesus §7Bounce");
            if (PlayerUtil.getBlockUnderPlayer(mc.thePlayer).equals(Blocks.LAVA)) {
                double[] dir = PlayerUtil.moveLooking(0);
                double xDir = dir[0];
                double zDir = dir[1];
                if (mc.gameSettings.keyBindForward.isKeyDown() || mc.gameSettings.keyBindBack.isKeyDown() || mc.gameSettings.keyBindRight.isKeyDown() || mc.gameSettings.keyBindLeft.isKeyDown()) {
                    mc.thePlayer.motionX = xDir * 0.16F;
                    mc.thePlayer.motionZ = zDir * 0.16F;
                    mc.getConnection().sendPacket(new CPacketEntityAction(mc.thePlayer, CPacketEntityAction.Action.STOP_SPRINTING));
                }
            }
            if (!shouldStopLavaBlinking && packets.size() > 40) {
                shouldStopLavaBlinking = true;
            }
            if (shouldStopLavaBlinking && !this.onLiquid)
                shouldStopLavaBlinking = false;

            if (mc.theWorld.containsAnyLiquid(mc.thePlayer.getEntityBoundingBox()) && mc.thePlayer.isInsideOfMaterial(Material.AIR) && !mc.thePlayer.isSneaking() && getWaterHeight() < 5 && ((!mc.thePlayer.isCollidedVertically && mc.thePlayer.fallDistance == 0) || (mc.thePlayer.isInWater()))) {
                mc.thePlayer.motionY = (0.08500000000000001D * (mc.thePlayer.isCollidedHorizontally ? 1 : 1));
            } else if (mc.thePlayer.isInWater() && mc.gameSettings.keyBindJump.isKeyDown()) {
                mc.thePlayer.motionY = 0.085;
            } else if (mc.thePlayer.isInWater() && mc.gameSettings.keyBindSneak.isKeyDown()) {
                mc.thePlayer.motionY = -0.15;
            }
            onLiquid = PlayerUtil.isOnLiquid(mc.thePlayer.getEntityBoundingBox());
            getdown = !getdown;
        } else if (mode.equalsIgnoreCase("MiniJump")) {
            this.setDisplayName("Jesus §7MiniJump");
            if (PlayerUtil.isInLiquid2() && Jesus.mc.thePlayer.isInsideOfMaterial(Material.AIR) && !Jesus.mc.thePlayer.isSneaking()) {
                Jesus.mc.thePlayer.motionY = 0.085;
            }
            if (!PlayerUtil.isOnLiquid() || PlayerUtil.isInLiquid() || !this.shouldSetBoundingBox()) {
                Jesus.shouldOffsetPacket = false;
            }
        } else if (mode.equalsIgnoreCase("Dolphin")) {
            this.setDisplayName("Jesus §7Dolphin");
            if (em.isPost()) {
                return;
            }
            boolean sh = shouldJesus();
            if (PlayerUtil.isInLiquid() && mc.thePlayer.isInsideOfMaterial(Material.AIR) && !mc.thePlayer.isSneaking()) {
                mc.thePlayer.motionY = 0.085;
            }
            if (!mc.thePlayer.onGround && !mc.thePlayer.isInWater() && sh) {
                stage = 1;
                timer.reset();
            }
            if (stage > 0 && !timer.delay(2500)) {
                if ((mc.thePlayer.isCollidedVertically && !MoveUtils.isOnGround(0.001)) || mc.thePlayer.isSneaking()) {
                    stage = -1;
                }
                mc.thePlayer.motionX *= 0;
                mc.thePlayer.motionZ *= 0;
                if (!PlayerUtil.isInLiquid() && !mc.thePlayer.isInWater()) {
                    MoveUtils.setMotion(0.25 + MoveUtils.getSpeedEffect() * 0.05);
                }
                double motionY = getMotionY(stage);
                if (motionY != -999) {
                    mc.thePlayer.motionY = motionY;
                }
                stage += 1;
            }
        }
    }
    @EventTarget
    private void onBoundingBox(final EventBoundingBox event) {
        if (mode.equalsIgnoreCase("MiniJump") || mode.equalsIgnoreCase("Dolphin")) {
            if (!PlayerUtil.isInLiquid2() && event.block instanceof BlockLiquid && Jesus.mc.theWorld.getBlockState(event.pos).getBlock() instanceof BlockLiquid && mc.theWorld.getBlockState(event.pos).getValue((IProperty<Integer>) BlockLiquid.LEVEL) == 0 && this.shouldSetBoundingBox() && event.pos.getY() + 1 <= Jesus.mc.thePlayer.boundingBox.minY) {
                event.boundingBox = new AxisAlignedBB(event.pos.getX(), event.pos.getY(), event.pos.getZ(), event.pos.getX() + 1, event.pos.getY() + (mode.equalsIgnoreCase("Dolphin") ? 0.999 : 1), event.pos.getZ() + 1);
                event.setCancelled(shouldSetBoundingBox());
            }
        }
    }
    @EventTarget
    public void onSendPacket(EventPacketSent event) {
        if (mode.equalsIgnoreCase("Bounce")) {
            if (this.onLiquid) {
                if (mc.thePlayer.fallDistance < 4)
                    event.setPacket(new CPacketPlayer.PositionRotation(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, true));
                if (getdown)
                    event.setPacket(new CPacketPlayer.Position(mc.thePlayer.posX, mc.thePlayer.posY + 0.00001 + (Math.random() * 0.01), mc.thePlayer.posZ, false));
            }
        } else if (mode.equalsIgnoreCase("MiniJump")) {
            if (event.getPacket() instanceof CPacketPlayer && PlayerUtil.isOnLiquid()) {
                final CPacketPlayer packet = (CPacketPlayer) event.getPacket();
                Jesus.shouldOffsetPacket = !Jesus.shouldOffsetPacket;
                if (Jesus.shouldOffsetPacket) {
                    packet.setY(packet.getPositionY() - 1.0E-6);
                }
            }
        }
    }
    @EventTarget
    public void onPacket(EventPacket packet) {
        Packet p = packet.getPacket();
        if(p instanceof SPacketPlayerPosLook) {
            stage = 0;
        }
    }

    private static int getWaterHeight() {
        int posX = (int) Math.floor(mc.thePlayer.posX);
        int posY = (int) Math.floor(mc.thePlayer.getEntityBoundingBox().minY);
        int posZ = (int) Math.floor(mc.thePlayer.posZ);
        return mc.theWorld.getBlockState(new BlockPos(posX, posY, posZ)).getBlock().getMetaFromState(mc.theWorld.getBlockState(new BlockPos(posX, posY, posZ)));
    }
    private boolean shouldSetBoundingBox() {
        return !Jesus.mc.thePlayer.isSneaking() && Jesus.mc.thePlayer.fallDistance < 4.0f;
    }
    public double getMotionY(double stage) {
        stage--;
        double[] motion = new double[]{0.500, 0.484, 0.468, 0.436, 0.404, 0.372, 0.340, 0.308, 0.276, 0.244, 0.212, 0.180, 0.166, 0.166, 0.156, 0.123, 0.135, 0.111, 0.086, 0.098, 0.073, 0.048, 0.06, 0.036, 0.0106, 0.015, 0.004, 0.004, 0.004, 0.004, -0.013, -0.045, -0.077, -0.109};
        if (stage < motion.length && stage >= 0) {
            return motion[(int) stage];
        } else {
            return -999;
        }
    }
    boolean shouldJesus() {
        double x = mc.thePlayer.posX;
        double y = mc.thePlayer.posY;
        double z = mc.thePlayer.posZ;
        ArrayList<BlockPos> pos = new ArrayList<BlockPos>(Arrays.asList(new BlockPos(x + 0.3, y, z + 0.3), new BlockPos(x - 0.3, y, z + 0.3), new BlockPos(x + 0.3, y, z - 0.3), new BlockPos(x - 0.3, y, z - 0.3)));
        for (BlockPos po : pos) {
            if (!(mc.theWorld.getBlockState(po).getBlock() instanceof BlockLiquid))
                continue;
            if (mc.theWorld.getBlockState(po).getValue((IProperty<Integer>) BlockLiquid.LEVEL) instanceof Integer) {
                if (mc.theWorld.getBlockState(po).getValue((IProperty<Integer>) BlockLiquid.LEVEL) <= 4) {
                    return true;
                }
            }
        }
        return false;
    }
}
