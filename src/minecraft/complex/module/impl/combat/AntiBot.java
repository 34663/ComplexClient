package complex.module.impl.combat;

import com.google.common.collect.Sets;
import complex.Complex;
import complex.command.Command;
import complex.event.EventTarget;
import complex.event.impl.EventPacket;
import complex.event.impl.EventUpdate;
import complex.module.Category;
import complex.module.Module;
import complex.module.data.Setting;
import complex.utils.timer.Timer3;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketSpawnPlayer;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class AntiBot extends Module {
    public static List<EntityPlayer> getInvalid() {
        return invalid;
    }
    public static List<EntityPlayer> invalid = new ArrayList<>();
    public static List<EntityPlayer> removed = new ArrayList<>();
    private String mode;
    public static Set<Integer> entityIdSet;
    public static Set<Integer> integerSet2;
    private Timer3 lastRemoved = new Timer3();

    public AntiBot() {
        super("AntiBot", null, 0, 0, Category.Combat);
    }

    @Override
    public void setup() {
        ArrayList<String> options = new ArrayList<>();
        options.add("Hypixel");
        options.add("Packet");
        options.add("Mineplex");
        Complex.getSettingsManager().rSetting(new Setting("AntiBot Mode", this, "Hypixel", options));
        Complex.getSettingsManager().rSetting(new Setting("BotKiller", this, true));
    }

    @Override
    public void onEnable() {
        mode = Complex.getSettingsManager().getSettingByName("AntiBot Mode").getValString();
        invalid.clear();

        if (mode.equalsIgnoreCase("Hypixel")) {
            AntiBot.integerSet2.clear();
            if (isEnabled()) {
                AntiBot.entityIdSet.clear();
            } else {
                if (this.mc.theWorld == null) {
                    return;
                }
                AntiBot.entityIdSet.addAll(this.mc.theWorld.loadedEntityList.stream().filter(AntiBot::isPlayer2).map(AntiBot::castToPlayer2).filter(this::isNotLocalPlayer2).map(Entity::getEntityId).collect(Collectors.toList()));
            }
        }
        super.onEnable();
    }

    @EventTarget
    public void onUpdate(EventUpdate eventUpdate) {
        boolean killer = Complex.getSettingsManager().getSettingByName("BotKiller").getValBoolean();
        mode = Complex.getSettingsManager().getSettingByName("AntiBot Mode").getValString();

        if (mode.equalsIgnoreCase("Hypixel")) {
            this.setDisplayName("AntiBot §7Hypixel");
            if (this.mc.theWorld == null || this.mc.thePlayer == null) {
                AntiBot.integerSet2.clear();
                return;
            }
            if (this.mc.thePlayer.ticksExisted < 10 || this.mc.thePlayer.ticksExisted % 40 == 0) {
                AntiBot.integerSet2.clear();
            }
            for (final Entity entity : this.mc.theWorld.loadedEntityList) {
                if (entity instanceof EntityPlayer) {
                    final EntityPlayer entityPlayer = (EntityPlayer) entity;
                    if (entityPlayer == this.mc.thePlayer) {
                        continue;
                    }
                    if (AntiBot.integerSet2.contains(entityPlayer.getEntityId())) {
                        continue;
                    }
                    final String formattedText = entityPlayer.getDisplayName().getFormattedText();
                    final String customNameTag = entityPlayer.getCustomNameTag();
                    final String name = entityPlayer.getName();
                    if (!customNameTag.equalsIgnoreCase("")) {
                        AntiBot.integerSet2.add(entityPlayer.getEntityId());
                    }
                    if(formattedText.contains("§8[NPC]")){
                        integerSet2.add(entityPlayer.getEntityId());
                    }
                    if (formattedText.startsWith("§") || !formattedText.endsWith("§r")) {
                        continue;
                    }
                    AntiBot.integerSet2.add(entityPlayer.getEntityId());
                }
            }
            this.mc.theWorld.loadedEntityList.stream().filter(AntiBot::isPlayer).map(AntiBot::castToPlayer).filter(this::isNotLocalPlayer).filter(AntiBot::notOnGround).forEach(AntiBot::isInAntiBotSet);
        } else if (mode.equalsIgnoreCase("Packet")) {
            this.setDisplayName("AntiBot §7Packet");
        } else if (mode.equalsIgnoreCase("Mineplex")) {
            this.setDisplayName("AntiBot §7Mineplex");
            for (Entity entity : mc.theWorld.loadedEntityList) {
                if (entity instanceof EntityPlayer) {
                    EntityPlayer entityPlayer = (EntityPlayer)entity;
                    if (!Float.isNaN(entityPlayer.getHealth()) && !invalid.contains(entityPlayer)) {
                        invalid.add(entityPlayer);
                    }
                    if (entityPlayer.onGround)
                        invalid.remove(entityPlayer);
                }
            }
        }
    }
    @EventTarget
    public void onPacket(EventPacket ep) {
        if (mode.equalsIgnoreCase("Packet")) {
            if (ep.isIncoming() && ep.getPacket() instanceof SPacketSpawnPlayer) {
                SPacketSpawnPlayer packet = (SPacketSpawnPlayer) ep.getPacket();
                double entX = packet.getX() / 32;
                double entY = packet.getY() / 32;
                double entZ = packet.getZ() / 32;
                double posX = mc.thePlayer.posX;
                double posY = mc.thePlayer.posY;
                double posZ = mc.thePlayer.posZ;
                double var7 = posX - entX;
                double var9 = posY - entY;
                double var11 = posZ - entZ;
                float distance = MathHelper.sqrt_float(var7 * var7 + var9 * var9 + var11 * var11);
                if (distance <= 17 && entY > mc.thePlayer.posY + 1 && (mc.thePlayer.posX != entX && mc.thePlayer.posY != entY && mc.thePlayer.posZ != entZ)) {
                    ep.setCancelled(true);
                }
            }
        }
    }

    private static boolean isPlayer(final Entity entity) {
        return entity instanceof EntityPlayer;
    }
    private static EntityPlayer castToPlayer2(final Entity entity) {
        return (EntityPlayer)entity;
    }
    private boolean isNotLocalPlayer2(final EntityPlayer entityPlayer) {
        return !entityPlayer.equals(this.mc.thePlayer);
    }
    private static void addToSet(final EntityPlayer entityPlayer) {
        AntiBot.entityIdSet.add(entityPlayer.getEntityId());
    }
    private static boolean isOnGround(final EntityPlayer entityPlayer) {
        return entityPlayer.onGround;
    }
    private boolean isNotLocalPlayer(final EntityPlayer entityPlayer) {
        return !entityPlayer.equals(this.mc.thePlayer);
    }
    private static EntityPlayer castToPlayer(final Entity entity) {
        return (EntityPlayer)entity;
    }
    private static boolean isPlayer2(final Entity entity) {
        return entity instanceof EntityPlayer;
    }
    private static void isInAntiBotSet(final EntityPlayer entityPlayer) {
        AntiBot.entityIdSet.add(entityPlayer.getEntityId());
    }
    private static boolean notOnGround(final EntityPlayer entityPlayer) {
        return !entityPlayer.onGround;
    }
    public static List<EntityPlayer> getTabPlayerList() {
        final NetHandlerPlayClient var4 = mc.thePlayer.connection;
        final List<EntityPlayer> list = new ArrayList<>();
        final List players = GuiPlayerTabOverlay.ENTRY_ORDERING.sortedCopy(var4.getPlayerInfoMap());
        for (final Object o : players) {
            final NetworkPlayerInfo info = (NetworkPlayerInfo) o;
            if (info == null) {
                continue;
            }
            list.add(mc.theWorld.getPlayerEntityByName(info.getGameProfile().getName()));
        }
        return list;
    }
    static {
        AntiBot.entityIdSet = Sets.newConcurrentHashSet();
        AntiBot.integerSet2 = Sets.newConcurrentHashSet();
    }
}
