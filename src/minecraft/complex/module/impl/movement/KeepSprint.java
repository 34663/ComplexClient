package complex.module.impl.movement;

import complex.event.EventTarget;
import complex.event.impl.EventPacket;
import complex.module.Category;
import complex.module.Module;
import net.minecraft.network.play.client.CPacketEntityAction;

public class KeepSprint extends Module {
    public KeepSprint() {
        super("KeepSprint", null, 0, 0, Category.Movement);
    }

    @Override
    public void onEnable() {
        this.setDisplayName("KeepSprint");
        super.onEnable();
    }

    @EventTarget
    public void onPacket(EventPacket e) {
        try {
            if (e.isIncoming() && e.getPacket() instanceof CPacketEntityAction) {
                CPacketEntityAction packet = (CPacketEntityAction) e.getPacket();
                if (packet.getAction() == CPacketEntityAction.Action.STOP_SPRINTING) {
                    e.setCancelled(true);
                }
            }
        } catch (ClassCastException ignored) {
        }
    }
}
