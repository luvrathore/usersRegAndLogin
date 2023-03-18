package com.example.usersRegAndLogin.model;

import com.example.usersRegAndLogin.model.annotations.ValidPassword;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordDto {

    private String oldPassword;

    private  String token;

    @ValidPassword
    private String newPassword;
}
