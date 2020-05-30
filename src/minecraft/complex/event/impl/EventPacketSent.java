package complex.event.impl;

import complex.event.Event;
import net.minecraft.network.Packet;

public class EventPacketSent extends Event {
    public boolean cancel;
    public Packet packet;

    public EventPacketSent(Packet packet) {
        this.packet = packet;
    }

    public Packet getPacket() {
        return this.packet;
    }

    @Override
    public boolean isCancelled() {
        return this.cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }

    public void setPacket(Packet packet) {
        this.packet = packet;
    }
}
