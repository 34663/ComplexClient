package complex.gui.clickgui.elements.menu;

import java.awt.Color;

import complex.Complex;
import complex.gui.clickgui.elements.Element;
import complex.gui.clickgui.elements.ModuleButton;
import complex.gui.clickgui.util.*;
import complex.gui.clickgui.util.ColorUtil;
import complex.module.data.*;
import complex.utils.font.CFontRenderer;
import complex.utils.font.FontLoaders;
import complex.utils.render.Colors;
import complex.utils.render.RenderingUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

public class ElementCheckBox extends Element {
	public ElementCheckBox(ModuleButton iparent, Setting iset) {
		parent = iparent;
		set = iset;
		super.setup();
	}

	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		Color temp = ColorUtil.getClickGUIColor();
		int color = new Color(temp.getRed(), temp.getGreen(), temp.getBlue(), 200).getRGB();

		RenderingUtils.drawRect(x, y, x + width, y + height, Colors.getColor(18, 18, 18));
		FontUtil.drawString(setstrg, x + width - FontUtil.getStringWidth(setstrg) - 1.5, y + FontUtil.getFontHeight() / 2 - 1.5, Colors.getColor(255, 255, 255));
		RenderingUtils.drawRoundedRect(x + 1, y + 2, x + 12, y + 13, 5.5f, set.getValBoolean() ? color : Colors.getColor(8, 8, 8));
		if (isCheckHovered(mouseX, mouseY)) {
			RenderingUtils.drawRoundedRect(x + 1, y + 2, x + 12, y + 13, 5.5f, Colors.getColor(255, 255, 255, 40));
		}
	}

	public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
		if (mouseButton == 0 && isCheckHovered(mouseX, mouseY)) {
			set.setValBoolean(!set.getValBoolean());

			if (Complex.getSettingsManager().getSettingByName("Sound").getValBoolean()) {
				Minecraft.getMinecraft().thePlayer.playSound(new SoundEvent(new ResourceLocation("entity.item.pickup")), 10.0F, 0.2F);
			}
			return true;
		}
		return super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	public boolean isCheckHovered(int mouseX, int mouseY) {
		return mouseX >= x + 1 && mouseX <= x + 12 && mouseY >= y + 2 && mouseY <= y + 13;
	}
}
