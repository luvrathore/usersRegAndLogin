package com.example.usersRegAndLogin.presistance.dao;

import com.example.usersRegAndLogin.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    VerificationToken findByToken(String token);
}
