package complex.module.impl.combat;

import complex.Complex;
import complex.event.EventTarget;
import complex.event.impl.EventRender3D;
import complex.event.impl.EventUpdate;
import complex.module.Category;
import complex.module.Module;
import complex.module.data.Setting;
import complex.utils.AStarCustomPathFinder;
import complex.utils.TeamUtils;
import complex.utils.render.Colors;
import complex.utils.render.RenderingUtils;
import complex.utils.timer.Timer2;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class InfiniteAura extends Module {
    private double dashDistance = 5;
    private ArrayList<Vec3d> path = new ArrayList<>();
    private List<Vec3d>[] test = new ArrayList[50];
    private List<EntityLivingBase> targets = new CopyOnWriteArrayList<>();
    private Timer2 cps = new Timer2();
    public static Timer2 timer = new Timer2();
    public static boolean canReach;

    public InfiniteAura() {
        super("InfiniteAura", null, 0, 0, Category.Combat);
    }

    @Override
    public void setup() {
        ArrayList<String> options = new ArrayList<>();
        options.add("Vanilla");
        Complex.getSettingsManager().rSetting(new Setting("InfiAura Mode", this, "Vanilla", options));
        Complex.getSettingsManager().rSetting(new Setting("InfiRange", this, 20, 1.0, 60.0, false));
        Complex.getSettingsManager().rSetting(new Setting("InfiCPS", this, 7.0, 1.0, 50.0, false));
        Complex.getSettingsManager().rSetting(new Setting("MaxTarget", this, 2, 1, 50, true));
        Complex.getSettingsManager().rSetting(new Setting("InfiESP", this, true));
        Complex.getSettingsManager().rSetting(new Setting("PathESP", this, true));
    }

    @Override
    public void onEnable() {
        this.setDisplayName("InfiniteAura");
        super.onEnable();
    }

    @EventTarget
    public void onUpdate(EventUpdate update) {
        int maxtTargets = (int) Complex.getSettingsManager().getSettingByName("MaxTarget").getValDouble();
        float delayValue = ((20 / (float) Complex.getSettingsManager().getSettingByName("InfiCPS").getValDouble()) * 1500);

        if (update.isPre()) {
            targets = getTargets();
            if (cps.check(delayValue)) {
                if (targets.size() > 0) {
                    test = new ArrayList[50];
                    for (int i = 0; i < (targets.size() > maxtTargets ? maxtTargets : targets.size()); i++) {
                        EntityLivingBase T = targets.get(i);
                        Vec3d topFrom = new Vec3d(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
                        Vec3d to = new Vec3d(T.posX, T.posY, T.posZ);

                        path = computePath(topFrom, to);
                        test[i] = path;
                        for (Vec3d pathElm : path) {
                            mc.thePlayer.connection.sendPacket(new CPacketPlayer.Position(pathElm.getX(), pathElm.getY(), pathElm.getZ(), true));
                        }

                        mc.thePlayer.swingArm(EnumHand.MAIN_HAND);
                        mc.playerController.attackEntity(mc.thePlayer, T);
                        Collections.reverse(path);
                        for (Vec3d pathElm : path) {
                            mc.thePlayer.connection.sendPacket(new CPacketPlayer.Position(pathElm.getX(), pathElm.getY(), pathElm.getZ(), true));
                        }
                    }
                    cps.reset();
                }
            }
        }
    }
    @EventTarget
    public void onRender3D(EventRender3D render3D) {
        int maxtTargets = (int) Complex.getSettingsManager().getSettingByName("MaxTarget").getValDouble();
        if (!targets.isEmpty() && Complex.getSettingsManager().getSettingByName("InfiESP").getValBoolean()) {
            if (targets.size() > 0) {
                for (int i = 0; i < (targets.size() > maxtTargets ? maxtTargets : targets.size()); i++) {
                    int color = targets.get(i).hurtResistantTime > 15 ? Colors.getColor(new Color(255, 70, 70, 200)) : Colors.getColor(new Color(100, 100, 100, 200));
                    drawESP(targets.get(i), color);
                }
            }
        }
        if (!path.isEmpty() && Complex.getSettingsManager().getSettingByName("PathESP").getValBoolean()) {
            for (int i = 0; i < targets.size(); i++) {
                try {
                    if (test != null)
                        for (Vec3d pos : test[i]) {
                            if (pos != null)
                                drawPath(pos);
                        }
                } catch (Exception ignored) {
                    ;
                }
            }
            if (cps.check(1000)) {
                test = new ArrayList[50];
                path.clear();
            }
        }
    }

    private ArrayList<Vec3d> computePath(Vec3d topFrom, Vec3d to) {
        if (!canPassThrow(new BlockPos(topFrom))) {
            topFrom = topFrom.addVector(0, 1, 0);
        }
        AStarCustomPathFinder pathfinder = new AStarCustomPathFinder(topFrom, to);
        pathfinder.compute();

        int i = 0;
        Vec3d lastLoc = null;
        Vec3d lastDashLoc = null;
        ArrayList<Vec3d> path = new ArrayList<Vec3d>();
        ArrayList<Vec3d> pathFinderPath = pathfinder.getPath();
        for (Vec3d pathElm : pathFinderPath) {
            if (i == 0 || i == pathFinderPath.size() - 1) {
                if (lastLoc != null) {
                    path.add(lastLoc.addVector(0.5, 0, 0.5));
                }
                path.add(pathElm.addVector(0.5, 0, 0.5));
                lastDashLoc = pathElm;
            } else {
                boolean canContinue = true;
                if (pathElm.squareDistanceTo(lastDashLoc) > dashDistance * dashDistance) {
                    canContinue = false;
                } else {
                    double smallX = Math.min(lastDashLoc.getX(), pathElm.getX());
                    double smallY = Math.min(lastDashLoc.getY(), pathElm.getY());
                    double smallZ = Math.min(lastDashLoc.getZ(), pathElm.getZ());
                    double bigX = Math.max(lastDashLoc.getX(), pathElm.getX());
                    double bigY = Math.max(lastDashLoc.getY(), pathElm.getY());
                    double bigZ = Math.max(lastDashLoc.getZ(), pathElm.getZ());
                    cordsLoop:
                    for (int x = (int) smallX; x <= bigX; x++) {
                        for (int y = (int) smallY; y <= bigY; y++) {
                            for (int z = (int) smallZ; z <= bigZ; z++) {
                                if (!AStarCustomPathFinder.checkPositionValidity(x, y, z, false)) {
                                    canContinue = false;
                                    break cordsLoop;
                                }
                            }
                        }
                    }
                }
                if (!canContinue) {
                    path.add(lastLoc.addVector(0.5, 0, 0.5));
                    lastDashLoc = lastLoc;
                }
            }
            lastLoc = pathElm;
            i++;
        }
        return path;
    }
    private boolean canPassThrow(BlockPos pos) {
        Block block = mc.theWorld.getBlockState(new BlockPos(pos.getX(), pos.getY(), pos.getZ())).getBlock();
        return block.getMaterial() == Material.AIR || block.getMaterial() == Material.PLANTS || block.getMaterial() == Material.VINE || block == Blocks.LADDER || block == Blocks.WATER || block == Blocks.FLOWING_WATER || block == Blocks.WALL_SIGN || block == Blocks.STANDING_SIGN;
    }
    private List<EntityLivingBase> getTargets() {
        List<EntityLivingBase> targets = new ArrayList<>();

        for (Object o : mc.theWorld.getLoadedEntityList()) {
            if (o instanceof EntityLivingBase) {
                EntityLivingBase entity = (EntityLivingBase) o;
                if (validEntity(entity)) {
                    targets.add(entity);
                }
            }
        }
        targets.sort((o1, o2) -> (int) (o1.getDistanceToEntity(mc.thePlayer) * 1000 - o2.getDistanceToEntity(mc.thePlayer) * 1000));
        return targets;
    }
    boolean validEntity(EntityLivingBase entity) {
        float range = (float) Complex.getSettingsManager().getSettingByName("InfiRange").getValDouble();
        boolean players = Complex.getSettingsManager().getSettingByName("Players").getValBoolean();
        boolean animals = Complex.getSettingsManager().getSettingByName("Animals").getValBoolean();

        if ((mc.thePlayer.isEntityAlive()) && !(entity instanceof EntityPlayerSP)) {
            if (mc.thePlayer.getDistanceToEntity(entity) <= range) {
                if (AntiBot.getInvalid().contains(entity)) {
                    return false;
                }
                if (entity.isPlayerSleeping()) {
                    return false;
                }

                if (entity instanceof EntityPlayer) {
                    if (players) {
                        EntityPlayer player = (EntityPlayer) entity;
                        if (!player.isEntityAlive() && player.getHealth() == 0.0) {
                            return false;
                        } else if (TeamUtils.isTeam(mc.thePlayer, player) && Complex.getSettingsManager().getSettingByName("Teams").getValBoolean()) {
                            return false;
                        } else if (player.isInvisible()) {
                            return false;
                        } else
                            return true;
                    }
                } else {
                    if (!entity.isEntityAlive()) {
                        return false;
                    }
                }

                if (entity instanceof EntityMob && animals) {
                    return true;
                }
                if ((entity instanceof EntityAnimal || entity instanceof EntityVillager) && animals) {
                    if (entity.getName().equals("Villager")) {
                        return false;
                    }
                    return true;
                }
            }
        }
        return false;
    }
    public void drawESP(Entity entity, int color) {
        double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * mc.timer.renderPartialTicks;
        double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * mc.timer.renderPartialTicks;
        double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * mc.timer.renderPartialTicks;
        double width = Math.abs(entity.boundingBox.maxX - entity.boundingBox.minX);
        double height = Math.abs(entity.boundingBox.maxY - entity.boundingBox.minY);
        Vec3d vec = new Vec3d(x - width / 2, y, z - width / 2);
        Vec3d vec2 = new Vec3d(x + width / 2, y + height, z + width / 2);
        RenderingUtils.pre3D();
        mc.entityRenderer.setupCameraTransform(mc.timer.renderPartialTicks, 2);
        RenderingUtils.glColor(color);
        RenderingUtils.drawOutlinedBoundingBox(new AxisAlignedBB(vec.getX() - RenderManager.renderPosX, vec.getY() - RenderManager.renderPosY, vec.getZ() - RenderManager.renderPosZ, vec2.getX() - RenderManager.renderPosX, vec2.getY() - RenderManager.renderPosY, vec2.getZ() - RenderManager.renderPosZ));
        GL11.glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
        RenderingUtils.post3D();
    }
    public void drawPath(Vec3d vec) {
        double x = vec.getX() - RenderManager.renderPosX;
        double y = vec.getY() - RenderManager.renderPosY;
        double z = vec.getZ() - RenderManager.renderPosZ;
        double width = 0.3;
        double height = mc.thePlayer.getEyeHeight();
        RenderingUtils.pre3D();
        GL11.glLoadIdentity();
        mc.entityRenderer.setupCameraTransform(mc.timer.renderPartialTicks, 2);
        int colors[] = {Colors.getColor(Color.black), Colors.getColor(Color.white)};
        for (int i = 0; i < 2; i++) {
            RenderingUtils.glColor(colors[i]);
            GL11.glLineWidth(3 - i * 2);
            GL11.glBegin(GL11.GL_LINE_STRIP);
            GL11.glVertex3d(x - width, y, z - width);
            GL11.glVertex3d(x - width, y, z - width);
            GL11.glVertex3d(x - width, y + height, z - width);
            GL11.glVertex3d(x + width, y + height, z - width);
            GL11.glVertex3d(x + width, y, z - width);
            GL11.glVertex3d(x - width, y, z - width);
            GL11.glVertex3d(x - width, y, z + width);
            GL11.glEnd();
            GL11.glBegin(GL11.GL_LINE_STRIP);
            GL11.glVertex3d(x + width, y, z + width);
            GL11.glVertex3d(x + width, y + height, z + width);
            GL11.glVertex3d(x - width, y + height, z + width);
            GL11.glVertex3d(x - width, y, z + width);
            GL11.glVertex3d(x + width, y, z + width);
            GL11.glVertex3d(x + width, y, z - width);
            GL11.glEnd();
            GL11.glBegin(GL11.GL_LINE_STRIP);
            GL11.glVertex3d(x + width, y + height, z + width);
            GL11.glVertex3d(x + width, y + height, z - width);
            GL11.glEnd();
            GL11.glBegin(GL11.GL_LINE_STRIP);
            GL11.glVertex3d(x - width, y + height, z + width);
            GL11.glVertex3d(x - width, y + height, z - width);
            GL11.glEnd();
        }
        RenderingUtils.post3D();
    }
}
