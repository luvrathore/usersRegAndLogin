package com.example.usersRegAndLogin.util.error;

public class UserAlreadyExistException extends RuntimeException{

    public UserAlreadyExistException(String message){
        super(message);
    }
}
