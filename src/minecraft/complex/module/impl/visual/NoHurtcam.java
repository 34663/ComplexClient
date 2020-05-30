package complex.module.impl.visual;

import complex.module.Category;
import complex.module.Module;

public class NoHurtcam extends Module {
    public NoHurtcam() {
        super("NoHurtcam", null, 0, 0, Category.Visual);
    }

    @Override
    public void onEnable() {
        this.setDisplayName("NoHurtcam");
        super.onEnable();
    }
}
