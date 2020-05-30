package complex.module.impl.misc;

import complex.event.EventTarget;
import complex.event.impl.EventPacket;
import complex.event.impl.EventUpdate;
import complex.module.Category;
import complex.module.Module;
import complex.utils.timer.TimeUtils;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketClientStatus;
import net.minecraft.network.play.client.CPacketKeepAlive;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class PingSpoof extends Module {
    private final HashMap<Packet<?>, Long> packetsMap = new HashMap<>();

    public PingSpoof() {
        super("PingSpoof", null, 0, 0, Category.Misc);
    }

    @Override
    public void onEnable() {
        this.setDisplayName("PingSpoof");
        super.onEnable();
    }

    @Override
    public void onDisable() {
        packetsMap.clear();
        super.onDisable();
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        try {
            synchronized (packetsMap) {
                for (final Iterator<Map.Entry<Packet<?>, Long>> iterator = packetsMap.entrySet().iterator(); iterator.hasNext(); ) {
                    final Map.Entry<Packet<?>, Long> entry = iterator.next();

                    if (entry.getValue() < System.currentTimeMillis()) {
                        mc.getConnection().sendPacket(entry.getKey());
                        iterator.remove();
                    }
                }
            }
        } catch (final Throwable t) {
            t.printStackTrace();
        }
    }

    @EventTarget
    public void onPacket(EventPacket event){
        final Packet packet = event.getPacket();

        if ((packet instanceof CPacketKeepAlive || packet instanceof CPacketClientStatus) && !(mc.thePlayer.isDead || mc.thePlayer.getHealth() <= 0) && !packetsMap.containsKey(packet)) {
            event.setCancelled(true);

            synchronized(packetsMap) {
                packetsMap.put(packet, System.currentTimeMillis() + TimeUtils.randomDelay(1000, 500));
            }
        }
    }
}
