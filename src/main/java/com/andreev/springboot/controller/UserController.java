package com.andreev.springboot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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

}
