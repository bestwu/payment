package cn.bestwu.pay.payment.loongpay;

import java.io.IOException;
import java.net.URLDecoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import sun.misc.BASE64Decoder;

public class MCipherDecode {

  private static byte[] getSrcBytes(byte[] srcBytes, byte[] wrapKey)
      throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchProviderException {
    SecretKeySpec key = new SecretKeySpec(wrapKey, "DES");

    Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding", "BC");

    cipher.init(Cipher.DECRYPT_MODE, key);

    return cipher.doFinal(srcBytes);
  }


  public static byte[] DecodeBase64String(String base64Src) throws IOException {
    BASE64Decoder de = new BASE64Decoder();
    return de.decodeBuffer(base64Src);

  }

  public static String getDecodeString(String urlString, String key)
      throws IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, NoSuchProviderException {
    key = key.substring(0, 8);
    String tempString = URLDecoder.decode(urlString, "ISO-8859-1");
    String basedString = tempString.replaceAll(",", "+");
    byte[] tempBytes = DecodeBase64String(basedString);
    byte[] tempSrcBytes = getSrcBytes(tempBytes, key.getBytes("ISO-8859-1"));
    return new String(tempSrcBytes, "ISO-8859-1");
  }


}