package com.andreev.springboot.config.security.oauth2.services;

import com.andreev.springboot.config.security.oauth2.model.CustomOAuthUser;
import com.andreev.springboot.config.security.oauth2.GoogleUserInfo;
import com.andreev.springboot.model.User;
import com.andreev.springboot.repositories.RoleRepository;
import com.andreev.springboot.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;


import java.util.Set;

@Service
public class GoogleOidcUserService extends OidcUserService {

    private final Logger logger = LoggerFactory.getLogger(GoogleOidcUserService.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public GoogleOidcUserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {

        var oidcUser = super.loadUser(userRequest);
        logger.info("User with email {} logged in with Google", oidcUser.getEmail());

        var newOidcUser = new CustomOAuthUser();

        newOidcUser.setUserInfo(oidcUser.getUserInfo());
        newOidcUser.setAttributes(oidcUser.getAttributes());
        newOidcUser.setClaims(oidcUser.getClaims());
        newOidcUser.setName(oidcUser.getEmail());
        newOidcUser.setIdToken(oidcUser.getIdToken());

        return processOidcUser(userRequest, newOidcUser);

    }

    private OidcUser processOidcUser(OidcUserRequest userRequest, CustomOAuthUser oidcUser) {

        googleProcessing(oidcUser);

        return oidcUser;
    }

    private void googleProcessing(CustomOAuthUser oidcUser) {
        var googleUserInfo = new GoogleUserInfo(oidcUser.getAttributes());


        var userOptional = userRepository.findUserByUsername(googleUserInfo.getEmail());

        if (userOptional == null) {
            userOptional = new User();
            userOptional.setUsername(googleUserInfo.getEmail());
            userOptional.setFirstName(googleUserInfo.getFirstName());
            userOptional.setLastName(googleUserInfo.getLastName());
            userOptional.setAge((byte) 0);
            userOptional.setPassword("");
            userOptional.setRoles(Set.of(roleRepository.getOne(1L)));

            userRepository.save(userOptional);
        } else {
            oidcUser.setAuthorities(userOptional.getRoles());
        }
    }
}
