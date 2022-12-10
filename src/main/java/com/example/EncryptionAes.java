package com.example;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

//TODO create constructor with key, each device will have separated key, JavaApp<->ACS != ACS<->ACQ
public class EncryptionAes {

    private final String key = "ThWmZq4t7w!z%C*F-JaNdRgUjXn2r5u8";

    public String encrypt(String message) {
        try {
            SecretKey key = getKeyFromPassword(this.key, "someSalt");
            IvParameterSpec iv = new IvParameterSpec(new byte[16]);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key, iv);
            byte[] plainText = message.getBytes(StandardCharsets.UTF_8);
            byte[] encryptedText = cipher.doFinal(plainText);
            String encryptedString = Base64.getEncoder().encodeToString(encryptedText);

            return encryptedString;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "exit";
    }
    private static SecretKey getKeyFromPassword(String password, String salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 65536, 256);
        return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
    }

    public String decrypt(String encrypt) {
        try {

            SecretKey key = getKeyFromPassword(this.key, "someSalt");
            IvParameterSpec iv = new IvParameterSpec(new byte[16]);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key, iv);
            byte[] encryptedText = Base64.getDecoder().decode(encrypt);
            byte[] plainText = cipher.doFinal(encryptedText);
            String decryptedMessage = new String(plainText);
            return decryptedMessage;

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return "exit";
    }

}
