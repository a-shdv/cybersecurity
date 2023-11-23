package com.company.cybersecurity.security;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

@Component
public class CBCUtil {
    private SecretKey secretKey;
    private IvParameterSpec ivParameterSpec;

    public CBCUtil() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128);
        this.secretKey = keyGenerator.generateKey();

        byte[] iv = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        this.ivParameterSpec = new IvParameterSpec(iv);
    }

    public String encrypt(String plaintext) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, this.secretKey, this.ivParameterSpec);

        byte[] encrypted = cipher.doFinal(plaintext.getBytes());
        return new String(Base64.encodeBase64(encrypted), StandardCharsets.UTF_8);
    }

    public String decrypt(String ciphertext) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, this.secretKey, this.ivParameterSpec);

        byte[] decrypted = cipher.doFinal(Base64.decodeBase64(ciphertext.getBytes(StandardCharsets.UTF_8)));
        return new String(decrypted, StandardCharsets.UTF_8);
    }
}
