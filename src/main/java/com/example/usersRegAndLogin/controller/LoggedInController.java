package com.example.usersRegAndLogin.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


@RestController
@Slf4j
@RequestMapping("loggedIn")
public class LoggedInController {
    @GetMapping("/hello")
    public String loggedInHello(@AuthenticationPrincipal UserDetails auth, HttpServletRequest request){
        Object principal = auth.getUsername();
        System.out.println("The name of loggedIn principal is  " + principal.toString());
        HttpSession httpSession = request.getSession();
        log.info("Fetching the session Id info {}",httpSession.getAttribute("sessionUserName"));
        return "I am logged In persion saying Hello !!";
    }

}
