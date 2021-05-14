package com.andreev.springboot.config.security.oauth2.services;

import com.andreev.springboot.config.security.oauth2.model.CustomOAuthUser;
import com.andreev.springboot.config.security.oauth2.GoogleUserInfo;
import com.andreev.springboot.model.User;
import com.andreev.springboot.repositories.RoleRepository;
import com.andreev.springboot.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class FacebookOAuth2UserService extends DefaultOAuth2UserService {
    private final Logger logger = LoggerFactory.getLogger(FacebookOAuth2UserService.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public FacebookOAuth2UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        var oauth2User = super.loadUser(userRequest);
        logger.info("User with email {} logged in with Facebook", oauth2User.getAttributes().get("email"));

        var newOAuth2User = new CustomOAuthUser();

        newOAuth2User.setAttributes(oauth2User.getAttributes());
        newOAuth2User.setName(oauth2User.getName());

        return processOidcUser(userRequest, newOAuth2User);
    }

    private CustomOAuthUser processOidcUser(OAuth2UserRequest userRequest, CustomOAuthUser oidcUser) {

        facebookProcessing(oidcUser);

        return oidcUser;
    }

    private boolean facebookProcessing(CustomOAuthUser oidcUser) {
        var googleUserInfo = new GoogleUserInfo(oidcUser.getAttributes());

        var userOptional = userRepository.findUserByUsername(googleUserInfo.getEmail());

        if (userOptional == null) {
            userOptional = new User();
            userOptional.setUsername(oidcUser.getAttributes().get("email").toString());
            userOptional.setFirstName(oidcUser.getAttributes().get("name").toString().split(" ")[0]);
            userOptional.setLastName(oidcUser.getAttributes().get("name").toString().split(" ")[1]);
            userOptional.setAge((byte)0);
            userOptional.setPassword("");
            userOptional.setRoles(Set.of(roleRepository.getOne(1L)));

            userRepository.save(userOptional);
        } else {
            oidcUser.setAuthorities(userOptional.getRoles());
            oidcUser.setName(oidcUser.getAttributes().get("email").toString());
        }

        return true;
    }
}
