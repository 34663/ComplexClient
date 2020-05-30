package complex.module.impl.player;

import complex.Complex;
import complex.event.EventTarget;
import complex.event.impl.EventUpdate;
import complex.module.Category;
import complex.module.Module;
import complex.module.data.Setting;

public class SpeedMine extends Module {
    public SpeedMine() {
        super("SpeedMine", null, 0, 0, Category.Player);
    }

    @Override
    public void setup() {
        Complex.getSettingsManager().rSetting(new Setting("DamageMP", this, 0.7, 0.1, 1.0, false));
        Complex.getSettingsManager().rSetting(new Setting("MineWater", this, true));
        Complex.getSettingsManager().rSetting(new Setting("MineGround", this, true));
        Complex.getSettingsManager().rSetting(new Setting("MineHaste", this, true));
    }

    @EventTarget
    public void onUpdate(EventUpdate em) {
        this.setDisplayName("SpeedMine");
        if (em.isPre()) {
            mc.playerController.blockHitDelay = 0;

            if (mc.playerController.curBlockDamageMP >= Complex.getSettingsManager().getSettingByName("DamageMP").getValDouble()) {
                mc.playerController.curBlockDamageMP = 1.0F;
            }
        }
    }
}
