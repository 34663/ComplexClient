package complex.module.impl.visual;

import complex.event.EventTarget;
import complex.event.impl.EventUpdate;
import complex.module.Category;
import complex.module.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ShowInvis extends Module {
    private List<Entity> entities;

    public ShowInvis() {
        super("ShowInvis", null, 0, 0, Category.Visual);
        this.entities = new ArrayList<Entity>();
    }

    @Override
    public void onEnable() {
        final Iterator<Entity> iterator = this.entities.iterator();
        while (iterator.hasNext()) {
            iterator.next().setInvisible(true);
        }
        this.entities.clear();
        super.onEnable();
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        this.setDisplayName("ShowInvis");
        for (final Entity entity : this.mc.theWorld.loadedEntityList) {
            if (entity.isInvisible() && entity instanceof EntityPlayer) {
                entity.setInvisible(false);
                this.entities.add(entity);
            }
        }
    }
}
