package complex.module.impl.combat;

import complex.Complex;
import complex.event.EventTarget;
import complex.event.impl.EventPacket;
import complex.event.impl.EventPacketSent;
import complex.module.Category;
import complex.module.Module;
import complex.module.data.Setting;
import net.minecraft.network.play.server.SPacketChat;

import java.util.ArrayList;

public class KillSults extends Module {
    public int var1;

    public KillSults() {
        super("KillSults", null, 0, 0, Category.Combat);
    }

    @Override
    public void setup() {
        ArrayList<String> options = new ArrayList<>();
        ArrayList<String> option2 = new ArrayList<>();
        option2.add("Global");
        option2.add("Direct");
        option2.add("None");

        options.add("Hypixel");
        options.add("Shotbow");
        options.add("Hive");
        options.add("Cube");
        Complex.getSettingsManager().rSetting(new Setting("Sults Mode", this, "Hypixel", options));
        Complex.getSettingsManager().rSetting(new Setting("SendChat Mode", this, "Global", option2));
    }

    @Override
    public void onEnable() {
        this.var1 = 0;
        super.onEnable();
    }

    @EventTarget
    public void onPacket(EventPacket v1) {
        String mode = Complex.getSettingsManager().getSettingByName("Sults Mode").getValString();
        String send = Complex.getSettingsManager().getSettingByName("SendChat Mode").getValString();
        String kil;
        String start = null;
        String killmsg = null;
        if (mode.equalsIgnoreCase("Hypixel")) {
            this.setDisplayName("KillSults §7Hypixel");
            kil = "was killed by ";
            killmsg = kil + mc.thePlayer.getName();
        } else if (mode.equalsIgnoreCase("Shotbow")) {
            this.setDisplayName("KillSults §7Shotbow");
            kil = "killed ";
            killmsg = kil + Aura.target.getName();

            if (send.equalsIgnoreCase("Global")) {
                start = "!";
            } else if (send.equalsIgnoreCase("Direct")) {
                start = "@" + Aura.target.getName() + " ";
            } else if (send.equalsIgnoreCase("None")) {
                start = "";
            }
        } else if (mode.equalsIgnoreCase("Hive")) {
            this.setDisplayName("KillSults §7Hive");
            kil = "was killed by ";
            killmsg = kil + mc.thePlayer.getName();
        } else if (mode.equalsIgnoreCase("Cube")) {
            this.setDisplayName("KillSults §7Cube");
            kil = "was slain by ";
            killmsg = kil + mc.thePlayer.getName();
        }

        if (v1.getPacket() instanceof SPacketChat) {
            final SPacketChat v2 = (SPacketChat) v1.getPacket();
            final String v3 = v2.getChatComponent().getUnformattedText();

            if (v3.contains(killmsg)) {
                // message
                final String[] Sults = {"L", "Bruh", "lol", "AAA"};

                if (this.var1 >= Sults.length) {
                    this.var1 = 0;
                }
                this.mc.thePlayer.sendChatMessage(start + Sults[this.var1]);
                this.var1++;
            }
        }
    }
}
