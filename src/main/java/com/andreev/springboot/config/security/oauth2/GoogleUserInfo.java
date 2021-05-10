package com.andreev.springboot.config.security.oauth2;

import java.util.Map;

public class GoogleUserInfo {

    private final Map<String, Object> attributes;

    public GoogleUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public String getId() {
        return (String) attributes.get("sub");
    }

    public String getFirstName() {
        return (String) attributes.get("given_name");
    }

    public String getLastName() {
        return (String) attributes.get("family_name");
    }

    public String getEmail() {
        return (String) attributes.get("email");
    }
}
