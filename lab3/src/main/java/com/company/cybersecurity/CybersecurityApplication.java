package com.company.cybersecurity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;

@SpringBootApplication
public class CybersecurityApplication {

    public static void main(String[] args) throws NoSuchPaddingException, NoSuchAlgorithmException, IOException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        SpringApplication.run(CybersecurityApplication.class, args);

//        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
//        SecretKeySpec keySpec = new SecretKeySpec(new byte[]{0x10, 0x10, 0x01, 0x04, 0x01, 0x01, 0x01, 0x02}, "DES");
//        IvParameterSpec ivSpec = new IvParameterSpec(new byte[]{0x10, 0x10, 0x01, 0x04, 0x01, 0x01, 0x01, 0x02});
//
//        Cipher cipher = Cipher.getInstance("DES/OFB/NoPadding");
//        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
//
//        // ENCRYPTION
//        FileInputStream inputStream = new FileInputStream("C:\\Users\\Антон\\IdeaProjects\\test\\lab3\\input.txt");
//        FileOutputStream encryptedStream = new FileOutputStream("encrypted.txt");
//        byte[] buffer = new byte[1024];
//        int bytesRead;
//        while ((bytesRead = inputStream.read(buffer)) != -1) {
//            byte[] output = cipher.update(buffer, 0, bytesRead);
//            if (output != null) {
//                encryptedStream.write(output);
//            }
//        }
//        byte[] outputBytes = cipher.doFinal();
//        if (outputBytes != null) {
//            encryptedStream.write(outputBytes);
//        }
//        inputStream.close();
//        encryptedStream.close();
//
//        // DECRYPTION
//        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
//        inputStream = new FileInputStream("encrypted.txt");
//        encryptedStream = new FileOutputStream("decrypted.txt");
//        while ((bytesRead = inputStream.read(buffer)) != -1) {
//            byte[] output = cipher.update(buffer, 0, bytesRead);
//            if (output != null) {
//                encryptedStream.write(output);
//            }
//        }
//        outputBytes = cipher.doFinal();
//        if (outputBytes != null) {
//            encryptedStream.write(outputBytes);
//        }
//        inputStream.close();
//        encryptedStream.close();
    }


}
