package complex.command.impl;

import complex.Complex;
import complex.command.Command;
import complex.module.Module;
import complex.module.ModuleManager;

public class Toggle extends Command {
    public Toggle() {
        super("t", "toggleModules");
    }

    @Override
    public void execute(String[] args) {
        if (args.length != 1) {
            sendChatMessageInfo(".t <Module>");
            return;
        }
        String module = args[0];
        if (module.equalsIgnoreCase("uriel")) {
            sendChatMessage("Enabled toggle setUp");
            return;
        }
        try {
            Complex.getModuleManager().getModule(module).toggle();
            sendChatMessage(Complex.getModuleManager().getModule(module).getName() + " was " + (Complex.getModuleManager().getModule(module).isEnabled() ? "Enabled" : "Disabled"));
        } catch (Exception e) {
            sendChatMessageError("Module Notfound!");
        }
    }
}
