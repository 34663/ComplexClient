package complex.module.impl.misc;

import complex.command.Command;
import complex.event.EventTarget;
import complex.event.impl.EventPacket;
import complex.event.impl.EventUpdate;
import complex.module.Category;
import complex.module.Module;
import complex.utils.timer.TickTimer;
import joptsimple.internal.Strings;
import net.minecraft.network.play.client.CPacketTabComplete;
import net.minecraft.network.play.server.SPacketTabComplete;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Plugins extends Module {
    private final TickTimer tickTimer = new TickTimer();

    public Plugins() {
        super("Plugins", null, 0, 0, Category.Misc);
    }

    @Override
    public void onEnable() {
        this.setDisplayName("Plugins");
        if (mc.thePlayer == null)
            return;
        mc.getConnection().sendPacket(new CPacketTabComplete("/"));
        tickTimer.reset();
        super.onEnable();
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        tickTimer.update();
        if (tickTimer.hasTimePassed(20)) {
            Command.sendChatMessage("§cPlugins check timed out...");
            tickTimer.reset();
        }
    }

    @EventTarget
    public void onPacket(EventPacket event) {
        if (event.getPacket() instanceof SPacketTabComplete) {
            final SPacketTabComplete s3APacketTabComplete = (SPacketTabComplete) event.getPacket();
            final List<String> plugins = new ArrayList<>();
            final String[] commands = s3APacketTabComplete.getMatches();

            for (final String command1 : commands) {
                final String[] command = command1.split(":");
                if (command.length > 1) {
                    final String pluginName = command[0].replace("/", "");
                    if (!plugins.contains(pluginName))
                        plugins.add(pluginName);
                }
                Command.sendChatMessage(command[0]);
            }

            Collections.sort(plugins);
            if (!plugins.isEmpty()) {
                Command.sendChatMessage("§aPlugins §7(§8" + plugins.size() + "§7): §c" + Strings.join(plugins.toArray(new String[0]), "§7, §c"));
            } else {
                Command.sendChatMessage("§cNo plugins found.");
            }
            toggle();
            tickTimer.reset();
        }
    }
}
