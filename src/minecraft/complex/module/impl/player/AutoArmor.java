package complex.module.impl.player;

import complex.Complex;
import complex.event.EventTarget;
import complex.event.impl.EventTick;
import complex.event.impl.EventUpdate;
import complex.module.Category;
import complex.module.Module;
import complex.module.data.Setting;
import complex.utils.timer.Timer;
import complex.utils.timer.Timer2;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;

public class AutoArmor extends Module {
    private final Item NULL_ITEM = Item.getItemFromBlock(Blocks.AIR);
    private Timer2 timer = new Timer2();
    private String mode;

    public AutoArmor() {
        super("AutoArmor", null, 0, 0, Category.Player);
    }

    @Override
    public void setup() {
        ArrayList<String> options = new ArrayList<>();
        options.add("Normal");
        options.add("OpenInv");
        Complex.getSettingsManager().rSetting(new Setting("AutoArmor Mode", this, "Normal", options));
        Complex.getSettingsManager().rSetting(new Setting("EquipDelay", this, 5.0, 0, 10.0, false));
    }

    @EventTarget
    public void onUpdate(EventUpdate em) {
        long delay = (long) (Complex.getSettingsManager().getSettingByName("EquipDelay").getValDouble() * 50);
        mode = Complex.getSettingsManager().getSettingByName("AutoArmor Mode").getValString();
        this.setDisplayName("AutoArmor");

        if (mode.equalsIgnoreCase("OpenInv") && !(mc.currentScreen instanceof GuiInventory)) {
            return;
        }
        if (mc.currentScreen == null || mc.currentScreen instanceof GuiInventory || mc.currentScreen instanceof GuiChat) {
            for (byte b = 5; b <= 8; b = (byte) (b + 1)) {
                if (timer.check(delay)) {
                    this.equip();
                    timer.reset();
                }
            }
        }
    }

    private void equip() {
        int[] bestArmorSlots = new int[4];
        int[] bestArmorValues = new int[4];

        for(int armorType = 0; armorType < 4; armorType++) {
            ItemStack oldArmor = mc.thePlayer.inventory.armorItemInSlot(armorType);
            if(oldArmor != null && oldArmor.getItem() instanceof ItemArmor)
                bestArmorValues[armorType] = ((ItemArmor)oldArmor.getItem()).damageReduceAmount;

            bestArmorSlots[armorType] = -1;
        }

        for(int slot = 0; slot < 36; slot++) {
            ItemStack stack = mc.thePlayer.inventory.getStackInSlot(slot);
            if(stack == null || !(stack.getItem() instanceof ItemArmor))
                continue;

            ItemArmor armor = (ItemArmor)stack.getItem();
            int armorType = this.getArmorType(armor);
            int armorValue = armor.damageReduceAmount;

            if(armorValue > bestArmorValues[armorType]) {
                bestArmorSlots[armorType] = slot;
                bestArmorValues[armorType] = armorValue;
            }
        }

        for(int armorType = 0; armorType < 4; armorType++) {
            int slot = bestArmorSlots[armorType];
            if (slot == -1)
                continue;

            ItemStack oldArmor = mc.thePlayer.inventory.armorItemInSlot(armorType);
            if (oldArmor == null || !this.isEmptySlot(oldArmor) || mc.thePlayer.inventory.getFirstEmptyStack() != -1) {
                if (slot < 9)
                    slot += 36;

                mc.playerController.windowClick(0, 8 - armorType, 0, ClickType.QUICK_MOVE, mc.thePlayer);
                mc.playerController.windowClick(0, slot, 0, ClickType.QUICK_MOVE, mc.thePlayer);

                break;
            }
        }
    }
    public int getArmorType(ItemArmor armor) {
        return armor.armorType.ordinal() - 2;
    }
    public boolean isEmptySlot(ItemStack slot) {
        return slot.getItem() == NULL_ITEM;
    }
}
