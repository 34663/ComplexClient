package complex.module.impl.visual;

import complex.Complex;
import complex.event.EventTarget;
import complex.event.impl.EventRender2D;
import complex.event.impl.EventRender3D;
import complex.event.impl.EventRenderNameTags;
import complex.module.Category;
import complex.module.Module;
import complex.module.data.Setting;
import complex.utils.MathUtils;
import complex.utils.RotationUtils;
import complex.utils.font.CFontRenderer;
import complex.utils.font.FontLoaders;
import complex.utils.render.Colors;
import complex.utils.render.RenderingUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import java.awt.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.*;
import java.util.List;

public class NameTags extends Module {
    private boolean hideInvisibles;
    private double gradualFOVModifier;
    private Character formatChar = new Character('§');
    public static Map<EntityLivingBase, double[]> entityPositions = new HashMap();
    public double color = 70.0D;

    public NameTags() {
        super("NameTags", null, 0, 0, Category.Visual);
    }

    @Override
    public void setup() {
        Complex.getSettingsManager().rSetting(new Setting("TagScale", this, 1.5, 0.1, 5.0, false));
        Complex.getSettingsManager().rSetting(new Setting("drawArmor", this, true));
    }

    @Override
    public void onEnable() {
        this.setDisplayName("NameTags");
        super.onEnable();
    }

    @EventTarget
    public void onEvent(EventRenderNameTags event) {
        event.setCancelled(true);
    }
    @EventTarget
    public void onRender3D(EventRender3D event) {
        updatePositions();
    }
    @EventTarget
    public void onRender2D(EventRender2D event) {
        boolean armor = Complex.getSettingsManager().getSettingByName("drawArmor").getValBoolean();

        GlStateManager.pushMatrix();
        ScaledResolution scaledRes = new ScaledResolution(mc);
        double twoDscale = scaledRes.getScaleFactor() / Math.pow(scaledRes.getScaleFactor(), 2.0D);
        GlStateManager.scale(twoDscale, twoDscale, twoDscale);
        for (Entity ent : entityPositions.keySet()) {
            GlStateManager.pushMatrix();
            if (ent != mc.thePlayer) {
                if (((ent instanceof EntityPlayer))) {
                    EntityPlayer player = (EntityPlayer)ent;
                    String str = ent.getDisplayName().getFormattedText();
                    String colorString = this.formatChar.toString();
                    double health = MathUtils.roundToPlace(((EntityPlayer) ent).getHealth() / 2.0F, 2);
                    double[] renderPositions = (double[]) entityPositions.get(ent);
                    float var1 = player.getHealth();
                    float var2 = (var1 / player.getMaxHealth()) * 10;
                    if ((renderPositions[3] < 0.0D) || (renderPositions[3] >= 1.0D)) {
                        GlStateManager.popMatrix();
                        continue;
                    }

                    GlStateManager.translate(renderPositions[0], renderPositions[1], 0.0D);
                    scale(ent);
                    GlStateManager.translate(0.0D, -2.5D, 0.0D);
                    int strWidth = 0;
                    strWidth = mc.fontRendererObj.getStringWidth(str);
                    RenderingUtils.drawRect(-strWidth / 2 - 4, -15.0D, strWidth / 2 + 4, -4.0D, Colors.getColor(0, 0, 0, 100));
                    GlStateManager.color(0.0F, 0.0F, 0.0F);
                    mc.fontRendererObj.drawString(str, -strWidth / 2, (float) -13.0D, -1);
                    GlStateManager.color(1.0F, 1.0F, 1.0F);
                    if (armor) {
                        List<ItemStack> itemsToRender = new ArrayList();
                        for (int i = 0; i < 5; i++) {
                            ItemStack stack = ((EntityPlayer) ent).getEquipmentInSlot(i);
                            if (stack != null) {
                                itemsToRender.add(stack);
                            }
                        }
                        int x = -(itemsToRender.size() * 9);
                        Iterator<ItemStack> iterator2 = itemsToRender.iterator();
                        while (iterator2.hasNext()) {
                            ItemStack stack = (ItemStack) iterator2.next();
                            RenderHelper.enableGUIStandardItemLighting();
                            mc.getRenderItem().renderItemIntoGUI(stack, x, -30);
                            mc.getRenderItem().renderItemOverlays(mc.fontRendererObj, stack, x, -30);
                            x += 2;
                            RenderHelper.disableStandardItemLighting();
                            String text = "";
                            if (stack != null) {
                                int y = 21;
                                int sLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.getEnchantmentByID(16), stack);
                                int fLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.getEnchantmentByID(20), stack);
                                int kLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.getEnchantmentByID(19), stack);
                                if (sLevel > 0) {
                                    drawEnchantTag("Sh" + sLevel, x, y);
                                    y -= 9;
                                }
                                if (fLevel > 0) {
                                    drawEnchantTag("Fir" + fLevel, x, y);
                                    y -= 9;
                                }
                                if (kLevel > 0) {
                                    drawEnchantTag("Kb" + kLevel, x, y);
                                } else if ((stack.getItem() instanceof ItemArmor)) {
                                    int pLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.getEnchantmentByID(0), stack);
                                    int tLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.getEnchantmentByID(7), stack);
                                    int uLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.getEnchantmentByID(34), stack);
                                    if (pLevel > 0) {
                                        drawEnchantTag("P" + pLevel, x, y);
                                        y -= 9;
                                    }
                                    if (tLevel > 0) {
                                        drawEnchantTag("Th" + tLevel, x, y);
                                        y -= 9;
                                    }
                                    if (uLevel > 0) {
                                        drawEnchantTag("Unb" + uLevel, x, y);
                                    }
                                } else if ((stack.getItem() instanceof net.minecraft.item.ItemBow)) {
                                    int powLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.getEnchantmentByID(48), stack);
                                    int punLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.getEnchantmentByID(49), stack);
                                    int fireLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.getEnchantmentByID(50), stack);
                                    if (powLevel > 0) {
                                        drawEnchantTag("Pow" + powLevel, x, y);
                                        y -= 9;
                                    }
                                    if (punLevel > 0) {
                                        drawEnchantTag("Pun" + punLevel, x, y);
                                        y -= 9;
                                    }
                                    if (fireLevel > 0) {
                                        drawEnchantTag("Fir" + fireLevel, x, y);
                                    }
                                } else if (stack.getRarity() == EnumRarity.EPIC) {
                                    drawEnchantTag("§lGod", x, y);
                                }
                                x += 16;
                            }
                        }
                    }
                }
                GlStateManager.popMatrix();
            }
        }
        GlStateManager.popMatrix();
    }

    private void drawEnchantTag(String text, int x, int y) {
        GlStateManager.pushMatrix();
        GlStateManager.disableDepth();
        x *= 1.7;
        y -= 4;
        GL11.glScalef(0.57F, 0.57F, 0.57F);
        mc.fontRendererObj.drawStringWithShadow(text, x, -36 - y, 64250);
        GlStateManager.enableDepth();
        GlStateManager.popMatrix();
    }
    private void scale(Entity ent) {
        float scale = (float) Complex.getSettingsManager().getSettingByName("TagScale").getValDouble();
        float target = scale * (mc.gameSettings.fovSetting / (mc.gameSettings.fovSetting * mc.thePlayer.getFovModifier()));
        if ((this.gradualFOVModifier == 0.0D) || (Double.isNaN(this.gradualFOVModifier))) {
            this.gradualFOVModifier = target;
        }
        double gradualFOVModifier = this.gradualFOVModifier;
        double n = target - this.gradualFOVModifier;
        this.gradualFOVModifier = (gradualFOVModifier + n / (Minecraft.debugFPS * 0.7D));
        scale *= (float) this.gradualFOVModifier;
        scale *= ((mc.currentScreen == null) && (GameSettings.isKeyDown(mc.gameSettings.ofKeyBindZoom)) ? 3 : 1);
        GlStateManager.scale(scale, scale, scale);
    }
    private void updatePositions() {
        entityPositions.clear();
        float pTicks = mc.timer.renderPartialTicks;
        for (Object o : mc.theWorld.loadedEntityList) {
            Entity ent = (Entity) o;
            if ((ent != mc.thePlayer) && ((ent instanceof EntityPlayer)) && ((!ent.isInvisible()) || (!this.hideInvisibles))) {

                double x = ent.lastTickPosX + (ent.posX - ent.lastTickPosX) * pTicks - mc.getRenderManager().viewerPosX;
                double y = ent.lastTickPosY + (ent.posY - ent.lastTickPosY) * pTicks - mc.getRenderManager().viewerPosY;
                double z = ent.lastTickPosZ + (ent.posZ - ent.lastTickPosZ) * pTicks - mc.getRenderManager().viewerPosZ;
                y += ent.height + 0.2D;
                if (convertTo2D(x, y, z)[2] >= 0.0D) {
                    if (convertTo2D(x, y, z)[2] < 1.0D) {
                        entityPositions.put((EntityPlayer) ent, new double[]{convertTo2D(x, y, z)[0], convertTo2D(x, y, z)[1], Math.abs(convertTo2D(x, y + 1.0D, z, ent)[1] - convertTo2D(x, y, z, ent)[1]), convertTo2D(x, y, z)[2]});
                    }
                }
            }
        }
    }
    private double[] convertTo2D(double x, double y, double z, Entity ent) {
        float pTicks = mc.timer.renderPartialTicks;
        float prevYaw = mc.thePlayer.rotationYaw;
        float prevPrevYaw = mc.thePlayer.prevRotationYaw;
        float[] rotations = RotationUtils.getRotationFromPosition(ent.lastTickPosX + (ent.posX - ent.lastTickPosX) * pTicks, ent.lastTickPosZ + (ent.posZ - ent.lastTickPosZ) * pTicks, ent.lastTickPosY + (ent.posY - ent.lastTickPosY) * pTicks - 1.6D);
        Entity renderViewEntity = mc.getRenderViewEntity();
        Entity renderViewEntity2 = mc.getRenderViewEntity();
        float n = rotations[0];
        renderViewEntity2.prevRotationYaw = n;
        renderViewEntity.rotationYaw = n;
        mc.entityRenderer.setupCameraTransform(pTicks, 0);
        double[] convertedPoints = convertTo2D(x, y, z);
        mc.getRenderViewEntity().rotationYaw = prevYaw;
        mc.getRenderViewEntity().prevRotationYaw = prevPrevYaw;
        mc.entityRenderer.setupCameraTransform(pTicks, 0);
        return convertedPoints;
    }
    private double[] convertTo2D(double x, double y, double z) {
        FloatBuffer screenCoords = BufferUtils.createFloatBuffer(3);
        IntBuffer viewport = BufferUtils.createIntBuffer(16);
        FloatBuffer modelView = BufferUtils.createFloatBuffer(16);
        FloatBuffer projection = BufferUtils.createFloatBuffer(16);
        GL11.glGetFloat(2982, modelView);
        GL11.glGetFloat(2983, projection);
        GL11.glGetInteger(2978, viewport);
        boolean result = GLU.gluProject((float) x, (float) y, (float) z, modelView, projection, viewport,
                screenCoords);
        if (result) {
            return new double[]{screenCoords.get(0), org.lwjgl.opengl.Display.getHeight() - screenCoords.get(1), screenCoords.get(2)};
        }
        return null;
    }
}
