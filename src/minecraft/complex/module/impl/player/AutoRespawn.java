package complex.module.impl.player;

import complex.event.EventTarget;
import complex.event.impl.EventUpdate;
import complex.module.Category;
import complex.module.Module;

public class AutoRespawn extends Module {
    public AutoRespawn() {
        super("AutoRespawn", null, 0, 0, Category.Player);
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @EventTarget
    public void onUpdate(EventUpdate em) {
        this.setDisplayName("AutoRespawn");
        mc.thePlayer.respawnPlayer();
    }
}
