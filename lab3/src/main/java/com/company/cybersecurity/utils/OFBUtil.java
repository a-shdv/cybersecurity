package com.company.cybersecurity.utils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;

public class OFBUtil {
    private static Cipher cipher;

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

    public static void encryptFile(InputStream inputStream, String encryptedFilePath) throws IOException, IllegalBlockSizeException, BadPaddingException {
        FileOutputStream outputStream = new FileOutputStream(encryptedFilePath);
        byte[] buffer = new byte[inputStream.available()];
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

    public static void decryptFile(String encryptedFilePath, String decryptedFilePath) throws IOException, IllegalBlockSizeException, BadPaddingException {
        FileInputStream inputStream = new FileInputStream(encryptedFilePath);
        FileOutputStream outputStream = new FileOutputStream(decryptedFilePath);
        byte[] buffer = new byte[inputStream.available()];
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
}