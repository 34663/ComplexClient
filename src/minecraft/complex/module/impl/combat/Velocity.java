package complex.module.impl.combat;

import complex.Complex;
import complex.event.EventTarget;
import complex.event.impl.EventPacket;
import complex.event.impl.EventUpdate;
import complex.module.Category;
import complex.module.Module;
import complex.module.data.Setting;
import complex.module.impl.movement.Fly;
import complex.utils.MoveUtils;
import complex.utils.timer.TimerUtil;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;

public class Velocity extends Module {
    private String mode;

    public Velocity() {
        super("Velocity", null, 0, 0, Category.Combat);
    }

    @Override
    public void setup() {
        ArrayList<String> options = new ArrayList<>();
        options.add("Cancel");
        options.add("Cube");
        options.add("AAC");
        options.add("AACReduce");
        Complex.getSettingsManager().rSetting(new Setting("Velocity Mode", this, "Cancel", options));
    }

    @EventTarget
    public void onUpdate(EventUpdate em) {
        mode = Complex.getSettingsManager().getSettingByName("Velocity Mode").getValString();;
        if (mode.equalsIgnoreCase("Cancel")) {
            this.setDisplayName("Velocity §7Cancel");
        } else if (mode.equalsIgnoreCase("Cube")) {
            this.setDisplayName("Velocity §7Cube");
        } else if (mode.equalsIgnoreCase("AAC")) {
            this.setDisplayName("Velocity §7AAC");
        } else if (mode.equalsIgnoreCase("Velocity §7AACReduce")) {
            this.setDisplayName("Velocity §7AACReduce");
        }
    }
    @EventTarget
    public void onPacket(EventPacket ep) {
        if (mode.equalsIgnoreCase("Cancel")) {
            if (ep.getPacket() instanceof SPacketEntityVelocity) {
                final SPacketEntityVelocity s12PacketEntityVelocity = (SPacketEntityVelocity) ep.getPacket();
                if (this.mc.thePlayer != null && this.mc.thePlayer.getEntityId() == s12PacketEntityVelocity.getEntityID()) {
                    ep.setCancelled(true);
                }
            } else if (ep.getPacket() instanceof SPacketExplosion) {
                ep.setCancelled(true);
            }
        } else if (mode.equalsIgnoreCase("Cube")) {
            if (ep.getPacket() instanceof SPacketEntityVelocity) {
                if (this.mc.thePlayer.getEntityId() == ((SPacketEntityVelocity) ep.getPacket()).getEntityID()) {
                    ep.setCancelled(true);
                }
            } else if (ep.getPacket() instanceof SPacketExplosion) {
                ep.setCancelled(true);
            }
        } else if (mode.equalsIgnoreCase("AAC")) {
            this.AAC();
        } else if (mode.equalsIgnoreCase("AACReduce")) {
            this.AACReduce();
        }
    }

    public void AAC() {
        if (Velocity.mc.thePlayer.hurtTime != 0 && Velocity.mc.thePlayer.hurtTime < 7 && Velocity.mc.thePlayer.fallDistance <= 2.0f) {
            float forward = 0.0f;
            float strafe = 0.0f;
            double speed = (float) 2 / 10.0f;
            if (!Velocity.mc.gameSettings.keyBindForward.pressed && !Velocity.mc.gameSettings.keyBindBack.pressed && !Velocity.mc.gameSettings.keyBindLeft.pressed && !Velocity.mc.gameSettings.keyBindRight.pressed) {
                speed = 0.24;
            }
            final float var5 = MathHelper.sin(Velocity.mc.thePlayer.rotationYaw * 3.1415927f / 180.0f);
            final float var6 = MathHelper.cos(Velocity.mc.thePlayer.rotationYaw * 3.1415927f / 180.0f);
            if (Velocity.mc.gameSettings.keyBindForward.pressed) {
                ++forward;
            }
            if (Velocity.mc.gameSettings.keyBindBack.pressed) {
                --forward;
            }
            if (Velocity.mc.gameSettings.keyBindLeft.pressed) {
                strafe += 0.7f;
                forward *= 0.65f;
            }
            if (Velocity.mc.gameSettings.keyBindRight.pressed) {
                strafe -= 0.7f;
                forward *= 0.65f;
            }
            if (!Velocity.mc.thePlayer.onGround) {
                Velocity.mc.thePlayer.motionX = (strafe * var6 - forward * var5) * speed;
                Velocity.mc.thePlayer.motionZ = (forward * var6 + strafe * var5) * speed;
            }
        } else if (Velocity.mc.thePlayer.hurtTime > 0) {
            Velocity.mc.thePlayer.onGround = true;
            final EntityPlayerSP thePlayer = Velocity.mc.thePlayer;
            thePlayer.motionX *= 0.6000000238418579;
            final EntityPlayerSP thePlayer2 = Velocity.mc.thePlayer;
            thePlayer2.motionZ *= 0.6000000238418579;
        }
    }
    public void AACReduce() {
        if (Velocity.mc.thePlayer.hurtTime > 0) {
            Velocity.mc.thePlayer.onGround = true;
        }
        if (Velocity.mc.thePlayer.hurtTime > 0) {
            if (Velocity.mc.gameSettings.keyBindForward.pressed || Velocity.mc.gameSettings.keyBindBack.pressed || Velocity.mc.gameSettings.keyBindLeft.pressed || Velocity.mc.gameSettings.keyBindRight.pressed) {
                Velocity.mc.thePlayer.hurtTime = 0;
                final EntityPlayerSP thePlayer = Velocity.mc.thePlayer;
                thePlayer.motionX /= 8.0;
                final EntityPlayerSP thePlayer2 = Velocity.mc.thePlayer;
                thePlayer2.motionZ /= 8.0;
                return;
            }
            Velocity.mc.thePlayer.hurtTime = 0;
            final EntityPlayerSP thePlayer3 = Velocity.mc.thePlayer;
            thePlayer3.motionX /= -8.0;
            final EntityPlayerSP thePlayer4 = Velocity.mc.thePlayer;
            thePlayer4.motionZ /= -8.0;
        }
    }
}
