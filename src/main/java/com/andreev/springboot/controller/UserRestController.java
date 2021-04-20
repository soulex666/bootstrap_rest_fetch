package com.andreev.springboot.controller;

import com.andreev.springboot.model.Role;
import com.andreev.springboot.model.User;
import com.andreev.springboot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserRestController {

    @Autowired
    private UserService userService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @GetMapping("/admin/getuser/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @GetMapping("/admin/getusers")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/admin/getroles")
    public List<Role> getAllRoles() {
        return userService.getAllRoles();
    }

    @PutMapping("/admin/edit")
    public void update(@RequestBody User user) {
        userService.update(user);
    }
//
}
