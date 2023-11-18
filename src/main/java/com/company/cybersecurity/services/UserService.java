package com.company.cybersecurity.services;

import com.company.cybersecurity.exceptions.OldPasswordIsWrongException;
import com.company.cybersecurity.exceptions.UserNotFoundException;
import com.company.cybersecurity.models.Role;
import com.company.cybersecurity.models.User;
import com.company.cybersecurity.repos.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @PostConstruct
    private void postConstruct() {
        User user = Optional.ofNullable(userRepository.findByUsername("user")).orElse(new User("user", bCryptPasswordEncoder.encode("user")));
        user.setRoles(Collections.singletonList(Role.USER));

        User admin = Optional.ofNullable(userRepository.findByUsername("admin")).orElse(new User("admin", bCryptPasswordEncoder.encode("admin")));
        admin.setRoles(Collections.singletonList(Role.ADMIN));

        userRepository.save(user);
        userRepository.save(admin);
    }


    public User findUserById(Long userId) throws UserNotFoundException {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден!"));
    }

    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public boolean saveUser(User user) {
        User userFromDB = userRepository.findByUsername(user.getUsername());

        if (userFromDB != null) {
            return false;
        }

        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setBanned(false);
        user.setRoles(Collections.singletonList(Role.USER));

        userRepository.save(user);
        return true;
    }


    public boolean deleteUser(Long userId) {
        if (userRepository.findById(userId).isPresent()) {
            userRepository.deleteById(userId);
            log.info("User with id " + userId + " has been deleted");
            return true;
        }

        log.info("Cannot delete user with id " + userId);
        return false;
    }

    public boolean changePassword(String oldPassword, String newPassword, User user) throws OldPasswordIsWrongException {
        if (bCryptPasswordEncoder.matches(oldPassword, user.getPassword())) {
            user.setPassword(bCryptPasswordEncoder.encode(newPassword));
            userRepository.save(user);
            return true;
        }
        return false;
    }


    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("Пользователь с именем " + username + "не найден!");
        }
        return user;
    }
}
