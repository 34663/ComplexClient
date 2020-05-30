package complex.utils.misc;

import net.minecraft.client.*;
import java.util.function.*;
import net.minecraft.scoreboard.*;
import java.util.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;

public class ServerUtil {
    public static ServerResult currentServer;
    public static boolean isInPit;

    public static void checkifPit() {
        final Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft.getCurrentServerData() != null) {
            final String lowerCase = minecraft.getCurrentServerData().serverIP.toLowerCase();
            if ((lowerCase.toLowerCase().endsWith("hypixel.net") || lowerCase.toLowerCase().endsWith("hypixel.net:25565")) && minecraft.theWorld.loadedEntityList.stream().anyMatch(ServerUtil::isBot)) {
                ServerUtil.currentServer = ServerResult.HYPIXEL;
            } else if (lowerCase.toLowerCase().endsWith("cubecraft.net") || lowerCase.toLowerCase().endsWith("cubecraft.net:25565")) {
                ServerUtil.currentServer = ServerResult.CUBECRAFT;
            } else if (lowerCase.toLowerCase().endsWith("mineplex.com") || lowerCase.toLowerCase().endsWith("mineplex.com:25565")) {
                ServerUtil.currentServer = ServerResult.MINEPLEX;
            } else {
                ServerUtil.currentServer = ServerResult.OTHER;
            }
        }
        ServerUtil.isInPit = false;
        if (minecraft.theWorld != null) {
            final Iterator<ScoreObjective> iterator = minecraft.theWorld.getScoreboard().getScoreObjectives().iterator();
            while (iterator.hasNext()) {
                if (iterator.next().getName().equals("Pit")) {
                    ServerUtil.isInPit = true;
                    break;
                }
            }
        }
    }

    private static boolean isBot(final Entity entity) {
        return entity instanceof EntityPlayer && entity.getDisplayName().getFormattedText().startsWith("§") && entity.getCustomNameTag().equals("");
    }

    public enum ServerResult {
        HYPIXEL("HYPIXEL", 0),
        CUBECRAFT("CUBECRAFT", 1),
        MINEPLEX("MINEPLEX", 2),
        OTHER("OTHER", 3);

        private static final ServerResult[] VALUES;

        private ServerResult(final String s, final int n) {
        }

        static {
            VALUES = new ServerResult[]{ServerResult.HYPIXEL, ServerResult.CUBECRAFT, ServerResult.MINEPLEX, ServerResult.OTHER};
        }
    }
}
