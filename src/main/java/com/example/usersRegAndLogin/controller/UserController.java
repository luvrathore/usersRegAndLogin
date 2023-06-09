package com.example.usersRegAndLogin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("users")
public class UserController {

    @GetMapping("hello")
    public ResponseEntity<String> greetMeHello(){
        return ResponseEntity.ok("Hey !! I am saying Hello");
    }
}
