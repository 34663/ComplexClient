package complex.module.impl.movement;

import complex.Complex;
import complex.event.EventTarget;
import complex.event.impl.EventUpdate;
import complex.module.Category;
import complex.module.Module;
import complex.module.data.Setting;
import complex.utils.PlayerUtil;
import complex.utils.timer.Timer;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;

public class NoSlowdown extends Module {
    Timer timer = new Timer();
    private String mode;

    public NoSlowdown() {
        super("NoSlowdown", null, 0, 0, Category.Movement);
    }

    @Override
    public void setup() {
        ArrayList<String> options = new ArrayList<>();
        options.add("Vanilla");
        options.add("NCP");
        options.add("AAC");
        Complex.getSettingsManager().rSetting(new Setting("NoSlow Mode", this, "Vanilla", options));
    }

    @EventTarget
    public void onUpdate(EventUpdate em) {
        mode = Complex.getSettingsManager().getSettingByName("NoSlow Mode").getValString();
        if (mode.equalsIgnoreCase("Vanilla")) {
            this.setDisplayName("NoSlowdown §7Vanilla");
        } else if (mode.equalsIgnoreCase("NCP")) {
            this.setDisplayName("NoSlowdown §7NCP");
            if (em.isPre()) {
                if (this.mc.thePlayer.isActiveItemStackBlocking() && PlayerUtil.isMoving()) {
                    this.mc.getConnection().sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                }
            }
        } else if (mode.equalsIgnoreCase("AAC")) {
            this.setDisplayName("NoSlowdown §7AAC");
        }
    }
}
