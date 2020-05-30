package complex.event.impl;

import complex.event.Event;
import net.minecraft.entity.Entity;

public class EventRenderNameTags extends Event {
    public Entity entity;

    public EventRenderNameTags(Entity entity) {
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }
}
