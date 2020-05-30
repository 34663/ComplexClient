package complex.module.impl.visual;

import complex.Complex;
import complex.event.EventTarget;
import complex.event.impl.EventPacket;
import complex.event.impl.EventPacketReceive;
import complex.event.impl.EventUpdate;
import complex.module.Category;
import complex.module.Module;
import complex.module.data.Setting;
import net.minecraft.network.play.server.SPacketTimeUpdate;

public class Ambience extends Module {
    public Ambience() {
        super("Ambience", null, 0, 0, Category.Visual);
    }

    @Override
    public void setup() {
        Complex.getSettingsManager().rSetting(new Setting("WorldTime", this, 11000.0F, 1.0F, 16000.0F, false));
    }

    @EventTarget
    public void onPacket(EventPacket e) {
        if (e.getPacket() instanceof SPacketTimeUpdate)
            e.setCancelled(true);
    }
    @EventTarget
    public void onUpdate(EventUpdate update) {
        this.setDisplayName("Ambience");
        long time = (long) Complex.getSettingsManager().getSettingByName("WorldTime").getValDouble();
        mc.theWorld.setWorldTime(time);
    }
}
