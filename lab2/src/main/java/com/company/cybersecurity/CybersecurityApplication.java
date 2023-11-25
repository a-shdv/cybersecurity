package com.company.cybersecurity;
import com.company.cybersecurity.config.StorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class CybersecurityApplication {
    public static void main(String[] args) {
        SpringApplication.run(CybersecurityApplication.class, args);
//        String filePath = "users-credentials.txt";
//        try {
//            MessageDigest digest = MessageDigest.getInstance("SHA-1");
//            byte[] fileBytes = Files.readAllBytes(Paths.get(filePath));
//            byte[] encodedhash = digest.digest(fileBytes);
//
//            StringBuilder hexString = new StringBuilder(2 * encodedhash.length);
//            for (byte b : encodedhash) {
//                String hex = Integer.toHexString(0xff & b);
//                if(hex.length() == 1)
//                    hexString.append('0');
//                hexString.append(hex);
//            }
//            String sha1hash = hexString.toString();
//
//            System.out.println("SHA-1 Hash Code in Hexadecimal Format: " + sha1hash);
//
//        } catch (NoSuchAlgorithmException | IOException e) {
//            e.printStackTrace();
//        }
    }
}
