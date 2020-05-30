package complex.utils.misc;

import complex.utils.MCUtil;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

public class MiscUtils implements MCUtil {
    private static MiscUtils instance;
    private float bgd;

    public static double a(final double n, final double n2, final double n3) {
        return n2 + (n - n2) * n3;
    }

    public static int a(final int n, final int n2, final float n3) {
        return Color.getHSBColor((System.currentTimeMillis() + n2) % n / (float)n, n3, 1.0f).getRGB();
    }

    public static double a(final double n, final int n2) {
        if (n2 < 0) {
            throw new IllegalArgumentException();
        }
        return new BigDecimal(n).setScale(n2, RoundingMode.HALF_UP).doubleValue();
    }

    public static double a(final double n, final double n2) {
        double n3 = new Random().nextDouble() * (n2 - n);
        if (n3 > n2) {
            n3 = n2;
        }
        double n4 = n3 + n;
        if (n4 > n2) {
            n4 = n2;
        }
        return n4;
    }

    public static void p_10000(final a[] array, final char c, final float n, final float n2) throws ArrayIndexOutOfBoundsException {
        try {
            instance.a(n, n2, (float) array[c].bfz, (float) array[c].bga, (float) array[c].bgb, (float) array[c].bgc, (float) array[c].bfz, (float) array[c].bga);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    protected void a(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8) {
        final float n9 = n5 / this.bgd;
        final float n10 = n6 / this.bgd;
        final float n11 = n7 / this.bgd;
        final float n12 = n8 / this.bgd;
        GL11.glTexCoord2f(n9 + n11, n10);
        GL11.glVertex2d((double) (n + n3), (double) n2);
        GL11.glTexCoord2f(n9, n10);
        GL11.glVertex2d((double) n, (double) n2);
        GL11.glTexCoord2f(n9, n10 + n12);
        GL11.glVertex2d((double) n, (double) (n2 + n4));
        GL11.glTexCoord2f(n9, n10 + n12);
        GL11.glVertex2d((double) n, (double) (n2 + n4));
        GL11.glTexCoord2f(n9 + n11, n10 + n12);
        GL11.glVertex2d((double) (n + n3), (double) (n2 + n4));
        GL11.glTexCoord2f(n9 + n11, n10);
        GL11.glVertex2d((double) (n + n3), (double) n2);
    }

    public static void b(final double n) {
        mc.thePlayer.motionX = -MathHelper.sin(a()) * n;
        mc.thePlayer.motionZ = MathHelper.cos(a()) * n;
    }

    public static float a() {
        final float rotationYaw = mc.thePlayer.rotationYaw;
        final float moveForward = mc.thePlayer.moveForward;
        final float moveStrafing = mc.thePlayer.moveStrafing;
        float n = rotationYaw + ((moveForward < 0.0f) ? 180 : 0);
        if (moveStrafing < 0.0f) {
            n += ((moveForward < 0.0f) ? -45 : ((moveForward == 0.0f) ? 90 : 45));
        }
        if (moveStrafing > 0.0f) {
            n -= ((moveForward < 0.0f) ? -45 : ((moveForward == 0.0f) ? 90 : 45));
        }
        return n * 0.017453292f;
    }

    public static double c(final double n, final int n2) {
        if (n2 < 0) {
            throw new IllegalArgumentException();
        }
        return new BigDecimal(n).setScale(n2, RoundingMode.HALF_UP).doubleValue();
    }

    protected static class a {
        public int bfz;
        public int bga;
        public int bgb;
        public int bgc;
    }
}
