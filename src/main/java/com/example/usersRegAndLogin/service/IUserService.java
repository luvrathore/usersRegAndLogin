package com.example.usersRegAndLogin.service;

import com.example.usersRegAndLogin.model.User;
import com.example.usersRegAndLogin.model.UserDto;

public interface IUserService {

    User registerNewUserAccount(UserDto accountDto);

    void createVerificationTokenForUser(User user, String token);

    String validateVerificationToken(String token);

    User getUser(String verificationToken);

    User findUserByEmail(final String email);

    void createPasswordResetTokenForUser(final User user, final String token);

    boolean checkIfValidOldPassword(User user, String password);

    void changeUserPassword(User user, String password);
}
