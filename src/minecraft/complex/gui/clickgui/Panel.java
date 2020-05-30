package complex.gui.clickgui;

import java.awt.Color;
import java.util.ArrayList;

import complex.gui.clickgui.elements.ModuleButton;
import complex.gui.clickgui.util.*;
import complex.gui.clickgui.util.ColorUtil;
import complex.utils.font.CFontRenderer;
import complex.utils.font.FontLoaders;
import complex.utils.render.Colors;
import complex.utils.render.RenderingUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;

public class Panel {
	public String title;
	public double x;
	public double y;
	private double x2;
	private double y2;
	public double width;
	public double height;
	public boolean dragging;
	public boolean extended;
	public boolean visible;
	public ArrayList<ModuleButton> Elements = new ArrayList<>();
	public ClickGUI clickgui;

	public Panel(String ititle, double ix, double iy, double iwidth, double iheight, boolean iextended, ClickGUI parent) {
		this.title = ititle;
		this.x = ix;
		this.y = iy;
		this.width = iwidth;
		this.height = iheight;
		this.extended = iextended;
		this.dragging = false;
		this.visible = true;
		this.clickgui = parent;
		setup();
	}

	public void setup() {}

	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
		Color temp = ColorUtil.getClickGUIColor().darker();
		int outlineColor = new Color(temp.getRed(), temp.getGreen(), temp.getBlue(), 170).getRGB();
		float k1 = 160;
		float k2 = sr.getScaledWidth() - 160;
		float k3 = 80;
		float k4 = sr.getScaledHeight() - 120;

		if (!this.visible)
			return;

		if (this.dragging) {
			x = x2 + mouseX;
			y = y2 + mouseY;
		}

		RenderingUtils.drawRect(x, y, x + width, y + height, Colors.getColor(20, 20, 20));
		RenderingUtils.drawRect(x + 4, y + 2, x + 4.3, y + height - 2, Colors.getColor(255, 255, 255));
		RenderingUtils.drawRect(x - 4 + width, y + 2, x - 4.3 + width, y + height - 2, Colors.getColor(255, 255, 255));
		FontUtil.drawTotalCenteredString(title, x + width / 2, y + height / 2 + 1, Colors.getColor(255, 255, 255));

		if (this.extended && !Elements.isEmpty()) {
			double startY = y + height;

			for (ModuleButton et : Elements) {
				RenderingUtils.drawRect(x, startY, x + width, startY + et.height + 1, Colors.getColor(18, 18, 18));
				et.x = x + 2;
				et.y = startY;
				et.width = width - 4;
				et.drawScreen(mouseX, mouseY, partialTicks);
				startY += et.height + 1;
			}
		}
	}

	public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
		if (!this.visible) {
			return false;
		}
		if (mouseButton == 0 && isHovered(mouseX, mouseY)) {
			x2 = this.x - mouseX;
			y2 = this.y - mouseY;
			dragging = true;
			return true;
		} else if (mouseButton == 1 && isHovered(mouseX, mouseY)) {
			extended = !extended;
			return true;
		} else if (extended) {
			for (ModuleButton et : Elements) {
				if (et.mouseClicked(mouseX, mouseY, mouseButton)) {
					return true;
				}
			}
		}
		return false;
	}

	public void mouseReleased(int mouseX, int mouseY, int state) {
		if (!this.visible) {
			return;
		}
		if (state == 0) {
			this.dragging = false;
		}
	}

	public boolean isHovered(int mouseX, int mouseY) {
		return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
	}
}
