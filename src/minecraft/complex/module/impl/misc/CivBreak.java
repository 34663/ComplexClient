package complex.module.impl.misc;

import complex.Complex;
import complex.command.Command;
import complex.event.EventTarget;
import complex.event.impl.EventBlockBreaking;
import complex.event.impl.EventPacketSent;
import complex.event.impl.EventRender3D;
import complex.event.impl.EventUpdate;
import complex.module.Category;
import complex.module.Module;
import complex.module.data.Setting;
import complex.utils.RotationUtils;
import complex.utils.render.Colors;
import complex.utils.render.RenderingUtils;
import complex.utils.timer.Timer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStone;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.*;
import org.lwjgl.opengl.GL11;
import java.util.ArrayList;

public class CivBreak extends Module {
    private Timer timer = new Timer();
    ArrayList<BlockPos> endstone = new ArrayList();
    public static BlockPos blockBreaking;
    private CPacketPlayerDigging packet;
    private BlockPos pos;
    private boolean sendClick;
    private String mode;

    public CivBreak() {
        super("Civbreak", null, 0, 0, Category.Misc);
    }

    @Override
    public void setup() {
        ArrayList<String> civmode = new ArrayList<>();
        civmode.add("Legit");
        civmode.add("Old");
        civmode.add("NoAC");
        Complex.getSettingsManager().rSetting(new Setting("Civ Mode", this, "Old", civmode));
        Complex.getSettingsManager().rSetting(new Setting("CivReach", this, 6.0, 1.0, 7.0, false));
    }

    @Override
    public void onDisable() {
        this.pos = null;
        this.packet = null;
        super.onDisable();
    }

    @EventTarget
    public void onBreakPacketSent(final EventPacketSent event) {
        if (mode.equalsIgnoreCase("Old") || mode.equalsIgnoreCase("NoAC")) {
            if (event.getPacket() instanceof CPacketPlayerDigging && ((CPacketPlayerDigging) event.getPacket()).getAction() == CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK) {
                this.packet = (CPacketPlayerDigging) event.getPacket();
            }
        }
    }
    @EventTarget
    public void onDigging(final EventBlockBreaking event) {
        if (mode.equalsIgnoreCase("Old") || mode.equalsIgnoreCase("NoAC")) {
            if (event.getState() == EventBlockBreaking.EnumBlock.CLICK && !this.sendClick) {
                this.pos = event.getPos();
            }
        }
    }
    @EventTarget
    public void onUpdate(EventUpdate em) {
        mode = Complex.getSettingsManager().getSettingByName("Civ Mode").getValString();

        if (mode.equalsIgnoreCase("Legit")) {
            this.setDisplayName("CivBreak §7Legit");
            if (em.isPre()) {
                int reach = 6;
                for (int y = reach; y >= -reach; --y) {
                    for (int x = -reach; x <= reach; ++x) {
                        for (int z = -reach; z <= reach; ++z) {
                            if (mc.thePlayer.isSneaking()) {
                                return;
                            }
                            BlockPos pos = new BlockPos(mc.thePlayer.posX + x, mc.thePlayer.posY + y, mc.thePlayer.posZ + z);
                            if (getFacingDirection(pos) != null && blockChecks(mc.theWorld.getBlockState(pos).getBlock()) && mc.thePlayer.getDistance(mc.thePlayer.posX + x, mc.thePlayer.posY + y, mc.thePlayer.posZ + z) < mc.playerController.getBlockReachDistance() - 0.2) {
                                if (!endstone.contains(pos))
                                    endstone.add(pos);
                            }
                        }
                    }
                }
                BlockPos closest = null;
                if (!endstone.isEmpty()) {
                    for (int i = 0; i < endstone.size(); i++) {
                        BlockPos bed = endstone.get(i);
                        if (mc.thePlayer.getDistance(bed.getX(), bed.getY(), bed.getZ()) > mc.playerController.getBlockReachDistance() - 0.2 || mc.theWorld.getBlockState(bed).getBlock() != Blocks.BED) {
                            endstone.remove(i);
                        }
                        if (closest == null || mc.thePlayer.getDistance(bed.getX(), bed.getY(), bed.getZ()) < mc.thePlayer.getDistance(closest.getX(), closest.getY(), closest.getZ())) {
                            closest = bed;
                        }
                    }
                }
                if (closest != null) {
                    float[] rot = getRotations(closest, getClosestEnum(closest));
                    em.setYaw(rot[0]);
                    em.setPitch(rot[1]);
                    mc.thePlayer.renderYawOffset = rot[0];
                    mc.thePlayer.rotationYawHead = rot[0];
                    mc.thePlayer.rotationPitchHead = rot[1];
                    blockBreaking = closest;
                    return;
                }
                blockBreaking = null;
            } else {
                if (blockBreaking != null) {
                    if (mc.playerController.blockHitDelay > 1) {
                        mc.playerController.blockHitDelay = 1;
                    }
                    mc.thePlayer.swingArm(EnumHand.MAIN_HAND);
                    EnumFacing direction = getClosestEnum(blockBreaking);
                    if (direction != null) {
                        mc.playerController.onPlayerDamageBlock(blockBreaking, direction);
                        Command.sendChatMessageInfo("こわれる＾～");
                        timer.reset();
                    }
                }
            }
        } else if (mode.equalsIgnoreCase("Old")) {
            this.setDisplayName("Civbreak §7Old");
            final double distance = MathHelper.sqrt_double(mc.thePlayer.getDistanceSq(this.pos));
            final int delay = 370;

            if (distance > Complex.getSettingsManager().getSettingByName("CivReach").getValDouble()) {
                if (this.packet != null) {
                    this.packet = null;
                    mc.getConnection().sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, new BlockPos(0, 0, 0), EnumFacing.UP));
                }
                this.sendClick = true;
                mc.playerController.onPlayerDamageBlock(new BlockPos(0, 0, 0), EnumFacing.UP);
                this.sendClick = false;
                return;
            }
            if (em.isPre() && this.pos != null) {
                final float[] rotations = RotationUtils.getBlockRotations(this.pos.getX(), this.pos.getY(), this.pos.getZ());
                em.setYaw(rotations[0]);
                em.setPitch(rotations[1]);
                mc.thePlayer.renderYawOffset = rotations[0];
                mc.thePlayer.rotationYawHead = rotations[0];
                mc.thePlayer.rotationPitchHead = rotations[1];
            }
            if (em.isPost()) {
                if (this.pos != null && this.packet != null && this.pos.toString().equals(this.packet.getPosition().toString())) {
                    if (timer.delay(delay)) {
                        mc.getConnection().sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
                        mc.getConnection().sendPacket(this.packet);
                        timer.reset();
                    }
                } else {
                    this.packet = null;
                }
                if (this.pos != null && this.packet == null) {
                    this.sendClick = true;
                    mc.getConnection().sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
                    mc.playerController.onPlayerDamageBlock(this.pos, EnumFacing.UP);
                    mc.thePlayer.swingArm(EnumHand.MAIN_HAND);
                    this.sendClick = false;
                }
                if (!(mc.thePlayer.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemBlock)) {
                    mc.getConnection().sendPacket(new CPacketPlayerTryUseItemOnBlock(this.pos, EnumFacing.UP, EnumHand.MAIN_HAND, 0.0f, 0.0f, 0.0f));
                }
            }
        } else if (mode.equalsIgnoreCase("NoAC")) {
            this.setDisplayName("Civbreak §7NoAC");
            final double distance = MathHelper.sqrt_double(mc.thePlayer.getDistanceSq(this.pos));
            final int delay = 50;

            if (distance > Complex.getSettingsManager().getSettingByName("CivReach").getValDouble()) {
                if (this.packet != null) {
                    this.packet = null;
                    mc.getConnection().sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, new BlockPos(0, 0, 0), EnumFacing.UP));
                }
                this.sendClick = true;
                mc.playerController.onPlayerDamageBlock(new BlockPos(0, 0, 0), EnumFacing.UP);
                this.sendClick = false;
                return;
            }
            if (em.isPre() && this.pos != null) {
                final float[] rotations = RotationUtils.getBlockRotations(this.pos.getX(), this.pos.getY(), this.pos.getZ());
                em.setYaw(rotations[0]);
                em.setPitch(rotations[1]);
                mc.thePlayer.renderYawOffset = rotations[0];
                mc.thePlayer.rotationYawHead = rotations[0];
                mc.thePlayer.rotationPitchHead = rotations[1];
            }
            if (em.isPost()) {
                if (this.pos != null && this.packet != null && this.pos.toString().equals(this.packet.getPosition().toString())) {
                    if (timer.delay(delay)) {
                        mc.getConnection().sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
                        mc.getConnection().sendPacket(this.packet);
                        timer.reset();
                    }
                } else {
                    this.packet = null;
                }
                if (this.pos != null && this.packet == null) {
                    this.sendClick = true;
                    mc.getConnection().sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
                    mc.playerController.onPlayerDamageBlock(this.pos, EnumFacing.UP);
                    mc.thePlayer.swingArm(EnumHand.MAIN_HAND);
                    this.sendClick = false;
                }
                if (!(mc.thePlayer.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemBlock)) {
                    mc.getConnection().sendPacket(new CPacketPlayerTryUseItemOnBlock(this.pos, EnumFacing.UP, EnumHand.MAIN_HAND, 0.0f, 0.0f, 0.0f));
                }
            }
        }
    }
    @EventTarget
    public void onRender3D(EventRender3D er) {
        onRenderBlockOutline(er.getPartialTicks());
    }

    public void onRenderBlockOutline(final float partialTicks) {
        if (this.pos != null) {
            double var10000 = this.pos.getX();
            final double var10001 = var10000 - RenderManager.renderPosX;
            var10000 = this.pos.getY();
            final double y = var10000 - RenderManager.renderPosY;
            var10000 = this.pos.getZ();
            final double z = var10000 - RenderManager.renderPosZ;
            final double xo = 1.0;
            final double yo = 1.0;
            final double zo = 1.0;

            RenderingUtils.pre3D();
            RenderingUtils.glColor(Colors.rainbow(200), 0.3F);
            RenderingUtils.drawFilledBox(new AxisAlignedBB(var10001, y, z, var10001 + xo, y + yo, z + zo));
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            RenderingUtils.post3D();
        }
    }
    public void drawESP(double x, double y, double z, double x2, double y2, double z2) {
        double x3 = x - RenderManager.renderPosX;
        double y3 = y - RenderManager.renderPosY;
        double z3 = z - RenderManager.renderPosZ;
        double x4 = x2 - RenderManager.renderPosX;
        double y4 = y2 - RenderManager.renderPosY;

        RenderingUtils.pre3D();
        RenderingUtils.glColor(Colors.rainbow(200), 0.3F);
        RenderingUtils.drawFilledBox(new AxisAlignedBB(x3, y3, z3, x4, y4, z2 - RenderManager.renderPosZ));
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        RenderingUtils.post3D();
    }
    private boolean blockChecks(Block block) {
        return block == Blocks.END_STONE;
    }
    public static float[] getRotations(BlockPos block, EnumFacing face){
        double x = block.getX() + 0.5 - mc.thePlayer.posX + (double)face.getFrontOffsetX()/2;
        double z = block.getZ() + 0.5 - mc.thePlayer.posZ + (double)face.getFrontOffsetZ()/2;
        double d1 = mc.thePlayer.posY + mc.thePlayer.getEyeHeight() -(block.getY() + 0.5);
        double d3 = MathHelper.sqrt_double(x * x + z * z);
        float yaw = (float)(Math.atan2(z, x) * 180.0D / Math.PI) - 90.0F;
        float pitch = (float)(Math.atan2(d1, d3) * 180.0D / Math.PI);
        if(yaw < 0.0F){
            yaw += 360f;
        }
        return  new float[]{yaw, pitch};
    }
    private EnumFacing getClosestEnum(BlockPos pos){
        EnumFacing closestEnum = EnumFacing.UP;
        float rotations = MathHelper.wrapDegrees(getRotations(pos, EnumFacing.UP)[0]);
        if(rotations >= 45 && rotations <= 135){
            closestEnum = EnumFacing.EAST;
        }else if((rotations >= 135 && rotations <= 180) ||
                (rotations <= -135 && rotations >= -180)){
            closestEnum = EnumFacing.SOUTH;
        }else if(rotations <= -45 && rotations >= -135){
            closestEnum = EnumFacing.WEST;
        }else if((rotations >= -45 && rotations <= 0) ||
                (rotations <= 45 && rotations >= 0)){
            closestEnum = EnumFacing.NORTH;
        }
        if (MathHelper.wrapDegrees(getRotations(pos, EnumFacing.UP)[1]) > 75 ||
                MathHelper.wrapDegrees(getRotations(pos, EnumFacing.UP)[1]) < -75){
            closestEnum = EnumFacing.UP;
        }
        return closestEnum;
    }
    private EnumFacing getFacingDirection(BlockPos pos) {
        EnumFacing direction = null;

        if (!mc.theWorld.getBlockState(pos.add(0, 1, 0)).getBlock().isSolidFullCube() && !(mc.theWorld.getBlockState(pos.add(0, 1, 0)).getBlock() instanceof BlockStone)) {
            direction = EnumFacing.UP;
        } else if (!mc.theWorld.getBlockState(pos.add(0, -1, 0)).getBlock().isSolidFullCube() && !(mc.theWorld.getBlockState(pos.add(0, -1, 0)).getBlock() instanceof BlockStone)) {
            direction = EnumFacing.DOWN;
        } else if (!mc.theWorld.getBlockState(pos.add(1, 0, 0)).getBlock().isSolidFullCube() && !(mc.theWorld.getBlockState(pos.add(1, 0, 0)).getBlock() instanceof BlockStone)) {
            direction = EnumFacing.EAST;
        } else if (!mc.theWorld.getBlockState(pos.add(-1, 0, 0)).getBlock().isSolidFullCube() && !(mc.theWorld.getBlockState(pos.add(-1, 0, 0)).getBlock() instanceof BlockStone)) {
            direction = EnumFacing.WEST;
        } else if (!mc.theWorld.getBlockState(pos.add(0, 0, 1)).getBlock().isSolidFullCube() && !(mc.theWorld.getBlockState(pos.add(0, 0, 1)).getBlock() instanceof BlockStone)) {
            direction = EnumFacing.SOUTH;
        } else if (!mc.theWorld.getBlockState(pos.add(0, 0, 1)).getBlock().isSolidFullCube() && !(mc.theWorld.getBlockState(pos.add(0, 0, -1)).getBlock() instanceof BlockStone)) {
            direction = EnumFacing.NORTH;
        }
        RayTraceResult rayResult = mc.theWorld.rayTraceBlocks(new Vec3d(mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ), new Vec3d(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5));
        if (rayResult != null && rayResult.getBlockPos() == pos) {
            return rayResult.sideHit;
        }
        return direction;
    }
}
