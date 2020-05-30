package complex.gui.clickgui.elements.menu;

import java.awt.Color;

import complex.Complex;
import complex.gui.clickgui.elements.Element;
import complex.gui.clickgui.elements.ModuleButton;
import complex.gui.clickgui.util.*;
import complex.gui.clickgui.util.ColorUtil;
import complex.module.data.Setting;
import complex.utils.font.CFontRenderer;
import complex.utils.font.FontLoaders;
import complex.utils.render.Colors;
import complex.utils.render.RenderingUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

public class ElementComboBox extends Element {

	public ElementComboBox(ModuleButton iparent, Setting iset) {
		parent = iparent;
		set = iset;
		super.setup();
	}

	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		Color temp = ColorUtil.getClickGUIColor();
		int color = new Color(temp.getRed(), temp.getGreen(), temp.getBlue(), 150).getRGB();

		RenderingUtils.drawRect(x, y, x + width, y + height, Colors.getColor(22, 22, 22));
		FontUtil.drawTotalCenteredString(setstrg, x + width / 2, y + 15 / 2, 0xffffffff);
		int clr1 = color;
		int clr2 = temp.getRGB();
		int gay = Colors.getColor(220, 220, 220);

		RenderingUtils.drawRect(x, y + 14, x + width, y + 15, Colors.getColor(18, 18, 18));
		if (comboextended) {
			RenderingUtils.drawRect(x, y + 15, x + width, y + height, Colors.getColor(18, 18, 18));
			double ay = y + 15;
			for (String sld : set.getOptions()) {
				String elementtitle = sld.substring(0, 1).toUpperCase() + sld.substring(1);
				FontUtil.drawCenteredString(elementtitle, x + width / 2, ay + 2, gay);

				if (mouseX >= x && mouseX <= x + width && mouseY >= ay && mouseY < ay + FontUtil.getFontHeight() + 2) {
					RenderingUtils.drawRect(x + width, ay, x, ay + FontUtil.getFontHeight() + 2, Colors.getColor(255, 255, 255, 30));
				}
				if (sld.equalsIgnoreCase(set.getValString())) {
					RenderingUtils.drawRect(x, ay, x + 1.5, ay + FontUtil.getFontHeight() + 2, clr1);
				}
				ay += FontUtil.getFontHeight() + 2;
			}
		}
	}


	public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
		if (mouseButton == 0) {
			if (isButtonHovered(mouseX, mouseY)) {
				comboextended = !comboextended;
				return true;
			}
			if (!comboextended) return false;
			double ay = y + 15;
			for (String slcd : set.getOptions()) {
				if (mouseX >= x && mouseX <= x + width && mouseY >= ay && mouseY <= ay + FontUtil.getFontHeight() + 2) {
					if (Complex.getSettingsManager().getSettingByName("Sound").getValBoolean()) {
						Minecraft.getMinecraft().thePlayer.playSound(new SoundEvent(new ResourceLocation("entity.item.pickup")), 10.0F, 0.2F);
					}

					if (clickgui != null && clickgui.setmgr != null)
						clickgui.setmgr.getSettingByName(set.getName()).setValString(slcd.toLowerCase());
					return true;
				}
				ay += FontUtil.getFontHeight() + 2;
			}
		}

		return super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	public boolean isButtonHovered(int mouseX, int mouseY) {
		return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + 15;
	}
}
