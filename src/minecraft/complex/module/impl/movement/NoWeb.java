package complex.module.impl.movement;

import complex.event.EventTarget;
import complex.event.impl.EventUpdate;
import complex.module.Category;
import complex.module.Module;
import net.minecraft.client.entity.EntityPlayerSP;

public class NoWeb extends Module {
    private boolean last;

    public NoWeb() {
        super("NoWeb", null, 0, 0, Category.Movement);
    }

    @Override
    public void onEnable() {
        this.setDisplayName("NoWeb");
        last = false;
        super.onEnable();
    }

    @EventTarget
    public void onUpdate(EventUpdate em) {
        if (NoWeb.mc.theWorld != null && NoWeb.mc.thePlayer != null) {
            this.last = true;
            if (NoWeb.mc.thePlayer.isInWeb) {
                final EntityPlayerSP thePlayer = NoWeb.mc.thePlayer;
                thePlayer.motionX *= 0.20000000298023224;
                final EntityPlayerSP thePlayer2 = NoWeb.mc.thePlayer;
                thePlayer2.motionZ *= 0.20000000298023224;
                NoWeb.mc.thePlayer.motionY = 0.0;
                NoWeb.mc.thePlayer.onGround = true;
            }
            NoWeb.mc.thePlayer.isInWeb = false;
        }
    }
}
