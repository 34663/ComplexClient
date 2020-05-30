package complex.module.impl.movement;

import complex.Complex;
import complex.event.EventTarget;
import complex.event.impl.EventPacket;
import complex.event.impl.EventUpdate;
import complex.module.Category;
import complex.module.Module;
import complex.module.data.Setting;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.play.client.CPacketEntityAction;

import java.util.ArrayList;

public class Sneak extends Module {
    private String mode;

    public Sneak() {
        super("Sneak", null, 0, 0, Category.Movement);
    }

    @Override
    public void setup() {
        ArrayList<String> sneakoption = new ArrayList<>();
        sneakoption.add("Normal");
        sneakoption.add("ServerSide");
        Complex.getSettingsManager().rSetting(new Setting("Sneak Mode", this, "Normal", sneakoption));
    }

    @Override
    public void onEnable() {
        mode = Complex.getSettingsManager().getSettingByName("Sneak Mode").getValString();
        if (mode.equalsIgnoreCase("Normal")) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), true);
        } else if (mode.equalsIgnoreCase("ServerSide")) {
            mc.getConnection().sendPacket(new CPacketEntityAction(mc.thePlayer, CPacketEntityAction.Action.START_SNEAKING));
        }
        super.onEnable();
    }
    @Override
    public void onDisable() {
        mc.thePlayer.setSneaking(false);
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), false);
        if (mode.equalsIgnoreCase("ServerSide")) {
            mc.getConnection().sendPacket(new CPacketEntityAction(mc.thePlayer, CPacketEntityAction.Action.STOP_SNEAKING));
        }
        super.onDisable();
    }

    @EventTarget
    public void onUpdate(EventUpdate eventUpdate) {
        mode = Complex.getSettingsManager().getSettingByName("Sneak Mode").getValString();

        if (mode.equalsIgnoreCase("Normal")) {
            this.setDisplayName("Sneak §7Normal");
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), true);
        } else if (mode.equalsIgnoreCase("ServerSide")) {
            this.setDisplayName("Sneak §7ServerSide");
//            if (!mc.thePlayer.isSneaking()) {
//                mc.getConnection().sendPacket(new CPacketEntityAction(mc.thePlayer, CPacketEntityAction.Action.START_SNEAKING));
//            }
        }
    }
    @EventTarget
    public void onPacket(EventPacket e) {
        if (e.getPacket() instanceof CPacketEntityAction) {
            CPacketEntityAction C0B = (CPacketEntityAction) e.getPacket();
            C0B.action = CPacketEntityAction.Action.START_SNEAKING;
        }
    }
}
