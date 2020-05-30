package complex.module.impl.movement;

import complex.Complex;
import complex.event.EventTarget;
import complex.event.impl.EventUpdate;
import complex.module.Category;
import complex.module.Module;
import complex.module.data.Setting;
import complex.utils.PlayerUtil;

public class Sprint extends Module {
    public Sprint() {
        super("Sprint", null, 0, 0, Category.Movement);
    }

    @Override
    public void setup() {
        Complex.getSettingsManager().rSetting(new Setting("OMNIDIR", this, true));
    }

    @EventTarget
    public void onUpdate(EventUpdate em) {
        this.setDisplayName("Sprint");
        if (em.isPre() && canSprint()) {
            mc.thePlayer.setSprinting(true);
        }
    }

    private boolean canSprint() {
        if(!Complex.getSettingsManager().getSettingByName("OMNIDIR").getValBoolean() && !mc.gameSettings.keyBindForward.pressed)
            return false;
        return PlayerUtil.isMoving() && mc.thePlayer.getFoodStats().getFoodLevel() > 6;
    }
}
