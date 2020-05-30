package complex.utils.render;

import complex.utils.MCUtil;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;

import static org.lwjgl.opengl.GL11.*;

public class OGLRender implements MCUtil {
    public static void enableGL3D(float lineWidth) {
        glDisable(GL_TEXTURE_2D);
        mc.entityRenderer.disableLightmap();
        glEnable(GL_LINE_SMOOTH);
        glLineWidth(lineWidth);
    }

    public static void disableGL3D() {
        glEnable(GL_TEXTURE_2D);
        glDisable(GL_LINE_SMOOTH);
    }

    public static double interpolate(double now, double then) {
        return then + (now - then) * mc.timer.renderPartialTicks;
    }

    public static double[] interpolate(Entity entity) {
        double posX = interpolate(entity.posX, entity.lastTickPosX) - RenderManager.renderPosX;
        double posY = interpolate(entity.posY, entity.lastTickPosY) - RenderManager.renderPosY;
        double posZ = interpolate(entity.posZ, entity.lastTickPosZ) - RenderManager.renderPosZ;

        return new double[] { posX, posY, posZ };
    }
}
