package complex.module.impl.hud;

import complex.Complex;
import complex.module.Category;
import complex.module.Module;
import complex.module.data.Setting;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;

public class ClickGui extends Module {
    public ClickGui() {
        super("ClickGui", null, Keyboard.KEY_RSHIFT, 0, Category.Hidden);
    }

    @Override
    public void setup() {
        Complex.getSettingsManager().rSetting(new Setting("Sound", this, true));
        Complex.getSettingsManager().rSetting(new Setting("Bluh", this, false));
        Complex.getSettingsManager().rSetting(new Setting("GuiRed", this, 0, 0, 255, true));
        Complex.getSettingsManager().rSetting(new Setting("GuiGreen", this, 255, 0, 255, true));
        Complex.getSettingsManager().rSetting(new Setting("GuiBlue", this, 255, 0, 255, true));
    }

    @Override
    public void onEnable() {
        mc.displayGuiScreen(Complex.getClickgui());
        toggle();
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}
