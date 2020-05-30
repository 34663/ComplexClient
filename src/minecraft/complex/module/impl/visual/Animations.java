package complex.module.impl.visual;

import complex.Complex;
import complex.event.EventTarget;
import complex.event.impl.EventUpdate;
import complex.module.Category;
import complex.module.Module;
import complex.module.data.Setting;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;

public class Animations extends Module {
    public static String mode;

    public Animations() {
        super("Animations", null, 0, 0, Category.Visual);
    }

    @Override
    public void setup() {
        ArrayList<String> options = new ArrayList<>();
        options.add("Vanilla");
        options.add("Vanilla2");
        options.add("Tap1");
        options.add("Tap2");
        options.add("Swipe");
        options.add("Sigma");
        options.add("Avatar");
        options.add("MiniJump");
        options.add("Astolfo");
        options.add("Dev");
        Complex.getSettingsManager().rSetting(new Setting("Animation Mode", this, "Vanilla", options));
        Complex.getSettingsManager().rSetting(new Setting("OldAnimation", this, true));
        Complex.getSettingsManager().rSetting(new Setting("Nigami", this, false));
        Complex.getSettingsManager().rSetting(new Setting("360°", this, false));
        Complex.getSettingsManager().rSetting(new Setting("MiniItem", this, false));
        Complex.getSettingsManager().rSetting(new Setting("Smooch", this, false));
    }

    @Override
    public void onEnable() {
        this.setDisplayName("Animations");
        super.onEnable();
    }

    @EventTarget
    public void onUpdate(EventUpdate em) {
        mode = Complex.getSettingsManager().getSettingByName("Animation Mode").getValString();
    }
}
