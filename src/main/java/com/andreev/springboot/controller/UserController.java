package com.andreev.springboot.controller;

import com.andreev.springboot.model.User;
import com.andreev.springboot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @GetMapping(value = "user")
    public String getUserPage(Model model) {
        model.addAttribute("currentUser", getUserData());
        model.addAttribute("currentUserRoles",
                getUserData().getRoles()
                        .stream()
                        .map(x -> x.getRole().replaceFirst("ROLE_", ""))
                        .collect(Collectors.toList()));

        model.addAttribute("currentUser", getUserData());
        return "user";
    }

    @GetMapping(value = {"/", "/login"})
    public String getLoginPage() {
        return "login";
    }

    @GetMapping("admin")
    public String index(Model model) {
        List<User> users = userService.getAllUsers();
        User user = new User();

        model.addAttribute("currentUser", getUserData());
        model.addAttribute("currentUserRoles",
                getUserData().getRoles()
                        .stream()
                        .map(x -> x.getRole().replaceFirst("ROLE_", ""))
                        .collect(Collectors.toList()));


        model.addAttribute("newUser", user);
        model.addAttribute("allRoles", userService.getAllRoles());
        model.addAttribute("users", users);

        return "index";
    }

    @PostMapping(value = {"/admin/adduser", "/admin/saveuser"})
    public String saveUser(@ModelAttribute User user) {

        String pass = passwordEncoder.encode(user.getPassword());
        user.setPassword(pass);

        userService.update(user);

        return "redirect:/admin";
    }

    @PostMapping("/admin/deleteuser")
    public String deleteUser(@RequestParam(name = "id") Long id) {
        userService.removeUserById(id);

        return "redirect:/admin";
    }

    @GetMapping(value = "/error_access")
    public String accessDenied(Model model) {
        model.addAttribute("currentUser", getUserData());

        return "error_access";
    }

    @GetMapping(value = "/error")
    public String errorAccess(Model model) {
        model.addAttribute("currentUser", getUserData());

        return "error_access";
    }

    private User getUserData() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            User secUser = (User) authentication.getPrincipal();
            return userService.findByUsername(secUser.getUsername());
        }

        return null;
    }
}
