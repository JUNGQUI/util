package com.jk.util.encrypt;

import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.tomcat.util.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;

public class EncodeDecode {
    String MODE = "AES/CBC/PKCS5Padding";
    String encode = "UTF-8";

    SecretKey secretKey = new SecretKeySpec("1234567891234567".getBytes(), "AES");
    IvParameterSpec ivParameterSpec = new IvParameterSpec("1234567891234567".getBytes());

    FastDateFormat FDF = FastDateFormat.getInstance("yyyyMMddHHmm");

    public void sample() {
        String s = "2c9f9571635d618a01635d7ff3b2026a";

        Calendar calendar = Calendar.getInstance();
        String date = FDF.format(calendar.getTime());

        String encoded = encryptBase(date.substring(0, 4) + s + date.substring(4));
        String decoded = decryptBase(encoded);

        String encoded2 = encryptForToken(date.substring(0, 4) + s + date.substring(4));
        String decoded2 = decryptToken(encoded2);
    }

    private String encryptBase(String str) {
        byte[] raw = encrypt(str.getBytes());
        try {
            return new String(Base64.encodeBase64(raw), encode);
        } catch (Exception ex) {
            return "";
        }
    }

    private String decryptBase(String str) {
        try {
            return decrypt(Base64.decodeBase64(str.getBytes(encode)));
        } catch (Exception ex) {
            return "";
        }
    }

    private String decrypt(byte[] org) {
        try {
            Cipher cipher = Cipher.getInstance(MODE);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
            return new String(cipher.doFinal(org), encode);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private byte[] encrypt(byte[] org) {
        try {
            Cipher cipher = Cipher.getInstance(MODE);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);
            return cipher.doFinal(org);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private String encryptForToken(String userId) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);
            return new String(Base64.encodeBase64(cipher.doFinal(userId.getBytes())), StandardCharsets.UTF_8);
        } catch (Exception ex) {
            return "";
        }
    }

    private String decryptToken(String token) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);

            return new String (
                    cipher.doFinal(
                            Base64.decodeBase64(
                                    token.getBytes(StandardCharsets.UTF_8))),
                    StandardCharsets.UTF_8
            );
        } catch (Exception ex) {
            return "";
        }
    }
}
