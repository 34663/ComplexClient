package complex.module.impl.movement;

import complex.Complex;
import complex.event.EventTarget;
import complex.event.impl.EventMove;
import complex.event.impl.EventUpdate;
import complex.module.Category;
import complex.module.Module;
import complex.module.data.Setting;
import complex.utils.MoveUtils;
import complex.utils.PlayerUtil;
import complex.utils.timer.Timer;
import complex.utils.timer.Timer2;
import complex.utils.timer.Timer3;
import net.minecraft.network.play.client.CPacketPlayer;

import java.util.ArrayList;

public class AntiVoid extends Module {
    private String mode;
    private final Timer3 timer = new Timer3();

    public AntiVoid() {
        super("AntiVoid", null, 0, 0, Category.Movement);
    }

    @Override
    public void setup() {
        ArrayList<String> options = new ArrayList<>();
        options.add("Motion");
        options.add("Packet");
        Complex.getSettingsManager().rSetting(new Setting("Void Mode", this, "Motion", options));
        Complex.getSettingsManager().rSetting(new Setting("Distance", this, 6.0, 1.0, 10, true));
    }

    @EventTarget
    public void onUpdate(EventUpdate em) {
        mode = Complex.getSettingsManager().getSettingByName("Void Mode").getValString();
        if (mode.equalsIgnoreCase("Motion")) {
            this.setDisplayName("AntiVoid §7Motion");
        } else if (mode.equalsIgnoreCase("Packet")) {
            this.setDisplayName("AntiVoid §7Packet");
        }
    }
    @EventTarget
    public void onMove(EventMove em) {
        double distance = Complex.getSettingsManager().getSettingByName("Distance").getValDouble();

        if (mc.thePlayer.fallDistance > distance && !mc.thePlayer.capabilities.isFlying && timer.hasTimeElapsed(250)) {
            if (!PlayerUtil.isBlockUnder()) {
                if (mode.equalsIgnoreCase("Motion")) {
                    mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + distance + 1, mc.thePlayer.posZ);
                } else if (mode.equalsIgnoreCase("Packet")) {
                    mc.getConnection().getNetworkManager().sendPacket(new CPacketPlayer.Position(mc.thePlayer.posX, mc.thePlayer.posY + distance + 1, mc.thePlayer.posZ, false));
                }

                mc.thePlayer.fallDistance = 0;
                timer.reset();
            }
        }
    }
}
