package com.andreev.springboot.controller;

import com.andreev.springboot.model.Role;
import com.andreev.springboot.model.User;
import com.andreev.springboot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserRestController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/admin/getuser/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @GetMapping("/principal")
    public User currentUser() {
        return userService.findByUsername(
                SecurityContextHolder.getContext().getAuthentication().getName());

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
        var oldUser = userService.getUserById(user.getId());

        if (!(oldUser.getPassword().equals(user.getPassword()))) {
            String pass = passwordEncoder.encode(user.getPassword());
            user.setPassword(pass);
        }

        userService.update(user);
    }

    @DeleteMapping("/admin/delete/{id}")
    public void delete(@PathVariable Long id) {
        userService.removeUserById(id);
    }

    @PostMapping("/admin/newuser")
    public void newEmployee(@RequestBody User user) {
        String pass = passwordEncoder.encode(user.getPassword());
        user.setPassword(pass);

        userService.update(user);
    }
}
