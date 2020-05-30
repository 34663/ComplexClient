package complex.module.impl.misc;

import complex.event.EventTarget;
import complex.event.impl.EventRender2D;
import complex.gui.clickgui.util.FontUtil;
import complex.module.Category;
import complex.module.Module;
import complex.module.impl.hud.HUD;
import complex.utils.BlockUtils;
import complex.utils.font.CFontRenderer;
import complex.utils.font.FontLoaders;
import complex.utils.render.Colors;
import complex.utils.render.RenderingUtils;
import net.minecraft.block.Block;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.BlockPos;

public class BlockInfo extends Module {
    public BlockInfo() {
        super("BlockInfo", null, 0, 0, Category.Misc);
    }

    @Override
    public void onEnable() {
        this.setDisplayName("BlockInfo");
        super.onEnable();
    }

    @EventTarget
    public void onRender2D(EventRender2D er) {
        if (mc.objectMouseOver != null) {
            mc.theWorld.getBlockState(mc.objectMouseOver.getBlockPos()).getBlock();
            if (BlockUtils.canBeClicked(mc.objectMouseOver.getBlockPos())) {
                final BlockPos blockPos = mc.objectMouseOver.getBlockPos();
                final Block block = mc.theWorld.getBlockState(blockPos).getBlock();

                if (mc.theWorld.getWorldBorder().contains(blockPos)) {
                    final String info = block.getLocalizedName() + " §7ID: " + Block.getIdFromBlock(block);
                    final ScaledResolution sr = new ScaledResolution(mc);
                    RenderingUtils.drawRoundedRect(sr.getScaledWidth() / 2, sr.getScaledHeight() / 2 + 7, sr.getScaledWidth() / 2 + FontUtil.getStringWidth(info) + 6, sr.getScaledHeight() / 2 + 21, 2.0f, Colors.getColor(0, 0, 0, 100));
                    FontUtil.drawString(info, sr.getScaledWidth() / 2 + 2.5, sr.getScaledHeight() / 2 + 7.5, Colors.getColor(255, 255, 255));
                }
            }
        }
    }
}
