package com.andreev.springboot.config.security.oauth2;

import com.andreev.springboot.model.User;
import com.andreev.springboot.repositories.RoleRepository;
import com.andreev.springboot.service.UserService;
import com.github.scribejava.apis.VkontakteApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.*;
import com.github.scribejava.core.oauth.AccessTokenRequestParams;
import com.github.scribejava.core.oauth.OAuth20Service;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ExecutionException;

@Service
public class VKOAuth {
    private final Logger logger = LoggerFactory.getLogger(VKOAuth.class);

    private OAuth20Service vkService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Value("${vk.client-id}")
    private String clientId;

    @Value("${vk.client-secret}")
    private String clientSecret;

    @Value("${vk.scope}")
    private String scope;

    @Value("${vk.callback}")
    private String callback;

    @Value("${vk.default_password}")
    private String password;

    private static final String PROTECTED_RESOURCE_URL = "https://api.vk.com/method/users.get?v="
            + VkontakteApi.VERSION;

    public VKOAuth(UserService userService, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    @PostConstruct
    private void init () {
        vkService = new ServiceBuilder(clientId)
                .apiSecret(clientSecret)
                .defaultScope(scope)
                .callback(callback)
                .build(VkontakteApi.instance());
    }

    public String getAuthorizationUrl() {
        return vkService.createAuthorizationUrlBuilder()
                .build();
    }

    public boolean isUserAuthenticated(String code) {
        try {
            logger.info("Попытка получить Access Token для VK по коду {}", code);
            final OAuth2AccessToken accessToken = vkService.getAccessToken(AccessTokenRequestParams.create(code));

            JSONObject rowResponse = new JSONObject(accessToken.getRawResponse());
            String email = rowResponse.getString("email");

            if (email != null) {
                logger.info("Получен адрес электронной почты '{}' с токена {} ", email, code);
            }

            final OAuthRequest request = new OAuthRequest(Verb.GET, PROTECTED_RESOURCE_URL);
            vkService.signRequest(accessToken, request);

            Response response1 = vkService.execute(request);

            JSONObject obj = new JSONObject(response1.getBody());
            JSONArray arr = obj.getJSONArray("response");

            logger.info("Попытка получения пользователя с электронной почтой '{}'", email);

            User userOptional = userService.findByUsername(email);

            if (userOptional == null) {
                logger.info("Пользователь с электронной почтой '{}' не найдем, сохраняем нового пользователя", email);
                userOptional = new User();
                userOptional.setUsername(email);
                userOptional.setFirstName(arr.getJSONObject(0).getString("first_name"));
                userOptional.setLastName(arr.getJSONObject(0).getString("last_name"));
                userOptional.setAge((byte) 0);
                userOptional.setPassword(passwordEncoder.encode(password));
                userOptional.setRoles(Set.of(roleRepository.getOne(1L)));

                userService.update(userOptional);

                logger.info("Пользователь с электронной почтой '{}' сохранён в базу данных", email);
            } else {
                logger.info("Пользователь с электронной почтой '{}' найден в базе данных", email);
            }

            logger.info("Создание аутентификации пользователя с электронной почтой '{}'", email);

            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(userOptional.getUsername(), password);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            logger.info("Аутентификации пользователя с электронной почтой '{}' установлена", email);

            return true;

        } catch (IOException | InterruptedException | ExecutionException | JSONException | OAuth2AccessTokenErrorResponse e) {
            logger.error("Проблема с доступом в VK по коду " + code, e);
        }

        return false;
    }
}
