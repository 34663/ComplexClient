package complex.module.impl.visual;

import complex.module.Category;
import complex.module.Module;

public class ItemPhysic extends Module {
    public ItemPhysic() {
        super("ItemPhysic", null, 0, 0, Category.Visual);
    }

    @Override
    public void onEnable() {
        this.setDisplayName("ItemPhysic");
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}
