package complex.module.impl.misc;

import complex.event.EventTarget;
import complex.event.impl.EventPacket;
import complex.event.impl.EventPacketReceive;
import complex.event.impl.EventUpdate;
import complex.module.Category;
import complex.module.Module;
import net.minecraft.network.play.server.SPacketPlayerPosLook;

public class NoRotation extends Module {
    public NoRotation() {
        super("NoRotation", null, 0, 0, Category.Misc);
    }

    @Override
    public void onEnable() {
        this.setDisplayName("NoRotation");
        super.onEnable();
    }

    @EventTarget
    public void onPacket(EventPacketReceive event) {
        if (event.getPacket() instanceof SPacketPlayerPosLook) {
            SPacketPlayerPosLook packet = (SPacketPlayerPosLook) event.getPacket();
            packet.setYaw(mc.thePlayer.rotationYaw);
            packet.setPitch(mc.thePlayer.rotationPitch);
        }
    }
}
