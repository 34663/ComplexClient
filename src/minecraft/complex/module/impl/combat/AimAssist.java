package complex.module.impl.combat;

import complex.Complex;
import complex.command.Command;
import complex.event.EventTarget;
import complex.event.impl.EventUpdate;
import complex.module.Category;
import complex.module.Module;
import complex.module.data.Setting;
import complex.utils.RotationUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSword;
import net.minecraft.util.EnumHand;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class AimAssist extends Module {
    private EntityLivingBase target;

    public AimAssist() {
        super("AimAssist", null, 0, 0, Category.Combat);
    }

    @Override
    public void setup() {
        Complex.getSettingsManager().rSetting(new Setting("CheckWeapon", this, true));
        Complex.getSettingsManager().rSetting(new Setting("X", this, 0, 0, 1.00, false));
        Complex.getSettingsManager().rSetting(new Setting("Y", this, 0, 0, 1.00, false));
        Complex.getSettingsManager().rSetting(new Setting("AssistRange", this, 4.2, 0.1, 10.0, false));
        Complex.getSettingsManager().rSetting(new Setting("HORIZONTAL", this, 10, 0, 10.0, false));
        Complex.getSettingsManager().rSetting(new Setting("VERTICAL", this, 10, 0, 10.0, false));
        Complex.getSettingsManager().rSetting(new Setting("FovYaw", this, 45, 5, 50, true));
        Complex.getSettingsManager().rSetting(new Setting("FovPitch", this, 25, 5, 50, true));
    }

    @EventTarget
    public void onUpdate(EventUpdate eventUpdate) {
        this.setDisplayName("AimAssist");

        if (eventUpdate.isPre()) {
            target = getBestEntity();
        } else if (eventUpdate.isPost() && mc.currentScreen == null) {
            if (mc.thePlayer.getHeldItem(EnumHand.MAIN_HAND) == null || mc.thePlayer.getHeldItem(EnumHand.MAIN_HAND).getItem() == null) {
                return;
            }
            final Item heldItem = mc.thePlayer.getHeldItem(EnumHand.MAIN_HAND).getItem();
            if (Complex.getSettingsManager().getSettingByName("CheckWeapon").getValBoolean() && heldItem != null) {
                if (!(heldItem instanceof ItemSword)) {
                    return;
                }
            }
            if (target != null && mc.thePlayer.isEntityAlive()) {
                stepAngle();
            }
        }
    }

    private void stepAngle() {
        float yawFactor = (float) Complex.getSettingsManager().getSettingByName("HORIZONTAL").getValDouble();
        float pitchFactor = (float) Complex.getSettingsManager().getSettingByName("VERTICAL").getValDouble();
        double xz = Complex.getSettingsManager().getSettingByName("X").getValDouble();
        double y = Complex.getSettingsManager().getSettingByName("Y").getValDouble();
        float targetYaw = RotationUtils.getYawChange(mc.thePlayer.rotationYaw, target.posX + randomNumber() * xz, target.posZ + randomNumber() * xz);

        if (targetYaw > 0 && targetYaw > yawFactor) {
            mc.thePlayer.rotationYaw += yawFactor;
        } else if (targetYaw < 0 && targetYaw < -yawFactor) {
            mc.thePlayer.rotationYaw -= yawFactor;
        } else {
            mc.thePlayer.rotationYaw += targetYaw;
        }

        float targetPitch = RotationUtils.getPitchChange(mc.thePlayer.rotationPitch, target, target.posY + randomNumber() * y);

        if (targetPitch > 0 && targetPitch > pitchFactor) {
            mc.thePlayer.rotationPitch += pitchFactor;
        } else if (targetPitch < 0 && targetPitch < -pitchFactor) {
            mc.thePlayer.rotationPitch -= pitchFactor;
        } else {
            mc.thePlayer.rotationPitch += targetPitch;
        }
    }
    private EntityLivingBase getBestEntity() {
        List<EntityLivingBase> loaded = new CopyOnWriteArrayList<>();
        for (Object o : mc.theWorld.getLoadedEntityList()) {
            if (o instanceof EntityLivingBase) {
                EntityLivingBase ent = (EntityLivingBase) o;
                if (ent.isEntityAlive() && ent instanceof EntityPlayer && ent.getDistanceToEntity(mc.thePlayer) < Complex.getSettingsManager().getSettingByName("AssistRange").getValDouble() && fovCheck(ent)) {
                    if (ent == Aura.target) {
                        return ent;
                    }
                    loaded.add(ent);
                }
            }
        }
        if (loaded.isEmpty()) {
            return null;
        }
        try {
            loaded.sort((o1, o2) -> {
                float[] rot1 = RotationUtils.getRotations(o1);
                float[] rot2 = RotationUtils.getRotations(o2);
                return (int) ((RotationUtils.getDistanceBetweenAngles(mc.thePlayer.rotationYaw, rot1[0]) + RotationUtils.getDistanceBetweenAngles(mc.thePlayer.rotationPitch, rot1[1])) - (RotationUtils.getDistanceBetweenAngles(mc.thePlayer.rotationYaw, rot2[0]) + RotationUtils.getDistanceBetweenAngles(mc.thePlayer.rotationPitch, rot2[1])));
            });
        } catch (Exception e) {
            Command.sendChatMessage("Exception with TM: " + e.getMessage());
        }
        return loaded.get(0);
    }
    private boolean fovCheck(EntityLivingBase ent) {
        float[] rotations = RotationUtils.getRotations(ent);
        float dist = mc.thePlayer.getDistanceToEntity(ent);
        if (dist == 0) {
            dist = 1;
        }
        float yawDist = RotationUtils.getDistanceBetweenAngles(mc.thePlayer.rotationYaw, rotations[0]);
        float pitchDist = RotationUtils.getDistanceBetweenAngles(mc.thePlayer.rotationPitch, rotations[1]);
        float fovYaw = (float) (Complex.getSettingsManager().getSettingByName("FovYaw").getValDouble() * 3 / dist);
        float fovPitch = (float) ((Complex.getSettingsManager().getSettingByName("FovPitch").getValDouble() * 3) / dist);
        return yawDist < fovYaw && pitchDist < fovPitch;
    }

    private int randomNumber() {
        return -1 + (int) (Math.random() * ((1 - (-1)) + 1));
    }
}
