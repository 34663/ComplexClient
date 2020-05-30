package complex.module.impl.combat;

import complex.Complex;
import complex.command.Command;
import complex.event.EventTarget;
import complex.event.impl.EventAttack;
import complex.event.impl.EventPacket;
import complex.event.impl.EventUpdate;
import complex.module.Category;
import complex.module.Module;
import complex.module.data.Setting;
import complex.utils.timer.MSTimer;
import net.minecraft.block.BlockAir;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;

public class Criticals extends Module {
    private final double[] offsets = new double[]{0.06D, 0.000125D, 0.001D, 0.000125D};
    private MSTimer msTimer = new MSTimer();
    private String mode;
    private int groundTicks;
    private boolean canCrit;

    public Criticals() {
        super("Criticals", null, 0, 0, Category.Combat);
    }

    @Override
    public void setup() {
        ArrayList<String> options = new ArrayList<>();
        options.add("Packet");
        options.add("Packet2");
        options.add("Packet3");
        Complex.getSettingsManager().rSetting(new Setting("Critical Mode", this, "Packet", options));
    }

    @EventTarget
    public void onUpdate(EventUpdate em) {
        mode = Complex.getSettingsManager().getSettingByName("Critical Mode").getValString();

        if (mode.equalsIgnoreCase("Packet")) {
            this.setDisplayName("Criticals §7Packet");
        } else if (mode.equalsIgnoreCase("Packet2")) {
            this.setDisplayName("Criticals §7Packet2");
        } else if (mode.equalsIgnoreCase("Packet3")) {
            this.setDisplayName("Criticals §7Packet3");
        }
    }
    @EventTarget
    public void onAttack(EventAttack event) {
        if (event.getEntity() instanceof EntityLivingBase) {
            Entity entity = event.getEntity();

            if (!mc.thePlayer.onGround || mc.thePlayer.isOnLadder() || mc.thePlayer.isInWeb || mc.thePlayer.isInWater() || mc.thePlayer.isInLava() || mc.thePlayer.ridingEntity != null || entity.hurtResistantTime > 15 || !msTimer.hasTimePassed(0))
                return;

            double x = mc.thePlayer.posX;
            double y = mc.thePlayer.posY;
            double z = mc.thePlayer.posZ;

            if (mode.equalsIgnoreCase("Packet")) {
                mc.getConnection().sendPacket(new CPacketPlayer.Position(x, y + 0.0625, z, true));
                mc.getConnection().sendPacket(new CPacketPlayer.Position(x, y, z, false));
                mc.getConnection().sendPacket(new CPacketPlayer.Position(x, y + 1.1E-5, z, false));
                mc.getConnection().sendPacket(new CPacketPlayer.Position(x, y, z, false));
                mc.thePlayer.onCriticalHit(entity);
            } else if (mode.equalsIgnoreCase("Packet2")) {
                mc.getConnection().sendPacket(new CPacketPlayer.Position(x, y + 0.11, z, false));
                mc.getConnection().sendPacket(new CPacketPlayer.Position(x, y + 0.1100013579, z, false));
                mc.getConnection().sendPacket(new CPacketPlayer.Position(x, y + 0.0000013579, z, false));
                mc.thePlayer.onCriticalHit(entity);
            } else if (mode.equalsIgnoreCase("Packet3")) {
                canCrit = mc.thePlayer.onGround && !mc.thePlayer.isOnLadder() && !mc.thePlayer.isInLava() && !mc.thePlayer.isInWater() && !Complex.getModuleManager().isEnabled("Fly") && groundTicks > 1;
                for (double offset : offsets) {
                    if (canCrit) {
                        mc.getConnection().sendPacketNoEvent(new CPacketPlayer.Position(mc.thePlayer.posX, mc.thePlayer.posY + offset, mc.thePlayer.posZ, false));
                        mc.thePlayer.onCriticalHit(entity);
                    }
                }

                if (mc.thePlayer.onGround) {
                    groundTicks++;
                } else {
                    groundTicks = 0;
                }
            }
        }
    }
}
