package complex.module.impl.movement;

import complex.Complex;
import complex.event.EventTarget;
import complex.event.impl.EventLadder;
import complex.event.impl.EventUpdate;
import complex.module.Category;
import complex.module.Module;
import complex.module.data.Setting;

import java.util.ArrayList;

public class FastLadder extends Module {
    private String mode;

    public FastLadder() {
        super("FastLadder", null, 0, 0, Category.Movement);
    }

    @Override
    public void setup() {
        ArrayList<String> options = new ArrayList<>();
        options.add("Vanilla");
        options.add("Cube");
        Complex.getSettingsManager().rSetting(new Setting("Ladder Mode", this, "Vanilla", options));
        Complex.getSettingsManager().rSetting(new Setting("motionY", this, 1.0, 0.2, 10, false));
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @EventTarget
    public void onUpdate(EventUpdate em) {
        mode = Complex.getSettingsManager().getSettingByName("Ladder Mode").getValString();

        switch (mode) {
            case "Vanilla":
                this.setDisplayName("FastLadder §7Vanilla");
                break;
            case "Cube":
                this.setDisplayName("FastLadder §7Cube");
                break;
        }
    }
    @EventTarget
    public void onLadder(EventLadder el) {
        if (mode.equalsIgnoreCase("Vanilla")) {
            if (el.isPre()) {
                el.setMotionY(Complex.getSettingsManager().getSettingByName("motionY").getValDouble());
            }
        }
    }
}
