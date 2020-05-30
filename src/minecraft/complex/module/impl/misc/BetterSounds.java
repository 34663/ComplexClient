package complex.module.impl.misc;

import complex.Complex;
import complex.module.Category;
import complex.module.Module;
import complex.module.data.Setting;

public class BetterSounds extends Module {
    public BetterSounds() {
        super("BetterSounds", null, 0, 0, Category.Misc);
    }

    @Override
    public void setup() {
        Complex.getSettingsManager().rSetting(new Setting("Fight", this, true));
        Complex.getSettingsManager().rSetting(new Setting("Test", this, false));
    }

    @Override
    public void onEnable() {
        this.setDisplayName("BetterSounds");
        super.onEnable();
    }
}
