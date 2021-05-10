package com.andreev.springboot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Controller
public class UserController {

    @GetMapping(value = "user")
    public String getUserPage() {
        return "user";
    }

    @GetMapping(value = {"/", "/login"})
    public String getLoginPage() {
        return "login";
    }

    @GetMapping("admin")
    public String index() {
        return "index";
    }

    @GetMapping(value = "/error_access")
    public String accessDenied() {
        return "error_access";
    }

    @GetMapping(value = "/error")
    public String errorAccess() {
        return "error_access";
    }

    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

    @GetMapping("/loginSuccess")
    public String getLoginInfo(OAuth2AuthenticationToken authentication) {
        authentication.getPrincipal().getName();
        OAuth2AuthorizedClient client = authorizedClientService
                .loadAuthorizedClient(
                        authentication.getAuthorizedClientRegistrationId(),
                        authentication.getName());
        Map<String, Object> attributes = authentication.getPrincipal().getAttributes();
        for(Map.Entry<String, Object> map : attributes.entrySet()) {
            System.out.println(map.getKey() + " : " + map.getValue());
        }

        return "redirect:/admin/newuser";
    }

}
