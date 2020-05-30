package complex.module.impl.visual;

import complex.Complex;
import complex.event.EventTarget;
import complex.event.impl.EventRender3D;
import complex.module.Category;
import complex.module.Module;
import complex.module.data.Setting;
import complex.module.impl.combat.AntiBot;
import complex.utils.render.RenderingUtils;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;

public class Tracer extends Module {
    private String mode;
    float h;

    public Tracer() {
        super("Tracer", null, 0, 0, Category.Visual);
    }

    @Override
    public void setup() {
        ArrayList<String> options = new ArrayList<>();
        options.add("Distance");
        options.add("Rainbow");
        options.add("Team");
        options.add("Custom");
        Complex.getSettingsManager().rSetting(new Setting("TracerColor", this, "Distance", options));
    }

    @EventTarget
    public void onRender3D(EventRender3D er) {
        mode = Complex.getSettingsManager().getSettingByName("TracerColor").getValString();
        boolean bobbing;
        if (h > 255) {
            h = 0;
        }
        h += 1.0;

        for (Entity entities : mc.theWorld.loadedEntityList) {
            if (entities != mc.thePlayer && entities != null) {
                if (entities instanceof EntityPlayer) {
                    float distance = mc.getRenderViewEntity().getDistanceToEntity(entities);
                    float posX = (float) ((float) (entities.lastTickPosX + (entities.posX - entities.lastTickPosX) * er.renderPartialTicks) - RenderManager.renderPosX);
                    float posY = (float) ((float) (entities.lastTickPosY + (entities.posY - entities.lastTickPosY) * er.renderPartialTicks) - RenderManager.renderPosY);
                    float posZ = (float) ((float) (entities.lastTickPosZ + (entities.posZ - entities.lastTickPosZ) * er.renderPartialTicks) - RenderManager.renderPosZ);

                    if (mode.equalsIgnoreCase("Distance")) {
                        if (distance <= 6F) {
                            GL11.glColor3f(1.0F, 0.0F, 0.0F);
                        } else if (distance <= 96F) {
                            GL11.glColor3f(1.0F, (distance / 100F), 0.0F);
                        } else if (distance > 96F) {
                            GL11.glColor3f(0.1F, 0.6F, 255.0F);
                        }
                    } else if (mode.equalsIgnoreCase("Rainbow")) {
                        final Color color = Color.getHSBColor(h / 255.0f, 0.6f, 1.0f);
                        RenderingUtils.glColor(color.getRGB());
                    } else if (mode.equalsIgnoreCase("Team")) {
                        String text = entities.getDisplayName().getFormattedText();
                        if (Character.toLowerCase(text.charAt(0)) == '§') {
                            char oneMore = Character.toLowerCase(text.charAt(999));
                            int colorCode = "0123456789abcdefklmnorg".indexOf(oneMore);
                            if (colorCode < 16) {
                                try {
                                    int newColor = mc.fontRendererObj.colorCode[colorCode];
                                    GL11.glColor4d((newColor >> 16), (newColor >> 8 & 0xFF), (newColor & 0xFF), 255);
                                } catch (ArrayIndexOutOfBoundsException ignored) {
                                }
                            }
                        } else {
                            RenderingUtils.glColor(255, 255, 255, 255);
                        }
                    } else if (mode.equalsIgnoreCase("Custom")) {
                        GL11.glColor3f(255, 255, 255);
                    }

                    if (AntiBot.getInvalid().contains(entities)) {
                        RenderingUtils.glColor(200, 100, 100, 100);
                    }

                    bobbing = mc.gameSettings.viewBobbing;
                    mc.gameSettings.viewBobbing = false;
                    RenderingUtils.draw3DLine(posX, posY, posZ, 1.5F);
                    mc.gameSettings.viewBobbing = bobbing;
                }
            }
        }
    }
}
