package com.example.usersRegAndLogin.controller;

import com.example.usersRegAndLogin.model.PasswordDto;
import com.example.usersRegAndLogin.model.User;
import com.example.usersRegAndLogin.model.UserDto;
import com.example.usersRegAndLogin.registration.OnRegistrationCompleteEvent;
import com.example.usersRegAndLogin.service.IUserService;
import com.example.usersRegAndLogin.util.GenericResponse;
import com.example.usersRegAndLogin.util.error.InvalidOldPasswordException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Locale;
import java.util.UUID;

@RestController
public class RegistrationController {

    @Autowired
    private IUserService userService;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private MessageSource messages;

    @Autowired
    private Environment env;

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());


    @PostMapping("/user/registration")
    public GenericResponse registerUserAccount(@RequestBody @Valid final UserDto accountDto, final HttpServletRequest request) {
        LOGGER.debug("Registering user account with information: {}", accountDto);

        final User registered = userService.registerNewUserAccount(accountDto);
        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(registered, request.getLocale(), getAppUrl(request)));
        return new GenericResponse("success");
    }

    @PostMapping("/user/resetPassword")
    public GenericResponse resetPassword(final HttpServletRequest request, @RequestParam("email") final String userEmail) {
        final User user = userService.findUserByEmail(userEmail);
        if (user != null) {
            final String token = UUID.randomUUID().toString();
            userService.createPasswordResetTokenForUser(user, token);
            mailSender.send(constructResetTokenEmail(getAppUrl(request), request.getLocale(), token, user));
        }
        return new GenericResponse(messages.getMessage("message.resetPasswordEmail", null, request.getLocale()));
    }

    @PostMapping("/user/updatePassword")
    public GenericResponse changeUserPassword(final Locale locale, @Valid PasswordDto passwordDto) {

        final User user = userService.findUserByEmail(((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getEmail());
        if (!userService.checkIfValidOldPassword(user, passwordDto.getOldPassword())) {
            throw new InvalidOldPasswordException();
        }

        userService.changeUserPassword(user, passwordDto.getNewPassword());
        return new GenericResponse(messages.getMessage("message.updatePasswordSuc", null, locale));
    }

    private String getAppUrl(HttpServletRequest request) {
        return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }

    private SimpleMailMessage constructResetTokenEmail(final String contextPath, final Locale locale, final String token, final User user) {
        final String url = contextPath + "/user/changePassword?token=" + token;
        final String message = messages.getMessage("message.resetPassword", null, locale);
        return constructEmail("Reset Password", message + " \r\n" + url, user);
    }

    private SimpleMailMessage constructEmail(String subject, String body, User user) {
        final SimpleMailMessage email = new SimpleMailMessage();
        email.setSubject(subject);
        email.setText(body);
        email.setTo(user.getEmail());
        email.setFrom(env.getProperty("support.email"));
        return email;
    }
}
