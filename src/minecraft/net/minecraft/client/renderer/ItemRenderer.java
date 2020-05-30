package net.minecraft.client.renderer;

import com.google.common.base.MoreObjects;
import java.util.Objects;

import complex.Complex;
import complex.module.impl.visual.Animations;
import complex.utils.TamoriUtils;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.storage.MapData;
import optifine.Config;
import optifine.DynamicLights;
import optifine.Reflector;
import optifine.ReflectorForge;
import org.lwjgl.opengl.GL11;
import shadersmod.client.Shaders;

public class ItemRenderer {
    private static final ResourceLocation RES_MAP_BACKGROUND = new ResourceLocation("textures/map/map_background.png");
    private static final ResourceLocation RES_UNDERWATER_OVERLAY = new ResourceLocation("textures/misc/underwater.png");

    /**
     * A reference to the Minecraft object.
     */
    private final Minecraft mc;
    private ItemStack itemStackMainHand = ItemStack.field_190927_a;
    private ItemStack itemStackOffHand = ItemStack.field_190927_a;
    private float equippedProgressMainHand;
    private float prevEquippedProgressMainHand;
    private float equippedProgressOffHand;
    private float prevEquippedProgressOffHand;
    private final RenderManager renderManager;
    private final RenderItem itemRenderer;

    public ItemRenderer(Minecraft mcIn) {
        this.mc = mcIn;
        this.renderManager = mcIn.getRenderManager();
        this.itemRenderer = mcIn.getRenderItem();
    }

    public void renderItem(EntityLivingBase entityIn, ItemStack heldStack, ItemCameraTransforms.TransformType transform) {
        this.renderItemSide(entityIn, heldStack, transform, false);
    }

    public void renderItemSide(EntityLivingBase entitylivingbaseIn, ItemStack heldStack, ItemCameraTransforms.TransformType transform, boolean leftHanded) {
        if (!heldStack.func_190926_b()) {
            Item item = heldStack.getItem();
            Block block = Block.getBlockFromItem(item);
            GlStateManager.pushMatrix();
            boolean flag = this.itemRenderer.shouldRenderItemIn3D(heldStack) && block.getBlockLayer() == BlockRenderLayer.TRANSLUCENT;

            if (flag && (!Config.isShaders() || !Shaders.renderItemKeepDepthMask)) {
                GlStateManager.depthMask(false);
            }

            this.itemRenderer.renderItem(heldStack, entitylivingbaseIn, transform, leftHanded);
            if (flag) {
                GlStateManager.depthMask(true);
            }
            GlStateManager.popMatrix();
        }
    }

    private boolean func_178107_a(Block p_178107_1_) {
        return p_178107_1_ != null && p_178107_1_.getBlockLayer() == BlockRenderLayer.TRANSLUCENT;
    }

    /**
     * Rotate the render around X and Y
     */
    private void rotateArroundXAndY(float angle, float angleY) {
        GlStateManager.pushMatrix();
        GlStateManager.rotate(angle, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(angleY, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.popMatrix();
    }

    private void setLightmap() {
        AbstractClientPlayer abstractclientplayer = this.mc.thePlayer;
        int i = this.mc.theWorld.getCombinedLight(new BlockPos(abstractclientplayer.posX, abstractclientplayer.posY + (double) abstractclientplayer.getEyeHeight(), abstractclientplayer.posZ), 0);

        if (Config.isDynamicLights()) {
            i = DynamicLights.getCombinedLight(this.mc.getRenderViewEntity(), i);
        }

        float f = (float) (i & 65535);
        float f1 = (float) (i >> 16);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, f, f1);
    }

    private void rotateArm(float p_187458_1_) {
        EntityPlayerSP entityplayersp = this.mc.thePlayer;
        float f = entityplayersp.prevRenderArmPitch + (entityplayersp.renderArmPitch - entityplayersp.prevRenderArmPitch) * p_187458_1_;
        float f1 = entityplayersp.prevRenderArmYaw + (entityplayersp.renderArmYaw - entityplayersp.prevRenderArmYaw) * p_187458_1_;
        GlStateManager.rotate((entityplayersp.rotationPitch - f) * 0.1F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate((entityplayersp.rotationYaw - f1) * 0.1F, 0.0F, 1.0F, 0.0F);
    }

    /**
     * Return the angle to render the Map
     */
    private float getMapAngleFromPitch(float pitch) {
        float f = 1.0F - pitch / 45.0F + 0.1F;
        f = MathHelper.clamp(f, 0.0F, 1.0F);
        f = -MathHelper.cos(f * (float) Math.PI) * 0.5F + 0.5F;
        return f;
    }

    private void renderArms() {
        if (!this.mc.thePlayer.isInvisible()) {
            GlStateManager.disableCull();
            GlStateManager.pushMatrix();
            GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
            this.renderArm(EnumHandSide.RIGHT);
            this.renderArm(EnumHandSide.LEFT);
            GlStateManager.popMatrix();
            GlStateManager.enableCull();
        }
    }

    private void renderArm(EnumHandSide p_187455_1_) {
        this.mc.getTextureManager().bindTexture(this.mc.thePlayer.getLocationSkin());
        Render<AbstractClientPlayer> render = this.renderManager.<AbstractClientPlayer>getEntityRenderObject(this.mc.thePlayer);
        RenderPlayer renderplayer = (RenderPlayer) render;
        GlStateManager.pushMatrix();
        float f = p_187455_1_ == EnumHandSide.RIGHT ? 1.0F : -1.0F;
        GlStateManager.rotate(92.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(45.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(f * -41.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.translate(f * 0.3F, -1.1F, 0.45F);

        if (p_187455_1_ == EnumHandSide.RIGHT) {
            renderplayer.renderRightArm(this.mc.thePlayer);
        } else {
            renderplayer.renderLeftArm(this.mc.thePlayer);
        }

        GlStateManager.popMatrix();
    }

    private void renderMapFirstPersonSide(float p_187465_1_, EnumHandSide p_187465_2_, float p_187465_3_, ItemStack p_187465_4_) {
        float f = p_187465_2_ == EnumHandSide.RIGHT ? 1.0F : -1.0F;
        GlStateManager.translate(f * 0.125F, -0.125F, 0.0F);

        if (!this.mc.thePlayer.isInvisible()) {
            GlStateManager.pushMatrix();
            GlStateManager.rotate(f * 10.0F, 0.0F, 0.0F, 1.0F);
            this.renderArmFirstPerson(p_187465_1_, p_187465_3_, p_187465_2_);
            GlStateManager.popMatrix();
        }

        GlStateManager.pushMatrix();
        GlStateManager.translate(f * 0.51F, -0.08F + p_187465_1_ * -1.2F, -0.75F);
        float f1 = MathHelper.sqrt(p_187465_3_);
        float f2 = MathHelper.sin(f1 * (float) Math.PI);
        float f3 = -0.5F * f2;
        float f4 = 0.4F * MathHelper.sin(f1 * ((float) Math.PI * 2F));
        float f5 = -0.3F * MathHelper.sin(p_187465_3_ * (float) Math.PI);
        GlStateManager.translate(f * f3, f4 - 0.3F * f2, f5);
        GlStateManager.rotate(f2 * -45.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(f * f2 * -30.0F, 0.0F, 1.0F, 0.0F);
        this.renderMapFirstPerson(p_187465_4_);
        GlStateManager.popMatrix();
    }

    private void renderMapFirstPerson(float p_187463_1_, float p_187463_2_, float p_187463_3_) {
        float f = MathHelper.sqrt(p_187463_3_);
        float f1 = -0.2F * MathHelper.sin(p_187463_3_ * (float) Math.PI);
        float f2 = -0.4F * MathHelper.sin(f * (float) Math.PI);
        GlStateManager.translate(0.0F, -f1 / 2.0F, f2);
        float f3 = this.getMapAngleFromPitch(p_187463_1_);
        GlStateManager.translate(0.0F, 0.04F + p_187463_2_ * -1.2F + f3 * -0.5F, -0.72F);
        GlStateManager.rotate(f3 * -85.0F, 1.0F, 0.0F, 0.0F);
        this.renderArms();
        float f4 = MathHelper.sin(f * (float) Math.PI);
        GlStateManager.rotate(f4 * 20.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(2.0F, 2.0F, 2.0F);
        this.renderMapFirstPerson(this.itemStackMainHand);
    }

    private void renderMapFirstPerson(ItemStack stack) {
        GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.scale(0.38F, 0.38F, 0.38F);
        GlStateManager.disableLighting();
        this.mc.getTextureManager().bindTexture(RES_MAP_BACKGROUND);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.translate(-0.5F, -0.5F, 0.0F);
        GlStateManager.scale(0.0078125F, 0.0078125F, 0.0078125F);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(-7.0D, 135.0D, 0.0D).tex(0.0D, 1.0D).endVertex();
        bufferbuilder.pos(135.0D, 135.0D, 0.0D).tex(1.0D, 1.0D).endVertex();
        bufferbuilder.pos(135.0D, -7.0D, 0.0D).tex(1.0D, 0.0D).endVertex();
        bufferbuilder.pos(-7.0D, -7.0D, 0.0D).tex(0.0D, 0.0D).endVertex();
        tessellator.draw();
        MapData mapdata = ReflectorForge.getMapData(Items.FILLED_MAP, stack, this.mc.theWorld);

        if (mapdata != null) {
            this.mc.entityRenderer.getMapItemRenderer().renderMap(mapdata, false);
        }

        GlStateManager.enableLighting();
    }

    private void renderArmFirstPerson(float p_187456_1_, float p_187456_2_, EnumHandSide p_187456_3_) {
        boolean flag = p_187456_3_ != EnumHandSide.LEFT;
        float f = flag ? 1.0F : -1.0F;
        float f1 = MathHelper.sqrt(p_187456_2_);
        float f2 = -0.3F * MathHelper.sin(f1 * (float) Math.PI);
        float f3 = 0.4F * MathHelper.sin(f1 * ((float) Math.PI * 2F));
        float f4 = -0.4F * MathHelper.sin(p_187456_2_ * (float) Math.PI);
        GlStateManager.translate(f * (f2 + 0.64000005F), f3 + -0.6F + p_187456_1_ * -0.6F, f4 + -0.71999997F);
        GlStateManager.rotate(f * 45.0F, 0.0F, 1.0F, 0.0F);
        float f5 = MathHelper.sin(p_187456_2_ * p_187456_2_ * (float) Math.PI);
        float f6 = MathHelper.sin(f1 * (float) Math.PI);
        GlStateManager.rotate(f * f6 * 70.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(f * f5 * -20.0F, 0.0F, 0.0F, 1.0F);
        AbstractClientPlayer abstractclientplayer = this.mc.thePlayer;
        this.mc.getTextureManager().bindTexture(abstractclientplayer.getLocationSkin());
        GlStateManager.translate(f * -1.0F, 3.6F, 3.5F);
        GlStateManager.rotate(f * 120.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(200.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(f * -135.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.translate(f * 5.6F, 0.0F, 0.0F);
        RenderPlayer renderplayer = (RenderPlayer) this.renderManager.<AbstractClientPlayer>getEntityRenderObject(abstractclientplayer);
        GlStateManager.disableCull();

        if (flag) {
            renderplayer.renderRightArm(abstractclientplayer);
        } else {
            renderplayer.renderLeftArm(abstractclientplayer);
        }

        GlStateManager.enableCull();
    }

    private void transformEatFirstPerson(float p_187454_1_, EnumHandSide p_187454_2_, ItemStack p_187454_3_) {
        float f = (float) this.mc.thePlayer.getItemInUseCount() - p_187454_1_ + 1.0F;
        float f1 = f / (float) p_187454_3_.getMaxItemUseDuration();

        if (f1 < 0.8F) {
            float f2 = MathHelper.abs(MathHelper.cos(f / 4.0F * (float) Math.PI) * 0.1F);
            GlStateManager.translate(0.0F, f2, 0.0F);
        }

        float f3 = 1.0F - (float) Math.pow((double) f1, 27.0D);
        int i = p_187454_2_ == EnumHandSide.RIGHT ? 1 : -1;
        GlStateManager.translate(f3 * 0.6F * (float) i, f3 * -0.5F, f3 * 0.0F);
        GlStateManager.rotate((float) i * f3 * 90.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(f3 * 10.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate((float) i * f3 * 30.0F, 0.0F, 0.0F, 1.0F);
    }

    private void transformFirstPerson(EnumHandSide p_187453_1_, float p_187453_2_) {
        int i = p_187453_1_ == EnumHandSide.RIGHT ? 1 : -1;
        float f = MathHelper.sin(p_187453_2_ * p_187453_2_ * (float) Math.PI);
        float l = 0;
        l += Math.floor(TamoriUtils.getGaen());
        GlStateManager.rotate((float) i * (45.0F + f * -20.0F), 0.0F, 1.0F, 0.0F);
        float f1 = MathHelper.sin(MathHelper.sqrt(p_187453_2_) * (float) Math.PI);
        GlStateManager.rotate((float) i * f1 * -20.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(f1 * -80.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(Complex.getSettingsManager().getSettingByName("360°").getValBoolean() ? (float) i * l : (float) i * -45.0F, 0.0F, 1.0F, 0.0F);
    }

    private void transformSideFirstPerson(EnumHandSide p_187459_1_, float p_187459_2_) {
        int i = p_187459_1_ == EnumHandSide.RIGHT ? 1 : -1;
        GlStateManager.translate((float) i * 0.56F, -0.52F + p_187459_2_ * -0.6F, -0.72F);
    }

    private void Gay(EnumHandSide p_187453_1_, float p_187453_2_) {
        int i = p_187453_1_ == EnumHandSide.RIGHT ? 1 : -1;
        float f = MathHelper.sin(p_187453_2_ * p_187453_2_ * (float) Math.PI);
        GlStateManager.rotate((float) i * (45.0F + f * 360.0F), 0.0F, 1.0F, 0.0F);
        float f1 = MathHelper.sin(MathHelper.sqrt_float(p_187453_2_) * (float) Math.PI);
        GlStateManager.rotate((float) i * f1 * -80.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(f1 * -80.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate((float) i * -45.0F, 0.0F, 1.0F, 0.0F);
    }

    private void Rotation(EnumHandSide p_187453_1_, float p_187453_2_) {
        int i = p_187453_1_ == EnumHandSide.RIGHT ? 1 : -1;
        float f = MathHelper.sin(p_187453_2_ * p_187453_2_ * (float) Math.PI);
        GlStateManager.rotate((float) i * (45.0F + f * -20.0F), 0.0F, 1.0F, 0.0F);
        float f1 = MathHelper.wrapDegrees(MathHelper.sin(p_187453_2_) * (float) Math.PI);
        GlStateManager.rotate((float) i * f1 * -80.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(f1 * -80.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate((float) i * -45.0F, 0.0F, 1.0F, 0.0F);
    }

    private void Cyka(EnumHandSide p_187453_1_, float p_187453_2_) {
        int i = p_187453_1_ == EnumHandSide.RIGHT ? 1 : -1;
        float f = MathHelper.abs(p_187453_2_ * p_187453_2_ * (float) Math.PI);
        GlStateManager.rotate((float) i * (45.0F + f * -20.0F), 0.0F, 1.0F, 0.0F);
        float f1 = MathHelper.wrapDegrees(MathHelper.sqrt(p_187453_2_) * (float) Math.PI);
        GlStateManager.rotate((float) i * f1 * -20.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(f1 * -80.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate((float) i * -45.0F, 0.0F, 1.0F, 0.0F);
    }

    private void Johnny(EnumHandSide p_187453_1_, float p_187453_2_) {
        int i = p_187453_1_ == EnumHandSide.RIGHT ? 1 : -1;
        float f = MathHelper.sin(p_187453_2_ * p_187453_2_ * (float) Math.PI);
        GlStateManager.rotate((float) i * (45.0F + f * -20.0F), 0.0F, 1.0F, 0.0F);
        float f1 = MathHelper.sin(MathHelper.cos(p_187453_2_) * (float) Math.PI);
        GlStateManager.rotate((float) i * f1 * -20.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(f1 * -360.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate((float) i * -45.0F, 0.0F, 1.0F, 0.0F);
    }

    private void Phish(EnumHandSide p_187453_1_, float p_187453_2_) {
        int i = p_187453_1_ == EnumHandSide.RIGHT ? 1 : -1;
        float f = MathHelper.sin(p_187453_2_ * p_187453_2_ * (float) Math.PI);
        GlStateManager.rotate((float) i * (45.0F + f * -20.0F), 0.0F, 1.0F, 0.0F);
        float f1 = MathHelper.sin(MathHelper.sqrt(p_187453_2_) * (float) Math.PI);
        GlStateManager.rotate((float) i * f1 * -80.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(f1 * -80.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate((float) i * -45.0F, 0.0F, 1.0F, 0.0F);
    }

    private void func_178103_a() {
        GlStateManager.translate(-0.5f, 0.2f, 0.0f);
        GlStateManager.rotate(30.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(-80.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotate(60.0f, 0.0f, 1.0f, 0.0f);
    }

    private void func_178103_b() {
        GlStateManager.translate(-0.5f, 0.2f, 0.0f);
        GlStateManager.rotate(50.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(-50.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotate(40.0f, 0.0f, 1.0f, 0.0f);
    }

    private void func_178103_c() {
        GlStateManager.translate(-0.5f, 0.15f, 0.0f);
        GlStateManager.rotate(20.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(-80.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotate(20.0f, 0.0f, 1.0f, 0.0f);
    }

    private void Vanilla(final float equipProgress, final float swingProgress) {
        GlStateManager.translate(0.56f, -0.44f, -0.71999997f);
        GlStateManager.translate(0.0f, equipProgress * -0.6f, 0.0f);
        GlStateManager.rotate(45.0f, 0.0f, 1.0f, 0.0f);
        final float f = MathHelper.sin(swingProgress * swingProgress * 3.1415927f);
        final float f2 = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * 3.1415927f);
        GlStateManager.rotate(f * -20.0f, 0.0f, 0.0f, 0.0f);
        GlStateManager.rotate(f2 * -20.0f, 0.0f, 0.0f, 1.0f);
        GlStateManager.rotate(f2 * -80.0f, 0.01f, 0.0f, 0.0f);
        GlStateManager.scale(0.7F, 0.7F, 0.7F);
    }

    private void Vanilla2(final float equipProgress, final float swingProgress) {
        GlStateManager.translate(0.56f, -0.44f, -0.71999997f);
        GlStateManager.translate(0.0f, equipProgress * -0.6f, 0.0f);
        GlStateManager.rotate(45.0f, 0.0f, 1.0f, 0.0f);
        final float f = MathHelper.sin(swingProgress * swingProgress * 3.1415927f);
        final float f2 = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * 3.1415927f);
        GlStateManager.rotate(f * -8.0f, 0.0f, 0.0f, 0.0f);
        GlStateManager.rotate(f2 * -8.0f, 0.0f, 0.0f, 1.0f);
        GlStateManager.rotate(f2 * -30.0f, 0.01f, 0.0f, 0.0f);
        GlStateManager.scale(0.7F, 0.7F, 0.7F);
    }

    private void Swipe(float equipProgress, float swingProgress) {
        GlStateManager.translate(0.56F, -0.32F, -0.71999997F);
        GlStateManager.translate(0.0F, 0, 0.0F);
        GlStateManager.rotate(25.0F, 0.0F, 1.5F, 0.0F);
        float f = MathHelper.sin(swingProgress * swingProgress * (float) Math.PI);
        float f1 = MathHelper.sin(MathHelper.sqrt(swingProgress) * (float) Math.PI);
        GlStateManager.rotate(f * -20.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(f1 * -20.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(f1 * -10.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(0.7F, 0.7F, 0.7F);
    }

    private void Tap1(final float equipProgress, final float swingProgress) {
        GlStateManager.translate(1.0F, -0.6f, -0.71999997f);
        GlStateManager.translate(0.0f, equipProgress * -0.6f, 0.0f);
        GlStateManager.rotate(5.0f, 0.0f, 1.0f, 0.0f);
        final float f = MathHelper.sin(swingProgress * swingProgress * 3.1415927f);
        final float f2 = MathHelper.sin(MathHelper.sqrt(swingProgress) * 3.1415927f);
        GlStateManager.rotate(f * -20.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(f2 * -10.0f, 0.0f, 0.0f, 1.0f);
        GlStateManager.rotate(f2 * -5.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.scale(1.0F, 1.0F, 1.0F);
    }

    private void Tap2(float var2, float swing) {
        float var3 = MathHelper.sin(swing * swing * (float) Math.PI);
        float var4 = MathHelper.sin(MathHelper.sqrt_float(swing) * (float) Math.PI);
        GlStateManager.translate(0.66F, -0.42F, -0.71999997F);
        GlStateManager.translate(0.0F,  var2 * -0.15F, 0.0F);
        GlStateManager.rotate(30 , 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(var4 * -30.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.scale(0.7F, 0.7F, 0.7F);
    }

    private void avatar(float equipProgress, float swingProgress) {
        GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
        GlStateManager.translate(0.0F, 0, 0.0F);
        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
        float f = MathHelper.sin(swingProgress * swingProgress * (float) Math.PI);
        float f1 = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float) Math.PI);
        GlStateManager.rotate(f * -20.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(f1 * -20.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(f1 * -40.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(0.7F, 0.7F, 0.7F);
    }

    /**
     * Renders the active item in the player's hand when in first person mode.
     */
    public void renderItemInFirstPerson(float partialTicks) {
        AbstractClientPlayer abstractclientplayer = this.mc.thePlayer;
        float f = abstractclientplayer.getSwingProgress(partialTicks);
        EnumHand enumhand = (EnumHand) MoreObjects.firstNonNull(abstractclientplayer.swingingHand, EnumHand.MAIN_HAND);
        float f1 = abstractclientplayer.prevRotationPitch + (abstractclientplayer.rotationPitch - abstractclientplayer.prevRotationPitch) * partialTicks;
        float f2 = abstractclientplayer.prevRotationYaw + (abstractclientplayer.rotationYaw - abstractclientplayer.prevRotationYaw) * partialTicks;
        boolean flag = true;
        boolean flag1 = true;

        if (abstractclientplayer.isHandActive()) {
            ItemStack itemstack = abstractclientplayer.getActiveItemStack();

            if (!itemstack.func_190926_b() && itemstack.getItem() == Items.BOW) {
                EnumHand enumhand1 = abstractclientplayer.getActiveHand();
                flag = enumhand1 == EnumHand.MAIN_HAND;
                flag1 = !flag;
            }
        }

        this.rotateArroundXAndY(f1, f2);
        this.setLightmap();
        this.rotateArm(partialTicks);
        GlStateManager.enableRescaleNormal();
        if (Complex.getModuleManager().isEnabled("Animations") && Complex.getSettingsManager().getSettingByName("Nigami").getValBoolean())
            f += Math.floor(TamoriUtils.getNigeria());

        if (flag) {
            float f3 = enumhand == EnumHand.MAIN_HAND ? f : 0.0F;
            float f5 = 1.0F - (this.prevEquippedProgressMainHand + (this.equippedProgressMainHand - this.prevEquippedProgressMainHand) * partialTicks);

            if (!Reflector.ForgeHooksClient_renderSpecificFirstPersonHand.exists() || !Reflector.callBoolean(Reflector.ForgeHooksClient_renderSpecificFirstPersonHand, EnumHand.MAIN_HAND, partialTicks, f1, f3, f5, this.itemStackMainHand)) {
                this.renderItemInFirstPerson(abstractclientplayer, partialTicks, f1, EnumHand.MAIN_HAND, f3, this.itemStackMainHand, f5);
            }
        }

        if (flag1 && !(itemStackOffHand.getItem() instanceof ItemShield && (Complex.getModuleManager().isEnabled("Animations") && Complex.getSettingsManager().getSettingByName("OldAnimation").getValBoolean()))) {
            float f4 = enumhand == EnumHand.OFF_HAND ? f : 0.0F;
            float f6 = 1.0F - (this.prevEquippedProgressOffHand + (this.equippedProgressOffHand - this.prevEquippedProgressOffHand) * partialTicks);

            if (!Reflector.ForgeHooksClient_renderSpecificFirstPersonHand.exists() || !Reflector.callBoolean(Reflector.ForgeHooksClient_renderSpecificFirstPersonHand, EnumHand.OFF_HAND, partialTicks, f1, f4, f6, this.itemStackOffHand)) {
                this.renderItemInFirstPerson(abstractclientplayer, partialTicks, f1, EnumHand.OFF_HAND, f4, this.itemStackOffHand, f6);
            }
        }

        GlStateManager.disableRescaleNormal();
        RenderHelper.disableStandardItemLighting();
    }

    public void renderItemInFirstPerson(AbstractClientPlayer abstractClientPlayer, float p_187457_2_, float p_187457_3_, EnumHand p_187457_4_, float p_187457_5_, ItemStack p_187457_6_, float p_187457_7_) {
        if (!Config.isShaders() || !Shaders.isSkipRenderHand(p_187457_4_)) {
            boolean flag = p_187457_4_ == EnumHand.MAIN_HAND;
            EnumHandSide enumhandside = flag ? abstractClientPlayer.getPrimaryHand() : abstractClientPlayer.getPrimaryHand().opposite();
            GlStateManager.pushMatrix();

            if (p_187457_6_.func_190926_b()) {
                if (flag && !abstractClientPlayer.isInvisible()) {
                    this.renderArmFirstPerson(p_187457_7_, p_187457_5_, enumhandside);
                }
            } else if (p_187457_6_.getItem() instanceof ItemMap) {
                if (flag && this.itemStackOffHand.func_190926_b()) {
                    this.renderMapFirstPerson(p_187457_3_, p_187457_7_, p_187457_5_);
                } else {
                    this.renderMapFirstPersonSide(p_187457_7_, enumhandside, p_187457_5_, p_187457_6_);
                }
            } else {
                float var2 = 1.0F - (this.prevEquippedProgressMainHand + (this.equippedProgressMainHand - this.prevEquippedProgressMainHand) * p_187457_2_);
                EntityPlayerSP var3 = this.mc.thePlayer;
                float swingprogress = var3.getSwingProgress(p_187457_2_);
                boolean flag1 = enumhandside == EnumHandSide.RIGHT;

                if (abstractClientPlayer.isHandActive() && abstractClientPlayer.getItemInUseCount() > 0 && abstractClientPlayer.getActiveHand() == p_187457_4_) {
                    int j = flag1 ? 1 : -1;

                    switch (p_187457_6_.getItemUseAction()) {
                        case NONE:
                            this.transformSideFirstPerson(enumhandside, p_187457_7_);
                            break;

                        case EAT:
                        case DRINK:
                            this.transformEatFirstPerson(p_187457_2_, enumhandside, p_187457_6_);
                            this.transformSideFirstPerson(enumhandside, p_187457_7_);
                            break;

                        case BLOCK:
                            this.transformSideFirstPerson(enumhandside, p_187457_7_);
                            break;

                        case BOW:
                            this.transformSideFirstPerson(enumhandside, p_187457_7_);
                            GlStateManager.translate((float) j * -0.2785682F, 0.18344387F, 0.15731531F);
                            GlStateManager.rotate(-13.935F, 1.0F, 0.0F, 0.0F);
                            GlStateManager.rotate((float) j * 35.3F, 0.0F, 1.0F, 0.0F);
                            GlStateManager.rotate((float) j * -9.785F, 0.0F, 0.0F, 1.0F);
                            float f5 = (float) p_187457_6_.getMaxItemUseDuration() - ((float) this.mc.thePlayer.getItemInUseCount() - p_187457_2_ + 1.0F);
                            float f6 = f5 / 20.0F;
                            f6 = (f6 * f6 + f6 * 2.0F) / 3.0F;

                            if (f6 > 1.0F) {
                                f6 = 1.0F;
                            }

                            if (f6 > 0.1F) {
                                float f7 = MathHelper.sin((f5 - 0.1F) * 1.3F);
                                float f3 = f6 - 0.1F;
                                float f4 = f7 * f3;
                                GlStateManager.translate(f4 * 0.0F, f4 * 0.004F, f4 * 0.0F);
                            }

                            GlStateManager.translate(f6 * 0.0F, f6 * 0.0F, f6 * 0.04F);
                            GlStateManager.scale(1.0F, 1.0F, 1.0F + f6 * 0.2F);
                            GlStateManager.rotate((float) j * 45.0F, 0.0F, -1.0F, 0.0F);
                    }
                } else {
                    float f = -0.4F * MathHelper.sin(MathHelper.sqrt(p_187457_5_) * (float) Math.PI);
                    float f1 = 0.2F * MathHelper.sin(MathHelper.sqrt(p_187457_5_) * ((float) Math.PI * 2F));
                    float f2 = -0.2F * MathHelper.sin(p_187457_5_ * (float) Math.PI);
                    float var15 = MathHelper.sin(swingprogress * swingprogress * 3.1415927F);
                    int i = flag1 ? 1 : -1;

                    if (Complex.getModuleManager().isEnabled("Animations")) {
                        if (Complex.getSettingsManager().getSettingByName("OldAnimation").getValBoolean() && (mc.gameSettings.keyBindUseItem.isKeyDown() || mc.thePlayer.isActiveItemStackBlocking()) && itemStackOffHand.getItem() instanceof ItemShield) {
                            if (Animations.mode.equalsIgnoreCase("Swipe")) {
                                this.Swipe(var2, swingprogress);
                                this.func_178103_a();
                            } else if (Animations.mode.equalsIgnoreCase("Tap1")) {
                                this.Tap1(0.0F, swingprogress);
                                this.func_178103_b();
                            } else if (Animations.mode.equalsIgnoreCase("Tap2")) {
                                this.Tap2(var2, swingprogress);
                                this.func_178103_c();
                            } else if (Animations.mode.equalsIgnoreCase("Vanilla")) {
                                this.Vanilla(0.0F, swingprogress);
                                this.func_178103_c();
                            } else if (Animations.mode.equalsIgnoreCase("Vanilla2")) {
                                this.Vanilla2(0.0F, swingprogress);
                                this.func_178103_c();
                            } else if (Animations.mode.equalsIgnoreCase("Sigma")) {
                                this.Vanilla(var2 * 0.5f, 0);
                                GlStateManager.scale(1.5F, 1.5F, 1.5F);
                                GlStateManager.rotate(-var15 * 55 / 2.0F, -8.0F, -0.0F, 9.0F);
                                GlStateManager.rotate(-var15 * 45, 1.0F, var15 / 2, -0.0F);
                                GlStateManager.translate(0.0F, -0.55F, 0.0F);
                                this.func_178103_c();
                                GL11.glTranslated(1.2, 0.3, 0.5);
                                GL11.glTranslatef(-1, this.mc.thePlayer.isSneaking() ? -0.1F : -0.2F, 0.2F);
                            } else if (Animations.mode.equalsIgnoreCase("Avatar")) {
                                this.avatar(var2, swingprogress);
                                this.func_178103_c();
                            } else if (Animations.mode.equalsIgnoreCase("MiniJump")) {
                                final float f5 = MathHelper.sin((float) (MathHelper.sqrt_float(f2) * 3.1));
                                GL11.glTranslated(-0.1, 0.0, 0.0);
                                this.Vanilla(f / 2.0f, 0.0f);
                                GlStateManager.rotate(-f5 * 40.0f / 2.0f, f5 / 2.0f, -0.0f, 9.0f);
                                GlStateManager.rotate(-f5 * 30.0f, 1.0f, f5 / 2.0f, -0.0f);
                                this.func_178103_c();
                            } else if (Animations.mode.equalsIgnoreCase("Astolfo")) {
                                TamoriUtils.NigaiRot();
                                this.Vanilla2(0.0F, swingprogress);
                                this.func_178103_c();
                            } else if (Animations.mode.equalsIgnoreCase("Dev")) {
                                this.transformSideFirstPerson(enumhandside, p_187457_7_);
                                this.Vanilla(0.0F, 0.0f);
                                this.func_178103_c();
                                GlStateManager.scale(2.6F, 2.6F, 2.6F);
                            }
                        } else {
                            if (!Complex.getSettingsManager().getSettingByName("Smooch").getValBoolean()) {
                                GlStateManager.translate((float) i * f, f1, f2);
                            }
                            this.transformSideFirstPerson(enumhandside, p_187457_7_);
                            this.transformFirstPerson(enumhandside, p_187457_5_);
                        }
                        if (Complex.getSettingsManager().getSettingByName("MiniItem").getValBoolean())
                            GlStateManager.scale(0.3F, 0.3F, 0.3F);
                    } else {
                        GlStateManager.translate((float) i * f, f1, f2);
                        this.transformSideFirstPerson(enumhandside, p_187457_7_);
                        this.transformFirstPerson(enumhandside, p_187457_5_);
                    }
                }
                this.renderItemSide(abstractClientPlayer, p_187457_6_, flag1 ? ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND : ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, !flag1);
            }
            GlStateManager.popMatrix();
        }
    }

    /**
     * Renders the overlays.
     */
    public void renderOverlays(float partialTicks) {
        GlStateManager.disableAlpha();

        if (this.mc.thePlayer.isEntityInsideOpaqueBlock()) {
            IBlockState iblockstate = this.mc.theWorld.getBlockState(new BlockPos(this.mc.thePlayer));
            BlockPos blockpos = new BlockPos(this.mc.thePlayer);
            EntityPlayer entityplayer = this.mc.thePlayer;

            for (int i = 0; i < 8; ++i) {
                double d0 = entityplayer.posX + (double) (((float) ((i >> 0) % 2) - 0.5F) * entityplayer.width * 0.8F);
                double d1 = entityplayer.posY + (double) (((float) ((i >> 1) % 2) - 0.5F) * 0.1F);
                double d2 = entityplayer.posZ + (double) (((float) ((i >> 2) % 2) - 0.5F) * entityplayer.width * 0.8F);
                BlockPos blockpos1 = new BlockPos(d0, d1 + (double) entityplayer.getEyeHeight(), d2);
                IBlockState iblockstate1 = this.mc.theWorld.getBlockState(blockpos1);

                if (iblockstate1.func_191058_s()) {
                    iblockstate = iblockstate1;
                    blockpos = blockpos1;
                }
            }

            if (iblockstate.getRenderType() != EnumBlockRenderType.INVISIBLE) {
                Object object = Reflector.getFieldValue(Reflector.RenderBlockOverlayEvent_OverlayType_BLOCK);

                if (!Reflector.callBoolean(Reflector.ForgeEventFactory_renderBlockOverlay, this.mc.thePlayer, partialTicks, object, iblockstate, blockpos)) {
                    this.renderBlockInHand(this.mc.getBlockRendererDispatcher().getBlockModelShapes().getTexture(iblockstate));
                }
            }
        }

        if (!this.mc.thePlayer.isSpectator()) {
            if (this.mc.thePlayer.isInsideOfMaterial(Material.WATER) && !Reflector.callBoolean(Reflector.ForgeEventFactory_renderWaterOverlay, this.mc.thePlayer, partialTicks)) {
                this.renderWaterOverlayTexture(partialTicks);
            }

            if (this.mc.thePlayer.isBurning() && !Reflector.callBoolean(Reflector.ForgeEventFactory_renderFireOverlay, this.mc.thePlayer, partialTicks)) {
                this.renderFireInFirstPerson();
            }
        }

        GlStateManager.enableAlpha();
    }

    /**
     * Render the block in the player's hand
     */
    private void renderBlockInHand(TextureAtlasSprite partialTicks) {
        this.mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        float f = 0.1F;
        GlStateManager.color(0.1F, 0.1F, 0.1F, 0.5F);
        GlStateManager.pushMatrix();
        float f1 = -1.0F;
        float f2 = 1.0F;
        float f3 = -1.0F;
        float f4 = 1.0F;
        float f5 = -0.5F;
        float f6 = partialTicks.getMinU();
        float f7 = partialTicks.getMaxU();
        float f8 = partialTicks.getMinV();
        float f9 = partialTicks.getMaxV();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(-1.0D, -1.0D, -0.5D).tex((double) f7, (double) f9).endVertex();
        bufferbuilder.pos(1.0D, -1.0D, -0.5D).tex((double) f6, (double) f9).endVertex();
        bufferbuilder.pos(1.0D, 1.0D, -0.5D).tex((double) f6, (double) f8).endVertex();
        bufferbuilder.pos(-1.0D, 1.0D, -0.5D).tex((double) f7, (double) f8).endVertex();
        tessellator.draw();
        GlStateManager.popMatrix();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    /**
     * Renders a texture that warps around based on the direction the player is looking. Texture needs to be bound
     * before being called. Used for the water overlay.
     */
    private void renderWaterOverlayTexture(float partialTicks) {
        if (!Config.isShaders() || Shaders.isUnderwaterOverlay()) {
            this.mc.getTextureManager().bindTexture(RES_UNDERWATER_OVERLAY);
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            float f = this.mc.thePlayer.getBrightness();
            GlStateManager.color(f, f, f, 0.5F);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.pushMatrix();
            float f1 = 4.0F;
            float f2 = -1.0F;
            float f3 = 1.0F;
            float f4 = -1.0F;
            float f5 = 1.0F;
            float f6 = -0.5F;
            float f7 = -this.mc.thePlayer.rotationYaw / 64.0F;
            float f8 = this.mc.thePlayer.rotationPitch / 64.0F;
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
            bufferbuilder.pos(-1.0D, -1.0D, -0.5D).tex((double) (4.0F + f7), (double) (4.0F + f8)).endVertex();
            bufferbuilder.pos(1.0D, -1.0D, -0.5D).tex((double) (0.0F + f7), (double) (4.0F + f8)).endVertex();
            bufferbuilder.pos(1.0D, 1.0D, -0.5D).tex((double) (0.0F + f7), (double) (0.0F + f8)).endVertex();
            bufferbuilder.pos(-1.0D, 1.0D, -0.5D).tex((double) (4.0F + f7), (double) (0.0F + f8)).endVertex();
            tessellator.draw();
            GlStateManager.popMatrix();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.disableBlend();
        }
    }

    /**
     * Renders the fire on the screen for first person mode. Arg: partialTickTime
     */
    private void renderFireInFirstPerson() {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 0.9F);
        GlStateManager.depthFunc(519);
        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        float f = 1.0F;

        for (int i = 0; i < 2; ++i) {
            GlStateManager.pushMatrix();
            TextureAtlasSprite textureatlassprite = this.mc.getTextureMapBlocks().getAtlasSprite("minecraft:blocks/fire_layer_1");
            this.mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            float f1 = textureatlassprite.getMinU();
            float f2 = textureatlassprite.getMaxU();
            float f3 = textureatlassprite.getMinV();
            float f4 = textureatlassprite.getMaxV();
            float f5 = -0.5F;
            float f6 = 0.5F;
            float f7 = -0.5F;
            float f8 = 0.5F;
            float f9 = -0.5F;
            GlStateManager.translate((float) (-(i * 2 - 1)) * 0.24F, -0.3F, 0.0F);
            GlStateManager.rotate((float) (i * 2 - 1) * 10.0F, 0.0F, 1.0F, 0.0F);
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
            bufferbuilder.pos(-0.5D, -0.5D, -0.5D).tex((double) f2, (double) f4).endVertex();
            bufferbuilder.pos(0.5D, -0.5D, -0.5D).tex((double) f1, (double) f4).endVertex();
            bufferbuilder.pos(0.5D, 0.5D, -0.5D).tex((double) f1, (double) f3).endVertex();
            bufferbuilder.pos(-0.5D, 0.5D, -0.5D).tex((double) f2, (double) f3).endVertex();
            tessellator.draw();
            GlStateManager.popMatrix();
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);
        GlStateManager.depthFunc(515);
    }

    public void updateEquippedItem() {
        this.prevEquippedProgressMainHand = this.equippedProgressMainHand;
        this.prevEquippedProgressOffHand = this.equippedProgressOffHand;
        EntityPlayerSP entityplayersp = this.mc.thePlayer;
        ItemStack itemstack = entityplayersp.getHeldItemMainhand();
        ItemStack itemstack1 = entityplayersp.getHeldItemOffhand();

        if (entityplayersp.isRowingBoat()) {
            this.equippedProgressMainHand = MathHelper.clamp(this.equippedProgressMainHand - 0.4F, 0.0F, 1.0F);
            this.equippedProgressOffHand = MathHelper.clamp(this.equippedProgressOffHand - 0.4F, 0.0F, 1.0F);
        } else {
            float f = entityplayersp.getCooledAttackStrength(1.0F);

            if (Reflector.ForgeHooksClient_shouldCauseReequipAnimation.exists()) {
                boolean flag = Reflector.callBoolean(Reflector.ForgeHooksClient_shouldCauseReequipAnimation, this.itemStackMainHand, itemstack, entityplayersp.inventory.currentItem);
                boolean flag1 = Reflector.callBoolean(Reflector.ForgeHooksClient_shouldCauseReequipAnimation, this.itemStackOffHand, itemstack1, Integer.valueOf(-1));

                if (!flag && !Objects.equals(this.itemStackMainHand, itemstack)) {
                    this.itemStackMainHand = itemstack;
                }

                if (!flag && !Objects.equals(this.itemStackOffHand, itemstack1)) {
                    this.itemStackOffHand = itemstack1;
                }

                this.equippedProgressMainHand += MathHelper.clamp((!flag ? f * f * f : 0.0F) - this.equippedProgressMainHand, -0.4F, 0.4F);
                this.equippedProgressOffHand += MathHelper.clamp((float) (!flag1 ? 1 : 0) - this.equippedProgressOffHand, -0.4F, 0.4F);
            } else {
                this.equippedProgressMainHand += MathHelper.clamp((Objects.equals(this.itemStackMainHand, itemstack) ? f * f * f : 0.0F) - this.equippedProgressMainHand, -0.4F, 0.4F);
                this.equippedProgressOffHand += MathHelper.clamp((float) (Objects.equals(this.itemStackOffHand, itemstack1) ? 1 : 0) - this.equippedProgressOffHand, -0.4F, 0.4F);
            }
        }

        if (this.equippedProgressMainHand < 0.1F) {
            this.itemStackMainHand = itemstack;

            if (Config.isShaders()) {
                Shaders.setItemToRenderMain(this.itemStackMainHand);
            }
        }

        if (this.equippedProgressOffHand < 0.1F) {
            this.itemStackOffHand = itemstack1;

            if (Config.isShaders()) {
                Shaders.setItemToRenderOff(this.itemStackOffHand);
            }
        }
    }

    public void resetEquippedProgress(EnumHand hand) {
        if (hand == EnumHand.MAIN_HAND) {
            this.equippedProgressMainHand = 0.0F;
        } else {
            this.equippedProgressOffHand = 0.0F;
        }
    }
}
