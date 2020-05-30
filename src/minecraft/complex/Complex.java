package complex;

import complex.command.CommandManager;
import complex.utils.changelog.ChangeLogManager;
import complex.utils.discordrpc.MainRPC;
import complex.utils.login.LoginUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import complex.event.EventManager;
import complex.utils.file.FileManager;
import complex.gui.clickgui.ClickGUI;
import complex.module.Module;
import complex.module.ModuleManager;
import complex.module.data.SettingsManager;
import complex.utils.MCUtil;
import java.io.File;

public enum Complex implements MCUtil {
    instance;

    private static final Logger logger = LogManager.getLogger(".");
    public static String clientName = "Complex";
    public static String version = "beta 0.7";
    public static String author = "Notch";
    public static String authCode = "";
    public static boolean Loggedin;
    private File dataDirectory;

    private MainRPC discordrpc = new MainRPC();
    private SettingsManager settingsManager;
    private static FileManager fileManager;
    private ModuleManager moduleManager;
    private ChangeLogManager changeLogManager;
    private CommandManager commandManager;
    private EventManager eventManager;
    private ClickGUI clickgui;

    public void setup() {
        settingsManager = new SettingsManager();
        (moduleManager = new ModuleManager(Module.class)).setup();
        (changeLogManager = new ChangeLogManager()).setChangeLogs();
        eventManager = new EventManager();
        clickgui = new ClickGUI();
        (commandManager = new CommandManager()).init_Command();
        (fileManager = new FileManager()).loadFiles();
        this.dataDirectory = new File("Complex");
        LoginUtils.loadID();

//        Display.setTitle("ゲイボルク");
        System.out.println("setup");
    }

    public void shutdown() {
        discordrpc.shutdown();
        (fileManager = new FileManager()).saveFiles();

        System.out.println("shutdown");
    }

    public static ModuleManager getModuleManager() {
        return instance.moduleManager;
    }

    public static SettingsManager getSettingsManager() {
        return instance.settingsManager;
    }

    public static EventManager getEventManager() {
        return instance.eventManager;
    }

    public static ClickGUI getClickgui() {
        return instance.clickgui;
    }

    public static FileManager getFileManager() {
        return fileManager;
    }

    public static Logger getLogger() {
        return logger;
    }

    public static File getDataDir() {
        return instance.dataDirectory;
    }

    public static CommandManager getCommandManager() {
        return instance.commandManager;
    }

    public static ChangeLogManager getChangeLogManager() {
        return instance.changeLogManager;
    }

    public static MainRPC getDiscordrpc() {
        return instance.discordrpc;
    }

    public static void setLoggedin(boolean loggedin) {
        Loggedin = loggedin;
    }
}
