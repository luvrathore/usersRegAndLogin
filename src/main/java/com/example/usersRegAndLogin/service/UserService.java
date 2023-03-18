package com.example.usersRegAndLogin.service;

import com.example.usersRegAndLogin.model.PasswordResetToken;
import com.example.usersRegAndLogin.model.User;
import com.example.usersRegAndLogin.model.UserDto;
import com.example.usersRegAndLogin.model.VerificationToken;
import com.example.usersRegAndLogin.presistance.dao.PasswordResetTokenRepository;
import com.example.usersRegAndLogin.presistance.dao.RoleRepository;
import com.example.usersRegAndLogin.presistance.dao.UserRepository;
import com.example.usersRegAndLogin.presistance.dao.VerificationTokenRepository;
import com.example.usersRegAndLogin.util.error.UserAlreadyExistException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.Calendar;

@Service
@Transactional
public class UserService implements IUserService{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private VerificationTokenRepository tokenRepository;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    public static final String TOKEN_INVALID = "invalidToken";
    public static final String TOKEN_EXPIRED = "expired";
    public static final String TOKEN_VALID = "valid";

    @Override
    public User registerNewUserAccount(UserDto accountDto) {

        if (emailExists(accountDto.getEmail())) {
            throw new UserAlreadyExistException("There is an account with that email address: " + accountDto.getEmail());
        }

        final User user = new User();

        user.setFirstName(accountDto.getFirstName());
        user.setLastName(accountDto.getLastName());
        user.setPassword(passwordEncoder.encode(accountDto.getPassword()));
        user.setEmail(accountDto.getEmail());
        user.setUsing2FA(accountDto.isUsing2FA());
        user.setRoles(Arrays.asList(roleRepository.findByName("ROLE_USER")));
        return userRepository.save(user);
    }

    @Override
    public void createVerificationTokenForUser(User user, String token) {
        final VerificationToken myToken = new VerificationToken(token, user);
        tokenRepository.save(myToken);
    }

    @Override
    public String validateVerificationToken(String token) {
        final VerificationToken verificationToken = tokenRepository.findByToken(token);
        if (verificationToken == null) {
            return TOKEN_INVALID;
        }
        final User user = verificationToken.getUser();
        final Calendar cal = Calendar.getInstance();

        if ((verificationToken.getExpiryDate()
                .getTime() - cal.getTime()
                .getTime()) <= 0) {
            tokenRepository.delete(verificationToken);
            return TOKEN_EXPIRED;
        }
        user.setEnabled(true);
        userRepository.save(user);
        return TOKEN_VALID;
    }

    @Override
    public User getUser(String verificationToken) {
        final VerificationToken token = tokenRepository.findByToken(verificationToken);
        if (token != null) {
            return token.getUser();
        }
        return null;
    }

    private boolean emailExists(final String email) {
        return userRepository.findByEmail(email) != null;
    }

    @Override
    public User findUserByEmail(final String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void createPasswordResetTokenForUser(final User user, final String token) {
        final PasswordResetToken myToken = new PasswordResetToken(token, user);
        passwordResetTokenRepository.save(myToken);
    }

    @Override
    public boolean checkIfValidOldPassword(User user, String oldPassword) {
        return passwordEncoder.matches(oldPassword, user.getPassword());
    }

    @Override
    public void changeUserPassword(User user, String password) {
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }
}
