package complex.utils.changelog;

import java.util.ArrayList;

public class ChangeLogManager {
    public static ArrayList<ChangeLog> changeLogs = new ArrayList<>();

    public void setChangeLogs() {
        changeLogs.add(new ChangeLog("Build Beta0.7", ChangelogType.NONE));
        changeLogs.add(new ChangeLog("Outline", ChangelogType.ADD));
        changeLogs.add(new ChangeLog("AimAssist", ChangelogType.ADD));
        changeLogs.add(new ChangeLog("BetterSounds", ChangelogType.ADD));
        changeLogs.add(new ChangeLog("DrawPlayer", ChangelogType.ADD));
        changeLogs.add(new ChangeLog("Fly", ChangelogType.IMPROVED));
        changeLogs.add(new ChangeLog("Speed", ChangelogType.IMPROVED));
        changeLogs.add(new ChangeLog("Animations", ChangelogType.IMPROVED));
        changeLogs.add(new ChangeLog("Criticals", ChangelogType.IMPROVED));
        changeLogs.add(new ChangeLog("ProphuntESP", ChangelogType.IMPROVED));
        changeLogs.add(new ChangeLog("DownWardScaffold", ChangelogType.DELETE));
        changeLogs.add(new ChangeLog("BangeeCordExploit", ChangelogType.PROTOTYPE));
        changeLogs.add(new ChangeLog("AntiVPNBlocker", ChangelogType.PROTOTYPE));
    }

    public ArrayList<ChangeLog> getChangeLogs() {
        return changeLogs;
    }
}
