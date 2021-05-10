package com.andreev.springboot.config.security.oauth2;

import com.andreev.springboot.model.User;
import com.andreev.springboot.repositories.RoleRepository;
import com.andreev.springboot.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class CustomOidcUserService extends OidcUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = super.loadUser(userRequest);

        try {
            return processOidcUser(userRequest, oidcUser);
        } catch (Exception ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OidcUser processOidcUser(OidcUserRequest userRequest, OidcUser oidcUser) {
        GoogleUserInfo googleUserInfo = new GoogleUserInfo(oidcUser.getAttributes());

        User userOptional = userRepository.findUserByUsername(googleUserInfo.getEmail());
        if (userOptional == null) {
            User user = new User();
            user.setUsername(googleUserInfo.getEmail());
            user.setFirstName(googleUserInfo.getFirstName());
            user.setLastName(googleUserInfo.getLastName());
            user.setRoles(Set.of(roleRepository.getOne(1L)));

            userRepository.save(user);
        }

        return oidcUser;
    }
}
