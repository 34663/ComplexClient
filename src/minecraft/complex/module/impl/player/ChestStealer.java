package complex.module.impl.player;

import complex.Complex;
import complex.event.EventTarget;
import complex.event.impl.EventUpdate;
import complex.module.Category;
import complex.module.Module;
import complex.module.data.Setting;
import complex.utils.MiscUtils;
import complex.utils.timer.Timer;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;

import java.util.ArrayList;
import java.util.List;

public class ChestStealer extends Module {
    private TileEntityChest tileEntityChest;
    private final List<TileEntityChest> entityChests;
    private final Timer timer1 = new Timer();
    private final Timer timer2 = new Timer();
    private final Timer timer3 = new Timer();
    private final String[] ys;

    public ChestStealer() {
        super("ChestStealer", null, 0, 0, Category.Player);
        this.ys = new String[] { "menu", "selector", "game", "gui", "server", "inventory", "play", "teleporter", "shop", "melee", "armor", "block", "castle", "mini", "warp", "teleport", "user", "team", "tool", "sure", "trade", "cancel", "accept", "soul", "book", "recipe", "profile", "tele", "port", "map", "kit", "select", "lobby", "vault", "lock", "quick", "travel", "cake", "war", "pvp" };
        this.entityChests = new ArrayList<TileEntityChest>();
    }

    @Override
    public void setup() {
        Complex.getSettingsManager().rSetting(new Setting("StealDelay", this, 20.0, 0.0, 40.0, false));
        Complex.getSettingsManager().rSetting(new Setting("ChestAura", this, false));
    }

    @Override
    public void onEnable() {
        this.setDisplayName("ChestStealer");
        super.onEnable();
    }

    @EventTarget
    public void onUpdate(EventUpdate update) {
        if (update.isPre()) {
            if (Complex.getSettingsManager().getSettingByName("ChestAura").getValBoolean()) {
                this.tileEntityChest = this.d();
            }
            if (this.mc.currentScreen instanceof GuiChest) {
                if (this.tileEntityChest != null) {
                    this.entityChests.add(this.tileEntityChest);
                }
                final GuiChest guiChest = (GuiChest) this.mc.currentScreen;
                final String lowerCase = guiChest.lowerChestInventory.getDisplayName().getUnformattedText().toLowerCase();
                final String[] ys = this.ys;
                final float delay = (float) Complex.getSettingsManager().getSettingByName("StealDelay").getValDouble() * 10;

                for (int length = ys.length, i = 0; i < length; ++i) {
                    if (lowerCase.contains(ys[i])) {
                        return;
                    }
                }
                for (int n = guiChest.getInventoryRows() * 9, j = 0; j < n; ++j) {
                    final Slot slot = guiChest.inventorySlots.getSlot(j);
                    if (slot.getHasStack()) {
                        if (this.timer2.delay(delay)) {
                            this.mc.playerController.windowClick(guiChest.inventorySlots.windowId, slot.slotNumber, 0, ClickType.QUICK_MOVE, this.mc.thePlayer);
                            this.timer2.reset();
                        }
                    }
                }
                if (!this.bruh(guiChest) || this.isEmpty()) {
                    if (this.timer1.delay(MiscUtils.a(75, 150))) {
                        this.mc.thePlayer.closeScreen();
                    }
                } else {
                    this.timer1.reset();
                }
            } else if (Complex.getSettingsManager().getSettingByName("ChestAura").getValBoolean() && this.tileEntityChest != null) {
                final float[] a = this.getRotationChest(this.tileEntityChest.getPos().getX() + 0.5f, this.tileEntityChest.getPos().getY() - 1, this.tileEntityChest.getPos().getZ() + 0.5f, this.mc.thePlayer);
                update.setPitch(a[1]);
                update.setYaw(a[0]);
            }
        } else if (this.tileEntityChest != null && !(this.mc.currentScreen instanceof GuiChest)) {
            this.mc.thePlayer.swingArm(EnumHand.MAIN_HAND);
            this.mc.getConnection().sendPacket(new CPacketPlayerTryUseItemOnBlock(this.tileEntityChest.getPos(), EnumFacing.UP, EnumHand.MAIN_HAND, 0.0f, 0.0f, 0.0f));
        }
        if (this.mc.currentScreen == null) {
            this.timer1.reset();
        }
    }

    private boolean isEmpty() {
        for (int i = 9; i <= tileEntityChest.getSizeInventory(); ++i) {
            if (this.mc.thePlayer.inventoryContainer.getSlot(i).getStack() == null) {
                return false;
            }
        }
        return true;
    }
    private TileEntityChest d() {
        TileEntity tileEntity = null;
        double distanceSq = 3.5 * 3.5;
        for (final TileEntity tileEntity2 : this.mc.theWorld.loadedTileEntityList) {
            if (tileEntity2 instanceof TileEntityChest && !this.entityChests.contains(tileEntity2) && this.mc.thePlayer.getDistanceSq(tileEntity2.getPos()) < distanceSq) {
                tileEntity = tileEntity2;
                distanceSq = this.mc.thePlayer.getDistanceSq(tileEntity.getPos());
            }
        }
        return (TileEntityChest) tileEntity;
    }
    private float[] getRotationChest(final double n, final double n2, final double n3, final EntityPlayer entityPlayer) {
        final double n4 = entityPlayer.posX - n;
        final double n5 = entityPlayer.posY - n2;
        final double n6 = entityPlayer.posZ - n3;
        final double sqrt = Math.sqrt(n4 * n4 + n5 * n5 + n6 * n6);
        return new float[]{(float) (Math.atan2(n6 / sqrt, n4 / sqrt) * 180.0 / 3.141592653589793 + 90.0), (float) (Math.asin(n5 / sqrt) * 180.0 / 3.141592653589793)};
    }
    private boolean bruh(final GuiChest guiChest) {
        int n = 0;
        for (int n2 = guiChest.getInventoryRows() * 9, i = 0; i < n2; ++i) {
            if (guiChest.inventorySlots.getSlot(i).getHasStack()) {
                ++n;
            }
        }
        return n > 0;
    }
}
