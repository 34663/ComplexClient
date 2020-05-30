package complex.utils.font;

import java.awt.Font;
 import java.io.InputStream;
 import net.minecraft.client.Minecraft;
 import net.minecraft.client.resources.IResource;
 import net.minecraft.util.ResourceLocation;

public abstract class FontLoaders {
    public static CFontRenderer smallTahoma = new CFontRenderer(getTahoma(11), false, false);
    public static CFontRenderer Tahoma12 = new CFontRenderer(getTahoma(12), true, true);
    public static CFontRenderer Tahoma15 = new CFontRenderer(getTahoma(15), true, true);
    public static CFontRenderer Tahoma20 = new CFontRenderer(getTahoma(20), true, true);
    public static CFontRenderer Tahoma22 = new CFontRenderer(getTahoma(22), true, true);
    public static CFontRenderer Tahoma25 = new CFontRenderer(getTahoma(25), true, true);
    public static CFontRenderer comfortar20 = new CFontRenderer(getComfortaa(20), true, true);
    public static CFontRenderer comfortar15 = new CFontRenderer(getComfortaa(15), true, true);
    public static CFontRenderer default20 = new CFontRenderer(getDefault(20), true, true);
    public static CFontRenderer default15 = new CFontRenderer(getDefault(15), true, true);
    public static CFontRenderer default18 = new CFontRenderer(getDefault(18), true, true);
    public static CFontRenderer default16 = new CFontRenderer(getDefault(16), true, true);
    public static CFontRenderer default25 = new CFontRenderer(getDefault(25), true, true);

    private static Font getTahoma(int size) {
        Font font;
        try {
            InputStream is = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("complex/font/Tahoma.ttf")).getInputStream();
            font = Font.createFont(0, is);
            font = font.deriveFont(0, size);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error loading font");
            font = new Font("default", 0, size);
        }
        return font;
    }
    private static Font getComfortaa(int size) {
        Font font;
        try {
            InputStream is = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("complex/font/Comfortar.ttf")).getInputStream();
            font = Font.createFont(0, is);
            font = font.deriveFont(0, size);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error loading font");
            font = new Font("default", 0, size);
        }
        return font;
    }
    private static Font getDefault(int size) {
        Font font;
        font = new Font("default", 0, size);
        return font;
    }
}