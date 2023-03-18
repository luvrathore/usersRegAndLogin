package com.example.usersRegAndLogin.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ResolvableType;
import org.springframework.http.*;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller
@Slf4j
public class RegistrationControllerOAuth {

    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;
    private static final String authorizationRequestBaseUri = "oauth2/authorize-client";

    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

    @GetMapping(value = "/oauth_login")
    public String registerUser2FA(Model model){
        Iterable<ClientRegistration> clientRegistrations = null;
        ResolvableType type = ResolvableType.forInstance(clientRegistrationRepository)
                .as(Iterable.class);
        if (type != ResolvableType.NONE && ClientRegistration.class.isAssignableFrom(type.resolveGenerics()[0])) {
            clientRegistrations = (Iterable<ClientRegistration>) clientRegistrationRepository;
        }

        Map<String, String> oauth2AuthenticationUrls = new HashMap<>();

        clientRegistrations.forEach(registration -> oauth2AuthenticationUrls.put(registration.getClientName(), authorizationRequestBaseUri + "/" + registration.getRegistrationId()));
        model.addAttribute("urls", oauth2AuthenticationUrls);
        return "oauth_login";
    }

    // http://localhost:8080/oauth2/authorize-client/google

    @GetMapping("/loginSuccess")
    public String getLoginInfo(Model model, OAuth2AuthenticationToken authentication, HttpSession httpSession) {

        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(authentication.getAuthorizedClientRegistrationId(), authentication.getName());

        String userInfoEndpointUri = client.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUri();

        if (!StringUtils.isEmpty(userInfoEndpointUri)) {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + client.getAccessToken()
                    .getTokenValue());

            HttpEntity<String> entity = new HttpEntity<String>("", headers);
            ResponseEntity<Map> response = restTemplate.exchange(userInfoEndpointUri, HttpMethod.GET, entity, Map.class);
            Map userAttributes = response.getBody();
            log.info("The userAttribute from UserInfo Provider={}",userAttributes.toString());
            model.addAttribute("name", userAttributes.get("name"));
            httpSession.setAttribute("sessionUserName",userAttributes.get("email"));
            model.addAttribute("sessionUserName",userAttributes.get("email"));
        }
        return "home";
    }
}
