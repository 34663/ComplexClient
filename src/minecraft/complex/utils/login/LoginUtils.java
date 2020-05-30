package complex.utils.login;

import complex.Complex;
import complex.event.EventManager;
import complex.module.Module;
import complex.utils.file.FileUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.util.text.TextFormatting;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LoginUtils {
    private static final File ID_DIR = FileUtils.getConfigFile("id");
    private static final String key = "Zx" + Math.log(2) / 3;

    public static void saveID(String id) {
        List<String> fileContent = new ArrayList<>();
        fileContent.add(id);
        FileUtils.write(ID_DIR, fileContent, true);
    }

    public static void loadID() {
        try {
            List<String> fileContent = FileUtils.read(ID_DIR);
            if (fileContent.toString().equalsIgnoreCase("["+getHWID()+"]")) {
                Complex.setLoggedin(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Complex.setLoggedin(false);
        }
    }

    public static String LoginThread() {
        String s = "";
        byte[] cipher_byte;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            s = obfuscate(getHWID());
            md.update(s.getBytes());
            cipher_byte = md.digest();
            StringBuilder sb = new StringBuilder(2 * cipher_byte.length);
            for(byte b: cipher_byte) {
                sb.append(String.format("%02x", b&0xff) );
            }
            s = sb.toString();
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return s;
    }

    public static String getHWID() throws NoSuchAlgorithmException, UnsupportedEncodingException {
        String s = "";
        final String main = String.valueOf(System.getenv("PROCESSOR_IDENTIFIER")) + System.getenv("COMPUTERNAME") + System.getProperty("user.name").trim();
        final byte[] bytes = main.getBytes("UTF-8");
        final MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        final byte[] md5 = messageDigest.digest(bytes);
        int i = 0;
        byte[] array;
        for (int length = (array = md5).length, j = 0; j < length; ++j) {
            final byte b = array[j];
            s = String.valueOf(s) + Integer.toHexString((b & 0xFF) | 0x300).substring(0, 3);
            if (i != md5.length - 1) {
                s = String.valueOf(s) + "-";
            }
            ++i;
        }
        return s;
    }

    public static String obfuscate(String s) {
        char[] result = new char[s.length()];
        for (int i = 0; i < s.length(); i++) {
            result[i] = (char) (s.charAt(i) + key.charAt(i % key.length()));
        }
        return new String(result);
    }

    public static String unobfuscate(String s) {
        char[] result = new char[s.length()];
        for (int i = 0; i < s.length(); i++) {
            result[i] = (char) (s.charAt(i) - key.charAt(i % key.length()));
        }
        return new String(result);
    }
}
