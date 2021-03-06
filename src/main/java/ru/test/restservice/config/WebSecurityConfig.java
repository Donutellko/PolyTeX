package ru.test.restservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.test.restservice.service.ClientDetailsService;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final ClientDetailsService userDetailsService;

    /**
     * Установка перегруженного объекта userDetailsService для локальной авторизации
     */
    @Override
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    /**
     * Установка шифровальщика паролей
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Установка доступа к urlам в зависимости от роли пользователя и настройка страниц аутентификации
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/h2-console/**", "/admin/**", "/actuator/**", "/swagger-ui**",
                        "/v2/api-docs/**").hasAnyRole("ADMIN")
                .and()
                .authorizeRequests()

                .antMatchers("/api/**", "/projects/**").hasAnyRole("USER", "ADMIN")
                .and()
                .authorizeRequests()
                .antMatchers("/**").permitAll()
                .and()
                .oauth2Login()
                .loginPage("/login")
                .and()
                .formLogin()
                .loginPage("/login")
                .permitAll()
                .failureUrl("/cred-error")
                .defaultSuccessUrl("/projects", true)
                .usernameParameter("email")
                .passwordParameter("password")
                .and()
                .logout()
                .logoutSuccessUrl("/login")
                .permitAll();

        http.headers().frameOptions().disable();
        http.csrf().disable();
    }

}