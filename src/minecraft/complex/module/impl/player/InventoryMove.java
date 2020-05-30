package complex.module.impl.player;

import complex.Complex;
import complex.event.EventTarget;
import complex.event.impl.EventRender2D;
import complex.event.impl.EventTick;
import complex.event.impl.EventUpdate;
import complex.module.Category;
import complex.module.Module;
import complex.module.data.Setting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

public class InventoryMove extends Module {
    public InventoryMove() {
        super("InventoryMove", null, 0, 0, Category.Player);
    }

    @Override
    public void setup() {
        Complex.getSettingsManager().rSetting(new Setting("Rotation", this, false));
    }

    @Override
    public void onEnable() {
        this.setDisplayName("InventoryMove");
        super.onEnable();
    }

    @EventTarget
    public void onUpdate(EventUpdate em) {
        KeyBinding[] keys = new KeyBinding[]{mc.gameSettings.keyBindJump, mc.gameSettings.keyBindForward, mc.gameSettings.keyBindBack, mc.gameSettings.keyBindLeft, mc.gameSettings.keyBindRight, mc.gameSettings.keyBindSprint};
        if (mc.currentScreen != null) {
            if (mc.currentScreen instanceof GuiChat) {
                return;
            }

            KeyBinding[] array = keys;
            int l = keys.length;

            for (int i = 0; i < l; ++i) {
                KeyBinding bind = array[i];
                bind.pressed = Keyboard.isKeyDown(bind.getKeyCode());
            }
        }
    }

    @EventTarget
    public void onRender2D(EventRender2D event) {
        if ((mc.currentScreen != null) && (!(mc.currentScreen instanceof GuiChat)) && Complex.getSettingsManager().getSettingByName("Rotation").getValBoolean()) {
            if (Keyboard.isKeyDown(200)) {
                pitch(mc.thePlayer.rotationPitch - 2.0F);
            }
            if (Keyboard.isKeyDown(208)) {
                pitch(mc.thePlayer.rotationPitch + 2.0F);
            }
            if (Keyboard.isKeyDown(203)) {
                yaw(mc.thePlayer.rotationYaw - 3.0F);
            }
            if (Keyboard.isKeyDown(205)) {
                yaw(mc.thePlayer.rotationYaw + 3.0F);
            }
        }
    }

    public static void pitch(float pitch) {
        Minecraft.getMinecraft().thePlayer.rotationPitch = pitch;
    }
    public static void yaw(float yaw) {
        Minecraft.getMinecraft().thePlayer.rotationYaw = yaw;
    }
}
