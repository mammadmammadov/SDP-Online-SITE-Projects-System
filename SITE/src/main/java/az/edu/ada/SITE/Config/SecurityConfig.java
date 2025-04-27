package az.edu.ada.SITE.Config;

import az.edu.ada.SITE.Entity.User;
import az.edu.ada.SITE.Repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.util.Optional;

/**
 * Configuration class for Spring Security settings.
 * Defines authentication, authorization, and security filter chain.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Defines the password encoder bean using BCrypt algorithm.
     *
     * @return PasswordEncoder instance.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Defines the UserDetailsService bean, which loads user-specific data during
     * authentication.
     *
     * @param userRepository the repository to access user data.
     * @return UserDetailsService that loads user by email.
     */
    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return email -> {
            Optional<User> user = userRepository.findByEmail(email);
            return user.orElseThrow(() -> new UsernameNotFoundException(email + " not found"));
        };
    }

    /**
     * Defines the authentication success handler bean.
     * Redirects users to different pages based on their role after login.
     *
     * @return AuthenticationSuccessHandler instance.
     */
    @Bean
    public AuthenticationSuccessHandler successHandler() {
        return (request, response, authentication) -> {
            User user = (User) authentication.getPrincipal();
            String redirectUrl = switch (user.getRole()) {
                case ADMIN -> "/admin/welcome";
                case STAFF -> "/staff/projects";
                case STUDENT -> "/student/projects";
            };
            response.sendRedirect(redirectUrl);
        };
    }

    /**
     * Defines the security filter chain, configuring security policies such as
     * access restrictions, login, and logout behavior.
     *
     * @param http HttpSecurity instance to configure security.
     * @return SecurityFilterChain built from the configuration.
     * @throws Exception in case of configuration errors.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/login/**", "/auth/**", "/h2-console/**", "/images/**", "/css/**", "/js/**")
                        .permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/staff/**").hasRole("STAFF")
                        .requestMatchers("/student/**").hasRole("STUDENT")
                        .anyRequest().authenticated())
                .formLogin(login -> login
                        .loginPage("/auth/login")
                        .successHandler(successHandler())
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/auth/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll());

        return http.build();
    }
}
