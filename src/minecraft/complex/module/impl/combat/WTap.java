package complex.module.impl.combat;

import complex.event.EventTarget;
import complex.event.impl.EventPacketReceive;
import complex.module.Category;
import complex.module.Module;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketUseEntity;

public class WTap extends Module {
    public WTap() {
        super("WTap", null, 0, 0, Category.Combat);
    }

    @Override
    public void onEnable() {
        this.setDisplayName("WTap");
        super.onEnable();
    }

    @EventTarget
    public void onPacketReceive(EventPacketReceive packetReceive) {
        if (mc.theWorld != null && mc.thePlayer != null && packetReceive.getPacket() instanceof CPacketUseEntity) {
            CPacketUseEntity packet = (CPacketUseEntity)packetReceive.getPacket();
            if (packet.getAction() == CPacketUseEntity.Action.ATTACK && packet.getEntityFromWorld(mc.theWorld) != mc.thePlayer && mc.thePlayer.getFoodStats().getFoodLevel() > 6) {
                mc.thePlayer.setSprinting(false);
                mc.getConnection().sendPacket(new CPacketEntityAction(mc.thePlayer, CPacketEntityAction.Action.STOP_SPRINTING));
                mc.thePlayer.setSprinting(true);
                mc.getConnection().sendPacket(new CPacketEntityAction(mc.thePlayer, CPacketEntityAction.Action.START_SPRINTING));
            }
        }
    }
}
