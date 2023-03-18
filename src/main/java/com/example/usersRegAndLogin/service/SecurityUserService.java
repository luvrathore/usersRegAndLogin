package com.example.usersRegAndLogin.service;

import com.example.usersRegAndLogin.model.PasswordResetToken;
import com.example.usersRegAndLogin.presistance.dao.PasswordResetTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;

@Component
public class SecurityUserService implements ISecurityUserService {

    @Autowired
    private PasswordResetTokenRepository passwordTokenRepository;

    @Override
    public String validatePasswordResetToken(String token) {

        final PasswordResetToken passToken = passwordTokenRepository.findByToken(token);

        return !isTokenFound(passToken) ? "invalidToken"
                : isTokenExpired(passToken) ? "expired"
                : null;
    }

    private boolean isTokenFound(PasswordResetToken passToken) {
        return passToken != null;
    }

    private boolean isTokenExpired(PasswordResetToken passToken) {
        final Calendar cal = Calendar.getInstance();
        return passToken.getExpiryDate().before(cal.getTime());
    }
}
