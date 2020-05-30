package complex.module.impl.player;

import complex.Complex;
import complex.event.EventTarget;
import complex.event.impl.EventUpdate;
import complex.module.Category;
import complex.module.Module;
import complex.module.data.Setting;
import complex.utils.MoveUtils;
import complex.utils.misc.ServerUtil;
import net.minecraft.network.play.client.CPacketPlayer;

import java.util.ArrayList;

public class NoFall extends Module {
    private String mode;
    double fall;

    public NoFall() {
        super("NoFall", null, 0, 0, Category.Player);
    }

    @Override
    public void setup() {
        ArrayList<String> options = new ArrayList<>();
        options.add("Hypixel");
        options.add("Spoof");
        options.add("Packet");
        options.add("Normal");
        Complex.getSettingsManager().rSetting(new Setting("NoFall Mode", this, "Hypixel", options));
    }

    @Override
    public void onEnable() {
        fall = 0;
        super.onEnable();
    }

    @EventTarget
    public void onUpdate(EventUpdate em) {
        mode = Complex.getSettingsManager().getSettingByName("NoFall Mode").getValString();

        if (mode.equalsIgnoreCase("Spoof")) {
            this.setDisplayName("NoFall §7Spoof");
        } else if (mode.equalsIgnoreCase("Packet")) {
            this.setDisplayName("NoFall §7Packet");
        } else if (mode.equalsIgnoreCase("Normal")) {
            this.setDisplayName("NoFall §7Normal");
        } else if (mode.equalsIgnoreCase("Hypixel")) {
            this.setDisplayName("NoFall §7Hypixel");
        }
        if (em.isPre()) {
            if (!mc.thePlayer.isSpectator() && !mc.thePlayer.capabilities.allowFlying) {
                if (mc.thePlayer.fallDistance >= 2.95) {
                    if (mode.equalsIgnoreCase("Spoof")) {
                        this.setDisplayName("NoFall §7Spoof");
                        em.setOnGround(true);
                        mc.thePlayer.fallDistance = 0;
                    } else if (mode.equalsIgnoreCase("Packet")) {
                        this.setDisplayName("NoFall §7Packet");
                        mc.getConnection().getNetworkManager().sendPacketNoEvent(new CPacketPlayer(true));
                    }
                }
                if (mode.equalsIgnoreCase("Normal")) {
                    if (ServerUtil.currentServer == ServerUtil.ServerResult.HYPIXEL) {
                        if (this.mc.thePlayer.motionY < -0.6 && !this.mc.thePlayer.onGround) {
                            em.setOnGround(true);
                        }
                        return;
                    }
                    final double n = this.mc.thePlayer.posY - this.mc.thePlayer.lastTickPosY;
                    if (n < 0.0) {
                        this.fall += n;
                    }
                    if (em.isOnground() || this.mc.thePlayer.onGround) {
                        this.fall = 0.0;
                    }
                    if (this.fall <= -3.0) {
                        this.fall = 0.0;
                        em.setOnGround(true);
                    }
                } else if (mode.equalsIgnoreCase("Hypixel")) {
                    if (!MoveUtils.isOnGround(0.001)) {
                        if (mc.thePlayer.motionY < -0.08)
                            fall -= mc.thePlayer.motionY;
                        if (fall > 2) {
                            fall = 0;

                            em.setOnGround(true);
                        }
                    } else {
                        fall = 0;
                    }
                }
            }
        }
    }
}
