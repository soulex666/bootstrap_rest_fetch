package com.andreev.springboot.config.security.oauth2.model;

import com.andreev.springboot.model.Role;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Component
@Data
@Getter
@Setter
public class CustomOAuthUser implements OidcUser, OAuth2User {
    private OidcUserInfo userInfo;
    private OidcIdToken idToken;
    private String name;
    private Map<String, Object> claims;
    private Map<String, Object> attributes;
    private Set<Role> authorities;

    public CustomOAuthUser() {
        authorities = Set.of(new Role(1L, "ROLE_USER"));
    }
}
