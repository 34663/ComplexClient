package complex.module.impl.misc;

import complex.Complex;
import complex.event.EventTarget;
import complex.event.impl.EventPacket;
import complex.event.impl.EventRender2D;
import complex.event.impl.EventRender3D;
import complex.event.impl.EventUpdate;
import complex.module.Category;
import complex.module.Module;
import complex.module.data.Setting;
import complex.utils.PlayerUtil;
import complex.utils.render.Colors;
import complex.utils.render.RenderingUtils;
import complex.utils.timer.Timer3;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketKeepAlive;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.Vec3d;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Blink extends Module {
    private List<Packet> packets = new CopyOnWriteArrayList<>();
    private List<Vec3d> crumbs = new CopyOnWriteArrayList<>();
    private Timer3 timer = new Timer3();

    public Blink() {
        super("Blink", null, 0, 0, Category.Misc);
    }

    @Override
    public void onEnable() {
        this.setDisplayName("Blink");
        crumbs.clear();
        super.onEnable();
    }

    @Override
    public void onDisable() {
        crumbs.clear();
        for (Packet packet : packets) {
            mc.getConnection().getNetworkManager().sendPacketNoEvent(packet);
        }
        packets.clear();
        super.onDisable();
    }

    @EventTarget
    public void onUpdate(EventUpdate eventUpdate) {
        this.setDisplayName("Blink " + (PlayerUtil.isMoving() ? "\2479" : "§7") + packets.size());
    }
    @EventTarget
    public void onPacket(EventPacket packet) {
        if (packet.isOutgoing() && packet.isPre() && (packet.getPacket() instanceof CPacketPlayer || packet.getPacket() instanceof CPacketKeepAlive)) {
            packets.add(packet.getPacket());
            packet.setCancelled(true);
        }
        if (packet.isIncoming() && packet.isPre()) {
            if (packet.getPacket() instanceof SPacketPlayerPosLook) {
                packet.setCancelled(true);
            }
        }
    }
    @EventTarget
    public void onRender3D(EventRender3D event) {
        if (timer.delay(50)) {
            crumbs.add(new Vec3d(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ));
            timer.reset();
        }
        if (!crumbs.isEmpty() && crumbs.size() > 2) {
            for (int i = 1; i < crumbs.size(); i++) {
                Vec3d vecBegin = crumbs.get(i - 1);
                Vec3d vecEnd = crumbs.get(i);
                int color = Colors.getColor(164, 24, 188);
                float beginX = (float) ((float) vecBegin.xCoord - RenderManager.renderPosX);
                float beginY = (float) ((float) vecBegin.yCoord - RenderManager.renderPosY);
                float beginZ = (float) ((float) vecBegin.zCoord - RenderManager.renderPosZ);
                float endX = (float) ((float) vecEnd.xCoord - RenderManager.renderPosX);
                float endY = (float) ((float) vecEnd.yCoord - RenderManager.renderPosY);
                float endZ = (float) ((float) vecEnd.zCoord - RenderManager.renderPosZ);
                final boolean bobbing = mc.gameSettings.viewBobbing;
                mc.gameSettings.viewBobbing = false;
                RenderingUtils.drawLine3D(beginX, beginY, beginZ, endX, endY, endZ, color);
                mc.gameSettings.viewBobbing = bobbing;
            }
        }
    }
}
