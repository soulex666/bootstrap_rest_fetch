package com.andreev.springboot.config.security;

import com.andreev.springboot.config.security.oauth2.services.FacebookOAuth2UserService;
import com.andreev.springboot.config.security.oauth2.services.GoogleOidcUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final UserDetailsService userDetailsService;
    private final SuccessUserHandler successUserHandler;
    private final GoogleOidcUserService googleOidcUserService;
    private final FacebookOAuth2UserService facebookOAuth2UserService;

    public SecurityConfig(@Qualifier("userDetailsServiceImpl") UserDetailsService userDetailsService,
                          SuccessUserHandler successUserHandler, FacebookOAuth2UserService facebookOAuth2UserService,
                          GoogleOidcUserService googleOidcUserService) {
        this.userDetailsService = userDetailsService;
        this.successUserHandler = successUserHandler;
        this.facebookOAuth2UserService = facebookOAuth2UserService;
        this.googleOidcUserService = googleOidcUserService;
    }

    @Autowired
    public void configureGlobalSecurity(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
    }

    @Override
    public void configure(WebSecurity web) {
        web
                .ignoring()
                .antMatchers("/resources/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.csrf().disable();

        http.exceptionHandling().accessDeniedPage("/error_access");

        http.formLogin()
                .loginPage("/")
                .successHandler(successUserHandler)
                .loginProcessingUrl("/")
                .usernameParameter("username")
                .passwordParameter("password")
                .permitAll();

        http.oauth2Login()
                .loginPage("/")
//                .defaultSuccessUrl("/loginSuccess")
                .userInfoEndpoint()
                .oidcUserService(googleOidcUserService)
                .userService(facebookOAuth2UserService)
                .and()
                .successHandler(successUserHandler);

        http.logout()
                .permitAll()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/");

        http.authorizeRequests()
                .antMatchers("/principal").authenticated()
                .antMatchers("/admin/**").access("hasAnyRole('ADMIN')")
                .antMatchers("/user/**").access("hasAnyRole('USER','ADMIN')");
    }

    @Bean
    public static BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
