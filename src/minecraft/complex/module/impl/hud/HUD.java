package complex.module.impl.hud;

import complex.Complex;
import complex.command.Command;
import complex.event.EventTarget;
import complex.event.impl.EventRender2D;
import complex.gui.clickgui.util.ColorUtil;
import complex.gui.clickgui.util.FontUtil;
import complex.module.Category;
import complex.module.Module;
import complex.module.ModuleManager;
import complex.module.data.ModuleComparator;
import complex.module.data.Setting;
import complex.utils.font.CFontRenderer;
import complex.utils.font.FontLoaders;
import complex.utils.render.Colors;
import complex.utils.render.RenderingUtils;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;

import java.awt.Color;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class HUD extends Module {
    private String mode, updown, hud;
    private int brightness = 130;
    private boolean ascending;
    private float[] hsb = new float[3];

    public HUD() {
        super("HUD", null, 0, 0, Category.Visual);
    }

    @Override
    public void setup() {
        ArrayList<String> options = new ArrayList<>();
        ArrayList<String> options2 = new ArrayList<>();
        ArrayList<String> options3 = new ArrayList<>();
        options.add("Rainbow");
        options.add("Pulsing");
        options.add("Poop");
        options.add("Bruh");
        options.add("Candy");
        options.add("Remix");
        options.add("Client");
        options.add("Default");
        options.add("Dev");

        options2.add("Up");
        options2.add("Down");

        options3.add("Default");
        options3.add("New");
        Complex.getSettingsManager().rSetting(new Setting("HUD Mode", this, "New", options3));
        Complex.getSettingsManager().rSetting(new Setting("HUDColor", this, "Rainbow", options));
        Complex.getSettingsManager().rSetting(new Setting("Wave Mode", this, "Up", options2));
        Complex.getSettingsManager().rSetting(new Setting("HUD Speed", this, 5, 1, 20, true));
    }

    @Override
    public void onEnable() {
        this.setDisplayName("HUD");
        super.onEnable();
    }

    @EventTarget
    public void onRender2D(EventRender2D er) {
        mode = Complex.getSettingsManager().getSettingByName("HUDColor").getValString();
        updown = Complex.getSettingsManager().getSettingByName("Wave Mode").getValString();
        hud = Complex.getSettingsManager().getSettingByName("HUD Mode").getValString();
        ScaledResolution sr = new ScaledResolution(mc);
        Color temp = ColorUtil.getClickGUIColor();
        CFontRenderer font = FontLoaders.default18;

        DecimalFormat df = new DecimalFormat("0.0000");
        String speed = df.format(Math.sqrt(mc.thePlayer.motionX * mc.thePlayer.motionX + mc.thePlayer.motionZ * mc.thePlayer.motionZ));
        font.drawStringWithShadow(speed, sr.getScaledWidth() / 2 - 2, sr.getScaledHeight() - 50, 0xffffff);

        int[] counter = {1};
        int yy = 1;
        int color = 0;

        if (mode.equalsIgnoreCase("Pulsing")) {
            if (ascending) {
                brightness += 2;
            } else {
                brightness -= 2;
            }
            if (brightness <= 130F) {
                ascending = true;
            }
            if (brightness >= 250F) {
                ascending = false;
            }
            Color c = new Color(temp.getRed(), temp.getGreen(), temp.getBlue());
            this.hsb = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), hsb);
            color = Color.getHSBColor(this.hsb[0], this.hsb[1], this.brightness / 255.0F).getRGB();
        } else if (mode.equalsIgnoreCase("Client")) {
            color  = Colors.getColor(temp.getRed(), temp.getGreen(), temp.getBlue());
        } else if (mode.equalsIgnoreCase("Default")) {
            color = Colors.getColor(255, 255, 255);
        }

        if (!mc.gameSettings.showDebugInfo) {
            ModuleManager.modules.sort(new ModuleComparator());
            for (Module m : Complex.getModuleManager().getModules()) {
                if (m.isEnabled() && !isCategory(Category.Hidden)) {
                    double x = sr.getScaledWidth() - m.getAnimation() - 2;
                    double y = yy + 3;

                    if (mode.equalsIgnoreCase("Rainbow")) {
                        color = Colors.rainbow(counter[0] * 120);
                    } else if (mode.equalsIgnoreCase("Remix")) {
                        color = TwoColoreffect(new Color(31, 156, 255), new Color(25, 55, 143), Math.abs(System.currentTimeMillis() / 10L) / 100.0 + 3.0F * (counter[0] * 2.55) / 60).getRGB();
                    } else if (mode.equalsIgnoreCase("Candy")) {
                        color = TwoColoreffect(new Color(71, 255, 249), new Color(255, 102, 204), Math.abs(System.currentTimeMillis() / 10L) / 100.0 + 3.0F * (counter[0] * 2.55) / 60).getRGB();
                    } else if (mode.equalsIgnoreCase("Dev")) {
                        color = TwoColoreffect(new Color(64, 118, 255), new Color(255, 136, 100), Math.abs(System.currentTimeMillis() / 10L) / 100.0 + 3.0F * (counter[0] * 2.55) / 60).getRGB();
                    } else if (mode.equalsIgnoreCase("Bruh")) {
                        color = TwoColoreffect(new Color(70, 255, 0), new Color(255, 224, 68), Math.abs(System.currentTimeMillis() / 10L) / 100.0 + 3.0F * (counter[0] * 2.55) / 60).getRGB();
                    } else if (mode.equalsIgnoreCase("Poop")) {
                        color = TwoColoreffect(new Color(140, 49, 0), new Color(255, 224, 68), Math.abs(System.currentTimeMillis() / 10L) / 100.0 + 3.0F * (counter[0] * 2.55) / 60).getRGB();
                    }

                    if (hud.equalsIgnoreCase("Default")) {
                        for (int i = 0; i < Complex.getSettingsManager().getSettingByName("HUD Speed").getValDouble(); i++) {
                            if (m.getAnimation() < FontUtil.getStringWidth(m.getDisplayName()))
                                m.setAnimation(m.getAnimation() + 1);
                            if (m.getAnimation() > FontUtil.getStringWidth(m.getDisplayName()))
                                m.setAnimation(m.getAnimation() - 1);
                            if (m.getAnimation2() < FontUtil.getFontHeight())
                                m.setAnimation2(m.getAnimation2() + 1);
                            if (m.getAnimation2() > FontUtil.getFontHeight())
                                m.setAnimation2(m.getAnimation2() - 1);
                        }
                        RenderingUtils.drawRect(x - 7, y - 2, x + FontUtil.getStringWidth(m.getDisplayName()) - 1, y + FontUtil.getFontHeight(), Colors.getColor(new Color(0, 0, 0, 150)));
                        RenderingUtils.drawRect(x + FontUtil.getStringWidth(m.getDisplayName()) - 2, y - 2, x + FontUtil.getStringWidth(m.getDisplayName()) - 1, y + FontUtil.getFontHeight(), color);
                        FontUtil.drawStringWithShadow(m.getDisplayName(), x - 4, y - .5, hud.equalsIgnoreCase("Test") ? 0xffffff : color);
                        yy += 11;
                    } else if (hud.equalsIgnoreCase("New")) {
                        for (int i = 0; i < Complex.getSettingsManager().getSettingByName("HUD Speed").getValDouble(); i++) {
                            if (m.getAnimation() < font.getStringWidth(m.getDisplayName()))
                                m.setAnimation(m.getAnimation() + 1);
                            if (m.getAnimation() > font.getStringWidth(m.getDisplayName()))
                                m.setAnimation(m.getAnimation() - 1);

                            if (m.getAnimation2() < font.getStringHeight(m.getDisplayName()))
                                m.setAnimation2(m.getAnimation2() + 1);
                            if (m.getAnimation2() > font.getStringHeight(m.getDisplayName()))
                                m.setAnimation2(m.getAnimation2() - 1);
                        }
                        RenderingUtils.drawRect(x-3.0, y-2, sr.getScaledWidth(), y+font.getStringHeight(m.getDisplayName()), Colors.getColor(new Color(34, 34, 34, 255)));
                        font.drawStringWithShadow(m.getDisplayName(), x - 1, y - .5, color);
                        yy += 10;
                    }
                    if (updown.equalsIgnoreCase("Up")) {
                        counter[0] += 1;
                    } else if (updown.equalsIgnoreCase("Down")) {
                        counter[0] -= 1;
                    }
                    continue;
                }
                if (m.getAnimation() > 0)
                    m.setAnimation(0);
            }
        }
    }

    public static void drawWaveString(String str, CFontRenderer font, float x, float y, float brightness) {
        float posX = x;
        for (int i = 0; i < str.length(); i++) {
            String ch = str.charAt(i) + "";
            font.drawStringWithShadow(ch, posX, y, effect(i * 3500000L, brightness, 150).getRGB());
            posX += font.getStringWidth(ch) / 0.9F;
        }
    }
    public static void Candy(String str, CFontRenderer font, float x, float y, float brightness) {
        float posX = x;
        for (int i = 0; i < str.length(); i++) {
            String ch = str.charAt(i) + "";
            font.drawString(ch, posX, y, TwoColoreffect(new Color(255, 187, 1), new Color(64, 119, 222), Math.abs(System.currentTimeMillis() / 10L) / 100.0 + 3.0F * (i * 3.55) / 60).getRGB());
            posX += font.getStringWidth(ch) / 0.9F;
        }
    }
    public static Color effect(long offset, float brightness, int speed) {
        float hue = (float) (System.nanoTime() + (offset * speed)) / 1.0E10F % 1.0F;
        long color = Long.parseLong(Integer.toHexString(Color.HSBtoRGB(hue, brightness, 1F)), 16);
        Color c = new Color((int) color);
        return new Color(c.getGreen() / 255.0f, c.getBlue() / 255.0F, c.getRed() / 255.0F, c.getAlpha() / 255.0F);
    }
    public static Color TwoColoreffect(final Color color, final Color color2, double delay) {
        if (delay > 1.0) {
            final double n2 = delay % 1.0;
            delay = (((int) delay % 2 == 0) ? n2 : (1.0 - n2));
        }
        final double n3 = 1.0 - delay;
        return new Color((int) (color.getRed() * n3 + color2.getRed() * delay), (int) (color.getGreen() * n3 + color2.getGreen() * delay), (int) (color.getBlue() * n3 + color2.getBlue() * delay), (int) (color.getAlpha() * n3 + color2.getAlpha() * delay));
    }
}
