package com.example.usersRegAndLogin.presistance.dao;

import com.example.usersRegAndLogin.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);
}
