package complex.module.impl.misc;

import complex.command.Command;
import complex.event.EventTarget;
import complex.event.impl.EventPacket;
import complex.event.impl.EventTick;
import complex.event.impl.EventUpdate;
import complex.module.Category;
import complex.module.Module;
import net.minecraft.network.play.server.SPacketPlayerListItem;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class VanishDetector extends Module {
    private Set<UUID> xy;
    private HashMap<UUID, String> xz;

    public VanishDetector() {
        super("VanishDetector", null, 0, 0, Category.Misc);
        this.xy = new HashSet<UUID>();
        this.xz = new HashMap<UUID, String>();
        this.setDisplayName("VanishDetector");
    }

    @EventTarget
    public void onTick(EventTick event) {
        if (this.mc.getConnection() != null) {
            this.mc.getConnection().getRealPlayerInfoMap().values().forEach(networkPlayerInfo -> {
                if (networkPlayerInfo.getGameProfile().getName() != null) {
                    this.xz.put(networkPlayerInfo.getGameProfile().getId(), networkPlayerInfo.getGameProfile().getName());
                }
            });
        }
    }
    @EventTarget
    public void onPacket(EventPacket event) {
        if (event.getPacket() instanceof SPacketPlayerListItem) {
            final SPacketPlayerListItem playerListItem = (SPacketPlayerListItem) event.getPacket();
            if (playerListItem.getAction() == SPacketPlayerListItem.Action.UPDATE_LATENCY) {
                playerListItem.getEntries().forEach(addPlayerData -> {
                    if (this.mc.getConnection().getPlayerInfo(addPlayerData.getProfile().getId()) == null) {
                        if (!this.xy.contains(addPlayerData.getProfile().getId())) {
                            String s1 = this.a(addPlayerData.getProfile().getId());
                            Command.sendChatMessageInfo(s1 + " is now vanished.");
                        }
                        this.xy.add(addPlayerData.getProfile().getId());
                    }
                });
            }
        }
    }
    @EventTarget
    public void onUpdate(EventUpdate event) {
        if (this.xy != null) {
            this.xy.forEach(p_175102_1_ -> {
                if (this.mc.getConnection().getPlayerInfo(p_175102_1_) != null) {
                    Command.sendChatMessageInfo(this.a(p_175102_1_) + " is no longer vanished.");
                }
                this.xy.remove(p_175102_1_);
            });
        }
    }

    public String a(final UUID uuid) {
        if (this.xz.containsKey(uuid)) {
            return this.xz.get(uuid);
        }
        return uuid.toString();
    }
}
