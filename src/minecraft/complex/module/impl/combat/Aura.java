package complex.module.impl.combat;

import complex.Complex;
import complex.event.EventTarget;
import complex.event.impl.EventAttack;
import complex.event.impl.EventRender2D;
import complex.event.impl.EventRender3D;
import complex.event.impl.EventUpdate;
import complex.module.Category;
import complex.module.Module;
import complex.module.data.Setting;
import complex.module.impl.visual.ESP;
import complex.utils.*;
import complex.utils.animate.AnimationUtil;
import complex.utils.misc.AuthenticationUtil;
import complex.utils.render.Colors;
import complex.utils.render.RenderingUtils;
import complex.utils.timer.Timer3;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class Aura extends Module {
    private String mode;
    private String Sort;
    public static EntityLivingBase target;
    public boolean attacking;
    private int targetIndex;
    private List<EntityLivingBase> targets;
    private Timer3 attackTimer, switchTimer;
    private float healthBarWidth;
    private float[] angles = new float[2];

    public Aura() {
        super("Aura", null, Keyboard.KEY_R, 0, Category.Combat);
    }

    @Override
    public void setup() {
        ArrayList<String> options = new ArrayList<>();
        ArrayList<String> Sort = new ArrayList<>();
        options.add("Priority");
        options.add("Switch");
        options.add("Test");
        Sort.add("Range");
        Sort.add("Angle");
        Sort.add("Health");
        Sort.add("Armor");
        Complex.getSettingsManager().rSetting(new Setting("Aura Mode", this, "Switch", options));
        Complex.getSettingsManager().rSetting(new Setting("Sort Mode", this, "Range", Sort));
        Complex.getSettingsManager().rSetting(new Setting("Range", this, 4.25, 0.00, 7.00, false));
        Complex.getSettingsManager().rSetting(new Setting("BlockRange", this, 5.00, 0.00, 9.00, false));
        Complex.getSettingsManager().rSetting(new Setting("MinAps", this, 150.0D, 0.1D, 300.0D, false));
        Complex.getSettingsManager().rSetting(new Setting("MaxAps", this, 170.0D, 0.1D, 300.0D, false));
        Complex.getSettingsManager().rSetting(new Setting("SwapTime", this, 600L, 0L, 1000L, false));
        Complex.getSettingsManager().rSetting(new Setting("AngleStep", this, 65, 0, 180, true));
        Complex.getSettingsManager().rSetting(new Setting("Players", this, true));
        Complex.getSettingsManager().rSetting(new Setting("Mobs", this, false));
        Complex.getSettingsManager().rSetting(new Setting("Animals", this, false));
        Complex.getSettingsManager().rSetting(new Setting("Teams", this, false));
        Complex.getSettingsManager().rSetting(new Setting("CoolDown", this, false));
        Complex.getSettingsManager().rSetting(new Setting("TargetHUD", this, false));
        Complex.getSettingsManager().rSetting(new Setting("Walls", this, true));
        Complex.getSettingsManager().rSetting(new Setting("AutoBlock", this, false));
        targets = new ArrayList<>();
        attackTimer = new Timer3();
        switchTimer = new Timer3();
    }

    @Override
    public void onEnable() {
        this.attackTimer.reset();
        this.switchTimer.reset();
        this.angles = new float[]{ mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch };
        super.onEnable();
    }

    @Override
    public void onDisable() {
        target = null;
        super.onDisable();
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        mode = Complex.getSettingsManager().getSettingByName("Aura Mode").getValString();
        int minAPS = (int) Complex.getSettingsManager().getSettingByName("MinAps").getValDouble();
        int maxAPS = (int) Complex.getSettingsManager().getSettingByName("MaxAps").getValDouble();
        double angleStep = Complex.getSettingsManager().getSettingByName("AngleStep").getValDouble();
        long SwapTime = (long) Complex.getSettingsManager().getSettingByName("SwapTime").getValDouble();
        boolean autoblock = Complex.getSettingsManager().getSettingByName("AutoBlock").getValBoolean();

        collectTargets();
        final int aps = randomNumber(minAPS, maxAPS);

        if (mode.equalsIgnoreCase("Switch")) {
            this.setDisplayName("Aura §7Switch");
            if (switchTimer.hasReached(SwapTime)) {
                targetIndex++;
                switchTimer.reset();
            }
            if (targetIndex >= targets.size()) {
                targetIndex = 0;
            }

            target = !targets.isEmpty() && targetIndex < targets.size() ? targets.get(targetIndex) : null;
        } else if (mode.equalsIgnoreCase("Priority")) {
            this.setDisplayName("Aura §7Priority");
            sortTargets();
            target = !targets.isEmpty() ? targets.get(0) : null;
        } else if (mode.equalsIgnoreCase("Test")) {
            this.setDisplayName("Aura §7Test");
            target = !targets.isEmpty() ? targets.get(0) : null;
        }

        if (event.isPre()) {
            if (target != null) {
                final float[] rotations = RotationUtils.getRotations(target);
                this.angles = new float[]{rotations[0], rotations[1]};

                if (this.angles[0] > rotations[0]) {
                    this.angles[0] -= angleStep;
                } else if (this.angles[0] < rotations[0]) {
                    this.angles[0] += angleStep;
                }

                float yawDiff = this.angles[0] - rotations[0];

                if (yawDiff < angleStep) {
                    this.angles[0] = rotations[0];
                }

                event.setYaw(angles[0]);
                event.setPitch(angles[1]);
                mc.thePlayer.renderYawOffset = angles[0];
                mc.thePlayer.rotationYawHead = angles[0];
                mc.thePlayer.rotationPitchHead = angles[1];

                if (aps != 0 && !Complex.getSettingsManager().getSettingByName("CoolDown").getValBoolean()) {
                    if (attackTimer.hasTimeElapsed(aps, true)) {
                        if (validEntity(target)) {
                            RayTraceResult ray = rayCast(mc.thePlayer, target.posX, target.posY + target.getEyeHeight(), target.posZ);

                            if (ray != null) {
                                Entity entityHit = ray.entityHit;

                                if (entityHit instanceof EntityLivingBase)
                                    if (validEntity((EntityLivingBase) entityHit)) {
                                        target = (EntityLivingBase) entityHit;
                                    }
                            }

                            EventAttack ej = new EventAttack(target, true);
                            EventAttack ej2 = new EventAttack(target, false);
                            ej.call();
                            mc.playerController.attackEntity(mc.thePlayer, target);
                            mc.thePlayer.swingArm(EnumHand.MAIN_HAND);
                            if (mode.equalsIgnoreCase("Test")) {
                                for (EntityLivingBase targ : targets) {
                                    mc.getConnection().sendPacket(new CPacketUseEntity(targ, CPacketUseEntity.Action.ATTACK));
                                    mc.thePlayer.attackTargetEntityWithCurrentItem(targ);
                                }
                            }
                            attacking = true;
                            ej2.call();
                            attackTimer.reset();
                        }
                    }
                } else if (PlayerUtil.getCooldown() >= 1) {
                    if (validEntity(target)) {
                        RayTraceResult ray = rayCast(mc.thePlayer, target.posX, target.posY + target.getEyeHeight(), target.posZ);

                        if (ray != null) {
                            Entity entityHit = ray.entityHit;

                            if (entityHit instanceof EntityLivingBase)
                                if (validEntity((EntityLivingBase) entityHit)) {
                                    target = (EntityLivingBase) entityHit;
                                }
                        }

                        EventAttack ej = new EventAttack(target, true);
                        EventAttack ej2 = new EventAttack(target, false);
                        ej.call();
                        mc.playerController.attackEntity(mc.thePlayer, target);
                        mc.thePlayer.swingArm(EnumHand.MAIN_HAND);
                        attacking = true;
                        ej2.call();
                        attackTimer.reset();
                    }
                }

                if (autoblock) {
                    if (mc.thePlayer.getHeldItem(EnumHand.OFF_HAND).getItem() instanceof ItemShield) {
                        mc.thePlayer.activeItemStackUseCount = 999;
                    }
                }
            } else {
                if (mc.thePlayer.isActiveItemStackBlocking()) {
                    mc.thePlayer.activeItemStackUseCount = 0;
                }
            }
        }
    }
    @EventTarget
    public void onRender2D(EventRender2D er) {
        EntityPlayer player = (EntityPlayer) target;
        if (player != null && Complex.getSettingsManager().getSettingByName("TargetHUD").getValBoolean()) {
            GlStateManager.pushMatrix();
            GlStateManager.translate((float)(er.getResolution().getScaledWidth() / 2 + 10), (float)(er.getResolution().getScaledHeight() - 90), 0.0F);
            RenderingUtils.drawRect(0.0D, 0.0D, 125.0D, 36.0D, Colors.getColor(0, 150));
            mc.fontRendererObj.drawStringWithShadow(player.getName(), 38.0F, 2.0F, -1);
            float health = player.getHealth();
            float[] fractions = new float[]{0.0F, 0.5F, 1.0F};
            Color[] colors = new Color[]{Color.RED, Color.YELLOW, Color.GREEN};
            float progress = health / player.getMaxHealth();
            Color customColor = health >= 0.0F ? ESP.blendColors(fractions, colors, progress).brighter() : Color.RED;
            double width = mc.fontRendererObj.getStringWidth(player.getName());
            width = MathUtils.getIncremental(width, 10.0D);
            if (width < 50.0D) {
                width = 50.0D;
            }

            double healthLocation = width * (double)progress;
            this.healthBarWidth = AnimationUtil.calculateCompensation((float) healthLocation, healthBarWidth, 5, 5f);

            RenderingUtils.drawBorderedRect(37.0D, 11.0D, 39.0D + width, 15.0D, 0.5D, Colors.getColor(0, 0), Colors.getColor(0));
            RenderingUtils.drawRect(37.5D, 11.5D, 38.0D + healthBarWidth + 0.5D, 14.5D, customColor.getRGB());

            RenderingUtils.drawBorderedRect(1.0D, 1.0D, 35.0D, 35.0D, 0.5D, Colors.getColor(0, 0), Colors.getColor(255));
            GlStateManager.scale(0.5D, 0.5D, 0.5D);
            String str = "HP: " + (int)health + " | Dist: " + (int)mc.thePlayer.getDistanceToEntity(player);
            mc.fontRendererObj.drawStringWithShadow(str, 76.0F, 35.0F, -1);
            String str2 = String.format("Yaw: %s Pitch: %s BodyYaw: %s", (int)player.rotationYaw, (int)player.rotationPitch, (int)player.renderYawOffset);
            mc.fontRendererObj.drawStringWithShadow(str2, 76.0F, 47.0F, -1);
            String str3 = String.format("TOG: %s HURT: %s TE: %s", player.ticksOnGround, AuthenticationUtil.authListPos, player.ticksExisted);
            mc.fontRendererObj.drawStringWithShadow(str3, 76.0F, 59.0F, -1);
            GlStateManager.scale(2.0F, 2.0F, 2.0F);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            List var5 = GuiPlayerTabOverlay.ENTRY_ORDERING.sortedCopy(mc.thePlayer.connection.getPlayerInfoMap());
            Iterator var17 = var5.iterator();

            while(var17.hasNext()) {
                Object aVar5 = var17.next();
                NetworkPlayerInfo var24 = (NetworkPlayerInfo)aVar5;
                if (mc.theWorld.getPlayerEntityByUUID(var24.getGameProfile().getId()) == player) {
                    mc.getTextureManager().bindTexture(var24.getLocationSkin());
                    Gui.drawScaledCustomSizeModalRect(2, 2, 8.0F, 8.0F, 8, 8, 32, 32, 64.0F, 64.0F);
                    if (player.isWearing(EnumPlayerModelParts.HAT)) {
                        Gui.drawScaledCustomSizeModalRect(2, 2, 40.0F, 8.0F, 8, 8, 32, 32, 64.0F, 64.0F);
                    }
                    GlStateManager.bindTexture(0);
                    break;
                }
            }
            GlStateManager.popMatrix();
        }
    }
    @EventTarget
    public void onRender3D(EventRender3D render3D) {
        if (target != null) {
            int color = target.hurtResistantTime > 15 ? Colors.getColor(new Color(255, 70, 70, 150)) : Colors.getColor(new Color(255, 255, 255, 150));
            if (mode.equalsIgnoreCase("Test")) {
                for (EntityLivingBase ent : targets) {
                    drawESP(ent, color);
                }
            } else {
                drawESP(target, color);
            }
        }
    }

    public void drawESP(Entity entity, int color) {
        double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * mc.timer.renderPartialTicks;
        double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * mc.timer.renderPartialTicks + entity.getEyeHeight() * 1.2;
        double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * mc.timer.renderPartialTicks;
        double width = Math.abs(entity.boundingBox.maxX - entity.boundingBox.minX) + 0.2;
        double height = 0.1;

        Vec3d vec = new Vec3d(x - width / 2, y, z - width / 2);
        Vec3d vec2 = new Vec3d(x + width / 2, y + height, z + width / 2);
        RenderingUtils.pre3D();
        mc.entityRenderer.setupCameraTransform(mc.timer.renderPartialTicks, 2);
        RenderingUtils.glColor(color);
        RenderingUtils.drawBoundingBox(new AxisAlignedBB(vec.xCoord - RenderManager.renderPosX, vec.yCoord - RenderManager.renderPosY, vec.zCoord - RenderManager.renderPosZ, vec2.xCoord - RenderManager.renderPosX, vec2.yCoord - RenderManager.renderPosY, vec2.zCoord - RenderManager.renderPosZ));
        GL11.glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
        RenderingUtils.post3D();
    }
    private void sortTargets() {
        Sort = Complex.getSettingsManager().getSettingByName("Sort Mode").getValString();

        switch (Sort) {
            case "Range":
                targets.sort(Comparator.comparingDouble(target::getDistanceToEntity));
                break;

            case "Health":
                targets.sort(Comparator.comparingDouble(EntityLivingBase::getHealth));
                break;

            case "Angle":
                targets.sort(Comparator.comparingDouble(RotationUtils::getYawChangeToEntity));
                break;

            case "Armor":
                targets.sort(Comparator.comparingDouble(EntityLivingBase::getTotalArmorValue));
                break;
        }
    }
    private void collectTargets() {
        targets.clear();

        for (Entity entity : mc.thePlayer.getEntityWorld().loadedEntityList) {
            if (entity instanceof EntityLivingBase) {
                EntityLivingBase entityLivingBase = (EntityLivingBase) entity;

                if (validEntity(entityLivingBase)) {
                    targets.add(entityLivingBase);
                }
            }
        }
    }
    boolean validEntity(EntityLivingBase entity) {
        double range = Complex.getSettingsManager().getSettingByName("Range").getValDouble();
        boolean players = Complex.getSettingsManager().getSettingByName("Players").getValBoolean();
        boolean animals = Complex.getSettingsManager().getSettingByName("Animals").getValBoolean();
        boolean mobs = Complex.getSettingsManager().getSettingByName("Mobs").getValBoolean();
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
                    if (entity instanceof EntityIronGolem || entity instanceof EntityAnimal || entity instanceof EntityVillager) {
                        if (entity.getName().equals("Villager") && entity instanceof EntityVillager) {
                            return false;
                        }
                        return true;
                    }
                }
                if (mobs) {
                    if (entity instanceof EntityMob) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    private RayTraceResult rayCast(final EntityLivingBase entityLivingBase, double x, double y, double z) {
        final float[] angles = RotationUtils.getAngles(entityLivingBase);
        if (angles == null) {
            return null;
        }
        return RayTraceUtil.rayTrace(this.mc.timer.renderPartialTicks, x, y, z, angles[1], angles[0]);
    }
    public static int randomNumber(int max, int min) {
        return Math.round(min + (float)Math.random() * ((max - min)));
    }
}
