package complex.gui.clickgui.util;

import complex.module.Module;
import complex.utils.MCUtil;
import complex.utils.font.CFontRenderer;
import complex.utils.font.FontLoaders;
import net.minecraft.client.gui.FontRenderer;

import java.util.Comparator;

public class ModuleButtonComparator implements Comparator<Module> {
    @Override
    public int compare(Module o1, Module o2) {
        return Integer.compare(FontUtil.getStringWidth2(o2.getName()), FontUtil.getStringWidth2(o1.getName()));
    }
}
