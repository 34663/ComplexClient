package complex.module;

import complex.Complex;
import complex.event.EventManager;
import complex.utils.MCUtil;
import complex.utils.file.FileUtils;
import complex.utils.render.Colors;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Module implements MCUtil, Toggleable {
    private static final File MODULE_DIR = FileUtils.getConfigFile("modules");

    private String name;
    private String description;
    private String displayName;
    private int keybind;
    private int color;
    private int enable = 0;
    private int animation, animation2;
    private boolean enabled;
    private boolean isHidden;
    private Category category;

    public Module(String name, String description, int keybind, int color, Category c) {
        this.name = name;
        this.description = description;
        this.keybind = keybind;
        this.color = color;
        this.category = c;

        setup();
//        loadStatus();
    }

    @Override
    public void toggle() {
        enabled = !enabled;

        if (Complex.getModuleManager().isSetup()) {
            saveStatus();
        }
        if (enabled) {
            EventManager.register(this);
            onEnable();
        } else {
            EventManager.unregister(this);
            onDisable();
        }
    }

    public static void saveStatus() {
        List<String> fileContent = new ArrayList<>();
        for (Module module : Complex.getModuleManager().getModules()) {
            fileContent.add(String.format("%s:%s:%s:%s", module.getName(), module.isEnabled(), module.getKeybind(), module.isHidden));
        }
        FileUtils.write(MODULE_DIR, fileContent, true);
    }

    public static void loadStatus() {
        try {
            List<String> fileContent = FileUtils.read(MODULE_DIR);
            for (String line : fileContent) {
                String[] split = line.split(":");
                String displayName = split[0];
                for (Module module : Complex.getModuleManager().getModules()) {
                    if (module.getName().equalsIgnoreCase(displayName)) {
                        String strEnabled = split[1];
                        boolean enabled = Boolean.parseBoolean(strEnabled);
                        String key = split[2];
                        module.setKeybind(Integer.parseInt(key));
                        if (split.length == 4) {
                            module.isHidden = Boolean.parseBoolean(split[3]);
                        }
                        if (enabled && !module.isEnabled()) {
                            module.enabled = true;
                            EventManager.register(module);
                            module.onEnable();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onEnable() {}

    public int getEnableTime() {
        return enable;
    }

    public void setEnableTime(int x) {
        this.enable = x;
    }

    @Override
    public void onDisable() {}

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getCategoryColor() {
        switch (category) {
            case Combat:
                return Colors.getColor(247, 77, 77);
            case Movement:
                return Colors.getColor(77, 117, 247);
            case Player:
                return Colors.getColor(255, 255, 84);
            case Visual:
                return Colors.getColor(58, 255, 104);
            case Exploit:
                return Colors.getColor(255, 150, 30);
        }
        return 0;
    }

    public Category getCategory() {
        return this.category;
    }

    public boolean isCategory(Category c){
        return c == this.category;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getKeybind() {
        return keybind;
    }

    public void setKeybind(int keybind) {
        this.keybind = keybind;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public boolean shouldRegister() {
        return true;
    }

    public int getAnimation() {
        return animation;
    }

    public void setAnimation(int animation) {
        this.animation = animation;
    }

    public int getAnimation2() {
        return animation2;
    }

    public void setAnimation2(int animation2) {
        this.animation2 = animation2;
    }

    public void setup() {}
}
