package net.minecraft.client.gui;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;

import java.awt.*;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;

import complex.Complex;
import complex.event.impl.EventRender2D;
import complex.gui.clickgui.util.ColorUtil;
import complex.module.impl.hud.HUD;
import complex.utils.font.CFontRenderer;
import complex.utils.font.FontLoaders;
import complex.utils.render.Colors;
import complex.utils.render.RenderingUtils;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.chat.IChatListener;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.chat.NormalChatListener;
import net.minecraft.client.gui.chat.OverlayChatListener;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.FoodStats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.border.WorldBorder;
import optifine.Config;
import optifine.CustomColors;
import optifine.CustomItems;
import optifine.Reflector;
import optifine.ReflectorForge;
import optifine.TextureAnimations;

public class GuiIngame extends Gui {
    private static final ResourceLocation VIGNETTE_TEX_PATH = new ResourceLocation("textures/misc/vignette.png");
    private static final ResourceLocation WIDGETS_TEX_PATH = new ResourceLocation("textures/gui/widgets.png");
    private static final ResourceLocation PUMPKIN_BLUR_TEX_PATH = new ResourceLocation("textures/misc/pumpkinblur.png");
    private final Random rand = new Random();
    private final Minecraft mc;
    private final RenderItem itemRenderer;

    /**
     * ChatGUI instance that retains all previous chat data
     */
    private final GuiNewChat persistantChatGUI;
    private int updateCounter;

    /**
     * The string specifying which record music is playing
     */
    private String recordPlaying = "";

    /**
     * How many ticks the record playing message will be displayed
     */
    private int recordPlayingUpFor;
    private boolean recordIsPlaying;

    /**
     * Previous frame vignette brightness (slowly changes by 1% each frame)
     */
    public float prevVignetteBrightness = 1.0F;

    /**
     * Remaining ticks the item highlight should be visible
     */
    private int remainingHighlightTicks;

    /**
     * The ItemStack that is currently being highlighted
     */
    private ItemStack highlightingItemStack = ItemStack.field_190927_a;
    private final GuiOverlayDebug overlayDebug;
    private final GuiSubtitleOverlay overlaySubtitle;

    /**
     * The spectator GUI for this in-game GUI instance
     */
    private final GuiSpectator spectatorGui;
    private final GuiPlayerTabOverlay overlayPlayerList;
    private final GuiBossOverlay overlayBoss;

    /**
     * A timer for the current title and subtitle displayed
     */
    private int titlesTimer;

    /**
     * The current title displayed
     */
    private String displayedTitle = "";

    /**
     * The current sub-title displayed
     */
    private String displayedSubTitle = "";

    /**
     * The time that the title take to fade in
     */
    private int titleFadeIn;

    /**
     * The time that the title is display
     */
    private int titleDisplayTime;

    /**
     * The time that the title take to fade out
     */
    private int titleFadeOut;
    private int playerHealth;
    private int lastPlayerHealth;

    /**
     * The last recorded system time
     */
    private long lastSystemTime;

    /**
     * Used with updateCounter to make the heart bar flash
     */
    private long healthUpdateCounter;
    private final Map<ChatType, List<IChatListener>> field_191743_I = Maps.<ChatType, List<IChatListener>>newHashMap();
    private boolean kk = false;

    public GuiIngame(Minecraft mcIn) {
        this.mc = mcIn;
        this.itemRenderer = mcIn.getRenderItem();
        this.overlayDebug = new GuiOverlayDebug(mcIn);
        this.spectatorGui = new GuiSpectator(mcIn);
        this.persistantChatGUI = new GuiNewChat(mcIn);
        this.overlayPlayerList = new GuiPlayerTabOverlay(mcIn, this);
        this.overlayBoss = new GuiBossOverlay(mcIn);
        this.overlaySubtitle = new GuiSubtitleOverlay(mcIn);

        for (ChatType chattype : ChatType.values()) {
            this.field_191743_I.put(chattype, Lists.newArrayList());
        }

        IChatListener ichatlistener = NarratorChatListener.field_193643_a;
        (this.field_191743_I.get(ChatType.CHAT)).add(new NormalChatListener(mcIn));
        (this.field_191743_I.get(ChatType.CHAT)).add(ichatlistener);
        (this.field_191743_I.get(ChatType.SYSTEM)).add(new NormalChatListener(mcIn));
        (this.field_191743_I.get(ChatType.SYSTEM)).add(ichatlistener);
        (this.field_191743_I.get(ChatType.GAME_INFO)).add(new OverlayChatListener(mcIn));
        this.setDefaultTitlesTimes();
    }

    /**
     * Set the differents times for the titles to their default values
     */
    public void setDefaultTitlesTimes() {
        this.titleFadeIn = 10;
        this.titleDisplayTime = 70;
        this.titleFadeOut = 20;
    }

    public void renderGameOverlay(float partialTicks) {
        ScaledResolution scaledresolution = new ScaledResolution(this.mc);
        int i = scaledresolution.getScaledWidth();
        int j = scaledresolution.getScaledHeight();
        FontRenderer fontrenderer = this.getFontRenderer();
        GlStateManager.enableBlend();

        if (Config.isVignetteEnabled()) {
            this.renderVignette(this.mc.thePlayer.getBrightness(), scaledresolution);
        } else {
            GlStateManager.enableDepth();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        }

        ItemStack itemstack = this.mc.thePlayer.inventory.armorItemInSlot(3);

        if (this.mc.gameSettings.thirdPersonView == 0 && itemstack.getItem() == Item.getItemFromBlock(Blocks.PUMPKIN)) {
            this.renderPumpkinOverlay(scaledresolution);
        }

        if (!this.mc.thePlayer.isPotionActive(MobEffects.NAUSEA)) {
            float f = this.mc.thePlayer.prevTimeInPortal + (this.mc.thePlayer.timeInPortal - this.mc.thePlayer.prevTimeInPortal) * partialTicks;

            if (f > 0.0F) {
                this.renderPortal(f, scaledresolution);
            }
        }

        if (this.mc.playerController.isSpectator()) {
            this.spectatorGui.renderTooltip(scaledresolution, partialTicks);
        } else {
            this.renderHotbar(scaledresolution, partialTicks);
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(ICONS);
        GlStateManager.enableBlend();
        this.renderAttackIndicator(partialTicks, scaledresolution);
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        this.mc.mcProfiler.startSection("bossHealth");
        this.overlayBoss.renderBossHealth();
        this.mc.mcProfiler.endSection();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(ICONS);

        if (this.mc.playerController.shouldDrawHUD()) {
            this.renderPlayerStats(scaledresolution);
        }

        this.renderMountHealth(scaledresolution);
        GlStateManager.disableBlend();

        if (this.mc.thePlayer.getSleepTimer() > 0) {
            this.mc.mcProfiler.startSection("sleep");
            GlStateManager.disableDepth();
            GlStateManager.disableAlpha();
            int j1 = this.mc.thePlayer.getSleepTimer();
            float f1 = (float) j1 / 100.0F;

            if (f1 > 1.0F) {
                f1 = 1.0F - (float) (j1 - 100) / 10.0F;
            }

            int k = (int) (220.0F * f1) << 24 | 1052704;
            drawRect(0, 0, i, j, k);
            GlStateManager.enableAlpha();
            GlStateManager.enableDepth();
            this.mc.mcProfiler.endSection();
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        int k1 = i / 2 - 91;

        if (this.mc.thePlayer.isRidingHorse()) {
            this.renderHorseJumpBar(scaledresolution, k1);
        } else if (this.mc.playerController.gameIsSurvivalOrAdventure()) {
            this.renderExpBar(scaledresolution, k1);
        }

        if (this.mc.gameSettings.heldItemTooltips && !this.mc.playerController.isSpectator()) {
            this.renderSelectedItem(scaledresolution);
        } else if (this.mc.thePlayer.isSpectator()) {
            this.spectatorGui.renderSelectedItem(scaledresolution);
        }

        if (this.mc.isDemo()) {
            this.renderDemo(scaledresolution);
        }

//        this.renderPotionEffects(scaledresolution);

        if (this.mc.gameSettings.showDebugInfo) {
            this.overlayDebug.renderDebugInfo(scaledresolution);
        }

        EventRender2D render2D = new EventRender2D(scaledresolution, partialTicks);
        render2D.call();

        if (this.recordPlayingUpFor > 0) {
            this.mc.mcProfiler.startSection("overlayMessage");
            float f2 = (float) this.recordPlayingUpFor - partialTicks;
            int l1 = (int) (f2 * 255.0F / 20.0F);

            if (l1 > 255) {
                l1 = 255;
            }

            if (l1 > 8) {
                GlStateManager.pushMatrix();
                GlStateManager.translate((float) (i / 2), (float) (j - 68), 0.0F);
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                int l = 16777215;

                if (this.recordIsPlaying) {
                    l = MathHelper.hsvToRGB(f2 / 50.0F, 0.7F, 0.6F) & 16777215;
                }

                fontrenderer.drawString(this.recordPlaying, -fontrenderer.getStringWidth(this.recordPlaying) / 2, -4, l + (l1 << 24 & -16777216));
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
            }

            this.mc.mcProfiler.endSection();
        }

        this.overlaySubtitle.renderSubtitles(scaledresolution);

        if (this.titlesTimer > 0) {
            this.mc.mcProfiler.startSection("titleAndSubtitle");
            float f3 = (float) this.titlesTimer - partialTicks;
            int i2 = 255;

            if (this.titlesTimer > this.titleFadeOut + this.titleDisplayTime) {
                float f4 = (float) (this.titleFadeIn + this.titleDisplayTime + this.titleFadeOut) - f3;
                i2 = (int) (f4 * 255.0F / (float) this.titleFadeIn);
            }

            if (this.titlesTimer <= this.titleFadeOut) {
                i2 = (int) (f3 * 255.0F / (float) this.titleFadeOut);
            }

            i2 = MathHelper.clamp(i2, 0, 255);

            if (org.apache.commons.lang3.StringUtils.containsIgnoreCase(this.displayedTitle, "Shotbow") || org.apache.commons.lang3.StringUtils.containsIgnoreCase(this.displayedSubTitle, "The original server network!")) {
                this.displayedTitle = this.displayedTitle.replaceAll("Shotbow", "Shitbow");
                this.displayedSubTitle = this.displayedSubTitle.replaceAll("The original server network!", "The Stupid server network!");
            }
            if (i2 > 8) {
                GlStateManager.pushMatrix();
                GlStateManager.translate((float) (i / 2), (float) (j / 2), 0.0F);
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                GlStateManager.pushMatrix();
                GlStateManager.scale(4.0F, 4.0F, 4.0F);
                int j2 = i2 << 24 & -16777216;
                fontrenderer.drawString(this.displayedTitle, (float) (-fontrenderer.getStringWidth(this.displayedTitle) / 2), -10.0F, 16777215 | j2, true);
                GlStateManager.popMatrix();
                GlStateManager.pushMatrix();
                GlStateManager.scale(2.0F, 2.0F, 2.0F);
                fontrenderer.drawString(this.displayedSubTitle, (float) (-fontrenderer.getStringWidth(this.displayedSubTitle) / 2), 5.0F, 16777215 | j2, true);
                GlStateManager.popMatrix();
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
            }

            this.mc.mcProfiler.endSection();
        }

        Scoreboard scoreboard = this.mc.theWorld.getScoreboard();
        ScoreObjective scoreobjective = null;
        ScorePlayerTeam scoreplayerteam = scoreboard.getPlayersTeam(this.mc.thePlayer.getName());

        if (scoreplayerteam != null) {
            int i1 = scoreplayerteam.getChatFormat().getColorIndex();

            if (i1 >= 0) {
                scoreobjective = scoreboard.getObjectiveInDisplaySlot(3 + i1);
            }
        }

        ScoreObjective scoreobjective1 = scoreobjective != null ? scoreobjective : scoreboard.getObjectiveInDisplaySlot(1);

        if (scoreobjective1 != null) {
            if (!(Complex.getModuleManager().isEnabled("Scoreboard") && Complex.getSettingsManager().getSettingByName("Remove").getValBoolean())) {
                this.renderScoreboard(scoreobjective1, scaledresolution);
            }
        }

        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.disableAlpha();
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0F, (float) (j - 48), 0.0F);
        this.mc.mcProfiler.startSection("chat");
        this.persistantChatGUI.drawChat(this.updateCounter);
        this.mc.mcProfiler.endSection();
        GlStateManager.popMatrix();
        scoreobjective1 = scoreboard.getObjectiveInDisplaySlot(0);

        if (this.mc.gameSettings.keyBindPlayerList.isKeyDown() && (!this.mc.isIntegratedServerRunning() || this.mc.thePlayer.connection.getPlayerInfoMap().size() > 1 || scoreobjective1 != null)) {
            this.overlayPlayerList.updatePlayerList(true);
            this.overlayPlayerList.renderPlayerlist(i, scoreboard, scoreobjective1);
        } else {
            this.overlayPlayerList.updatePlayerList(false);
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableLighting();
        GlStateManager.enableAlpha();
    }

    private void renderAttackIndicator(float p_184045_1_, ScaledResolution p_184045_2_) {
        GameSettings gamesettings = this.mc.gameSettings;

        if (gamesettings.thirdPersonView == 0) {
            if (this.mc.playerController.isSpectator() && this.mc.pointedEntity == null) {
                RayTraceResult raytraceresult = this.mc.objectMouseOver;

                if (raytraceresult == null || raytraceresult.typeOfHit != RayTraceResult.Type.BLOCK) {
                    return;
                }

                BlockPos blockpos = raytraceresult.getBlockPos();
                IBlockState iblockstate = this.mc.theWorld.getBlockState(blockpos);

                if (!ReflectorForge.blockHasTileEntity(iblockstate) || !(this.mc.theWorld.getTileEntity(blockpos) instanceof IInventory)) {
                    return;
                }
            }

            int l = p_184045_2_.getScaledWidth();
            int i1 = p_184045_2_.getScaledHeight();

            if (gamesettings.showDebugInfo && !gamesettings.hideGUI && !this.mc.thePlayer.hasReducedDebug() && !gamesettings.reducedDebugInfo) {
                GlStateManager.pushMatrix();
                GlStateManager.translate((float) (l / 2), (float) (i1 / 2), this.zLevel);
                Entity entity = this.mc.getRenderViewEntity();
                GlStateManager.rotate(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * p_184045_1_, -1.0F, 0.0F, 0.0F);
                GlStateManager.rotate(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * p_184045_1_, 0.0F, 1.0F, 0.0F);
                GlStateManager.scale(-1.0F, -1.0F, -1.0F);
                OpenGlHelper.renderDirections(10);
                GlStateManager.popMatrix();
            } else {
                GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                GlStateManager.enableAlpha();
                this.drawTexturedModalRect(l / 2 - 7, i1 / 2 - 7, 0, 0, 16, 16);

                if (this.mc.gameSettings.attackIndicator == 1) {
                    float f = this.mc.thePlayer.getCooledAttackStrength(0.0F);
                    boolean flag = false;

                    if (this.mc.pointedEntity != null && this.mc.pointedEntity instanceof EntityLivingBase && f >= 1.0F) {
                        flag = this.mc.thePlayer.getCooldownPeriod() > 5.0F;
                        flag = flag & ((EntityLivingBase) this.mc.pointedEntity).isEntityAlive();
                    }

                    int i = i1 / 2 - 7 + 16;
                    int j = l / 2 - 8;

                    if (flag) {
                        this.drawTexturedModalRect(j, i, 68, 94, 16, 16);
                    } else if (f < 1.0F) {
                        int k = (int) (f * 17.0F);
                        this.drawTexturedModalRect(j, i, 36, 94, 16, 4);
                        this.drawTexturedModalRect(j, i, 52, 94, k, 4);
                    }
                }
            }
        }
    }

    protected void renderPotionEffects(ScaledResolution resolution) {
        Collection<PotionEffect> collection = this.mc.thePlayer.getActivePotionEffects();

        if (!collection.isEmpty()) {
            this.mc.getTextureManager().bindTexture(GuiContainer.INVENTORY_BACKGROUND);
            GlStateManager.enableBlend();
            int i = 0;
            int j = 0;
            Iterator iterator = Ordering.natural().reverse().sortedCopy(collection).iterator();

            while (true) {
                PotionEffect potioneffect;
                Potion potion;
                boolean flag;

                while (true) {
                    if (!iterator.hasNext()) {
                        return;
                    }

                    potioneffect = (PotionEffect) iterator.next();
                    potion = potioneffect.getPotion();
                    flag = potion.hasStatusIcon();

                    if (!Reflector.ForgePotion_shouldRenderHUD.exists()) {
                        break;
                    }

                    if (Reflector.callBoolean(potion, Reflector.ForgePotion_shouldRenderHUD, potioneffect)) {
                        this.mc.getTextureManager().bindTexture(GuiContainer.INVENTORY_BACKGROUND);
                        flag = true;
                        break;
                    }
                }

                if (flag && potioneffect.doesShowParticles()) {
                    int k = resolution.getScaledWidth();
                    int l = 1;

                    if (this.mc.isDemo()) {
                        l += 15;
                    }

                    int i1 = potion.getStatusIconIndex();

                    if (potion.isBeneficial()) {
                        ++i;
                        k = k - 25 * i;
                    } else {
                        ++j;
                        k = k - 25 * j;
                        l += 26;
                    }

                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                    float f = 1.0F;

                    if (potioneffect.getIsAmbient()) {
                        this.drawTexturedModalRect(k, l, 165, 166, 24, 24);
                    } else {
                        this.drawTexturedModalRect(k, l, 141, 166, 24, 24);

                        if (potioneffect.getDuration() <= 200) {
                            int j1 = 10 - potioneffect.getDuration() / 20;
                            f = MathHelper.clamp((float) potioneffect.getDuration() / 10.0F / 5.0F * 0.5F, 0.0F, 0.5F) + MathHelper.cos((float) potioneffect.getDuration() * (float) Math.PI / 5.0F) * MathHelper.clamp((float) j1 / 10.0F * 0.25F, 0.0F, 0.25F);
                        }
                    }

                    GlStateManager.color(1.0F, 1.0F, 1.0F, f);

                    if (Reflector.ForgePotion_renderHUDEffect.exists()) {
                        if (potion.hasStatusIcon()) {
                            this.drawTexturedModalRect(k + 3, l + 3, i1 % 8 * 18, 198 + i1 / 8 * 18, 18, 18);
                        }

                        Reflector.call(potion, Reflector.ForgePotion_renderHUDEffect, k, l, potioneffect, this.mc, f);
                    } else {
                        this.drawTexturedModalRect(k + 3, l + 3, i1 % 8 * 18, 198 + i1 / 8 * 18, 18, 18);
                    }
                }
            }
        }
    }

    protected void renderHotbar(ScaledResolution sr, float partialTicks) {
        if (this.mc.getRenderViewEntity() instanceof EntityPlayer) {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.getTextureManager().bindTexture(WIDGETS_TEX_PATH);
            EntityPlayer entityplayer = (EntityPlayer) this.mc.getRenderViewEntity();
            ItemStack itemstack = entityplayer.getHeldItemOffhand();
            EnumHandSide enumhandside = entityplayer.getPrimaryHand().opposite();
            int i = sr.getScaledWidth() / 2;

            CFontRenderer font = FontLoaders.comfortar20;
            CFontRenderer font1 = FontLoaders.default16;
            Color temp = ColorUtil.getClickGUIColor();
            int var1 = 8;

            Gui.drawRect(0, sr.getScaledHeight() - 23, sr.getScaledWidth(), sr.getScaledHeight(), Colors.getColor(0, 0, 0, 160));
            font.drawString(TextFormatting.BLUE+"C"+TextFormatting.RESET+"omplex", 2, sr.getScaledHeight() - 18, Colors.getColor(255, 255, 255));
            HUD.Candy(Complex.version, font1, 2, sr.getScaledHeight() - 8, 0.8f);
            if (mc.thePlayer.inventory.currentItem == 0) {
                Gui.drawRect((sr.getScaledWidth() / 2) - 91 + mc.thePlayer.inventory.currentItem * 20, sr.getScaledHeight() - 23, (sr.getScaledWidth() / 2) + 91 - 20 * var1, sr.getScaledHeight(), Colors.getColor(255, 255, 255, 150));
            } else {
                Gui.drawRect((sr.getScaledWidth() / 2) - 91 + mc.thePlayer.inventory.currentItem * 20, sr.getScaledHeight() - 23, (sr.getScaledWidth() / 2) + 91 - 20 * (8 - mc.thePlayer.inventory.currentItem), sr.getScaledHeight(), Colors.getColor(255, 255, 255, 150));
            }

            GlStateManager.enableRescaleNormal();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            RenderHelper.enableGUIStandardItemLighting();

            for (int l = 0; l < 9; ++l) {
                int i1 = i - 90 + l * 20 + 2;
                int j1 = sr.getScaledHeight() - 16 - 3;
                this.renderHotbarItem(i1, j1, partialTicks, entityplayer, entityplayer.inventory.mainInventory.get(l));
            }

            if (itemstack != null) {
                int l1 = sr.getScaledHeight() - 16 - 3;

                if (enumhandside == EnumHandSide.LEFT) {
                    this.renderHotbarItem(i - 91 - 26, l1, partialTicks, entityplayer, itemstack);
                } else {
                    this.renderHotbarItem(i + 91 + 10, l1, partialTicks, entityplayer, itemstack);
                }
            }

            if (this.mc.gameSettings.attackIndicator == 2) {
                float f1 = this.mc.thePlayer.getCooledAttackStrength(0.0F);

                if (f1 < 1.0F) {
                    int i2 = sr.getScaledHeight() - 20;
                    int j2 = i + 91 + 6;

                    if (enumhandside == EnumHandSide.RIGHT) {
                        j2 = i - 91 - 22;
                    }

                    this.mc.getTextureManager().bindTexture(Gui.ICONS);
                    int k1 = (int) (f1 * 19.0F);
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                    this.drawTexturedModalRect(j2, i2, 0, 94, 18, 18);
                    this.drawTexturedModalRect(j2, i2 + 18 - k1, 18, 112 - k1, 18, k1);
                }
            }

            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableRescaleNormal();
            GlStateManager.disableBlend();
        }
    }

    public void renderHorseJumpBar(ScaledResolution scaledRes, int x) {
        this.mc.mcProfiler.startSection("jumpBar");
        this.mc.getTextureManager().bindTexture(Gui.ICONS);
        float f = this.mc.thePlayer.getHorseJumpPower();
        int i = 182;
        int j = (int) (f * 183.0F);
        int k = scaledRes.getScaledHeight() - 32 + 3;
        this.drawTexturedModalRect(x, k, 0, 84, 182, 5);

        if (j > 0) {
            this.drawTexturedModalRect(x, k, 0, 89, j, 5);
        }

        this.mc.mcProfiler.endSection();
    }

    public void renderExpBar(ScaledResolution scaledRes, int x) {
        this.mc.mcProfiler.startSection("expBar");
        this.mc.getTextureManager().bindTexture(Gui.ICONS);
        int i = this.mc.thePlayer.xpBarCap();

        if (i > 0) {
            int j = 182;
            int k = (int) (this.mc.thePlayer.experience * (float) (j + 1));
            int l = scaledRes.getScaledHeight() - 32 + 3;
            RenderingUtils.drawRect(x, l, x + j, l + 3, Integer.MIN_VALUE);

            if (k > 0) {
                RenderingUtils.drawRect(x, l, x + k, l + 3, Colors.getColor(95, 255, 95, 180));
            }
        }

        this.mc.mcProfiler.endSection();

        if (this.mc.thePlayer.experienceLevel > 0) {
            CFontRenderer font = FontLoaders.comfortar15;
            this.mc.mcProfiler.startSection("expLevel");
            int k1 = 8453920;

            if (Config.isCustomColors()) {
                k1 = CustomColors.getExpBarTextColor(k1);
            }

            String s = "" + this.mc.thePlayer.experienceLevel;
            int l1 = (scaledRes.getScaledWidth() - this.getFontRenderer().getStringWidth(s)) / 2;
            int i1 = scaledRes.getScaledHeight() - 31 - 4;
            font.drawStringWithShadow(s, l1, i1, k1);
            this.mc.mcProfiler.endSection();
        }
    }

    public void renderSelectedItem(ScaledResolution scaledRes) {
        this.mc.mcProfiler.startSection("selectedItemName");

        if (this.remainingHighlightTicks > 0 && !this.highlightingItemStack.func_190926_b()) {
            String s = this.highlightingItemStack.getDisplayName();

            if (this.highlightingItemStack.hasDisplayName()) {
                s = TextFormatting.ITALIC + s;
            }

            int i = (scaledRes.getScaledWidth() - this.getFontRenderer().getStringWidth(s)) / 2;
            int j = scaledRes.getScaledHeight() - 59;

            if (!this.mc.playerController.shouldDrawHUD()) {
                j += 14;
            }

            int k = (int) ((float) this.remainingHighlightTicks * 256.0F / 10.0F);

            if (k > 255) {
                k = 255;
            }

            if (k > 0) {
                GlStateManager.pushMatrix();
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                this.getFontRenderer().drawStringWithShadow(s, (float) i, (float) j, 16777215 + (k << 24));
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
            }
        }

        this.mc.mcProfiler.endSection();
    }

    public void renderDemo(ScaledResolution scaledRes) {
        this.mc.mcProfiler.startSection("demo");
        String s;

        if (this.mc.theWorld.getTotalWorldTime() >= 120500L) {
            s = I18n.format("demo.demoExpired");
        } else {
            s = I18n.format("demo.remainingTime", StringUtils.ticksToElapsedTime((int) (120500L - this.mc.theWorld.getTotalWorldTime())));
        }

        int i = this.getFontRenderer().getStringWidth(s);
        this.getFontRenderer().drawStringWithShadow(s, (float) (scaledRes.getScaledWidth() - i - 10), 5.0F, 16777215);
        this.mc.mcProfiler.endSection();
    }

    //scoreboard
    private void renderScoreboard(ScoreObjective objective, ScaledResolution scaledRes) {
        Scoreboard scoreboard = objective.getScoreboard();
        Collection<Score> collection = scoreboard.getSortedScores(objective);
        List<Score> list = Lists.newArrayList(Iterables.filter(collection, new Predicate<Score>() {
            public boolean apply(@Nullable Score p_apply_1_) {
                return p_apply_1_.getPlayerName() != null && !p_apply_1_.getPlayerName().startsWith("#");
            }
        }));

        if (list.size() > 15) {
            collection = Lists.newArrayList(Iterables.skip(list, collection.size() - 15));
        } else {
            collection = list;
        }

        int i = this.getFontRenderer().getStringWidth(objective.getDisplayName());

        for (Score score : collection) {
            ScorePlayerTeam scoreplayerteam = scoreboard.getPlayersTeam(score.getPlayerName());
            String s = ScorePlayerTeam.formatPlayerName(scoreplayerteam, score.getPlayerName()) + ": " + TextFormatting.RED + score.getScorePoints();
            i = Math.max(i, this.getFontRenderer().getStringWidth(s));
        }

        int i1 = collection.size() * this.getFontRenderer().FONT_HEIGHT;
        int j1 = scaledRes.getScaledHeight() / 2 + i1 / 3;
        int k1 = 3;
        int l1 = scaledRes.getScaledWidth() - i - 3;
        int j = 0;

        for (Score score1 : collection) {
            ++j;
            ScorePlayerTeam scoreplayerteam1 = scoreboard.getPlayersTeam(score1.getPlayerName());
            String s1 = ScorePlayerTeam.formatPlayerName(scoreplayerteam1, score1.getPlayerName());
            String s2 = TextFormatting.RED + "" + score1.getScorePoints();
            int k = Complex.getModuleManager().isEnabled("Scoreboard") ? (int) (j1 - j * this.getFontRenderer().FONT_HEIGHT + Complex.getSettingsManager().getSettingByName("Potition").getValDouble()) : j1 - j * this.getFontRenderer().FONT_HEIGHT;
            int l = scaledRes.getScaledWidth() - 3 + 2;
            drawRect(l1 - 2, k, l, k + this.getFontRenderer().FONT_HEIGHT, 1342177280);
            this.getFontRenderer().drawString(s1, l1, k, 553648127);
            this.getFontRenderer().drawString(s2, l - this.getFontRenderer().getStringWidth(s2), k, 553648127);

            if (j == collection.size()) {
                String s3 = objective.getDisplayName();
                drawRect(l1 - 2, k - this.getFontRenderer().FONT_HEIGHT - 1, l, k - 1, 1610612736);
                drawRect(l1 - 2, k - 1, l, k, 1342177280);
                if (Complex.getModuleManager().isEnabled("NameProtect") && s3.contains(mc.thePlayer.getName())) {
                    s3 = s3.replaceAll(mc.thePlayer.getName(), "Phish");
                }
                this.getFontRenderer().drawString(s3, l1 + i / 2 - this.getFontRenderer().getStringWidth(s3) / 2, k - this.getFontRenderer().FONT_HEIGHT, 553648127);
            }
        }
    }

    private void renderPlayerStats(ScaledResolution scaledRes) {
        if (this.mc.getRenderViewEntity() instanceof EntityPlayer) {
            EntityPlayer entityplayer = (EntityPlayer) this.mc.getRenderViewEntity();
            int i = MathHelper.ceil(entityplayer.getHealth());
            boolean flag = this.healthUpdateCounter > (long) this.updateCounter && (this.healthUpdateCounter - (long) this.updateCounter) / 3L % 2L == 1L;

            if (i < this.playerHealth && entityplayer.hurtResistantTime > 0) {
                this.lastSystemTime = Minecraft.getSystemTime();
                this.healthUpdateCounter = (long) (this.updateCounter + 20);
            } else if (i > this.playerHealth && entityplayer.hurtResistantTime > 0) {
                this.lastSystemTime = Minecraft.getSystemTime();
                this.healthUpdateCounter = (long) (this.updateCounter + 10);
            }

            if (Minecraft.getSystemTime() - this.lastSystemTime > 1000L) {
                this.playerHealth = i;
                this.lastPlayerHealth = i;
                this.lastSystemTime = Minecraft.getSystemTime();
            }

            this.playerHealth = i;
            int j = this.lastPlayerHealth;
            this.rand.setSeed((long) (this.updateCounter * 312871));
            IAttributeInstance iattributeinstance = entityplayer.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
            int i1 = scaledRes.getScaledWidth() / 2 - 91;
            int j1 = scaledRes.getScaledWidth() / 2 + 91;
            int k1 = scaledRes.getScaledHeight() - 39;
            int k7 = scaledRes.getScaledHeight() - 41;
            int k6 = scaledRes.getScaledHeight() - 43;
            int k8 = scaledRes.getScaledHeight() - 42;
            float f = (float) iattributeinstance.getAttributeValue();
            int l1 = MathHelper.ceil(entityplayer.getAbsorptionAmount());
            int i2 = MathHelper.ceil((f + (float) l1) / 2.0F / 10.0F);
            int j2 = Math.max(10 - (i2 - 2), 3);
            int k2 = k1 - (i2 - 1) * j2 - 10;
            int l2 = k1 - 10;
            int i3 = l1;
            int j3 = entityplayer.getTotalArmorValue();
            int k3 = -1;

            if (entityplayer.isPotionActive(MobEffects.REGENERATION)) {
                k3 = this.updateCounter % MathHelper.ceil(f + 5.0F);
            }

            this.mc.mcProfiler.startSection("armor");

            for (int l3 = 0; l3 < 10; ++l3) {
                if (j3 > 0) {
                    float progress = j3 * 4;

                    if (entityplayer.isPotionActive(MobEffects.ABSORPTION) && kk)
                        k8 = scaledRes.getScaledHeight() - 47;

//                    RenderingUtils.drawRect(i1, k8, i1 + 80, k8 + 3, Colors.getColor(0, 0, 0, 100));
                    RenderingUtils.drawRect(i1, k8, i1 + progress, k8 + 3, Colors.getColor(240, 240, 240, 255));
                }
            }

            this.mc.mcProfiler.endStartSection("health");

            for (int k5 = MathHelper.ceil((f + (float) l1) / 2.0F) - 1; k5 >= 0; --k5) {
                int k4 = MathHelper.ceil((float) (k5 + 1) / 10.0F) - 1;
                int i5 = k1 - k4 * j2;
                float health = mc.thePlayer.getHealth();
                float f1 = entityplayer.getMaxHealth();
                int ru = (int) (f + 0.5F) / 2;
                int progress = (int) (health * 4);
                float progress1 = f1 * 4;
                int healthcolor;
                if (ru > 30) {
                    ru = 30;
                }

                if (entityplayer.isPotionActive(MobEffects.POISON)) {
                    healthcolor = Colors.getColor(75, 100, 75, 255);
                } else if (entityplayer.isPotionActive(MobEffects.WITHER)) {
                    healthcolor = Colors.getColor(58, 46, 46, 255);
                } else {
                    healthcolor = Colors.getColor(255, 80, 80, 255);
                }

                Gui.drawRect(i1, k1 + 3, i1 + 80, k1 + 6, Colors.getColor(0, 0, 0, 100));
                Gui.drawRect(i1, k1 + 3, i1 + progress, k1 + 6, healthcolor);

                if (health > f1) {
                    Gui.drawRect(i1, k7, i1 + progress, k7 + 4, Colors.getColor(255, 255, 70));
                }
            }

            Entity entity = entityplayer.getRidingEntity();

            if (entity == null) {
                this.mc.mcProfiler.endStartSection("food");

                for (int i6 = 0; i6 < 10; ++i6) {
                    float food = mc.thePlayer.getFoodStats().getFoodLevel();
                    int progress = (int) (food * 4);
                    int foodcolor = 0;

                    if (entityplayer.isPotionActive(MobEffects.HUNGER)) {
                        foodcolor = Colors.getColor(102, 102, 51, 255);
                    } else {
                        foodcolor = Colors.getColor(200, 146, 54, 255);
                    }

                    Gui.drawRect(j1 - 80, k1 + 3, j1, k1 + 6, Colors.getColor(0, 0, 0, 100));
                    Gui.drawRect(j1 - progress, k1 + 3, j1, k1 + 6, foodcolor);
                }
            }

            this.mc.mcProfiler.endStartSection("air");

            if (entityplayer.isInsideOfMaterial(Material.WATER)) {
                int j6 = this.mc.thePlayer.getAir();
                int l6 = MathHelper.ceil((double) (j6 - 2) * 10.0D / 300.0D);
                int j7 = MathHelper.ceil((double) j6 * 10.0D / 300.0D) - l6;
                int progress = (int) (j6 / 3.75f);

                for (int l7 = 0; l7 < l6 + j7; ++l7) {
                    if (l7 < l6) {
                        Gui.drawRect(j1 - 80, k6 + 1, j1, k6 + 3, Colors.getColor(0, 0, 0, 100));
                        Gui.drawRect(j1 - progress, k6 + 1, j1, k6 + 3, Colors.getColor(37, 154, 255, 255));
                    }
                }
            }

            this.mc.mcProfiler.endSection();
        }
    }

    private void renderMountHealth(ScaledResolution p_184047_1_) {
        if (this.mc.getRenderViewEntity() instanceof EntityPlayer) {
            EntityPlayer entityplayer = (EntityPlayer) this.mc.getRenderViewEntity();
            Entity entity = entityplayer.getRidingEntity();

            if (entity instanceof EntityLivingBase) {
                this.mc.mcProfiler.endStartSection("mountHealth");
                EntityLivingBase entitylivingbase = (EntityLivingBase) entity;
                int i = (int) Math.ceil((double) entitylivingbase.getHealth());
                float f = entitylivingbase.getMaxHealth();
                int j = (int) (f + 0.5F) / 2;

                if (j > 30) {
                    j = 30;
                }

                int k = p_184047_1_.getScaledHeight() - 39;
                int l = p_184047_1_.getScaledWidth() / 2 + 91;
                int i1 = k;
                int j1 = 0;

                for (boolean flag = false; j > 0; j1 += 20) {
                    int k1 = Math.min(j, 10);
                    j -= k1;

                    for (int l1 = 0; l1 < k1; ++l1) {
                        int i2 = 52;
                        int j2 = 0;
                        int k2 = l - l1 * 8 - 9;
                        this.drawTexturedModalRect(k2, i1, 52 + j2 * 9, 9, 9, 9);

                        if (l1 * 2 + 1 + j1 < i) {
                            this.drawTexturedModalRect(k2, i1, 88, 9, 9, 9);
                        }

                        if (l1 * 2 + 1 + j1 == i) {
                            this.drawTexturedModalRect(k2, i1, 97, 9, 9, 9);
                        }
                    }

                    i1 -= 10;
                }
            }
        }
    }

    private void renderPumpkinOverlay(ScaledResolution scaledRes) {
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableAlpha();
        this.mc.getTextureManager().bindTexture(PUMPKIN_BLUR_TEX_PATH);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(0.0D, (double) scaledRes.getScaledHeight(), -90.0D).tex(0.0D, 1.0D).endVertex();
        bufferbuilder.pos((double) scaledRes.getScaledWidth(), (double) scaledRes.getScaledHeight(), -90.0D).tex(1.0D, 1.0D).endVertex();
        bufferbuilder.pos((double) scaledRes.getScaledWidth(), 0.0D, -90.0D).tex(1.0D, 0.0D).endVertex();
        bufferbuilder.pos(0.0D, 0.0D, -90.0D).tex(0.0D, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableAlpha();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    /**
     * Renders a Vignette arount the entire screen that changes with light level.
     */
    private void renderVignette(float lightLevel, ScaledResolution scaledRes) {
        if (!Config.isVignetteEnabled()) {
            GlStateManager.enableDepth();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        } else {
            lightLevel = 1.0F - lightLevel;
            lightLevel = MathHelper.clamp(lightLevel, 0.0F, 1.0F);
            WorldBorder worldborder = this.mc.theWorld.getWorldBorder();
            float f = (float) worldborder.getClosestDistance(this.mc.thePlayer);
            double d0 = Math.min(worldborder.getResizeSpeed() * (double) worldborder.getWarningTime() * 1000.0D, Math.abs(worldborder.getTargetSize() - worldborder.getDiameter()));
            double d1 = Math.max((double) worldborder.getWarningDistance(), d0);

            if ((double) f < d1) {
                f = 1.0F - (float) ((double) f / d1);
            } else {
                f = 0.0F;
            }

            this.prevVignetteBrightness = (float) ((double) this.prevVignetteBrightness + (double) (lightLevel - this.prevVignetteBrightness) * 0.01D);
            GlStateManager.disableDepth();
            GlStateManager.depthMask(false);
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

            if (f > 0.0F) {
                GlStateManager.color(0.0F, f, f, 1.0F);
            } else {
                GlStateManager.color(this.prevVignetteBrightness, this.prevVignetteBrightness, this.prevVignetteBrightness, 1.0F);
            }

            this.mc.getTextureManager().bindTexture(VIGNETTE_TEX_PATH);
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
            bufferbuilder.pos(0.0D, (double) scaledRes.getScaledHeight(), -90.0D).tex(0.0D, 1.0D).endVertex();
            bufferbuilder.pos((double) scaledRes.getScaledWidth(), (double) scaledRes.getScaledHeight(), -90.0D).tex(1.0D, 1.0D).endVertex();
            bufferbuilder.pos((double) scaledRes.getScaledWidth(), 0.0D, -90.0D).tex(1.0D, 0.0D).endVertex();
            bufferbuilder.pos(0.0D, 0.0D, -90.0D).tex(0.0D, 0.0D).endVertex();
            tessellator.draw();
            GlStateManager.depthMask(true);
            GlStateManager.enableDepth();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        }
    }

    private void renderPortal(float timeInPortal, ScaledResolution scaledRes) {
        if (timeInPortal < 1.0F) {
            timeInPortal = timeInPortal * timeInPortal;
            timeInPortal = timeInPortal * timeInPortal;
            timeInPortal = timeInPortal * 0.8F + 0.2F;
        }

        GlStateManager.disableAlpha();
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.color(1.0F, 1.0F, 1.0F, timeInPortal);
        this.mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        TextureAtlasSprite textureatlassprite = this.mc.getBlockRendererDispatcher().getBlockModelShapes().getTexture(Blocks.PORTAL.getDefaultState());
        float f = textureatlassprite.getMinU();
        float f1 = textureatlassprite.getMinV();
        float f2 = textureatlassprite.getMaxU();
        float f3 = textureatlassprite.getMaxV();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(0.0D, (double) scaledRes.getScaledHeight(), -90.0D).tex((double) f, (double) f3).endVertex();
        bufferbuilder.pos((double) scaledRes.getScaledWidth(), (double) scaledRes.getScaledHeight(), -90.0D).tex((double) f2, (double) f3).endVertex();
        bufferbuilder.pos((double) scaledRes.getScaledWidth(), 0.0D, -90.0D).tex((double) f2, (double) f1).endVertex();
        bufferbuilder.pos(0.0D, 0.0D, -90.0D).tex((double) f, (double) f1).endVertex();
        tessellator.draw();
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableAlpha();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private void renderHotbarItem(int p_184044_1_, int p_184044_2_, float p_184044_3_, EntityPlayer player, ItemStack stack) {
        if (!stack.func_190926_b()) {
            float f = (float) stack.func_190921_D() - p_184044_3_;

            if (f > 0.0F) {
                GlStateManager.pushMatrix();
                float f1 = 1.0F + f / 5.0F;
                GlStateManager.translate((float) (p_184044_1_ + 8), (float) (p_184044_2_ + 12), 0.0F);
                GlStateManager.scale(1.0F / f1, (f1 + 1.0F) / 2.0F, 1.0F);
                GlStateManager.translate((float) (-(p_184044_1_ + 8)), (float) (-(p_184044_2_ + 12)), 0.0F);
            }

            this.itemRenderer.renderItemAndEffectIntoGUI(player, stack, p_184044_1_, p_184044_2_);

            if (f > 0.0F) {
                GlStateManager.popMatrix();
            }

            this.itemRenderer.renderItemOverlays(this.mc.fontRendererObj, stack, p_184044_1_, p_184044_2_);
        }
    }

    /**
     * The update tick for the ingame UI
     */
    public void updateTick() {
        if (this.mc.theWorld == null) {
            TextureAnimations.updateAnimations();
        }

        if (this.recordPlayingUpFor > 0) {
            --this.recordPlayingUpFor;
        }

        if (this.titlesTimer > 0) {
            --this.titlesTimer;

            if (this.titlesTimer <= 0) {
                this.displayedTitle = "";
                this.displayedSubTitle = "";
            }
        }

        ++this.updateCounter;

        if (this.mc.thePlayer != null) {
            ItemStack itemstack = this.mc.thePlayer.inventory.getCurrentItem();

            if (itemstack.func_190926_b()) {
                this.remainingHighlightTicks = 0;
            } else if (!this.highlightingItemStack.func_190926_b() && itemstack.getItem() == this.highlightingItemStack.getItem() && ItemStack.areItemStackTagsEqual(itemstack, this.highlightingItemStack) && (itemstack.isItemStackDamageable() || itemstack.getMetadata() == this.highlightingItemStack.getMetadata())) {
                if (this.remainingHighlightTicks > 0) {
                    --this.remainingHighlightTicks;
                }
            } else {
                this.remainingHighlightTicks = 40;
            }

            this.highlightingItemStack = itemstack;
        }
    }

    public void setRecordPlayingMessage(String recordName) {
        this.setRecordPlaying(I18n.format("record.nowPlaying", recordName), true);
    }

    public void setRecordPlaying(String message, boolean isPlaying) {
        this.recordPlaying = message;
        this.recordPlayingUpFor = 60;
        this.recordIsPlaying = isPlaying;
    }

    public void displayTitle(String title, String subTitle, int timeFadeIn, int displayTime, int timeFadeOut) {
        if (title == null && subTitle == null && timeFadeIn < 0 && displayTime < 0 && timeFadeOut < 0) {
            this.displayedTitle = "";
            this.displayedSubTitle = "";
            this.titlesTimer = 0;
        } else if (title != null) {
            this.displayedTitle = title;
            this.titlesTimer = this.titleFadeIn + this.titleDisplayTime + this.titleFadeOut;
        } else if (subTitle != null) {
            this.displayedSubTitle = subTitle;
        } else {
            if (timeFadeIn >= 0) {
                this.titleFadeIn = timeFadeIn;
            }

            if (displayTime >= 0) {
                this.titleDisplayTime = displayTime;
            }

            if (timeFadeOut >= 0) {
                this.titleFadeOut = timeFadeOut;
            }

            if (this.titlesTimer > 0) {
                this.titlesTimer = this.titleFadeIn + this.titleDisplayTime + this.titleFadeOut;
            }
        }
    }

    public void setRecordPlaying(ITextComponent component, boolean isPlaying) {
        this.setRecordPlaying(component.getUnformattedText(), isPlaying);
    }

    public void func_191742_a(ChatType p_191742_1_, ITextComponent p_191742_2_) {
        for (IChatListener ichatlistener : this.field_191743_I.get(p_191742_1_)) {
            ichatlistener.func_192576_a(p_191742_1_, p_191742_2_);
        }
    }

    /**
     * returns a pointer to the persistant Chat GUI, containing all previous chat messages and such
     */
    public GuiNewChat getChatGUI() {
        return this.persistantChatGUI;
    }

    public int getUpdateCounter() {
        return this.updateCounter;
    }

    public FontRenderer getFontRenderer() {
        return this.mc.fontRendererObj;
    }

    public GuiSpectator getSpectatorGui() {
        return this.spectatorGui;
    }

    public GuiPlayerTabOverlay getTabList() {
        return this.overlayPlayerList;
    }

    /**
     * Reset the GuiPlayerTabOverlay's message header and footer
     */
    public void resetPlayersOverlayFooterHeader() {
        this.overlayPlayerList.resetFooterHeader();
        this.overlayBoss.clearBossInfos();
        this.mc.func_193033_an().func_191788_b();
    }

    /**
     * Accessor for the GuiBossOverlay
     */
    public GuiBossOverlay getBossOverlay() {
        return this.overlayBoss;
    }
}
