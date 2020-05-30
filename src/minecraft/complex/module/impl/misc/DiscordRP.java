package complex.module.impl.misc;

import complex.event.EventTarget;
import complex.event.impl.EventUpdate;
import complex.module.Category;
import complex.module.Module;
import complex.utils.discordrpc.MainRPC;
import net.minecraft.client.gui.GuiMainMenu;

public class DiscordRP extends Module {
    private MainRPC discordrpc = new MainRPC();

    public DiscordRP() {
        super("DiscordRPC", null, 0, 0, Category.Misc);
    }

    @Override
    public void onEnable() {
        this.setDisplayName("DiscordRPC");
        discordrpc.start();
        super.onEnable();
    }

    @Override
    public void onDisable() {
        discordrpc.shutdown();
        super.onDisable();
    }
}
