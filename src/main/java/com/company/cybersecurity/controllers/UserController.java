package com.company.cybersecurity.controllers;

import com.company.cybersecurity.configs.CustomAuthenticationFailureHandler;
import com.company.cybersecurity.dtos.ChangePasswordDto;
import com.company.cybersecurity.dtos.ChangePasswordExpiredDto;
import com.company.cybersecurity.dtos.RegistrationDto;
import com.company.cybersecurity.exceptions.OldPasswordIsWrongException;
import com.company.cybersecurity.exceptions.PasswordsMismatchException;
import com.company.cybersecurity.exceptions.UserAlreadyExistsException;
import com.company.cybersecurity.models.User;
import com.company.cybersecurity.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Controller
public class UserController {
    private final UserService userService;
    private final ApplicationContext context;

    @Autowired
    public UserController(UserService userService, ApplicationContext context) {
        this.userService = userService;
        this.context = context;
    }

    @GetMapping("/login")
    public String login(Model model) {
        CustomAuthenticationFailureHandler handler = context.getBean(CustomAuthenticationFailureHandler.class);
        Exception ex = handler.getException();
        String message;

        if (ex != null) {
            if (ex instanceof CredentialsExpiredException) {
                message = "Срок действия пароля пользователя истек. Пожалуйста, смените пароль!";
                model.addAttribute("expirationMessage", message);
            }

            if (ex instanceof BadCredentialsException) {
                message = "Неверное имя пользователя или пароль!";
                model.addAttribute("badCredentialsMessage", message);
            }
        }
        return "users/login";
    }

    @GetMapping("/registration")
    public String registration() {
        return "users/registration";
    }

    @PostMapping("/registration")
    public String registration(@ModelAttribute("registrationDto") RegistrationDto dto, Model model) {
        User userFromDb = userService.findUserByUsername(dto.getUsername());
        String message;
        try {
            boolean isPasswordsMatch = userService.isPasswordsMatch(dto.getPassword(), dto.getConfirmPassword());
            boolean isAlreadyExists = userService.isAlreadyExists(userFromDb);
            if (isPasswordsMatch && !isAlreadyExists) {
                userService.saveUser(RegistrationDto.toUser(dto));
            }
            message = "Пользователь " + dto.getUsername() + " успешно создан!";
        } catch (PasswordsMismatchException passwordMismatchException) {
            message = "Пароли не совпадают!";
            model.addAttribute("message", message);
            return "users/registration";
        } catch (UserAlreadyExistsException userAlreadyExistsException) {
            message = "Такой пользователь уже существует!";
            model.addAttribute("message", message);
            return "users/registration";
        }
        model.addAttribute("message", message);
        return "home";
    }

    @GetMapping("/change-password")
    public String changePassword(@AuthenticationPrincipal User user, Model model) {
        var passwordLastChanged = user.getPasswordLastChanged();
        if (passwordLastChanged != null)
            model.addAttribute("days", ChronoUnit.DAYS.between(passwordLastChanged, LocalDateTime.now()));
        return "users/change-password";
    }

    @GetMapping("/change-password-expired")
    public String changePasswordExpired() {
        return "users/change-password-expired";
    }

    @PostMapping("/change-password-expired")
    public String changePasswordExpired(@ModelAttribute("changePasswordExpiredDto") ChangePasswordExpiredDto dto, Model model) {
        User user = userService.findByEmail(dto.getEmail());

        String message = null;
        try {
            boolean isPasswordsMatch = userService.isPasswordsMatch(dto.getNewPassword(), dto.getConfirmPassword());
            boolean isOldPasswordRight = userService.isOldPasswordRight(dto.getOldPassword(), user);
            if (isPasswordsMatch && isOldPasswordRight) {
                userService.changePassword(dto.getNewPassword(), user);
                message = "Пароль изменен успешно!";
            }
        } catch (OldPasswordIsWrongException oldPasswordIsWrongException) {
            message = "Старый пароль не верный!";
            model.addAttribute("message", message);
            return "users/change-password-expired";
        } catch (PasswordsMismatchException passwordsMismatchException) {
            message = "Пароли не совпадают!";
            model.addAttribute("message", message);
            return "users/change-password-expired";
        }

        model.addAttribute("message", message);
        return "home";
    }

    @GetMapping("/change-username")
    public String changeUsername() {

        return "users/change-username";
    }

    @PostMapping("/change-password")
    public String changePassword(@ModelAttribute("changePasswordDto") ChangePasswordDto dto, @AuthenticationPrincipal User user, Model model) {
        String message = null;
        try {
            boolean isPasswordsMatch = userService.isPasswordsMatch(dto.getNewPassword(), dto.getConfirmPassword());
            boolean isOldPasswordRight = userService.isOldPasswordRight(dto.getOldPassword(), user);
            if (isPasswordsMatch && isOldPasswordRight) {
                userService.changePassword(dto.getNewPassword(), user);
                message = "Пароль изменен успешно!";
            }
        } catch (OldPasswordIsWrongException oldPasswordIsWrongException) {
            message = "Старый пароль не верный!";
            model.addAttribute("message", message);
            return "users/registration";
        } catch (PasswordsMismatchException passwordsMismatchException) {
            message = "Пароли не совпадают!";
            model.addAttribute("message", message);
            return "users/registration";
        }
        model.addAttribute("message", message);
        return "users/change-password";
    }


}
