package complex.module.impl.visual;

import complex.Complex;
import complex.event.EventTarget;
import complex.event.impl.EventRender3D;
import complex.module.Category;
import complex.module.Module;
import complex.module.data.Setting;
import complex.utils.BlockUtils;
import complex.utils.render.RenderingUtils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Timer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import java.awt.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import static org.lwjgl.opengl.GL11.*;

public class PropHuntESP extends Module {
    public static final Map<BlockPos, Long> blocks = new HashMap<>();

    public PropHuntESP() {
        super("PropHuntESP", null, 0, 0, Category.Visual);
    }

    @Override
    public void setup() {
        Complex.getSettingsManager().rSetting(new Setting("FarmHunt", this, false));
        Complex.getSettingsManager().rSetting(new Setting("BlockHunt", this, false));
    }

    @EventTarget
    public void onRender3D(EventRender3D event) {
        final Color color = new Color(255, 255 ,255, 95);

        for (final Entity entity : mc.theWorld.loadedEntityList) {
            if (!(entity instanceof EntityFallingBlock))
                continue;

            drawEntityBox(entity, color, true);
        }

        synchronized (blocks) {
            final Iterator<Map.Entry<BlockPos, Long>> iterator = blocks.entrySet().iterator();

            while (iterator.hasNext()) {
                final Map.Entry<BlockPos, Long> entry = iterator.next();

                if (System.currentTimeMillis() - entry.getValue() > 2000L) {
                    iterator.remove();
                    continue;
                }

                drawBlockBox(entry.getKey(), color, true);
            }
        }
    }

    public static void drawEntityBox(final Entity entity, final Color color, final boolean outline) {
        final RenderManager renderManager = mc.getRenderManager();
        final Timer timer = mc.timer;

        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        enableGlCap(GL_BLEND);
        disableGlCap(GL_TEXTURE_2D, GL_DEPTH_TEST);
        glDepthMask(false);

        final double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * timer.renderPartialTicks - renderManager.renderPosX;
        final double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * timer.renderPartialTicks - renderManager.renderPosY;
        final double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * timer.renderPartialTicks - renderManager.renderPosZ;

        final AxisAlignedBB entityBox = entity.getEntityBoundingBox();
        final AxisAlignedBB axisAlignedBB = new AxisAlignedBB(
                entityBox.minX - entity.posX + x - 0.05D,
                entityBox.minY - entity.posY + y,
                entityBox.minZ - entity.posZ + z - 0.05D,
                entityBox.maxX - entity.posX + x + 0.05D,
                entityBox.maxY - entity.posY + y + 0.15D,
                entityBox.maxZ - entity.posZ + z + 0.05D
        );

        if (outline) {
            glLineWidth(1F);
            enableGlCap(GL_LINE_SMOOTH);
            RenderingUtils.glColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
            RenderGlobal.drawSelectionBoundingBox(axisAlignedBB, color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
        }

        RenderingUtils.glColor(color.getRed(), color.getGreen(), color.getBlue(), outline ? 26 : 35);
        RenderingUtils.drawFilledBox(axisAlignedBB);
        GlStateManager.resetColor();
        glDepthMask(true);
        RenderingUtils.resetCaps();
    }
    public static void drawBlockBox(final BlockPos blockPos, final Color color, final boolean outline) {
        final RenderManager renderManager = mc.getRenderManager();
        final Timer timer = mc.timer;

        final double x = blockPos.getX() - renderManager.renderPosX;
        final double y = blockPos.getY() - renderManager.renderPosY;
        final double z = blockPos.getZ() - renderManager.renderPosZ;

        AxisAlignedBB axisAlignedBB = new AxisAlignedBB(x, y, z, x + 1.0, y + 1.0, z + 1.0);
        final Block block = BlockUtils.getBlock(blockPos);

        if (block != null) {
            final EntityPlayer player = mc.thePlayer;

            final double posX = player.lastTickPosX + (player.posX - player.lastTickPosX) * (double) timer.renderPartialTicks;
            final double posY = player.lastTickPosY + (player.posY - player.lastTickPosY) * (double) timer.renderPartialTicks;
            final double posZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double) timer.renderPartialTicks;
            axisAlignedBB = block.getSelectedBoundingBox((IBlockState) block, mc.theWorld, blockPos).expand(0.0020000000949949026D, 0.0020000000949949026D, 0.0020000000949949026D).offset(-posX, -posY, -posZ);
        }

        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        enableGlCap(GL_BLEND);
        disableGlCap(GL_TEXTURE_2D, GL_DEPTH_TEST);
        glDepthMask(false);

        RenderingUtils.glColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha() != 255 ? color.getAlpha() : outline ? 26 : 35);
        RenderingUtils.drawFilledBox(axisAlignedBB);

        if (outline) {
            glLineWidth(1F);
            enableGlCap(GL_LINE_SMOOTH);
            RenderingUtils.glColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
            RenderGlobal.drawSelectionBoundingBox(axisAlignedBB, color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
        }

        GlStateManager.resetColor();
        glDepthMask(true);
        RenderingUtils.resetCaps();
    }

    public static void enableGlCap(final int cap) {
        RenderingUtils.setGlCap(cap, true);
    }
    public static void disableGlCap(final int... caps) {
        for (final int cap : caps)
            RenderingUtils.setGlCap(cap, false);
    }
}
