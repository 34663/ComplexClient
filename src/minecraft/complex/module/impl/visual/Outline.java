package complex.module.impl.visual;

import complex.Complex;
import complex.module.Category;
import complex.module.Module;
import complex.module.data.Setting;

public class Outline extends Module {
    public Outline() {
        super("Outline", null, 0, 0, Category.Visual);
    }

    @Override
    public void setup() {
        Complex.getSettingsManager().rSetting(new Setting("LineWidth", this, 1.5F, 0.1F, 5.0F, true));
    }

    @Override
    public void onEnable() {
        this.setDisplayName("Outline");
        super.onEnable();
    }
}
