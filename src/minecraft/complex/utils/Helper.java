package complex.utils;

import java.util.List;
import complex.utils.render.Colors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import org.apache.http.util.EntityUtils;

public class Helper {
    private static EntityUtils entityUtils;
    private static Colors colorUtils = new Colors();
    private static MathUtils mathUtils;

    protected static List<Entity> getLoadedEntities() {
        return Minecraft.getMinecraft().theWorld.loadedEntityList;
    }

    public static boolean hasArmor(EntityPlayer player) {
        if (player.inventory == null) {
            return false;
        }
        ItemStack boots = player.inventory.armorInventory.get(0);
        ItemStack pants = player.inventory.armorInventory.get(1);
        ItemStack chest = player.inventory.armorInventory.get(2);
        ItemStack head = player.inventory.armorInventory.get(3);
        return (boots != null) || (pants != null) || (chest != null) || (head != null);
    }

    public static Minecraft mc() {
        return Minecraft.getMinecraft();
    }

    public static EntityPlayerSP player() {
        return mc().thePlayer;
    }

    public static WorldClient world() {
        return mc().theWorld;
    }

    public static EntityUtils entityUtils() {
        return entityUtils;
    }

    public static ScaledResolution scaled() {
        return new ScaledResolution(mc());
    }

    public static Colors colorUtils() {
        return colorUtils;
    }

    public static MathUtils mathUtils() {
        return mathUtils;
    }

    public static void sendPacket(Packet p) {
        mc().getConnection().sendPacket(p);
    }
}