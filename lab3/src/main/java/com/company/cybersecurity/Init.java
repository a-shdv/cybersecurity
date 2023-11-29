package com.company.cybersecurity;

import com.company.cybersecurity.models.User;

import com.company.cybersecurity.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.stream.Collectors;

@Component
@Slf4j
public class Init {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    public final static String filePath = System.getProperty("user.dir") + "/temp.txt";

    @Autowired
    public Init(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void run() throws IOException {
        /*Get users credentials*/
        String content = userService.findAll()
                .stream()
                .sorted(Comparator.comparing(User::getId))
                .map(User::toString)
                .collect(Collectors.joining(System.lineSeparator()));

        File file = new File(filePath);

        /*Temp file*/
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(content);
            writer.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        file.deleteOnExit();
    }
}