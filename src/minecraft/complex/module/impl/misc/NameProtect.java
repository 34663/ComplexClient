package complex.module.impl.misc;

import complex.Complex;
import complex.command.Command;
import complex.event.EventTarget;
import complex.event.impl.EventPacket;
import complex.event.impl.EventText;
import complex.event.impl.EventUpdate;
import complex.module.Category;
import complex.module.Module;
import complex.module.data.Setting;
import complex.utils.render.Colors;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketChat;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class NameProtect extends Module {
    public static List<String> strings = new ArrayList<>();

    public NameProtect() {
        super("NameProtect", null, 0, 0, Category.Misc);
    }

    @Override
    public void setup() {
        Complex.getSettingsManager().rSetting(new Setting("Name", this, false));
        Complex.getSettingsManager().rSetting(new Setting("Skin", this, false));
        Complex.getSettingsManager().rSetting(new Setting("Test", this, false));
    }

    @Override
    public void onEnable() {
        this.setDisplayName("NameProtect");
        super.onEnable();
    }

    @EventTarget
    public void onText(EventText eventText) {
        if (mc.thePlayer == null)
            return;
        eventText.setText(StringUtils.replace(eventText.getText(), mc.thePlayer.getName(), "§9Me§f"));

        if (Complex.getSettingsManager().getSettingByName("Name").getValBoolean())
            for (final NetworkPlayerInfo playerInfo : mc.getConnection().getPlayerInfoMap())
                eventText.setText(StringUtils.replace(eventText.getText(), playerInfo.getGameProfile().getName(), "Protected User"));
    }
}
