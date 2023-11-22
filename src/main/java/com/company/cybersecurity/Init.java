package com.company.cybersecurity;

import com.company.cybersecurity.models.User;
import com.company.cybersecurity.security.AESUtil;
import com.company.cybersecurity.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationContextInitializedEvent;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Comparator;
import java.util.stream.Collectors;

@Component
@Slf4j
public class Init {
    private final UserService userService;
    private final AESUtil aesUtil;
    public final static String encryptedFilePath = System.getProperty("user.dir") + "/users-credentials.txt";
    public final static String decryptedFilePath = System.getProperty("user.dir") + "/temp.txt";

    @Autowired
    public Init(UserService userService, AESUtil aesUtil) {
        this.userService = userService;
        this.aesUtil = aesUtil;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void run() throws IOException {
        /*Get users credentials*/
        String content = userService.findAllUsers()
                .stream()
                .sorted(Comparator.comparing(User::getId))
                .map(User::toString)
                .collect(Collectors.joining(System.lineSeparator()));

        String encryptedContent = null;
        String decryptedContent = null;

        /*Encryption and decryption*/
        try {
            encryptedContent = aesUtil.encrypt(content);
            decryptedContent = aesUtil.decrypt(encryptedContent);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        File encryptedFile = new File(encryptedFilePath);
        File decryptedFile = new File(decryptedFilePath);

        encryptedFile.createNewFile();
        decryptedFile.createNewFile();

        /*Perm file*/
        try (FileWriter writer = new FileWriter(encryptedFilePath)) {
            writer.write(encryptedContent);
            writer.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        /*Temp file*/
        try (FileWriter writer = new FileWriter(decryptedFilePath)) {
            writer.write(decryptedContent);
            writer.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        decryptedFile.deleteOnExit();
    }
}