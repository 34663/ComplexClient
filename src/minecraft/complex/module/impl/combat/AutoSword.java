package complex.module.impl.combat;

import complex.Complex;
import complex.event.EventTarget;
import complex.event.impl.EventUpdate;
import complex.module.Category;
import complex.module.Module;
import complex.module.data.Setting;
import complex.utils.timer.Timer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;

public class AutoSword extends Module {
    private Timer timer = new Timer();

    public AutoSword() {
        super("AutoSword", null, 0, 0, Category.Combat);
    }

    @Override
    public void setup() {
        Complex.getSettingsManager().rSetting(new Setting("MoveSlot", this, 0, 0, 8, true));
        Complex.getSettingsManager().rSetting(new Setting("MoveDelay", this, 100, 1, 2000, true));
        Complex.getSettingsManager().rSetting(new Setting("InvOnly", this, false));
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        this.setDisplayName("AutoSword §7" + (int)Complex.getSettingsManager().getSettingByName("MoveDelay").getValDouble());
        if (event.isPre() || !this.timer.delay((float) Complex.getSettingsManager().getSettingByName("MoveDelay").getValDouble()) || (this.mc.currentScreen != null && !(this.mc.currentScreen instanceof GuiInventory)) || (!(this.mc.currentScreen instanceof GuiInventory) && Complex.getSettingsManager().getSettingByName("InvOnly").getValBoolean())) {
            return;
        }
        int slotId = -1;
        float n = 0.0f;
        for (int i = 9; i < 45; ++i) {
            if (this.mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                final ItemStack stack = this.mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (stack.getItem() instanceof ItemSword) {
                    final float a = this.bestSword(stack);
                    if (a >= n) {
                        n = a;
                        slotId = i;
                    }
                }
            }
        }
        final ItemStack stack2 = this.mc.thePlayer.inventoryContainer.getSlot(36 + (int) Complex.getSettingsManager().getSettingByName("MoveSlot").getValDouble()).getStack();
        if (slotId != -1 || stack2 == null || (stack2.getItem() instanceof ItemSword && n > this.bestSword(stack2))) {
            final float n2 = (stack2 != null && stack2.getItem() != null && stack2.getItem() instanceof ItemSword) ? this.bestSword(stack2) : -83474.0f;
            if (slotId != 36 + Complex.getSettingsManager().getSettingByName("MoveSlot").getValDouble() && slotId != -1 && n > n2) {
                this.mc.playerController.windowClick(this.mc.thePlayer.inventoryContainer.windowId, slotId, (int) Complex.getSettingsManager().getSettingByName("MoveSlot").getValDouble(), ClickType.SWAP, this.mc.thePlayer);
                this.timer.reset();
            }
        }
    }

    private float bestSword(final ItemStack itemStack) {
        return ((ItemSword) itemStack.getItem()).getDamageVsEntity() + EnchantmentHelper.getEnchantmentLevel(Enchantment.getEnchantmentByID(16), itemStack) * 1.25f + EnchantmentHelper.getEnchantmentLevel(Enchantment.getEnchantmentByID(20), itemStack) * 0.01f + EnchantmentHelper.getEnchantmentLevel(Enchantment.getEnchantmentByID(19), itemStack) * 1.00f + EnchantmentHelper.getEnchantmentLevel(Enchantment.getEnchantmentByID(34), itemStack) * 1.00f;
    }
}
