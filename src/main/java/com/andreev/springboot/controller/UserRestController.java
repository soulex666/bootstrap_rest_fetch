package com.andreev.springboot.controller;

import com.andreev.springboot.model.Role;
import com.andreev.springboot.model.User;
import com.andreev.springboot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            User secUser = (User) authentication.getPrincipal();

            return userService.findByUsername(secUser.getUsername());
        }

        return null;
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
        User oldUser = userService.getUserById(user.getId());

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
