package complex.command;

import complex.utils.MCUtil;
import net.minecraft.util.text.TextComponentString;

public abstract class Command implements MCUtil {
    public static final String[] code = {"§1", "§2", "§3", "§4", "§5", "§6", "§9", "§a", "§b", "§c", "§d", "§e", "§f"};
    private String name, description;

    public Command(String name, String description){
        this.name = name;
        this.description = description;
    }

    public abstract void execute(String[] args);
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public static void sendChatMessage(String message){
        mc.thePlayer.addChatMessage(new TextComponentString(" §9Complex§8 >> §7" + message));
    }
    public static void sendChatMessageError(String message){
        mc.thePlayer.addChatMessage(new TextComponentString(" §9Complex§8 >> §7" + "§c" + message));
    }
    public static void sendChatMessageInfo(String message){
        mc.thePlayer.addChatMessage(new TextComponentString(" §9Complex§8 >> §7" + "§a" + message));
    }
    public static void sendChatMessageIRC(String IRCMessage) {
        String ChatPrefix = "§9[IRC] §b";
        sendMessage(ChatPrefix + IRCMessage);
    }
    public static void sendMessage(String message){
        mc.thePlayer.addChatMessage(new TextComponentString(message));
    }
    public static void printUsage(){
        String message = null;
        sendChatMessage(message);
    }
}
