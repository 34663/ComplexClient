package complex.event.impl;

import complex.event.Event;
import net.minecraft.entity.Entity;

public class EventLivingUpdate extends Event {
    private Entity entity;

    public EventLivingUpdate(Entity entity) {
        this.entity = entity;
    }

    public Entity getEntity() {
        return this.entity;
    }
}
