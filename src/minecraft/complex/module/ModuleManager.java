package complex.module;

import complex.Complex;
import complex.module.data.AbstractManager;
import complex.module.data.Setting;
import complex.module.impl.combat.*;
import complex.module.impl.exploit.*;
import complex.module.impl.hud.*;
import complex.module.impl.misc.*;
import complex.module.impl.misc.NameProtect;
import complex.module.impl.movement.*;
import complex.module.impl.player.*;
import complex.module.impl.visual.*;

import java.util.ArrayList;

public class ModuleManager extends AbstractManager {
    public static ArrayList<Module> modules = new ArrayList<Module>();
    private boolean setup;

    public ModuleManager(Class clazz) {
        super(clazz, 0);
    }

    @Override
    public void setup() {
        addModule(new FastPlace());
        addModule(new HUD());
        addModule(new ClickGui());
        addModule(new Velocity());
        addModule(new Criticals());
        addModule(new Step());
        addModule(new Sprint());
        addModule(new NoSlowdown());
        addModule(new KeepSprint());
        addModule(new AutoArmor());
        addModule(new AntiBot());
        addModule(new Speed());
        addModule(new InventoryMove());
        addModule(new Fly());
        addModule(new NoFall());
        addModule(new AutoRespawn());
        addModule(new SpeedMine());
        addModule(new NoRotation());
        addModule(new Chams());
        addModule(new NoWeb());
        addModule(new ESP());
        addModule(new Aura());
        addModule(new LongJump());
        addModule(new Skeletons());
        addModule(new AntiVoid());
        addModule(new FastLadder());
        addModule(new ChestStealer());
        addModule(new Clipper());
        addModule(new Spammer());
        addModule(new NoHurtcam());
        addModule(new KillSults());
        addModule(new ChestESP());
        addModule(new ItemPhysic());
        addModule(new DMGIndicator());
        addModule(new DiscordRP());
        addModule(new Plugins());
        addModule(new Jesus());
        addModule(new TargetStrafe());
        addModule(new ArmorStatus());
        addModule(new PingSpoof());
        addModule(new Tracer());
        addModule(new BlockInfo());
        addModule(new NameTags());
        addModule(new AutoSword());
        addModule(new CivBreak());
        addModule(new Scaffold());
        addModule(new DickESP());
        addModule(new VanishDetector());
        addModule(new PropHuntESP());
        addModule(new RodAimBot());
        addModule(new ShowInvis());
        addModule(new FastUse());
        addModule(new Glide());
        addModule(new Crasher());
        addModule(new InfiniteAura());
        addModule(new ArmAngle());
        addModule(new ChatBypass());
        addModule(new ScoreBruh());
        addModule(new Ambience());
        addModule(new TestScaffold());
        addModule(new CrashItem());
        addModule(new ViewClip());
        addModule(new Animations());
        addModule(new Blink());
        addModule(new WTap());
        addModule(new Brightness());
        addModule(new Sneak());
        addModule(new NBTDebug());
        addModule(new BCExploit());
        addModule(new TextureBypass());
        addModule(new PayloadDebug());
        addModule(new AntiVPNBlock());
        addModule(new DrawPlayer());
        addModule(new BetterSounds());
        addModule(new AimAssist());
        addModule(new AntiRotation());
        addModule(new Outline());
        addModule(new DRemake());
        addModule(new TEST());

        setup = true;

        if (!getModule("HUD").isEnabled()) {
            getModule("HUD").toggle();
        }
    }

    public void addModule(Module mod){
        modules.add(mod);
    }
    public Module getModule(String name) {
        for (Module module : getModules()) {
            if (module.getName().toLowerCase().equals(name.toLowerCase())) {
                return module;
            }
        }
        return null;
    }
    public boolean isEnabled(String clazz) {
        Module module = getModule(clazz);
        return module != null && module.isEnabled();
    }
    public ArrayList<Module> getModules(){
        return modules;
    }
    public void getModuleCategory(Category cat) {
    }
    public ArrayList<Module> getModuleByCategory(Category cat) {
        ArrayList<Module> mods = new ArrayList<>();
        for (Module module : Complex.getModuleManager().getModules()) {
            if(module.getCategory() == cat && !module.isHidden()){
                mods.add(module);
            }
        }
        return mods.isEmpty()?null:mods;
    }
    public boolean hasDoubleValue(final Module mod) {
        for (final Setting s : Complex.getSettingsManager().getSettingsByMod(mod)) {
            final Double doub = s.getValDouble();
            final Boolean bool = s.getValBoolean();
            final String stri = s.getValString();
            if (doub != null && s.getMax() != 0.0) {
                return true;
            }
        }
        return false;
    }
    public boolean hasStringVal(final Module mod) {
        try {
            for (final Setting s : Complex.getSettingsManager().getSettingsByMod(mod)) {
                final Double doub = s.getValDouble();
                final Boolean bool = s.getValBoolean();
                final String stri = s.getValString();
                if (stri != null) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
    public boolean hasBooleanVal(final Module mod) {
        for (final Setting s : Complex.getSettingsManager().getSettingsByMod(mod)) {
            final Double doub = s.getValDouble();
            final Boolean bool = s.getValBoolean();
            final String stri = s.getValString();
            if (bool != null && s.getMax() == 0.0 && s.getValString() == null) {
                return true;
            }
        }
        return false;
    }

    public boolean isSetup() {
        return setup;
    }
}
