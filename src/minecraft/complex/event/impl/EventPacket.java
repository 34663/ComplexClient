package complex.event.impl;

import complex.event.Event;
import net.minecraft.network.Packet;

public class EventPacket extends Event {
    private Packet packet;
    private boolean outgoing;

    public boolean isPre() {
        return pre;
    }

    public boolean isPost() {
        return !pre;
    }

    private boolean pre;

    public EventPacket(Packet packet, boolean outgoing) {
        this.packet = packet;
        this.outgoing = outgoing;
        this.pre = true;
    }

    public EventPacket(Packet packet) {
        this.packet = packet;
        this.pre = false;
    }

    public Packet getPacket() {
        return packet;
    }

    public void setPacket(Packet packet) {
        this.packet = packet;
    }

    public boolean isOutgoing() {
        return outgoing;
    }

    public boolean isIncoming() {
        return !outgoing;
    }
}
