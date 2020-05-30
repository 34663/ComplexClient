package complex.gui.clickgui.util;

import complex.Complex;
import java.awt.Color;

public class ColorUtil {
	
	public static Color getClickGUIColor(){
		return new Color((int) Complex.getSettingsManager().getSettingByName("GuiRed").getValDouble(), (int)Complex.getSettingsManager().getSettingByName("GuiGreen").getValDouble(), (int)Complex.getSettingsManager().getSettingByName("GuiBlue").getValDouble());
	}
}
