package complex.module.impl.visual;

import complex.Complex;
import complex.event.EventTarget;
import complex.event.impl.EventUpdate;
import complex.module.Category;
import complex.module.Module;
import complex.module.data.Setting;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.item.ItemSword;
import net.minecraft.util.EnumHand;

import java.util.ArrayList;

public class ArmAngle extends Module {
    private String mode;

    public ArmAngle() {
        super("ArmAngle", null, 0, 0, Category.Visual);
    }

    @Override
    public void setup() {
        ArrayList<String> options = new ArrayList<>();
        options.add("xd");
        options.add("Bruh");
        options.add(":)");
        options.add(":(");
        options.add("Blyat");
        Complex.getSettingsManager().rSetting(new Setting("Angle Mode", this, "xd", options));
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @EventTarget
    public void onUpdate(EventUpdate update) {
        mode = Complex.getSettingsManager().getSettingByName("Angle Mode").getValString();
        this.setDisplayName("ArmAngle");
        if (update.isPre()) {
            if (mode.equalsIgnoreCase("xd")) {
                final EntityPlayerSP playerSP = this.mc.thePlayer;
                playerSP.renderArmPitch -= 18.0f;
            } else if (mode.equalsIgnoreCase(":)")) {
                final EntityPlayerSP playerSP = this.mc.thePlayer;
                playerSP.renderArmPitch -= 90.0f;
            } else if (mode.equalsIgnoreCase(":(")) {
                final EntityPlayerSP playerSP = this.mc.thePlayer;
                playerSP.renderArmPitch -= 90.0f;
                playerSP.renderArmYaw -= 30.0F;
            } else if (mode.equalsIgnoreCase("Bruh")) {
                final EntityPlayerSP playerSP = this.mc.thePlayer;
                playerSP.renderArmYaw *= 2.0F;
            } else if (mode.equalsIgnoreCase("Blyat")) {
                final EntityPlayerSP playerSP = this.mc.thePlayer;
                playerSP.renderArmPitch *= 1.8F;
            }
        }
    }
}
