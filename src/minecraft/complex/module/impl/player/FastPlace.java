package complex.module.impl.player;

import complex.event.EventTarget;
import complex.event.impl.EventUpdate;
import complex.module.Category;
import complex.module.Module;
import org.lwjgl.input.Keyboard;

public class FastPlace extends Module {
    public FastPlace() {
        super("FastPlace", null, 0, 0, Category.Player);
    }

    @Override
    public void onEnable() {
        this.setDisplayName("FastPlace");
        super.onEnable();
    }

    @Override
    public void onDisable() {
        mc.rightClickDelayTimer = 4;
        super.onDisable();
    }

    @EventTarget
    public void onUpdate(EventUpdate em) {
        mc.rightClickDelayTimer = 1;
    }
}
