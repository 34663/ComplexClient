package complex.module.impl.misc;

import complex.event.EventTarget;
import complex.event.impl.EventPacket;
import complex.module.Category;
import complex.module.Module;
import net.minecraft.network.play.client.CPacketChatMessage;

import java.util.ArrayList;

public class ChatBypass extends Module {
    public ChatBypass() {
        super("ChatBypass", null, 0, 0, Category.Misc);
    }

    @Override
    public void onEnable() {
        this.setDisplayName("ChatBypass");
        super.onEnable();
    }

    @EventTarget
    public void onPacket(EventPacket packet) {
        if (packet.isOutgoing() && packet.getPacket() instanceof CPacketChatMessage) {
            final CPacketChatMessage c01PacketChatMessage = (CPacketChatMessage)packet.getPacket();
            String string = "";
            final ArrayList<String> list = new ArrayList<String>();
            final String[] split = c01PacketChatMessage.getMessage().split(" ");
            for (int i = 0; i < split.length; ++i) {
                final char[] charArray = split[i].toCharArray();
                for (int j = 0; j < charArray.length; ++j) {
                    list.add(charArray[j] + "\u061c");
                }
                list.add(" ");
            }
            for (int k = 0; k < list.size(); ++k) {
                string += list.get(k);
            }
            if (c01PacketChatMessage.getMessage().startsWith("%")) {
                c01PacketChatMessage.setMessage(string.replaceFirst("%", ""));
                list.clear();
            }
        }
    }
}
