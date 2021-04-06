package com.andreev.springboot.controller;

import com.andreev.springboot.model.User;
import com.andreev.springboot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping(value = "user")
    public String getUserPage(Model model) {
        model.addAttribute("currentUser", getUserData());
        return "user";
    }

    @GetMapping(value = "/login")
    public String login(Model model) {
        model.addAttribute("currentUser", getUserData());
        return "login";
    }

    @GetMapping(value = "/")
    public String getLoginPage() {
        return "redirect:login";
    }

    @GetMapping("admin")
    public String index(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);

        return "index";
    }

    @PostMapping("/admin/adduser")
    public String addUser(@RequestParam(name = "first_name", defaultValue = "---") String firstName,
                          @RequestParam(name = "last_name", defaultValue = "---") String lastName,
                          @RequestParam(name = "age", defaultValue = "0") Byte age,
                          @RequestParam(name = "username") String username,
                          @RequestParam(name = "password") String password) {

        userService.saveUser(firstName, lastName, age, username, password);

        return "redirect:/admin";
    }

    @GetMapping("/admin/details/{id}")
    public String details(Model model, @PathVariable(name = "id") Long id) {
        User user = userService.getUserById(id);
        if (user == null) {
            return "redirect:/admin";
        }
        model.addAttribute("user", user);

        return "details";
    }

    @PostMapping("/admin/saveuser")
    public String saveUser(@RequestParam(name = "id") Long id,
                           @RequestParam(name = "first_name") String firstName,
                           @RequestParam(name = "last_name") String lastName,
                           @RequestParam(name = "age") Byte age,
                           @RequestParam(name = "username") String username,
                           @RequestParam(name = "password") String password) {

        User user = userService.getUserById(id);

        if (user != null) {
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setAge(age);
            user.setUsername(username);
            user.setPassword(password);

            userService.update(user);
        }

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
