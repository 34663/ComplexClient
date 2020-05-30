package complex.utils.file.files;

import complex.Complex;
import complex.module.Module;
import complex.utils.file.FileManager;

import java.io.*;
import java.util.Iterator;

public class Modules extends FileManager.CustomFile {
    public Modules(String name, boolean Module2, boolean loadOnStart) {
        super(name, Module2, loadOnStart);
    }

    @Override
    public void loadFile() throws IOException {
        BufferedReader variable9 = new BufferedReader(new FileReader(this.getFile()));
        Iterator var2 = Complex.getModuleManager().getModules().iterator();
        String line;

        while((line = variable9.readLine()) != null) {
            String[] arguments = line.split(":");

            for (int i = 0; i < 2; ++i) {
                arguments[i].replace(" ", "");
            }

            for (Module mod : Complex.getModuleManager().getModules()) {
                if (mod == Complex.getModuleManager().getModule(arguments[0])) {
                    mod.setEnabled(Boolean.valueOf(arguments[1]));
                    mod.setKeybind(Integer.valueOf(arguments[2]));
                }
            }
        }

        variable9.close();
        System.out.println("Loaded " + this.getName() + " File!");
    }

    @Override
    public void saveFile() throws IOException {
        PrintWriter mods = new PrintWriter(new FileWriter(this.getFile()));
        Iterator var2 = Complex.getModuleManager().getModules().iterator();

        while(var2.hasNext()) {
            Module mod = (Module)var2.next();
            mods.println(mod.getName() + ":" + mod.isEnabled() + ":" + mod.getKeybind());
        }
        mods.close();
    }
}
