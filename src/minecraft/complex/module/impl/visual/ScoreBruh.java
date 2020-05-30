package complex.module.impl.visual;

import complex.Complex;
import complex.module.Category;
import complex.module.Module;
import complex.module.data.Setting;

public class ScoreBruh extends Module {
    public ScoreBruh() {
        super("ScoreBoard", null, 0, 0, Category.Visual);
    }

    @Override
    public void setup() {
        Complex.getSettingsManager().rSetting(new Setting("Potition", this, 150, -150, 200, true));
        Complex.getSettingsManager().rSetting(new Setting("Remove", this, false));
    }

    @Override
    public void onEnable() {
        this.setDisplayName("ScoreBoard");
        super.onEnable();
    }
}
