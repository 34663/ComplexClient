package complex.utils;

import net.minecraft.client.renderer.GlStateManager;

public class TamoriUtils {
    private static float niga = 0;
    private static float gae = 0;
    private static float scale = 0;
    private static boolean b0 = true;
    private static boolean b1 = true;

    public static float getNigeria() {
        float nigasa = niga;

        if (b0) {
            niga += 1.00F;
        } else {
            niga -= 1.00F;
        }

        if (b0 && niga > 180.0F) {
            b0 = !b0;
        } else if (!b0 && niga < 90.0F) {
            b0 = !b0;
        }
        return nigasa;
    }

    public static float getGaen() {
        gae += 3.0F;
        return gae;
    }

    public static float getScale() {
        if (b1) {
            scale += 0.05F;
        } else {
            scale -= 0.05F;
        }

        if (b1 && scale > 0.0F) {
            b1 = !b1;
        } else if (!b1 && scale < -0.5F) {
            b1 = !b1;
        }
        return scale;
    }

    public static void NigaiRot() {
        float l = 0;
        l += Math.floor(getGaen());
//        GlStateManager.translate(0.0F, 0.0F, getScale());
        GlStateManager.rotate(l, 0.0F, 0.0F, 1.0F);
        GlStateManager.scale(0.7F, 0.7F, 0.7F);
    }
}
