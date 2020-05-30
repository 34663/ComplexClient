package complex.module.data;

import complex.Complex;
import complex.module.Module;
import complex.utils.MCUtil;
import complex.utils.font.CFontRenderer;
import complex.utils.font.FontLoaders;
import net.minecraft.client.gui.FontRenderer;

import java.awt.*;
import java.util.Comparator;

public class ModuleComparator implements Comparator<Module>, MCUtil {
    private FontRenderer mcfont = mc.fontRendererObj;
    private CFontRenderer font = FontLoaders.default18;

    @Override
    public int compare(Module o1, Module o2) {
        if (Complex.getSettingsManager().getSettingByName("HUD Mode").getValString().equalsIgnoreCase("Default")) {
            return Integer.compare(mcfont.getStringWidth(o2.getDisplayName()), mcfont.getStringWidth(o1.getDisplayName()));
        } else if (Complex.getSettingsManager().getSettingByName("HUD Mode").getValString().equalsIgnoreCase("New")) {
            return Integer.compare(font.getStringWidth(o2.getDisplayName()), font.getStringWidth(o1.getDisplayName()));
        }
        return 0;
    }
}
