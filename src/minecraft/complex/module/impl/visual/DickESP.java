package complex.module.impl.visual;

import complex.Complex;
import complex.event.EventTarget;
import complex.event.impl.EventRender3D;
import complex.module.Category;
import complex.module.Module;
import complex.module.data.Setting;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Cylinder;
import org.lwjgl.util.glu.Sphere;

public class DickESP extends Module {
    public DickESP() {
        super("DickESP", null, 0, 0, Category.Visual);
    }

    @Override
    public void setup() {
        Complex.getSettingsManager().rSetting(new Setting("SelfLength", this, 0.8, 0.1, 40.0, false));
        Complex.getSettingsManager().rSetting(new Setting("EnemyLength", this, 0.8, 0.1, 40.0, false));
        Complex.getSettingsManager().rSetting(new Setting("Uncircumcised", this, false));
    }

    @EventTarget
    public void onRender3D(EventRender3D event) {
        this.setDisplayName("DickESP");

        for (final Entity entity : this.mc.theWorld.loadedEntityList) {
            if (entity instanceof EntityPlayer) {
                final EntityPlayer entityPlayer = (EntityPlayer) entity;
                final double n = entityPlayer.lastTickPosX + (entityPlayer.posX - entityPlayer.lastTickPosX) * this.mc.timer.renderPartialTicks - this.mc.getRenderManager().getRenderPosX();
                final double n2 = entityPlayer.lastTickPosY + (entityPlayer.posY - entityPlayer.lastTickPosY) * this.mc.timer.renderPartialTicks - this.mc.getRenderManager().getRenderPosY();
                final double n3 = entityPlayer.lastTickPosZ + (entityPlayer.posZ - entityPlayer.lastTickPosZ) * this.mc.timer.renderPartialTicks - this.mc.getRenderManager().getRenderPosZ();
                GL11.glPushMatrix();
                RenderHelper.disableStandardItemLighting();
                GL11.glDisable(2896);
                GL11.glDisable(3553);
                GL11.glEnable(3042);
                GL11.glBlendFunc(770, 771);
                GL11.glDisable(2929);
                GL11.glEnable(2848);
                GL11.glDepthMask(false);
                GL11.glLineWidth(1.0f);
                GL11.glTranslated(n, n2, n3);
                GL11.glRotatef(-entityPlayer.rotationYaw, 0.0f, entityPlayer.height, 0.0f);
                GL11.glTranslated(-n, -n2, -n3);
                GL11.glTranslated(n, n2 + entityPlayer.height / 2.0f - 0.22499999403953552, n3);
                GL11.glColor4f(0.5882353f, 0.29411766f, 0.0f, 1.0f);
                GL11.glTranslated(0.0, 0.0, 0.07500000298023224);
                final Cylinder cylinder = new Cylinder();
                cylinder.setDrawStyle(100012);
                cylinder.draw(0.1f, 0.11f, (entityPlayer == this.mc.thePlayer) ? ((float) Complex.getSettingsManager().getSettingByName("SelfLength").getValDouble()) : (float) Complex.getSettingsManager().getSettingByName("EnemyLength").getValDouble(), 25, 20);
                GL11.glColor4f(0.6082353f, 0.31411764f, 0.0f, 1.0f);
                GL11.glTranslated(0.0, 0.0, 0.02500000298023223);
                GL11.glTranslated(-0.09000000074505805, 0.0, 0.0);
                final Sphere sphere = new Sphere();
                sphere.setDrawStyle(100012);
                sphere.draw(0.14f, 10, 20);
                GL11.glTranslated(0.16000000149011612, 0.0, 0.0);
                final Sphere sphere2 = new Sphere();
                sphere2.setDrawStyle(100012);
                sphere2.draw(0.14f, 10, 20);
                GL11.glColor4f(0.3882353f, 0.15411764f, 0.0f, 1.0f);
                GL11.glTranslated(-0.07000000074505806, 0.0, ((entityPlayer == this.mc.thePlayer) ? Complex.getSettingsManager().getSettingByName("SelfLength").getValDouble() : Complex.getSettingsManager().getSettingByName("EnemyLength").getValDouble()) - (Complex.getSettingsManager().getSettingByName("Uncircumcised").getValBoolean() ? 0.15 : 0.0));
                final Sphere sphere3 = new Sphere();
                sphere3.setDrawStyle(100012);
                sphere3.draw(0.13f, 15, 20);
                GL11.glDepthMask(true);
                GL11.glDisable(2848);
                GL11.glEnable(2929);
                GL11.glDisable(3042);
                GL11.glEnable(2896);
                GL11.glEnable(3553);
                RenderHelper.enableStandardItemLighting();
                GL11.glPopMatrix();
                GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            }
        }
    }
}
