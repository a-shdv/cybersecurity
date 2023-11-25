package com.company.cybersecurity.controllers;

import com.company.cybersecurity.configs.CustomAuthenticationFailureHandler;
import com.company.cybersecurity.dtos.*;
import com.company.cybersecurity.exceptions.*;
import com.company.cybersecurity.models.User;
import com.company.cybersecurity.services.UserService;
import liquibase.pro.packaged.S;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
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
        CustomAuthenticationFailureHandler failureHandler = context.getBean(CustomAuthenticationFailureHandler.class);
        Exception ex = failureHandler.getException();
        String message;

        model.asMap().clear();
        if (ex != null) {
            if (ex instanceof CredentialsExpiredException) {
                message = "Срок действия пароля пользователя истек. Пожалуйста, смените пароль!";
                model.addAttribute("expirationMessage", message);
            }

            if (ex instanceof BadCredentialsException) {
                message = "Неверное имя пользователя или пароль!";
                model.addAttribute("badCredentialsMessage", message);
            }

            if (ex instanceof DisabledException) {
                message = "Ваше имя пользователя или пароль не проходит правила модерации. Пожалуйста, проверьте, содержит ли ваш пароль строчные или прописные буквы, а также знаки арифметических операций. Длина пароля также должна быть >= 3-х символов. Если это не помогло, то, пожалуйста, смените имя пользователя.";
                model.addAttribute("disabledMessage", message);
            }

            if (ex instanceof LockedException) {
                message = "К сожалению, ваш аккаунт заблокирован из-за неподобающего поведения!";
                model.addAttribute("lockedMessage", message);
            }
            failureHandler.setException(null);
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
        } catch (WrongPasswordFormatException e) {
            message = "Пароль должен содержать строчные, прописные буквы, а также знаки арифметических операций, и должен быть > 3-х символов!";
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

    @PostMapping("/change-password")
    public String changePassword(@ModelAttribute("changePasswordDto") ChangePasswordDto dto, @AuthenticationPrincipal User user, Model model) {
        String message = null;
        model.asMap().clear();
        try {
            boolean isPasswordsMatch = userService.isPasswordsMatch(dto.getNewPassword(), dto.getConfirmPassword());
            boolean isOldPasswordRight = userService.isOldPasswordRight(dto.getOldPassword(), user);
            if (isPasswordsMatch && isOldPasswordRight) {
                userService.changePassword(dto.getNewPassword(), user);
                message = "Пароль изменен успешно!";
                model.addAttribute("successMessage", message);
            }
        } catch (OldPasswordIsWrongException oldPasswordIsWrongException) {
            message = "Старый пароль не верный!";
            model.addAttribute("message", message);
        } catch (PasswordsMismatchException passwordsMismatchException) {
            message = "Пароли не совпадают!";
            model.addAttribute("message", message);
        } catch (WrongPasswordFormatException e) {
            message = "Пароль должен содержать строчные, прописные буквы, а также знаки арифметических операций, и должен быть > 3-х символов!";
            model.addAttribute("message", message);
        }
        return "users/change-password";
    }

    @GetMapping("/change-password-expired")
    public String changePasswordExpired() {
        return "users/change-password-expired";
    }

    @PostMapping("/change-password-expired")
    public String changePasswordExpired(@ModelAttribute("changePasswordExpiredDto") ChangePasswordExpiredDto dto, Model model) {
        User user;

        String message = null;
        try {
            user = userService.findUserByEmail(dto.getEmail());
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
        } catch (UserNotFoundException userNotFoundException) {
            message = "Пользователь не найден!";
            model.addAttribute("message", message);
            return "users/change-password-expired";
        } catch (WrongPasswordFormatException e) {
            message = "Пароль должен содержать строчные, прописные буквы, а также знаки арифметических операций, и должен быть > 3-х символов!";
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

    @PostMapping("/change-username")
    public String changeUsername(@ModelAttribute("changeUsernameDto") ChangeUsernameDto dto, Model model) {
        String message;
        User userFromDb;
        try {
            userFromDb = userService.findUserByEmail(dto.getEmail());
            userService.changeUsername(dto.getNewUsername(), userFromDb);
            userService.enableByUsername(userFromDb.getUsername());
            message = "Имя пользователя успешно изменено!";
            model.addAttribute("message", message);
        } catch (UserNotFoundException e) {
            message = "Пользователь не найден!";
            model.addAttribute("message", message);
        }

        return "users/change-username";
    }

    @GetMapping("/change-password-decline")
    public void changePasswordDecline() {
        System.exit(0);
    }

    @GetMapping("/about-us")
    public String aboutUs() {
        return "about-us";
    }
}
