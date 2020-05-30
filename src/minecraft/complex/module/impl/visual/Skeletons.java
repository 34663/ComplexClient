package complex.module.impl.visual;

import complex.Complex;
import complex.event.EventTarget;
import complex.event.impl.EventRender3D;
import complex.event.impl.EventRenderMovel;
import complex.module.Category;
import complex.module.Module;
import complex.module.data.Setting;
import complex.utils.GLUtils;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;

public class Skeletons extends Module {
    private static final Map<EntityPlayer, float[][]> playerRotationMap = new HashMap<>();

    public Skeletons() {
        super("Skeletons", null, 0, 0, Category.Visual);
    }

    @Override
    public void onEnable() {
        this.setDisplayName("Skeletons");
        super.onEnable();
    }

    @EventTarget
    public void onRender3D(EventRender3D event) {
        setupRender(true);
        playerRotationMap.keySet().removeIf(player -> !mc.theWorld.playerEntities.contains(player));
        for (EntityPlayer player : playerRotationMap.keySet()) {
            if (player instanceof EntityPlayerSP || player.isInvisible()) {
                continue;
            }
            glPushMatrix();
            final float[][] modelRotations = playerRotationMap.get(player);
            glLineWidth(1.0f);
            glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            final double x = GLUtils.interpolate(player.posX, player.lastTickPosX, event.getPartialTicks()) - mc.getRenderManager().getRenderPosX();
            final double y = GLUtils.interpolate(player.posY, player.lastTickPosY, event.getPartialTicks()) - mc.getRenderManager().getRenderPosY();
            final double z = GLUtils.interpolate(player.posZ, player.lastTickPosZ, event.getPartialTicks()) - mc.getRenderManager().getRenderPosZ();
            glTranslated(x, y, z);
            final float bodyYawOffset = player.prevRenderYawOffset + (player.renderYawOffset - player.prevRenderYawOffset) * mc.timer.renderPartialTicks;
            glRotatef(-bodyYawOffset, 0.0f, 1.0f, 0.0f);
            glTranslated(0.0, 0.0, player.isSneaking() ? -0.235 : 0.0);
            final float legHeight = player.isSneaking() ? 0.6f : 0.75f;
            glPushMatrix();
            glTranslated(-0.125, legHeight, 0.0);
            if (modelRotations[3][0] != 0.0f) {
                glRotatef(modelRotations[3][0] * 57.295776f, 1.0f, 0.0f, 0.0f);
            }
            if (modelRotations[3][1] != 0.0f) {
                glRotatef(modelRotations[3][1] * 57.295776f, 0.0f, 1.0f, 0.0f);
            }
            if (modelRotations[3][2] != 0.0f) {
                glRotatef(modelRotations[3][2] * 57.295776f, 0.0f, 0.0f, 1.0f);
            }
            glBegin(3);
            glVertex3d(0.0, 0.0, 0.0);
            glVertex3d(0.0, -legHeight, 0.0);
            glEnd();
            glPopMatrix();
            glPushMatrix();
            glTranslated(0.125, legHeight, 0.0);
            if (modelRotations[4][0] != 0.0f) {
                glRotatef(modelRotations[4][0] * 57.295776f, 1.0f, 0.0f, 0.0f);
            }
            if (modelRotations[4][1] != 0.0f) {
                glRotatef(modelRotations[4][1] * 57.295776f, 0.0f, 1.0f, 0.0f);
            }
            if (modelRotations[4][2] != 0.0f) {
                glRotatef(modelRotations[4][2] * 57.295776f, 0.0f, 0.0f, 1.0f);
            }
            glBegin(3);
            glVertex3d(0.0, 0.0, 0.0);
            glVertex3d(0.0, -legHeight, 0.0);
            glEnd();
            glPopMatrix();
            glTranslated(0.0, 0.0, player.isSneaking() ? 0.25 : 0.0);
            glPushMatrix();
            glTranslated(0.0, player.isSneaking() ? -0.05 : 0.0, player.isSneaking() ? -0.01725 : 0.0);

            // Left arm
            glPushMatrix();
            glTranslated(-0.375, (double) legHeight + 0.55, 0.0);
            if (modelRotations[1][0] != 0.0f) {
                glRotatef(modelRotations[1][0] * 57.295776f, 1.0f, 0.0f, 0.0f);
            }
            if (modelRotations[1][1] != 0.0f) {
                glRotatef(modelRotations[1][1] * 57.295776f, 0.0f, 1.0f, 0.0f);
            }
            if (modelRotations[1][2] != 0.0f) {
                glRotatef(-modelRotations[1][2] * 57.295776f, 0.0f, 0.0f, 1.0f);
            }
            glBegin(3);
            glVertex3d(0.0, 0.0, 0.0);
            glVertex3d(0.0, -0.5, 0.0);
            glEnd();
            glPopMatrix();

            // Right arm
            glPushMatrix();
            glTranslated(0.375, (double) legHeight + 0.55, 0.0);
            if (modelRotations[2][0] != 0.0f) {
                glRotatef(modelRotations[2][0] * 57.295776f, 1.0f, 0.0f, 0.0f);
            }
            if (modelRotations[2][1] != 0.0f) {
                glRotatef(modelRotations[2][1] * 57.295776f, 0.0f, 1.0f, 0.0f);
            }
            if (modelRotations[2][2] != 0.0f) {
                glRotatef(-modelRotations[2][2] * 57.295776f, 0.0f, 0.0f, 1.0f);
            }
            glBegin(3);
            glVertex3d(0.0, 0.0, 0.0);
            glVertex3d(0.0, -0.5, 0.0);
            glEnd();
            glPopMatrix();
            glRotatef(bodyYawOffset - player.rotationYawHead, 0.0f, 1.0f, 0.0f);

            // Head
            glPushMatrix();
            glTranslated(0.0, (double) legHeight + 0.55, 0.0);
            if (modelRotations[0][0] != 0.0f) {
                glRotatef(modelRotations[0][0] * 57.295776f, 1.0f, 0.0f, 0.0f);
            }
            glBegin(3);
            glVertex3d(0.0, 0.0, 0.0);
            glVertex3d(0.0, 0.3, 0.0);
            glEnd();
            glPopMatrix();

            glPopMatrix();
            glRotatef(player.isSneaking() ? 25.0f : 0.0f, 1.0f, 0.0f, 0.0f);
            glTranslated(0.0, player.isSneaking() ? -0.16175 : 0.0, player.isSneaking() ? -0.48025 : 0.0);

            // Pelvis
            glPushMatrix();
            glTranslated(0.0, legHeight, 0.0);
            glBegin(3);
            glVertex3d(-0.125, 0.0, 0.0);
            glVertex3d(0.125, 0.0, 0.0);
            glEnd();
            glPopMatrix();

            // Body
            glPushMatrix();
            glTranslated(0.0, legHeight, 0.0);
            glBegin(3);
            glVertex3d(0.0, 0.0, 0.0);
            glVertex3d(0.0, 0.55, 0.0);
            glEnd();
            glPopMatrix();

            // Shoulder
            glPushMatrix();
            glTranslated(0.0, (double) legHeight + 0.55, 0.0);
            glBegin(3);
            glVertex3d(-0.375, 0.0, 0.0);
            glVertex3d(0.375, 0.0, 0.0);
            glEnd();
            glPopMatrix();

            glPopMatrix();
        }
        setupRender(false);
    }

    private void setupRender(boolean start) {
        if (start) {
            glDisable(GL_LINE_SMOOTH);
            glDisable(GL_DEPTH_TEST);
            glDisable(GL_TEXTURE_2D);
        } else {
            glEnable(GL_DEPTH_TEST);
            glEnable(GL_TEXTURE_2D);
        }
        GL11.glDepthMask(!start);
    }

    public static void updateModel(EntityPlayer player, ModelPlayer model) {
        playerRotationMap.put(player, new float[][]{{model.bipedHead.rotateAngleX, model.bipedHead.rotateAngleY, model.bipedHead.rotateAngleZ}, {model.bipedRightArm.rotateAngleX, model.bipedRightArm.rotateAngleY, model.bipedRightArm.rotateAngleZ}, {model.bipedLeftArm.rotateAngleX, model.bipedLeftArm.rotateAngleY, model.bipedLeftArm.rotateAngleZ}, {model.bipedRightLeg.rotateAngleX, model.bipedRightLeg.rotateAngleY, model.bipedRightLeg.rotateAngleZ}, {model.bipedLeftLeg.rotateAngleX, model.bipedLeftLeg.rotateAngleY, model.bipedLeftLeg.rotateAngleZ}});
    }
}
