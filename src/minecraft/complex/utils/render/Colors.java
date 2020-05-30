package complex.utils.render;

import complex.module.impl.visual.ESP;
import net.minecraft.entity.player.EntityPlayer;

import java.awt.Color;

public class Colors {
    public static int getColor(Color color) {
        return getColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    public static int getColor(int brightness) {
        return getColor(brightness, brightness, brightness, 255);
    }

    public static int getColor(int brightness, int alpha) {
        return getColor(brightness, brightness, brightness, alpha);
    }

    public static int getColor(int red, int green, int blue) {
        return getColor(red, green, blue, 255);
    }

    public static int getColor(int red, int green, int blue, int alpha) {
        int color = 0;
        color |= alpha << 24;
        color |= red << 16;
        color |= green << 8;
        color |= blue;
        return color;
    }

    public static Color getHealthColor(EntityPlayer player) {
        float health = player.getHealth();
        float[] fractions = new float[]{0.0F, 0.5F, 1.0F};
        Color[] colors = new Color[]{Color.RED, Color.YELLOW, Color.GREEN};
        float progress = health / player.getMaxHealth();
        Color customColor = health >= 0.0F ? ESP.blendColors(fractions, colors, progress).brighter() : Color.RED;

        return customColor;
    }

    public static int rainbow(int delay) {
        double rainbow = Math.ceil((System.currentTimeMillis() + delay) / 8);
        rainbow %= 360.0D;
        return Color.getHSBColor((float) (rainbow / 360.0D), 0.7f, 1.0F).getRGB();
    }

}
