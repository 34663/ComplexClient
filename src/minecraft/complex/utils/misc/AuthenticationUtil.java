package complex.utils.misc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.function.Consumer;
import javax.crypto.spec.SecretKeySpec;

public class AuthenticationUtil {
   public static int authListPos;

   public static String getMD5Hash(String text) throws NoSuchAlgorithmException {
      MessageDigest digest = MessageDigest.getInstance("MD5");
      byte[] hash = digest.digest(text.getBytes(StandardCharsets.UTF_8));
      return bytesToHex(hash);
   }

   private static String bytesToHex(byte[] hash) {
      StringBuffer hexString = new StringBuffer();

      for(int i = 0; i < hash.length; ++i) {
         String hex = Integer.toHexString(255 & hash[i]);
         if (hex.length() == 1) {
            hexString.append('0');
         }

         hexString.append(hex);
      }

      return hexString.toString();
   }
}
