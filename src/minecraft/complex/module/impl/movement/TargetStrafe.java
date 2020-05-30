package complex.module.impl.movement;

import complex.Complex;
import complex.event.EventTarget;
import complex.event.impl.EventRender3D;
import complex.event.impl.EventUpdate;
import complex.module.Category;
import complex.module.Module;
import complex.module.data.Setting;
import complex.module.impl.combat.AntiBot;
import complex.utils.MiscUtils;
import complex.utils.MoveUtils;
import complex.utils.RotationUtils;
import complex.utils.TeamUtils;
import complex.utils.timer.Timer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TargetStrafe extends Module {
    private Timer timer = new Timer();
    public static EntityLivingBase vn;
    private final List<EntityLivingBase> vo = new ArrayList<EntityLivingBase>();
    public static Vec3d vec3d;
    public static int vy;
    public static int vz;
    private boolean wa;
    private boolean wb;

    public TargetStrafe() {
        super("TargetStrafe", null, 0, 0, Category.Movement);
    }

    @Override
    public void setup() {
        Complex.getSettingsManager().rSetting(new Setting("SRange", this, 5.2f, 1.0f, 7.0f, false));
        Complex.getSettingsManager().rSetting(new Setting("SDistance", this, 3.9f, 0.1f, 5.0f, false));
    }

    @Override
    public void onEnable() {
        this.setDisplayName("TargetStrafe");
        this.wa = false;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        if (this.mc.thePlayer == null) {
            return;
        }
        this.mc.timer.timerSpeed = 1.0f;
        this.wa = false;
        super.onDisable();
    }

    @EventTarget
    public void onRender3D(EventRender3D render3D) {
        if (this.mc.theWorld == null || this.mc.thePlayer == null) {
            return;
        }
        for (final Entity entity : this.mc.theWorld.getLoadedEntityList()) {
            if (entity instanceof EntityLivingBase) {
                final EntityLivingBase entityLivingBase = (EntityLivingBase)entity;
                if (entityLivingBase.isDead || entityLivingBase == this.mc.thePlayer) {
                    continue;
                }
                if (!this.validEntity(entityLivingBase)) {
                    continue;
                }
                this.drawCircle(entity, (vn != null && entity == vn) ? new Color(255, 72, 67) : new Color(255, 255, 255), render3D.getPartialTicks());
            }
        }
    }
    @EventTarget
    public void onUpdate(EventUpdate em) {
        double distance = Complex.getSettingsManager().getSettingByName("SDistance").getValDouble();

        if (!Complex.getModuleManager().isEnabled("Speed")) {
            vn = null;
            this.wa = false;
            return;
        }

        vn = this.c();
        if (vn != null) {
            final ArrayList<Vec3d> list = new ArrayList<Vec3d>();
            for (float n = 0.0f; n < 6.283184051513672; n += 0.23271053f) {
                list.add(new Vec3d(distance * Math.cos(n) + vn.posX, vn.posY, distance * Math.sin(n) + vn.posZ));
            }
            vz = list.size();
            if (!this.wa) {
                // なにこれ
                final ArrayList<Vec3d> list2 = new ArrayList<Vec3d>(list);
                list2.sort(Comparator.comparingDouble(vec3 -> this.mc.thePlayer.getDistance(vec3.xCoord, vec3.yCoord, vec3.zCoord)));
                vy = list.indexOf(list2.get(0));
                this.wa = true;
            } else {
                final BlockPos blockPos = new BlockPos(list.get(vy).xCoord, list.get(vy).yCoord, list.get(vy).zCoord);
                vec3d = new Vec3d(blockPos.getX(), list.get(vy).yCoord, blockPos.getZ());
                if (this.a(vec3d) || this.mc.theWorld.getBlockState(new BlockPos(vec3d.xCoord, this.mc.thePlayer.posY, vec3d.zCoord)).getBlock().getCollisionBoundingBox(this.mc.theWorld.getBlockState(new BlockPos(vec3d.xCoord, this.mc.thePlayer.posY, vec3d.zCoord)), this.mc.theWorld, new BlockPos(vec3d.xCoord, this.mc.thePlayer.posY, vec3d.zCoord)) != null || this.mc.theWorld.getBlockState(new BlockPos(vec3d.xCoord, this.mc.thePlayer.posY + 1.0, vec3d.zCoord)).getBlock().getCollisionBoundingBox(this.mc.theWorld.getBlockState(new BlockPos(vec3d.xCoord, this.mc.thePlayer.posY + 1.0, vec3d.zCoord)), this.mc.theWorld, new BlockPos(vec3d.xCoord, this.mc.thePlayer.posY + 1.0, vec3d.zCoord)) != null || this.mc.theWorld.getBlockState(new BlockPos(vec3d.xCoord, this.mc.thePlayer.posY + 2.0, vec3d.zCoord)).getBlock().getCollisionBoundingBox(this.mc.theWorld.getBlockState(new BlockPos(vec3d.xCoord, this.mc.thePlayer.posY + 2.0, vec3d.zCoord)), this.mc.theWorld, new BlockPos(vec3d.xCoord, this.mc.thePlayer.posY + 2.0, vec3d.zCoord)) != null) {
                    if (!(MoveUtils.tarst = !MoveUtils.tarst)) {
                        if (vy + 1 > list.size() - 1) {
                            vy = 0;
                        } else {
                            ++vy;
                        }
                    } else if (vy - 1 < 0) {
                        vy = list.size() - 1;
                    } else {
                        --vy;
                    }
                } else {
                    if (this.mc.thePlayer.isCollidedHorizontally) {
                        if (!this.wb) {
                            MoveUtils.tarst = !MoveUtils.tarst;
                            this.a(list);
                            this.wb = true;
                        }
                    } else {
                        this.wb = false;
                    }
                    if (this.mc.thePlayer.getDistance(vec3d.xCoord, this.mc.thePlayer.posY, vec3d.zCoord) <= this.mc.thePlayer.getDistance(this.mc.thePlayer.prevPosX, this.mc.thePlayer.prevPosY, this.mc.thePlayer.prevPosZ) * 2.0) {
                        this.a(list);
                    }
                }
            }
        } else {
            this.wa = false;
            vy = 0;
            vec3d = null;
        }
    }

    private EntityLivingBase c() {
        this.vo.clear();
        final double n = Double.MAX_VALUE;
        if (this.mc.theWorld != null) {
            for (final Entity next : this.mc.theWorld.loadedEntityList) {
                if (next instanceof EntityLivingBase) {
                    final EntityLivingBase entityIn2 = (EntityLivingBase) next;
                    if (this.mc.thePlayer.getDistanceToEntity(entityIn2) >= n || !this.validEntity(entityIn2)) {
                        continue;
                    }
                    this.vo.add(entityIn2);
                }
            }
        }
        if (this.vo.isEmpty()) {
            return null;
        }
        this.vo.sort(Comparator.comparingDouble(entityIn -> this.mc.thePlayer.getDistanceToEntity(entityIn)));
        return this.vo.get(0);
    }
    boolean validEntity(EntityLivingBase entity) {
        double range = Complex.getSettingsManager().getSettingByName("Range").getValDouble();
        boolean players = Complex.getSettingsManager().getSettingByName("Players").getValBoolean();
        boolean animals = Complex.getSettingsManager().getSettingByName("Animals").getValBoolean();
        if ((mc.thePlayer.isEntityAlive()) && !(entity instanceof EntityPlayerSP)) {
            if (mc.thePlayer.getDistanceToEntity(entity) <= range) {
                if (!RotationUtils.canEntityBeSeen(entity) && !Complex.getSettingsManager().getSettingByName("Walls").getValBoolean())
                    return false;
                if (AntiBot.getInvalid().contains(entity) || entity.isPlayerSleeping())
                    return false;
                if (entity instanceof EntityPlayer) {
                    if (players) {
                        EntityPlayer player = (EntityPlayer) entity;
                        if (!player.isEntityAlive() && player.getHealth() == 0.0) {
                            return false;
                        } else if (!TeamUtils.isTeams((EntityPlayer) entity) && Complex.getSettingsManager().getSettingByName("Teams").getValBoolean()) {
                            return false;
                        } else if (player.isInvisible() && !Complex.getSettingsManager().getSettingByName("Invible").getValBoolean()) {
                            return false;
                        } else {
                            return true;
                        }
                    }
                } else {
                    if (!entity.isEntityAlive())
                        return false;
                }
                if (animals) {
                    if (entity instanceof EntityMob || entity instanceof EntityIronGolem || entity instanceof EntityAnimal || entity instanceof EntityVillager) {
                        if (entity.getName().equals("Villager") && entity instanceof EntityVillager) {
                            return false;
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }
    public boolean a(final Vec3d vec3) {
        for (int i = (int) Math.ceil(vec3.yCoord); i >= 0; --i) {
            if (this.mc.theWorld.getBlockState(new BlockPos(vec3.xCoord, i, vec3.zCoord)).getBlock() != Blocks.AIR) {
                return false;
            }
        }
        return true;
    }
    private void a(final ArrayList<Vec3d> list) {
        if (!MoveUtils.tarst) {
            if (vy + 1 > list.size() - 1) {
                vy = 0;
            } else {
                ++vy;
            }
        } else if (vy - 1 < 0) {
            vy = list.size() - 1;
        } else {
            --vy;
        }
    }
    private void drawCircle(final Entity entity, final Color color, final float n) {
        double distance = Complex.getSettingsManager().getSettingByName("SDistance").getValDouble();
        GL11.glPushMatrix();
        GL11.glDisable(3553);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        final double n2 = MiscUtils.a(entity.posX, entity.lastTickPosX, n) - this.mc.getRenderManager().getRenderPosX();
        final double y = MiscUtils.a(entity.posY, entity.lastTickPosY, n) - this.mc.getRenderManager().getRenderPosY();
        final double n3 = MiscUtils.a(entity.posZ, entity.lastTickPosZ, n) - this.mc.getRenderManager().getRenderPosZ();
        GL11.glLineWidth(4.0f);
        final ArrayList<Vec3d> list = new ArrayList<Vec3d>();
        for (float n4 = 0.0f; n4 < 6.283184051513672; n4 += 0.23271053f) {
            list.add(new Vec3d(distance * Math.cos(n4) + n2, y, distance * Math.sin(n4) + n3));
        }
        GL11.glEnable(2852);
        GL11.glBegin(3);
        final float n5 = 0.003921569f * color.getRed();
        final float n6 = 0.003921569f * color.getGreen();
        final float n7 = 0.003921569f * color.getBlue();
        for (final Vec3d vec3 : list) {
            GL11.glColor3f(n5, n6, n7);
            GL11.glVertex3d(vec3.xCoord, vec3.yCoord, vec3.zCoord);
        }
        GL11.glEnd();
        GL11.glDisable(2852);
        GL11.glDisable(2848);
        GL11.glDisable(3042);
        GL11.glEnable(3553);
        GL11.glLineWidth(1.0f);
        GL11.glPopMatrix();
    }
}
