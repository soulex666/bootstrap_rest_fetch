package com.andreev.springboot.controller;

import com.andreev.springboot.config.security.oauth2.VKOAuth;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class UserController {
    private final VKOAuth vkoAuth;

    public UserController(VKOAuth vkoAuth) {
        this.vkoAuth = vkoAuth;
    }

    @GetMapping(value = "user")
    public String getUserPage() {
        return "user";
    }

    @GetMapping(value = {"/", "/login"})
    public String getLoginPage() {
        return "login";
    }

    @GetMapping(value = "/login/vk")
    public String getVKLogin(@RequestParam(name = "code", required = false) String code) {
        if (code != null) {
            return vkoAuth.isUserAuthenticated(code) ? "redirect:/user" : "redirect:/login";
        } else {
            final String authorizationUrl = vkoAuth.getAuthorizationUrl();

            return authorizationUrl != null ? ("redirect:" + authorizationUrl) : "redirect:/login";
        }
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
}
