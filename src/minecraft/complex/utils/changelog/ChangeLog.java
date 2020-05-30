package complex.utils.changelog;

import net.minecraft.util.text.TextFormatting;

public class ChangeLog {
    private String changeName;
    private final ChangelogType type;

    public ChangeLog(String name, ChangelogType type) {
        this.changeName = name;
        this.type = type;

        switch (type) {
            case NONE:
                changeName = ": " + changeName;
                break;
            case ADD:
                changeName = "[+] Add " + changeName;
                break;
            case DELETE:
                changeName = "[-] Del " + changeName;
                break;
            case IMPROVED:
                changeName = "[/] Fix " + changeName;
                break;
            case PROTOTYPE:
                changeName = "[?] Prot " + changeName;
        }
    }

    public String getChangeName() {
        return changeName;
    }

    public ChangelogType getType() {
        return type;
    }
}
