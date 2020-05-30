package complex.module.impl.visual;

import complex.Complex;
import complex.event.EventTarget;
import complex.event.impl.EventRender3D;
import complex.event.impl.EventRenderEntity;
import complex.event.impl.EventUpdate;
import complex.module.Category;
import complex.module.Module;
import complex.module.data.Setting;
import complex.module.impl.combat.AntiBot;
import complex.module.impl.hud.HUD;
import complex.utils.TeamUtils;
import complex.utils.render.Colors;
import complex.utils.render.OutlineUtils;
import complex.utils.render.RenderingUtils;
import complex.utils.render.Stencil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.tileentity.TileEntity;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;

public class Chams extends Module {
    private String mode;

    public Chams() {
        super("Chams", null, 0, 0, Category.Visual);
    }

    @Override
    public void setup() {
        ArrayList<String> options = new ArrayList<>();
        options.add("Fill");
        options.add("Wall");
        Complex.getSettingsManager().rSetting(new Setting("Chams Mode", this, "Fill", options));
        Complex.getSettingsManager().rSetting(new Setting("Entitys", this, true));
        Complex.getSettingsManager().rSetting(new Setting("Items", this, false));
        Complex.getSettingsManager().rSetting(new Setting("CRed", this, 20, 0, 255, true));
        Complex.getSettingsManager().rSetting(new Setting("CGreen", this, 255, 0, 255, true));
        Complex.getSettingsManager().rSetting(new Setting("CBlue", this, 180, 0, 255, true));
        Complex.getSettingsManager().rSetting(new Setting("CAlpha", this, 255, 25, 255, true));
    }

    @EventTarget
    public void onUpdate(EventUpdate em) {
        mode = Complex.getSettingsManager().getSettingByName("Chams Mode").getValString();

        if (mode.equalsIgnoreCase("Fill")) {
            this.setDisplayName("Chams §7Fill");
        } else if (mode.equalsIgnoreCase("Wall")) {
            this.setDisplayName("Chams §7Wall");
        }
    }
    @EventTarget
    public void onRenderEntity(EventRenderEntity er) {
        int red = (int) Complex.getSettingsManager().getSettingByName("CRed").getValDouble();
        int green = (int) Complex.getSettingsManager().getSettingByName("CGreen").getValDouble();
        int blue = (int) Complex.getSettingsManager().getSettingByName("CBlue").getValDouble();
        int alpha = (int) Complex.getSettingsManager().getSettingByName("CAlpha").getValDouble();
        int color = new Color(red, green, blue, alpha).getRGB();
        int bot = new Color(100, 100, 100, alpha).getRGB();

        if (er.getEntity() instanceof EntityPlayer && er.isPre() && mode.equalsIgnoreCase("Fill")) {
            er.setCancelled(true);

            try {
                final EntityLivingBase entityLiving = er.getEntity();
                Render var11 = mc.getRenderManager().getEntityRenderObject(er.getEntity());
                if (var11 != null && mc.getRenderManager().renderEngine != null && var11 instanceof RenderLivingBase) {
                    GL11.glPushMatrix();
                    GL11.glDisable(2929);
                    GL11.glDisable(3553);
                    GL11.glEnable(3042);
                    GlStateManager.disableLighting();

                    if (AntiBot.getInvalid().contains(entityLiving)) {
                        color = bot;
                    } else {
                        color = entityLiving.hurtTime >= 1 ? new Color(255, 50, 50, alpha).getRGB() : HUD.TwoColoreffect(new Color(71, 255, 249, alpha), new Color(255, 102, 204, alpha), Math.abs(System.currentTimeMillis() / 10L) / 100.0 + 3.0F / 60).getRGB();
                    }
                    RenderingUtils.glColor(color);
                    ((RenderLivingBase) var11).renderModel(er.getEntity(), er.getLimbSwing(), er.getLimbSwingAmount(), er.getAgeInTicks(), er.getRotationYawHead(), er.getRotationPitch(), er.getOffset());
                    GL11.glEnable(2929);
                    RenderingUtils.glColor(color);
                    ((RenderLivingBase) var11).renderModel(er.getEntity(), er.getLimbSwing(), er.getLimbSwingAmount(), er.getAgeInTicks(), er.getRotationYawHead(), er.getRotationPitch(), er.getOffset());
                    GL11.glEnable(3553);
                    GL11.glDisable(3042);
                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                    GlStateManager.enableLighting();

                    GL11.glPopMatrix();
                    ((RenderLivingBase) var11).renderLayers(er.getEntity(), er.getLimbSwing(), er.getLimbSwingAmount(), mc.timer.renderPartialTicks, er.getAgeInTicks(), er.getRotationYawHead(), er.getRotationPitch(), er.getOffset());
                    GL11.glPopMatrix();
                }
            } catch (Exception ignore) {
                ;
            }
        }
    }
}
