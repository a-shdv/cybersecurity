package com.company.cybersecurity.repositories;

import com.company.cybersecurity.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
