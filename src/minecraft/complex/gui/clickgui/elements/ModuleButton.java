package complex.gui.clickgui.elements;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;

import complex.Complex;
import complex.gui.clickgui.elements.menu.ElementCheckBox;
import complex.gui.clickgui.elements.menu.ElementComboBox;
import complex.gui.clickgui.elements.menu.ElementSlider;
import complex.gui.clickgui.elements.menu.*;
import complex.gui.clickgui.Panel;
import complex.gui.clickgui.util.*;
import complex.gui.clickgui.util.ColorUtil;
import complex.module.Module;
import complex.module.ModuleManager;
import complex.module.data.*;
import complex.utils.font.CFontRenderer;
import complex.utils.font.FontLoaders;
import complex.utils.render.Colors;
import complex.utils.render.RenderingUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import org.lwjgl.input.Keyboard;

public class ModuleButton {
	public Module mod;
	public ArrayList<Element> menuelements;
	public Panel parent;
	public double x;
	public double y;
	public double width;
	public double height;
	public boolean extended = false;
	public boolean listening = false;

	public ModuleButton(Module imod, Panel pl) {
		mod = imod;
		height = Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT + 2;
		parent = pl;
		menuelements = new ArrayList<>();

		if (Complex.getSettingsManager().getSettingsByMod(imod) != null)
			for (Setting s : Complex.getSettingsManager().getSettingsByMod(imod)) {
				if (s.isCheck()) {
					menuelements.add(new ElementCheckBox(this, s));
				} else if (s.isSlider()) {
					menuelements.add(new ElementSlider(this, s));
				} else if (s.isCombo()) {
					menuelements.add(new ElementComboBox(this, s));
				}
			}
	}

	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		Color temp = ColorUtil.getClickGUIColor();
		int color = new Color(temp.getRed(), temp.getGreen(), temp.getBlue(), 150).getRGB();
		int textcolor = 0xffafafaf;

		if (isHovered(mouseX, mouseY)) {
			RenderingUtils.drawRect(x - 2, y, x + width + 2, y + height + 1, Colors.getColor(35, 35, 35));
		}
		if (mod.isEnabled()) {
			RenderingUtils.drawRoundedRect(x - 1, y + 1, x + 1, y + height, 1.0f, color);
			textcolor = 0xffefefef;
		}

		FontUtil.drawTotalCenteredStringWithShadow(mod.getName(), x + width / 2, y + 1 + height / 2, textcolor);
	}

	public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
		if (!isHovered(mouseX, mouseY))
			return false;

		if (mouseButton == 0) {
			mod.toggle();

			if (Complex.getSettingsManager().getSettingByName("Sound").getValBoolean()) {
				if (mod.isEnabled()) {
					Minecraft.getMinecraft().thePlayer.playSound(new SoundEvent(new ResourceLocation("entity.item.pickup")), 1f, 1f);
				} else {
					Minecraft.getMinecraft().thePlayer.playSound(new SoundEvent(new ResourceLocation("entity.item.pickup")), 1f, 0.5f);
				}
			}
		} else if (mouseButton == 1) {
			if (menuelements != null && menuelements.size() > 0) {
				boolean b = !this.extended;
				Complex.getClickgui().closeAllSettings();
				this.extended = b;

				if (Complex.getSettingsManager().getSettingByName("Sound").getValBoolean()) {
					if (extended) {
						Minecraft.getMinecraft().thePlayer.playSound(new SoundEvent(new ResourceLocation("block.piston.extend")), 1f, 1f);
					} else {
						Minecraft.getMinecraft().thePlayer.playSound(new SoundEvent(new ResourceLocation("block.piston.contract")), 1f, 1f);
					}
				}
			}
		} else if (mouseButton == 2) {
			listening = true;
		}
		return true;
	}

	public boolean keyTyped(char typedChar, int keyCode) throws IOException {
		if (listening) {
			if (keyCode != Keyboard.KEY_ESCAPE) {
				mod.setKeybind(keyCode);
			} else {
				mod.setKeybind(Keyboard.KEY_NONE);
			}
			listening = false;
			return true;
		}
		return false;
	}

	public boolean isHovered(int mouseX, int mouseY) {
		return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
	}
}
