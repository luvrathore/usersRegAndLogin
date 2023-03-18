package com.example.usersRegAndLogin.presistance.dao;

import com.example.usersRegAndLogin.model.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    PasswordResetToken findByToken(String token);
}
