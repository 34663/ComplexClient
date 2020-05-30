package complex.gui.clickgui.elements.menu;

import java.awt.Color;

import complex.gui.clickgui.elements.Element;
import complex.gui.clickgui.elements.ModuleButton;
import complex.gui.clickgui.util.*;
import complex.gui.clickgui.util.ColorUtil;
import complex.module.data.*;
import complex.utils.font.CFontRenderer;
import complex.utils.font.FontLoaders;
import complex.utils.render.Colors;
import complex.utils.render.RenderingUtils;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.math.MathHelper;

public class ElementSlider extends Element {
	public boolean dragging;

	public ElementSlider(ModuleButton iparent, Setting iset) {
		parent = iparent;
		set = iset;
		dragging = false;
		super.setup();
	}

	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		String displayval = "" + Math.round(set.getValDouble() * 100D) / 100D;
		boolean hoveredORdragged = isSliderHovered(mouseX, mouseY) || dragging;

		Color temp = ColorUtil.getClickGUIColor();
		int color = new Color(temp.getRed(), temp.getGreen(), temp.getBlue(), hoveredORdragged ? 250 : 200).getRGB();
		int color2 = new Color(temp.getRed(), temp.getGreen(), temp.getBlue(), hoveredORdragged ? 255 : 230).getRGB();
		double percentBar = (set.getValDouble() - set.getMin()) / (set.getMax() - set.getMin());

		RenderingUtils.drawRect(x, y, x + width, y + height, Colors.getColor(18, 18, 18));
		FontUtil.drawString(setstrg, x + 1, y + 2, Colors.getColor(255, 255, 255));
		FontUtil.drawString(displayval, x + width - FontUtil.getStringWidth(displayval), y + 2, Colors.getColor(255, 255, 255));
		RenderingUtils.drawRect(x, y + 12, x + width, y + 13.5, Colors.getColor(18, 18, 18));
		RenderingUtils.drawRect(x, y + 12, x + (percentBar * width), y + 13.5, color);

		if (percentBar > 0 && percentBar < 1)
			RenderingUtils.drawRect(x + (percentBar * width) - 1, y + 12, x + Math.min((percentBar * width), width), y + 13.5, color2);

		if (this.dragging) {
			double diff = set.getMax() - set.getMin();
			double val = set.getMin() + (MathHelper.clamp_double((mouseX - x) / width, 0, 1)) * diff;
			set.setValDouble(val);
		}
	}

	public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
		if (mouseButton == 0 && isSliderHovered(mouseX, mouseY)) {
			this.dragging = true;
			return true;
		}
		return super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	public void mouseReleased(int mouseX, int mouseY, int state) {
		this.dragging = false;
	}

	public boolean isSliderHovered(int mouseX, int mouseY) {
		return mouseX >= x && mouseX <= x + width && mouseY >= y + 11 && mouseY <= y + 14;
	}
}