package complex.gui.clickgui;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import complex.Complex;
import complex.gui.clickgui.elements.Element;
import complex.gui.clickgui.elements.ModuleButton;
import complex.gui.clickgui.elements.menu.ElementSlider;
import complex.gui.clickgui.elements.menu.*;
import complex.gui.clickgui.util.*;
import complex.gui.clickgui.util.ColorUtil;
import complex.gui.clickgui.util.FontUtil;
import complex.module.Category;
import complex.module.Module;
import complex.module.ModuleManager;
import complex.module.data.*;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class ClickGUI extends GuiScreen {
	 public static ArrayList<Panel> panels;
	 public static ArrayList<Panel> rpanels;
	 private ModuleButton mb = null;
	 public SettingsManager setmgr;

	 public ClickGUI() {
		 setmgr = Complex.getSettingsManager();

		 FontUtil.setupFontUtils();
		 panels = new ArrayList<>();
		 double pwidth = 80;
		 double pheight = 15;
		 double px = 10;
		 double py = 10;
		 double pyplus = pheight + 10;

		 for (Category c : Category.values()) {
			 String title = Character.toUpperCase(c.name().toLowerCase().charAt(0)) + c.name().toLowerCase().substring(1);
			 ClickGUI.panels.add(new Panel(title, px, py, pwidth, pheight, false, this) {
				 @Override
				 public void setup() {
					 for (Module m : ModuleManager.modules) {
						 if (!m.getCategory().equals(c))
						 	continue;

						 this.Elements.add(new ModuleButton(m, this));
					 }
				 }
			 });
			 py += pyplus;
		 }

		 rpanels = new ArrayList<Panel>();
		 for (Panel p : panels) {
			 rpanels.add(p);
		 }
		 Collections.reverse(rpanels);

	 }

	 @Override
	 public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		 for (Panel p : panels) {
			 p.drawScreen(mouseX, mouseY, partialTicks);
		 }

		 ScaledResolution s = new ScaledResolution(mc);
		 mb = null;
		 listen:
		 for (Panel p : panels) {
			 if (p != null && p.visible && p.extended && p.Elements != null && p.Elements.size() > 0) {
				 for (ModuleButton e : p.Elements) {
					 if (e.listening) {
						 mb = e;
						 break listen;
					 }
				 }
			 }
		 }

		 for (Panel panel : panels) {
			 if (panel.extended && panel.visible && panel.Elements != null) {
				 for (ModuleButton b : panel.Elements) {
					 if (b.extended && b.menuelements != null && !b.menuelements.isEmpty()) {
						 double off = 0;
						 Color temp = ColorUtil.getClickGUIColor().darker();
						 int outlineColor = new Color(temp.getRed(), temp.getBlue(), temp.getGreen(), 170).getRGB();

						 for (Element e : b.menuelements) {
							 e.offset = off;
							 e.update();
							 e.drawScreen(mouseX, mouseY, partialTicks);
							 off += e.height;
						 }
					 }
				 }
			 }
		 }

		 if (mb != null) {
			 drawRect(0, 0, this.width, this.height, 0x88101010);
			 GL11.glPushMatrix();
			 GL11.glTranslatef(s.getScaledWidth() / 2, s.getScaledHeight() / 2, 0.0F);
			 GL11.glScalef(4.0F, 4.0F, 0F);
			 FontUtil.drawTotalCenteredStringWithShadow("Listening...", 0, -10, 0xffffffff);
			 GL11.glScalef(0.5F, 0.5F, 0F);
			 FontUtil.drawTotalCenteredStringWithShadow("Press 'ESCAPE' to unbind " + mb.mod.getName() + (mb.mod.getKeybind() > -1 ? " (" + Keyboard.getKeyName(mb.mod.getKeybind()) + ")" : ""), 0, 0, 0xffffffff);
			 GL11.glPopMatrix();
		 }

		 super.drawScreen(mouseX, mouseY, partialTicks);
	 }

	 @Override
	 public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		 if (mb != null) return;

		 for (Panel panel : rpanels) {
			 if (panel.extended && panel.visible && panel.Elements != null) {
				 for (ModuleButton b : panel.Elements) {
					 if (b.extended) {
						 for (Element e : b.menuelements) {
							 if (e.mouseClicked(mouseX, mouseY, mouseButton))
								 return;
						 }
					 }
				 }
			 }
		 }

		 for (Panel p : rpanels) {
			 if (p.mouseClicked(mouseX, mouseY, mouseButton))
				 return;
		 }

		 try {
			 super.mouseClicked(mouseX, mouseY, mouseButton);
		 } catch (IOException e) {
			 e.printStackTrace();
		 }
	 }

	 @Override
	 public void mouseReleased(int mouseX, int mouseY, int state) {
		 if (mb != null) return;

		 for (Panel panel : rpanels) {
			 if (panel.extended && panel.visible && panel.Elements != null) {
				 for (ModuleButton b : panel.Elements) {
					 if (b.extended) {
						 for (Element e : b.menuelements) {
							 e.mouseReleased(mouseX, mouseY, state);
						 }
					 }
				 }
			 }
		 }

		 for (Panel p : rpanels) {
			 p.mouseReleased(mouseX, mouseY, state);
		 }

		 super.mouseReleased(mouseX, mouseY, state);
	 }

	 @Override
	 protected void keyTyped(char typedChar, int keyCode) {
		 for (Panel p : rpanels) {
			 if (p != null && p.visible && p.extended && p.Elements != null && p.Elements.size() > 0) {
				 for (ModuleButton e : p.Elements) {
					 try {
						 if (e.keyTyped(typedChar, keyCode)) return;
					 } catch (IOException e1) {
						 e1.printStackTrace();
					 }
				 }
			 }
		 }

		 try {
			 super.keyTyped(typedChar, keyCode);
		 } catch (IOException e2) {
			 e2.printStackTrace();
		 }
	 }

	 @Override
	 public void initGui() {
		 if (OpenGlHelper.shadersSupported && mc.getRenderViewEntity() instanceof EntityPlayer && Complex.getSettingsManager().getSettingByName("Bluh").getValBoolean()) {
			 if (mc.entityRenderer.theShaderGroup != null) {
				 mc.entityRenderer.theShaderGroup.deleteShaderGroup();
			 }
			 mc.entityRenderer.loadShader(new ResourceLocation("shaders/post/blur.json"));
		 }
	 }

	 @Override
	 public void onGuiClosed() {
		 if (mc.entityRenderer.theShaderGroup != null) {
			 mc.entityRenderer.theShaderGroup.deleteShaderGroup();
			 mc.entityRenderer.theShaderGroup = null;
		 }

		 for (Panel panel : ClickGUI.rpanels) {
			 if (panel.extended && panel.visible && panel.Elements != null) {
				 for (ModuleButton b : panel.Elements) {
					 if (b.extended) {
						 for (Element e : b.menuelements) {
							 if (e instanceof ElementSlider) {
								 ((ElementSlider) e).dragging = false;
							 }
						 }
					 }
				 }
			 }
		 }
	 }

	 public void closeAllSettings() {
		 for (Panel p : rpanels) {
			 if (p != null && p.visible && p.extended && p.Elements != null
					 && p.Elements.size() > 0) {
				 for (ModuleButton e : p.Elements) {
					 e.extended = false;
				 }
			 }
		 }
	 }
 }
