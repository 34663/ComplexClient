package complex.module.impl.visual;

import complex.Complex;
import complex.event.EventTarget;
import complex.event.impl.EventRender2D;
import complex.event.impl.EventRender3D;
import complex.event.impl.EventRenderNameTags;
import complex.module.Category;
import complex.module.Module;
import complex.module.data.Setting;
import complex.module.impl.combat.AntiBot;
import complex.utils.GLUtils;
import complex.utils.TeamUtils;
import complex.utils.font.CFontRenderer;
import complex.utils.font.FontLoaders;
import complex.utils.render.Colors;
import complex.utils.render.RenderingUtils;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Vector3d;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import sun.plugin.com.event.COMEventHandler;

import javax.vecmath.Vector4d;
import java.awt.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.text.NumberFormat;
import java.util.*;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glPopMatrix;

public class ESP extends Module {
    public List<EntityPlayer> collectedEntities = new ArrayList<>();
    private final IntBuffer viewport = GLAllocation.createDirectIntBuffer(16);
    private final FloatBuffer modelview = GLAllocation.createDirectFloatBuffer(16);
    private final FloatBuffer projection = GLAllocation.createDirectFloatBuffer(16);
    private final FloatBuffer vector = GLAllocation.createDirectFloatBuffer(4);
    public static Map entityPositionstop = new HashMap();
    public static Map entityPositionsbottom = new HashMap();
    private String mode, colormode;

    public ESP() {
        super("ESP", null, 0, 0, Category.Visual);
    }

    @Override
    public void setup() {
        ArrayList<String> options = new ArrayList<>();
        ArrayList<String> espcolor = new ArrayList<>();
        options.add("2DBox");
        options.add("2DCorners");
        options.add("2DTest");
        options.add("Cylinder");
        espcolor.add("Default");
        espcolor.add("Teams");
        Complex.getSettingsManager().rSetting(new Setting("ESP Mode", this, "2DBox", options));
        Complex.getSettingsManager().rSetting(new Setting("ESPColor", this, "Default", espcolor));
        Complex.getSettingsManager().rSetting(new Setting("Label", this, true));
        Complex.getSettingsManager().rSetting(new Setting("Armor", this, false));
        Complex.getSettingsManager().rSetting(new Setting("Border", this, true));
        Complex.getSettingsManager().rSetting(new Setting("Health", this, true));
    }

    @EventTarget
    public void onRenderTags(EventRenderNameTags event) {
        if (event.getEntity() instanceof EntityPlayer)
            if (isValid((EntityPlayer) event.getEntity())) {
                event.setCancelled(true);
            }
    }
    @EventTarget
    public void onRender2D(EventRender2D event) {
        mode = Complex.getSettingsManager().getSettingByName("ESP Mode").getValString();
        colormode = Complex.getSettingsManager().getSettingByName("ESPColor").getValString();
        if (mode.equalsIgnoreCase("2DBox")) {
            this.setDisplayName("ESP §72DBox");
        } else if (mode.equalsIgnoreCase("2DCorners")) {
            this.setDisplayName("ESP §72DCorners");
        } else if (mode.equalsIgnoreCase("2DTest")) {
            this.setDisplayName("ESP §72DTest");
        } else if (mode.equalsIgnoreCase("Cylinder")) {
            this.setDisplayName("ESP §7Cylinder");
        }

        CFontRenderer smallfont = FontLoaders.smallTahoma;
        GL11.glPushMatrix();
        this.collectedEntities.clear();
        this.collectEntities();
        double boxWidth = .5F;
        double scaling = event.getResolution().getScaleFactor() / Math.pow(event.getResolution().getScaleFactor(), 2.0);
        GlStateManager.scale(scaling, scaling, scaling);

        for (EntityPlayer entity : collectedEntities) {
            if (isValid(entity) && mode.equalsIgnoreCase("2DBox") || mode.equalsIgnoreCase("2DCorners") || mode.equalsIgnoreCase("2DTest")) {
                double x = RenderingUtils.interpolate(entity.posX, entity.lastTickPosX);
                double y = RenderingUtils.interpolate(entity.posY, entity.lastTickPosY);
                double z = RenderingUtils.interpolate(entity.posZ, entity.lastTickPosZ);
                double width = entity.width / 1.5;
                double height = entity.height + (entity.isSneaking() ? -0.3 : 0.2);
                AxisAlignedBB aabb = new AxisAlignedBB(x - width, y, z - width, x + width, y + height, z + width);
                List<Vec3d> vectors = Arrays.asList(new Vec3d(aabb.minX, aabb.minY, aabb.minZ), new Vec3d(aabb.minX, aabb.maxY, aabb.minZ), new Vec3d(aabb.maxX, aabb.minY, aabb.minZ), new Vec3d(aabb.maxX, aabb.maxY, aabb.minZ), new Vec3d(aabb.minX, aabb.minY, aabb.maxZ), new Vec3d(aabb.minX, aabb.maxY, aabb.maxZ), new Vec3d(aabb.maxX, aabb.minY, aabb.maxZ), new Vec3d(aabb.maxX, aabb.maxY, aabb.maxZ));
                mc.entityRenderer.setupCameraTransform(event.getPartialTicks(), 0);
                Vector4d position = null;

                for (Vec3d vector : vectors) {
                    vector = project2D(event.getResolution(), vector.getX() - mc.getRenderManager().viewerPosX, vector.getY() - mc.getRenderManager().viewerPosY, vector.getZ() - mc.getRenderManager().viewerPosZ);

                    if (vector != null && vector.getZ() >= 0.0 && vector.getZ() < 1.0) {
                        if (position == null) {
                            position = new Vector4d(vector.getX(), vector.getY(), vector.getZ(), 0.0);
                        }

                        position.x = Math.min(vector.getX(), position.x);
                        position.y = Math.min(vector.getY(), position.y);
                        position.z = Math.max(vector.getX(), position.z);
                        position.w = Math.max(vector.getY(), position.w);
                    }
                }

                mc.entityRenderer.setupOverlayRendering();

                if (position != null) {
                    double posX = position.x;
                    double posY = position.y;
                    double endPosX = position.z;
                    double endPosY = position.w;
                    int backgroundColor = 0;
                    int black = 0;
                    int color2 = 0;

                    if (colormode.equalsIgnoreCase("Default")) {
                        backgroundColor = new Color(0, 0, 0, 120).getRGB();
                        black = new Color(0, 0, 0, 150).getRGB();
                        color2 = Color.WHITE.getRGB();
                    } else if (colormode.equalsIgnoreCase("Teams")) {
                        backgroundColor = new Color(0, 0, 0, 120).getRGB();
                        black = new Color(0, 0, 0, 150).getRGB();
                        color2 = TeamUtils.isTeams(entity)?new Color(167, 31, 31).getRGB():new Color(68, 129, 68).getRGB();
                    }

                    if (Complex.getSettingsManager().getSettingByName("Border").getValBoolean()) {

                        if (mode.equalsIgnoreCase("2DBox")) {
                            RenderingUtils.drawRect(posX - 1, posY, posX + boxWidth, endPosY + .5, black);
                            RenderingUtils.drawRect(posX - 1, posY - .5, endPosX + .5, posY + .5 + boxWidth, black);
                            RenderingUtils.drawRect(endPosX - .5 - boxWidth, posY, endPosX + .5, endPosY + .5, black);
                            RenderingUtils.drawRect(posX - 1, endPosY - boxWidth - .5, endPosX + .5, endPosY + .5, black);
                            RenderingUtils.drawRect(posX - .5, posY, posX + boxWidth - .5, endPosY, color2);
                            RenderingUtils.drawRect(posX, endPosY - boxWidth, endPosX, endPosY, color2);
                            RenderingUtils.drawRect(posX - .5, posY, endPosX, posY + boxWidth, color2);
                            RenderingUtils.drawRect(endPosX - boxWidth, posY, endPosX, endPosY, color2);
                        } else if (mode.equalsIgnoreCase("2DCorners")) {
                            // === 黒 ===
                            // 左
                            RenderingUtils.drawRect(posX + .5, posY, posX - 1, posY + (endPosY - posY) / 4 + .5, black);
                            RenderingUtils.drawRect(posX + .5, endPosY + .5, posX - 1, endPosY + (posY - endPosY) / 4 - .5, black);

                            // 右
                            RenderingUtils.drawRect(endPosX - 1, endPosY + .5, endPosX + .5, endPosY - (endPosY - posY) / 4 - .5, black);
                            RenderingUtils.drawRect(endPosX - 1, posY - .5, endPosX + .5, posY + (endPosY - posY) / 4 + .5, black);

                            // 下
                            RenderingUtils.drawRect(posX - 1, endPosY - 1, posX + (endPosX - posX) / 3 + .5, endPosY + .5, black);
                            RenderingUtils.drawRect(endPosX - 1, endPosY - 1, endPosX + (posX - endPosX) / 3 - .5, endPosY + .5, black);

                            // 上
                            RenderingUtils.drawRect(posX - 1, posY - .5, posX + (endPosX - posX) / 3 + .5, posY + 1, black);
                            RenderingUtils.drawRect(endPosX - 1, posY - .5, endPosX + (posX - endPosX) / 3 - .5, posY + 1, black);

                            // === 白 ===
                            // 左
                            RenderingUtils.drawRect(posX, posY, posX - .5, posY + (endPosY - posY) / 4, color2);
                            RenderingUtils.drawRect(posX, endPosY, posX - .5, endPosY + (posY - endPosY) / 4, color2);

                            // 右
                            RenderingUtils.drawRect(endPosX - .5, posY, endPosX, posY + (endPosY - posY) / 4, color2);
                            RenderingUtils.drawRect(endPosX - .5, endPosY, endPosX, endPosY + (posY - endPosY) / 4, color2);

                            // 下
                            RenderingUtils.drawRect(posX, endPosY - .5, posX + (endPosX - posX) / 3, endPosY, color2);
                            RenderingUtils.drawRect(endPosX, endPosY - .5, endPosX + (posX - endPosX) / 3, endPosY, color2);

                            // 上
                            RenderingUtils.drawRect(posX - .5, posY, posX + (endPosX - posX) / 3, posY + .5, color2);
                            RenderingUtils.drawRect(endPosX, posY, endPosX + (posX - endPosX) / 3, posY + .5, color2);
                        } else if (mode.equalsIgnoreCase("2DTest")) {
                            RenderingUtils.drawRect(endPosX - .5 - boxWidth, posY, endPosX + .5, endPosY + .5, black);
                            RenderingUtils.drawRect(posX - 1, posY, posX + boxWidth, endPosY + .5, black);

                            RenderingUtils.drawRect(posX - 1, endPosY - 1, posX + (endPosX - posX) / 4 + .5, endPosY + .5, black);
                            RenderingUtils.drawRect(endPosX - 1, endPosY - 1, endPosX + (posX - endPosX) / 4 - .5, endPosY + .5, black);
                            RenderingUtils.drawRect(posX - 1, posY - .5, posX + (endPosX - posX) / 4 + .5, posY + 1, black);
                            RenderingUtils.drawRect(endPosX, posY - .5, endPosX + (posX - endPosX) / 4 - .5, posY + 1, black);

                            RenderingUtils.drawRect(posX - .5, posY, posX + boxWidth - .5, endPosY, color2);
                            RenderingUtils.drawRect(endPosX - boxWidth, posY, endPosX, endPosY, color2);

                            RenderingUtils.drawRect(posX, endPosY - .5, posX + (endPosX - posX) / 4, endPosY, color2);
                            RenderingUtils.drawRect(endPosX, endPosY - .5, endPosX + (posX - endPosX) / 4, endPosY, color2);
                            RenderingUtils.drawRect(posX - .5, posY, posX + (endPosX - posX) / 4, posY + .5, color2);
                            RenderingUtils.drawRect(endPosX, posY, endPosX + (posX - endPosX) / 4, posY + .5, color2);
                        }
                    }

                    float health = entity.getHealth();
                    double hpPercentage = health / entity.getMaxHealth();
                    hpPercentage = MathHelper.clamp_double(hpPercentage, 0, 1);
                    double hpHeight = (endPosY - posY) * hpPercentage;
                    double difference = posY - endPosY + 0.5;
                    float[] fractions = new float[]{0.0F, 0.5F, 1.0F};
                    Color[] colors = new Color[]{Color.RED, Color.YELLOW, Color.GREEN};
                    Color customColor = health >= 0.0F ? ESP.blendColors(fractions, colors, (float) hpPercentage).brighter() : Color.RED;

                    if (Complex.getSettingsManager().getSettingByName("Health").getValBoolean()) {
                        RenderingUtils.drawRect(posX - 2.5, posY - .5, posX - 1.0, endPosY + 0.5, backgroundColor);

                        if (health > 0) {
                            RenderingUtils.drawRect(posX - 2.0, endPosY, posX - 1.5, endPosY - hpHeight, customColor.getRGB());
                        }
                        if (-difference > 50) {
                            smallfont.drawCenteredStringWithShadow(String.valueOf((int) (hpPercentage * 100)), posX - 2.5, (int) (endPosY - hpHeight), -1);
                        }
                    }
                    if (Complex.getSettingsManager().getSettingByName("Label").getValBoolean()) {
                        double dif = (endPosX - posX) / 2;
                        double tagX = posX + dif;
                        double tagY = posY + (mc.thePlayer.getDistanceToEntity(entity) / 10F) - 9F;
                        smallfont.drawString(entity.getName(), (((float) tagX)) - (smallfont.getStringWidth(entity.getName()) / 2F), ((float) tagY), 0xffffff);
                    }
                    if (Complex.getSettingsManager().getSettingByName("Armor").getValBoolean()) {
                        float armorValue = entity.getTotalArmorValue();
                        double armorPercentage = armorValue / 20F;
                        armorPercentage = MathHelper.clamp_double(armorPercentage, 0, 1);
                        double armorWidth = (endPosX - posX) * armorPercentage;
                        final int armorColor = new Color(129, 133, 218).getRGB();

                        RenderingUtils.drawRect(posX - .5, endPosY + 1.5, posX - .5 + endPosX - posX + 1, endPosY + 1.0 + 2, backgroundColor);
                        if (armorValue > 0) {
                            RenderingUtils.drawRect(posX, endPosY + 2.0, posX + armorWidth, endPosY + 2.5, armorColor);
                        }
                    }
                }
            }
        }

        GL11.glPopMatrix();
        GlStateManager.enableBlend();
        mc.entityRenderer.setupOverlayRendering();
    }
    @EventTarget
    public void onRender3D(EventRender3D event) {
        for (Object o : mc.theWorld.loadedEntityList) {
            if (o instanceof EntityPlayer && o != mc.thePlayer) {
                EntityPlayer entity = (EntityPlayer)o;
                if (!isValid(entity))
                    continue;
                double posX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * event.getPartialTicks() - RenderManager.renderPosX;
                double posY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * event.getPartialTicks() - RenderManager.renderPosY;
                double posZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * event.getPartialTicks() - RenderManager.renderPosZ;
                int color = entity.hurtTime > 0 ? (new Color(255, 102, 113)).getRGB() : (new Color(186, 100, 200)).getRGB();
                boolean draw = true;
                if (Complex.getModuleManager().isEnabled("Antibot") && AntiBot.getInvalid().contains(entity))
                    draw = false;
                if (draw) {
                    if (mode.equalsIgnoreCase("Cylinder"))
                        RenderingUtils.drawWolframEntityESP(entity, color, posX, posY, posZ);
                }
            }
        }
    }

    private boolean isValid(EntityPlayer entityLivingBase) {
        return !entityLivingBase.isDead && !entityLivingBase.isInvisible() && mc.thePlayer != entityLivingBase;
    }
    private void collectEntities() {
        for (EntityPlayer entity : mc.theWorld.playerEntities) {
            if (isValid(entity)) {
                collectedEntities.add(entity);
            }
        }
    }
    private Vec3d project2D(ScaledResolution scaledResolution, double x, double y, double z) {
        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, modelview);
        GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, projection);
        GL11.glGetInteger(GL11.GL_VIEWPORT, viewport);

        if (GLU.gluProject((float) x, (float) y, (float) z, modelview, projection, viewport, vector)) {
            return new Vec3d(vector.get(0) / scaledResolution.getScaleFactor(), (Display.getHeight() - vector.get(1)) / scaledResolution.getScaleFactor(), vector.get(2));
        }

        return null;
    }
    public static Color blendColors(float[] fractions, Color[] colors, float progress) {
        Color color = null;
        if (fractions == null) throw new IllegalArgumentException("Fractions can't be null");
        if (colors == null) throw new IllegalArgumentException("Colours can't be null");
        if (fractions.length != colors.length) throw new IllegalArgumentException("Fractions and colours must have equal number of elements");
        int[] indicies = getFractionIndicies(fractions, progress);
        float[] range = new float[]{fractions[indicies[0]], fractions[indicies[1]]};
        Color[] colorRange = new Color[]{colors[indicies[0]], colors[indicies[1]]};
        float max = range[1] - range[0];
        float value = progress - range[0];
        float weight = value / max;
        return blend(colorRange[0], colorRange[1], 1.0f - weight);
    }
    public static Color blend(Color color1, Color color2, double ratio) {
        float r = (float)ratio;
        float ir = 1.0f - r;
        float[] rgb1 = new float[3];
        float[] rgb2 = new float[3];
        color1.getColorComponents(rgb1);
        color2.getColorComponents(rgb2);
        float red = rgb1[0] * r + rgb2[0] * ir;
        float green = rgb1[1] * r + rgb2[1] * ir;
        float blue = rgb1[2] * r + rgb2[2] * ir;
        if (red < 0.0f) {
            red = 0.0f;
        } else if (red > 255.0f) {
            red = 255.0f;
        }
        if (green < 0.0f) {
            green = 0.0f;
        } else if (green > 255.0f) {
            green = 255.0f;
        }
        if (blue < 0.0f) {
            blue = 0.0f;
        } else if (blue > 255.0f) {
            blue = 255.0f;
        }
        Color color = null;
        try {
            color = new Color(red, green, blue);
        }
        catch (IllegalArgumentException exp) {
            NumberFormat nf = NumberFormat.getNumberInstance();
            System.out.println(nf.format(red) + "; " + nf.format(green) + "; " + nf.format(blue));
            exp.printStackTrace();
        }
        return color;
    }
    public static int[] getFractionIndicies(float[] fractions, float progress) {
        int startPoint;
        int[] range = new int[2];
        for (startPoint = 0; startPoint < fractions.length && fractions[startPoint] <= progress; ++startPoint) {
        }
        if (startPoint >= fractions.length) {
            startPoint = fractions.length - 1;
        }
        range[0] = startPoint - 1;
        range[1] = startPoint;
        return range;
    }
}
