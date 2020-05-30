package complex.module.impl.player;

import complex.Complex;
import complex.event.EventTarget;
import complex.event.impl.EventUpdate;
import complex.module.Category;
import complex.module.Module;
import complex.module.data.Setting;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.network.play.client.CPacketPlayer;

import java.util.ArrayList;

public class FastUse extends Module {
    private String mode;
    int ticks = 0;

    public FastUse() {
        super("FastUse", null, 0, 0, Category.Player);
    }

    @Override
    public void setup() {
        ArrayList<String> options = new ArrayList<>();
        options.add("Packet1");
        options.add("Packet2");
        Complex.getSettingsManager().rSetting(new Setting("Use Mode", this, "Packet1", options));
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        mode = Complex.getSettingsManager().getSettingByName("Use Mode").getValString();
        if (mode.equalsIgnoreCase("Packet1")) {
            this.setDisplayName("FastUse §7Packet1");
            if (mc.thePlayer.getItemInUseMaxCount() > 1) { //When you start to make it fast:  0~32
                ticks++;
                if (ticks >= 1) { //How many ticks between the packet and the next one
                    mc.thePlayer.connection.sendPacket(new CPacketPlayer(true));
                    ticks = 0;
                }
            }
        } else if (mode.equalsIgnoreCase("Packet2")) {
            this.setDisplayName("FastUse §7Packet2");
        }
    }
}
