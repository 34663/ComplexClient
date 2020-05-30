package complex.module.impl.movement;

import complex.Complex;
import complex.event.EventTarget;
import complex.event.impl.EventUpdate;
import complex.module.Category;
import complex.module.Module;
import complex.module.data.Setting;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;

public class Clipper extends Module {
    int tryTele, lastTeleportTimer, ticks;
    private String mode;

    public Clipper() {
        super("Clipper", null, 0, 0, Category.Movement);
    }

    @Override
    public void setup() {
        ArrayList<String> options = new ArrayList<>();
        options.add("Cube");
        options.add("Eagler");
        Complex.getSettingsManager().rSetting(new Setting("Clip Mode", this, "Cube", options));
    }

    @Override
    public void onEnable() {
        tryTele = 3;
        lastTeleportTimer = 100;
        ticks = 400;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1.0F;
        super.onDisable();
    }

    @EventTarget
    public void onUpdate(EventUpdate em) {
        mode = Complex.getSettingsManager().getSettingByName("Clip Mode").getValString();

        if (isEnabled()) {
            if (mode.equalsIgnoreCase("Cube")) {
                this.setDisplayName("Clipper §7Cube");
                lastTeleportTimer += 1;
                tryTele += 1;
                ticks += 1;
                if (tryTele > 5) {
                    tryTele = 3;
                }
                if (mc.gameSettings.keyBindSneak.pressed == true) {
                    if (mc.theWorld.isAirBlock(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - tryTele, mc.thePlayer.posZ)) && lastTeleportTimer > 10) {
                        if (!mc.theWorld.isAirBlock(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ))) {
                            if (mc.theWorld.isAirBlock(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - tryTele + 1, mc.thePlayer.posZ))) {
                                if (!mc.theWorld.isAirBlock(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - tryTele - 1, mc.thePlayer.posZ)) || !mc.theWorld.isAirBlock(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - tryTele - 2, mc.thePlayer.posZ)) || !mc.theWorld.isAirBlock(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - tryTele - 3, mc.thePlayer.posZ)) || !mc.theWorld.isAirBlock(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - tryTele - 4, mc.thePlayer.posZ)) || !mc.theWorld.isAirBlock(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - tryTele - 5, mc.thePlayer.posZ))) {
                                    mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - tryTele, mc.thePlayer.posZ);
                                    lastTeleportTimer = 0;
                                    mc.gameSettings.keyBindSneak.pressed = false;
                                    mc.thePlayer.motionY = -1.3;
                                }
                            }
                        }
                    }
                }
                if (mc.gameSettings.keyBindJump.pressed == true) {
                    if (mc.theWorld.isAirBlock(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY + 4, mc.thePlayer.posZ)) && lastTeleportTimer > 10) {
                        if (!mc.theWorld.isAirBlock(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY + 2, mc.thePlayer.posZ))) {
                            if (mc.theWorld.isAirBlock(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY + 3, mc.thePlayer.posZ))) {
                                mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 4.2, mc.thePlayer.posZ);
                                mc.thePlayer.motionY = -2;
                                lastTeleportTimer = 0;
                                mc.gameSettings.keyBindJump.pressed = false;
                            }
                        }
                    }
                }
            } else if (mode.equalsIgnoreCase("Eagler")) {
                final float blocks = 4;
                this.mc.getConnection().sendPacket(new CPacketPlayer.Position(this.mc.thePlayer.posX, this.mc.thePlayer.posY - blocks, this.mc.thePlayer.posZ, this.mc.thePlayer.onGround));
                this.mc.getConnection().sendPacket(new CPacketPlayer.Position(this.mc.thePlayer.posX, this.mc.thePlayer.posY - blocks, this.mc.thePlayer.posZ, this.mc.thePlayer.onGround));
                this.mc.getConnection().sendPacket(new CPacketPlayer.Position(this.mc.thePlayer.posX, this.mc.thePlayer.posY - blocks, this.mc.thePlayer.posZ, this.mc.thePlayer.onGround));
                if (this.mc.thePlayer.onGround) {
                    this.mc.thePlayer.jump();
                }
                this.toggle();
            }
        }
    }
}
