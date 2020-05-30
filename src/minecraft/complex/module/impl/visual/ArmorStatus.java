package complex.module.impl.visual;

import complex.event.EventTarget;
import complex.event.impl.EventRender2D;
import complex.module.Category;
import complex.module.Module;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;
import java.util.*;

public class ArmorStatus extends Module {
    public ArmorStatus() {
        super("ArmorStatus", null, 0, 0, Category.Visual);
    }

    @Override
    public void onEnable() {
        this.setDisplayName("ArmorStatus");
        super.onEnable();
    }

    @EventTarget
    public void onRender2D(EventRender2D er) {
        GL11.glPushMatrix();
        List stuff = new ArrayList();
        boolean onwater = mc.thePlayer.isEntityAlive() && mc.thePlayer.isInsideOfMaterial(Material.WATER);
        int split = -3;

        ItemStack errything;
        for (int index = 3; index >= 0; --index) {
            errything = mc.thePlayer.inventory.armorInventory.get(index);
            if (errything != null) {
                stuff.add(errything);
            }
        }

        if (mc.thePlayer.getCurrentEquippedItem() != null) {
            stuff.add(mc.thePlayer.getCurrentEquippedItem());
        }

        Iterator var8 = stuff.iterator();

        while (var8.hasNext()) {
            errything = (ItemStack) var8.next();
            if (mc.theWorld != null) {
                RenderHelper.enableGUIStandardItemLighting();
                split += 16;
            }

            GlStateManager.pushMatrix();
            GlStateManager.disableAlpha();
            GlStateManager.clear(256);
            mc.getRenderItem().zLevel = -150.0F;
            mc.getRenderItem().renderItemAndEffectIntoGUI(errything, split + er.getResolution().getScaledWidth() - 330, er.getResolution().getScaledHeight() - 40);
            mc.getRenderItem().renderItemOverlays(mc.fontRendererObj, errything, split + er.getResolution().getScaledWidth() - 330, er.getResolution().getScaledHeight() - 40);
            mc.getRenderItem().zLevel = 0.0F;
            GlStateManager.disableBlend();
            GlStateManager.scale(0.5D, 0.5D, 0.5D);
            GlStateManager.disableDepth();
            GlStateManager.disableLighting();
            GlStateManager.enableDepth();
            GlStateManager.scale(2.0F, 2.0F, 2.0F);
            GlStateManager.enableAlpha();
            GlStateManager.popMatrix();
            errything.getEnchantmentTagList();
        }
        GL11.glPopMatrix();
    }
}
