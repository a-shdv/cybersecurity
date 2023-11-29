package com.company.cybersecurity.utils;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class OFBUtil {
    private SecretKey secretKey;
    private IvParameterSpec ivParameterSpec;

    public OFBUtil() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("DES");
        keyGenerator.init(56);
        this.secretKey = keyGenerator.generateKey();

        byte[] iv = new byte[8];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        this.ivParameterSpec = new IvParameterSpec(iv);
    }

    public static String hashFile(String path) {

        return path;
    }
//
//    public String encrypt(String plaintext) throws Exception {
//        Cipher cipher = Cipher.getInstance("DES/OFB/NoPadding");
//        cipher.init(Cipher.ENCRYPT_MODE, this.secretKey, this.ivParameterSpec);
//
//        byte[] encrypted = cipher.doFinal(plaintext.getBytes());
//        return new String(Base64.encodeBase64(encrypted), StandardCharsets.UTF_8);
//    }
//
//    public String decrypt(String ciphertext) throws Exception {
//        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
//        cipher.init(Cipher.DECRYPT_MODE, this.secretKey, this.ivParameterSpec);
//
//        byte[] decrypted = cipher.doFinal(Base64.decodeBase64(ciphertext.getBytes(StandardCharsets.UTF_8)));
//        return new String(decrypted, StandardCharsets.UTF_8);
//    }
}
