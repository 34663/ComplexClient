package complex.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

public class TeamUtils implements MCUtil {
   public static boolean isTeam(EntityPlayer e, EntityPlayer e2) {
      return e.getDisplayName().getFormattedText().contains("§" + isTeam(e)) && e2.getDisplayName().getFormattedText().contains("§" + isTeam(e));
   }

   public static String isTeam(EntityPlayer player) {
      Matcher m = Pattern.compile("§(.).*§r").matcher(player.getDisplayName().getFormattedText());
      return m.find() ? m.group(1) : "f";
   }

   public static boolean isTeams(final EntityPlayer otherEntity) {
      boolean b = false;
      TextFormatting TextFormatting = null;
      TextFormatting TextFormatting2 = null;
      if (otherEntity != null) {
         for (final TextFormatting TextFormatting3 : TextFormatting.values()) {
            if (TextFormatting3 != TextFormatting.RESET) {
               if (mc.thePlayer.getDisplayName().getFormattedText().contains(TextFormatting3.toString()) && TextFormatting == null) {
                  TextFormatting = TextFormatting3;
               }
               if (otherEntity.getDisplayName().getFormattedText().contains(TextFormatting3.toString()) && TextFormatting2 == null) {
                  TextFormatting2 = TextFormatting3;
               }
            }
         }
         try {
            if (TextFormatting != null && TextFormatting2 != null) {
               b = (TextFormatting != TextFormatting2);
            } else if (mc.thePlayer.getTeam() != null) {
               b = !mc.thePlayer.isOnSameTeam(otherEntity);
            } else if (mc.thePlayer.inventory.armorInventory.get(3).getItem() instanceof ItemBlock) {
               b = !ItemStack.areItemStacksEqual(mc.thePlayer.inventory.armorInventory.get(3), otherEntity.inventory.armorInventory.get(3));
            }
         } catch (Exception ignored) {
            ;
         }
      }
      return b;
   }
}
