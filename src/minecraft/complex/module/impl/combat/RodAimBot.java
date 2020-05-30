package complex.module.impl.combat;

import complex.event.EventTarget;
import complex.event.impl.EventUpdate;
import complex.module.Category;
import complex.module.Module;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;

import java.util.List;

public class RodAimBot extends Module {
    public RodAimBot() {
        super("RodAimBot", null, 0, 0, Category.Combat);
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        final boolean var9 = false;
        final List l = mc.theWorld.loadedEntityList;
        if (mc.thePlayer == null) {
            return;
        }
        if (mc.thePlayer.getHeldItem(EnumHand.MAIN_HAND) == null) {
            return;
        }
        if (!(mc.thePlayer.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemFishingRod)) {
            return;
        }
        EntityLivingBase entity = null;
        for (final Object o : l) {
            if (o instanceof EntityLivingBase) {
                final EntityLivingBase elb = (EntityLivingBase) o;
                if (!mc.thePlayer.canEntityBeSeen(elb)) {
                    continue;
                }
                if (elb.getName().contains("Shop")) {
                    return;
                }
                if (entity == null) {
                    if (elb == mc.thePlayer) {
                        continue;
                    }
                    entity = elb;
                } else {
                    if (mc.thePlayer.getDistanceToEntity(entity) <= mc.thePlayer.getDistanceToEntity(elb) || elb == mc.thePlayer) {
                        continue;
                    }
                    entity = elb;
                }
            }
        }
        if (entity == mc.thePlayer || entity == null) {
            return;
        }
        if (entity.getDistanceToEntity(mc.thePlayer) >= 15.0f) {
            return;
        }
        final double var10 = mc.thePlayer.getDistanceSq(entity.posX, entity.getEntityBoundingBox().minY, entity.posZ);
        final float var11 = MathHelper.sqrt_float(var10) / 15.0f;
        final float var12 = MathHelper.clamp(var11, 0.1f, 1.0f);
        final float[] rot = this.attackEntityWithRangedAttack(entity, var12);
        event.setPitch(rot[0]);
        event.setYaw(rot[1]);
    }

    public float[] attackEntityWithRangedAttack(final EntityLivingBase p_82196_1_, final float p_82196_2_) {
        final EntityArrow var3 = new EntityArrow(mc.theWorld, mc.thePlayer, p_82196_1_, 0.0f, 14.0f) {
            @Override
            protected ItemStack getArrowStack() {
                return null;
            }
        };
        final double var4 = mc.thePlayer.getDistance(p_82196_1_.posX, p_82196_1_.getEntityBoundingBox().minY, p_82196_1_.posZ) * 0.7;
        final float prevX = (float) ((p_82196_1_.prevPosX - p_82196_1_.posX) * var4);
        final float prevZ = (float) ((p_82196_1_.prevPosZ - p_82196_1_.posZ) * var4);
        final float prevY = 0.0f;
        var3.posY = mc.thePlayer.posY + mc.thePlayer.getEyeHeight() - 0.10000000149011612;
        final double var5 = p_82196_1_.posX - prevX - mc.thePlayer.posX;
        final double var6 = p_82196_1_.getEntityBoundingBox().minY + p_82196_1_.height / 2.0f - prevY - var3.posY;
        final double var7 = p_82196_1_.posZ - prevZ - mc.thePlayer.posZ;
        final double var8 = MathHelper.sqrt_double(var5 * var5 + var7 * var7);
        if (var8 >= 1.0E-7) {
            final float var9 = (float) (Math.atan2(var7, var5) * 180.0 / 3.141592653589793) - 90.0f;
            final float var10 = (float) (-(Math.atan2(var6, var8) * 180.0 / 3.141592653589793) - var4 * 1.2);
            final double var11 = var5 / var8;
            final double var12 = var7 / var8;
            var3.setLocationAndAngles(mc.thePlayer.posX + var11, var3.posY, mc.thePlayer.posZ + var12, var9, var10);
        }
        final float[] rot = {var3.rotationPitch, var3.rotationYaw};
        return rot;
    }
}
