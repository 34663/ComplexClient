package complex.module.impl.visual;

import complex.event.EventTarget;
import complex.event.impl.EventRender2D;
import complex.module.Category;
import complex.module.Module;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;

public class DrawPlayer extends Module {
    public DrawPlayer() {
        super("DrawPlayer", null, 0, 0, Category.Visual);
    }

    @EventTarget
    public void onRender2D(EventRender2D render2D) {
        this.setDisplayName("DrawPlayer");
        GuiInventory.drawEntityOnScreen(render2D.getResolution().getScaledWidth() - 25, render2D.getResolution().getScaledHeight() - 35, 35, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, mc.thePlayer);
    }
}
