package complex.module.impl.visual;

import complex.Complex;
import complex.event.EventTarget;
import complex.event.impl.EventUpdate;
import complex.module.Category;
import complex.module.Module;
import complex.module.data.Setting;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import java.util.ArrayList;

public class Brightness extends Module {
    private float oldBrightness;

    public Brightness() {
        super("FullBright", null, 0, 0, Category.Visual);
    }

    @Override
    public void setup() {
        ArrayList<String> brightmode = new ArrayList<>();
        brightmode.add("Gamma");
        brightmode.add("Potion");
        Complex.getSettingsManager().rSetting(new Setting("Bright Mode", this, "Gamma", brightmode));
    }

    @Override
    public void onEnable() {
        this.oldBrightness = mc.gameSettings.gammaSetting;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        mc.gameSettings.gammaSetting = this.oldBrightness;
        this.mc.thePlayer.removePotionEffect(Potion.getPotionById(16));
        super.onDisable();
    }

    @EventTarget
    public void onUpdate(EventUpdate eventUpdate) {
        String mode = Complex.getSettingsManager().getSettingByName("Bright Mode").getValString();

        if (mode.equalsIgnoreCase("Gamma")) {
            this.setDisplayName("FullBright §7Gamma");
            mc.gameSettings.gammaSetting = 10F;
        } else if (mode.equalsIgnoreCase("Potion")) {
            this.setDisplayName("FullBright §7Potion");
            mc.thePlayer.addPotionEffect(new PotionEffect(Potion.getPotionById(16), 5200, 1));
        }
    }
}
