package com.example.usersRegAndLogin.controller;

import com.example.usersRegAndLogin.model.Privilege;
import com.example.usersRegAndLogin.model.Role;
import com.example.usersRegAndLogin.model.User;
import com.example.usersRegAndLogin.service.ISecurityUserService;
import com.example.usersRegAndLogin.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class SubRegistrationController {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    private IUserService userService;

    @Autowired
    private ISecurityUserService securityUserService;

    @Autowired
    private MessageSource messages;

    @GetMapping("/registrationConfirm")
    public ModelAndView confirmRegistration(final HttpServletRequest request,
                                            final ModelMap model,
                                            @RequestParam("token") final String token){
        Locale locale = request.getLocale();
        model.addAttribute("lang", locale.getLanguage());
        final String result = userService.validateVerificationToken(token);

        if (result.equals("valid")) {
            final User user = userService.getUser(token);
            // if (user.isUsing2FA()) {
            // model.addAttribute("qr", userService.generateQRUrl(user));
            // return "redirect:/qrcode.html?lang=" + locale.getLanguage();
            // }
            authWithoutPassword(user);
            model.addAttribute("messageKey", "message.accountVerified");
            return new ModelAndView("redirect:/console", model);
        }
        model.addAttribute("messageKey", "auth.message." + result);
        model.addAttribute("expired", "expired".equals(result));
        model.addAttribute("token", token);
        return new ModelAndView("redirect:/badUser", model);
    }

    public void authWithoutPassword(User user) {

        List<Privilege> privileges = user.getRoles()
                .stream()
                .map(Role::getPrivileges)
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());

        List<GrantedAuthority> authorities = privileges.stream()
                .map(p -> new SimpleGrantedAuthority(p.getName()))
                .collect(Collectors.toList());

        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @GetMapping("/user/changePassword")
    public ModelAndView showChangePasswordPage(final ModelMap model, @RequestParam("token") final String token) {
        final String result = securityUserService.validatePasswordResetToken(token);

        if(result != null) {
            String messageKey = "auth.message." + result;
            model.addAttribute("messageKey", messageKey);
            return new ModelAndView("redirect:/login", model);
        } else {
            model.addAttribute("token", token);
            return new ModelAndView("redirect:/updatePassword");
        }
    }

    @GetMapping("/updatePassword")
    public ModelAndView updatePassword(final HttpServletRequest request, final ModelMap model, @RequestParam("messageKey" ) final Optional<String> messageKey) {

        Locale locale = request.getLocale();
        model.addAttribute("lang", locale.getLanguage());
        messageKey.ifPresent( key -> {
                    String message = messages.getMessage(key, null, locale);
                    model.addAttribute("message", message);
                }
        );
        return new ModelAndView("updatePassword", model);
    }

    @GetMapping("/login")
    public ModelAndView login(final HttpServletRequest request, final ModelMap model, @RequestParam("messageKey" ) final Optional<String> messageKey, @RequestParam("error" ) final Optional<String> error) {
        Locale locale = request.getLocale();
        model.addAttribute("lang", locale.getLanguage());
        messageKey.ifPresent( key -> {
                    String message = messages.getMessage(key, null, locale);
                    model.addAttribute("message", message);
                }
        );
        error.ifPresent( e ->  model.addAttribute("error", e));

        return new ModelAndView("login", model);
    }

    @GetMapping("/formLoginSuccess")
    public ModelAndView formLoginSuccess(ModelMap model , @AuthenticationPrincipal UserDetails userDetails,
                                         HttpSession httpSession){

        log.info("The user details info are as follows :: {}",userDetails.toString());
        httpSession.setAttribute("sessionUserName",userDetails.getUsername());
        model.addAttribute("sessionUserName",userDetails.getUsername());
        return new ModelAndView("home", model);
    }

    @GetMapping("/formLoginFailure")
    public ModelAndView formLoginFailure(ModelMap model){
        log.info("Since the user is not present at all , so we are redirecting the user to registration Page");
        return new ModelAndView("globalRegistration",model);
    }

    @GetMapping("/globalLogin")
    public ModelAndView globalLoginInitiator(ModelMap model){
        return new ModelAndView("globalLogin",model);
    }

    @GetMapping("/globalRegistration")
    public ModelAndView globalRegistration(ModelMap model){
        return new ModelAndView("globalRegistration",model);
    }
}
