package com.company.cybersecurity.utils;

import org.springframework.beans.factory.annotation.Value;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.security.*;

public class OFBUtil {
    private static Cipher cipher;
    @Value("${encryptedFileUpload.path}")
    public static String encryptedFilePath;

    static {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        SecretKeySpec keySpec = new SecretKeySpec(new byte[]{0x10, 0x10, 0x01, 0x04, 0x01, 0x01, 0x01, 0x02}, "DES");
        IvParameterSpec ivSpec = new IvParameterSpec(new byte[]{0x10, 0x10, 0x01, 0x04, 0x01, 0x01, 0x01, 0x02});

        try {
            cipher = Cipher.getInstance("DES/OFB/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException |
                 InvalidAlgorithmParameterException e) {
            e.getMessage();
        }
    }

    public static void encryptFile(String path) throws IOException, IllegalBlockSizeException, BadPaddingException {
        FileInputStream inputStream = new FileInputStream(path);
        FileOutputStream outputStream = new FileOutputStream("C:\\Users\\Антон\\IdeaProjects\\trash\\uploads\\encrypted\\test.txt");
        byte[] buffer = new byte[(int) Paths.get(path).toFile().length()];
        int numOfBytes;
        while ((numOfBytes = inputStream.read(buffer)) != -1) {
            byte[] output = cipher.update(buffer, 0, numOfBytes);
            if (output != null) {
                outputStream.write(output);
            }
        }
        byte[] outputBytes = cipher.doFinal();
        if (outputBytes != null) {
            outputStream.write(outputBytes);
        }
        inputStream.close();
        outputStream.close();
    }

    public static void decryptFile(String path) throws IOException, IllegalBlockSizeException, BadPaddingException {
        FileInputStream inputStream = new FileInputStream("encrypted.txt");
        FileOutputStream outputStream = new FileOutputStream("decrypted.txt");
        byte[] buffer = new byte[1024];
        int bytesRead;
        byte[] outputBytes;

        while ((bytesRead = inputStream.read(buffer)) != -1) {
            byte[] output = cipher.update(buffer, 0, bytesRead);
            if (output != null) {
                outputStream.write(output);
            }
        }
        outputBytes = cipher.doFinal();
        if (outputBytes != null) {
            outputStream.write(outputBytes);
        }
        inputStream.close();
        outputStream.close();
    }

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
