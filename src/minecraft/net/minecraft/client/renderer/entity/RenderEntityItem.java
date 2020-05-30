package net.minecraft.client.renderer.entity;

import java.util.Random;

import complex.Complex;
import complex.module.Module;
import complex.utils.font.CFontRenderer;
import complex.utils.font.FontLoaders;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import optifine.Reflector;
import org.lwjgl.opengl.GL11;

public class RenderEntityItem extends Render<EntityItem> {
    private final RenderItem itemRenderer;
    private final Random random = new Random();
    public static Random randomm;
    public static Minecraft mc;
    public static RenderItem renderItem;
    public static long tick;
    public static double rotation;

    public RenderEntityItem(RenderManager renderManagerIn, RenderItem p_i46167_2_) {
        super(renderManagerIn);
        this.itemRenderer = p_i46167_2_;
        this.shadowSize = 0.15F;
        this.shadowOpaque = 0.75F;
    }

    private int transformModelCount(EntityItem itemIn, double p_177077_2_, double p_177077_4_, double p_177077_6_, float p_177077_8_, IBakedModel p_177077_9_) {
        Module physic = Complex.getModuleManager().getModule("ItemPhysic");
        ItemStack itemstack = itemIn.getEntityItem();
        Item item = itemstack.getItem();

        if (item == null) {
            return 0;
        } else {
            boolean flag = p_177077_9_.isGui3d();
            int i = this.getModelCount(itemstack);
            float f1 = physic.isEnabled() ? 0.0F : MathHelper.sin(((float) itemIn.getAge() + p_177077_8_) / 10.0F + itemIn.hoverStart) * 0.1F + 0.1F;
            GlStateManager.rotate(0.0f, 0.0f, 1.0f, 0.0f);
            float f2 = p_177077_9_.getItemCameraTransforms().getTransform(ItemCameraTransforms.TransformType.GROUND).scale.y;
            GlStateManager.translate((float) p_177077_2_, (float) p_177077_4_ + f1 + 0.25F * f2, (float) p_177077_6_);

            if (!(physic.isEnabled()) && (flag || this.renderManager.options != null)) {
                float f3 = (((float) itemIn.getAge() + p_177077_8_) / 20.0F + itemIn.hoverStart) * (180F / (float) Math.PI);
                GlStateManager.rotate(f3, 0.0F, 1.0F, 0.0F);
            }

            if (physic.isEnabled()) {
                this.shadowSize = 0.0f;
            } else {
                this.shadowSize = 0.15f;
            }

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            return i;
        }
    }

    private int getModelCount(ItemStack stack) {
        int i = 1;

        if (stack.func_190916_E() > 48) {
            i = 5;
        } else if (stack.func_190916_E() > 32) {
            i = 4;
        } else if (stack.func_190916_E() > 16) {
            i = 3;
        } else if (stack.func_190916_E() > 1) {
            i = 2;
        }

        return i;
    }

    public static boolean shouldSpreadItems() {
        return true;
    }

    /**
     * Renders the desired {@code T} type Entity.
     */
    public void doRender(EntityItem entity, double x, double y, double z, float entityYaw, float partialTicks) {
        if (Complex.getModuleManager().isEnabled("Chams") && Complex.getSettingsManager().getSettingByName("Chams Mode").getValString().equalsIgnoreCase("Wall") && Complex.getSettingsManager().getSettingByName("Items").getValBoolean()) {
            GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
            GL11.glPolygonOffset(1.0F, -1000000F);
        }
        if (!Complex.getModuleManager().isEnabled("ItemPhysic")) {
            ItemStack itemstack = entity.getEntityItem();
            int i = itemstack.func_190926_b() ? 187 : Item.getIdFromItem(itemstack.getItem()) + itemstack.getMetadata();
            this.random.setSeed((long) i);
            boolean flag = false;

            if (this.bindEntityTexture(entity)) {
                this.renderManager.renderEngine.getTexture(this.getEntityTexture(entity)).setBlurMipmap(false, false);
                flag = true;
            }

            GlStateManager.enableRescaleNormal();
            GlStateManager.alphaFunc(516, 0.1F);
            GlStateManager.enableBlend();
            RenderHelper.enableStandardItemLighting();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.pushMatrix();
            IBakedModel ibakedmodel = this.itemRenderer.getItemModelWithOverrides(itemstack, entity.world, (EntityLivingBase) null);
            int j = this.transformModelCount(entity, x, y, z, partialTicks, ibakedmodel);
            float f = ibakedmodel.getItemCameraTransforms().ground.scale.x;
            float f1 = ibakedmodel.getItemCameraTransforms().ground.scale.y;
            float f2 = ibakedmodel.getItemCameraTransforms().ground.scale.z;
            boolean flag1 = ibakedmodel.isGui3d();

            if (!flag1) {
                float f3 = -0.0F * (float) (j - 1) * 0.5F * f;
                float f4 = -0.0F * (float) (j - 1) * 0.5F * f1;
                float f5 = -0.09375F * (float) (j - 1) * 0.5F * f2;
                GlStateManager.translate(f3, f4, f5);
            }

            if (this.renderOutlines) {
                GlStateManager.enableColorMaterial();
                GlStateManager.enableOutlineMode(this.getTeamColor(entity));
            }

            for (int k = 0; k < j; ++k) {
                if (flag1) {
                    GlStateManager.pushMatrix();

                    if (k > 0) {
                        float f7 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                        float f9 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                        float f6 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                        GlStateManager.translate(f7, f9, f6);
                    }

                    ibakedmodel.getItemCameraTransforms().applyTransform(ItemCameraTransforms.TransformType.GROUND);
                    this.itemRenderer.renderItem(itemstack, ibakedmodel);
                    GlStateManager.popMatrix();
                } else {
                    GlStateManager.pushMatrix();

                    if (k > 0) {
                        float f8 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
                        float f10 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
                        GlStateManager.translate(f8, f10, 0.0F);
                    }

                    ibakedmodel.getItemCameraTransforms().applyTransform(ItemCameraTransforms.TransformType.GROUND);
                    this.itemRenderer.renderItem(itemstack, ibakedmodel);
                    GlStateManager.popMatrix();
                    GlStateManager.translate(0.0F * f, 0.0F * f1, 0.09375F * f2);
                }
            }

            if (this.renderOutlines) {
                GlStateManager.disableOutlineMode();
                GlStateManager.disableColorMaterial();
            }

            GlStateManager.popMatrix();
            GlStateManager.disableRescaleNormal();
            GlStateManager.disableBlend();
            this.bindEntityTexture(entity);

            if (flag) {
                this.renderManager.renderEngine.getTexture(this.getEntityTexture(entity)).restoreLastBlurMipmap();
            }
        } else if (Complex.getModuleManager().isEnabled("ItemPhysic")) {
            RenderEntityItem.rotation = (System.nanoTime() - RenderEntityItem.tick) / 3000000.0 * 1.0;
            if (!RenderEntityItem.mc.inGameHasFocus) {
                RenderEntityItem.rotation = 0.0;
            }
            final EntityItem item = entity;
            final ItemStack itemstack2 = item.getEntityItem();
            if (itemstack2.getItem() != null) {
                RenderEntityItem.randomm.setSeed(187L);
                boolean flag2 = false;
                if (TextureMap.LOCATION_BLOCKS_TEXTURE != null) {
                    RenderEntityItem.mc.getRenderManager().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
                    RenderEntityItem.mc.getRenderManager().renderEngine.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
                    flag2 = true;
                }
                GlStateManager.enableRescaleNormal();
                GlStateManager.alphaFunc(516, 0.1f);
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                GlStateManager.pushMatrix();
                final IBakedModel ibakedmodel2 = renderItem.getItemModelMesher().getItemModel(itemstack2);
                final int k = this.transformModelCount(item, x, y, z, partialTicks, ibakedmodel2);
                final BlockPos pos = new BlockPos(item);
                if (item.rotationPitch > 360.0f) {
                    item.rotationPitch = 0.0f;
                }
                if (item != null && !Double.isNaN(item.getAge()) && !Double.isNaN(item.getAir()) && !Double.isNaN(item.getEntityId()) && item.getPosition() != null) {
                    if (!item.onGround) {
                        final BlockPos posUp = new BlockPos(item);
                        posUp.add(0, 1, 0);
                        final Material m1 = item.world.getBlockState(posUp).getBlock().getMaterial();
                        final Material m2 = item.world.getBlockState(pos).getBlock().getMaterial();
                        final boolean m3 = item.isInsideOfMaterial(Material.WATER);
                        final boolean m4 = item.isInWater();
                        if (m3 | m1 == Material.WATER | m2 == Material.WATER | m4) {
                            final EntityItem tmp748_746 = item;
                            tmp748_746.rotationPitch += (float) (RenderEntityItem.rotation / 4.0);
                        } else {
                            final EntityItem tmp770_768 = item;
                            tmp770_768.rotationPitch += (float) (RenderEntityItem.rotation * 2.0);
                        }
                    }
                }
                GL11.glRotatef(item.rotationYaw, 0.0f, 1.0f, 0.0f);
                GL11.glRotatef(item.rotationPitch + 90.0f, 1.0f, 0.0f, 0.0f);
                for (int l = 0; l < k; ++l) {
                    if (ibakedmodel2.isAmbientOcclusion()) {
                        GlStateManager.pushMatrix();
                        GlStateManager.scale(0.3f, 0.3f, 0.3f);
                        RenderEntityItem.renderItem.renderItem(itemstack2, ibakedmodel2);
                        GlStateManager.popMatrix();
                    } else {
                        GlStateManager.pushMatrix();
                        GlStateManager.scale(0.6f, 0.6f, 0.6f);
                        if (l > 0 && shouldSpreadItems()) {
                            GlStateManager.translate(0.0f, 0.0f, 0.046875f * l);
                        }
                        RenderEntityItem.renderItem.renderItem(itemstack2, ibakedmodel2);
                        if (!shouldSpreadItems()) {
                            GlStateManager.translate(0.0f, 0.0f, 0.046875f);
                        }
                        GlStateManager.popMatrix();
                    }
                }
                GlStateManager.popMatrix();
                GlStateManager.disableRescaleNormal();
                GlStateManager.disableBlend();
                RenderEntityItem.mc.getRenderManager().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
                if (flag2) {
                    RenderEntityItem.mc.getRenderManager().renderEngine.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
                }
            }
        }

        if (Complex.getModuleManager().isEnabled("Chams") && Complex.getSettingsManager().getSettingByName("Chams Mode").getValString().equalsIgnoreCase("Wall") && Complex.getSettingsManager().getSettingByName("Items").getValBoolean()) {
            GL11.glPolygonOffset(1.0F, 1000000F);
            GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
        }

        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    @Override
    protected void renderName(EntityItem entity, double x, double y, double z) {
        if (!Reflector.RenderLivingEvent_Specials_Pre_Constructor.exists() || !Reflector.postForgeBusEvent(Reflector.RenderLivingEvent_Specials_Pre_Constructor, entity, this, x, y, z)) {
            double d0 = entity.getDistanceSqToEntity(this.renderManager.renderViewEntity);
            CFontRenderer font = FontLoaders.Tahoma15;

            String s = entity.getDisplayName().getFormattedText();
            GlStateManager.alphaFunc(516, 0.1F);
            this.renderEntityName(entity, font, x, y, z, s, d0);

            if (Reflector.RenderLivingEvent_Specials_Post_Constructor.exists()) {
                Reflector.postForgeBusEvent(Reflector.RenderLivingEvent_Specials_Post_Constructor, entity, this, x, y, z);
            }
        }
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(EntityItem entity) {
        return TextureMap.LOCATION_BLOCKS_TEXTURE;
    }

    static {
        RenderEntityItem.randomm = new Random();
        RenderEntityItem.mc = Minecraft.getMinecraft();
        RenderEntityItem.renderItem = RenderEntityItem.mc.getRenderItem();
    }
}
