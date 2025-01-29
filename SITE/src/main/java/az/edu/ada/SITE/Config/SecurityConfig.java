package az.edu.ada.SITE.Config;

import az.edu.ada.SITE.Repository.UserRepository;
import az.edu.ada.SITE.Entity.User;
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

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Provides a custom implementation of UserDetailsService to retrieve user
     * details from the database.
     */
    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return username -> {
            Optional<User> user = userRepository.findByUsername(username);
            return user.orElseThrow(() -> new UsernameNotFoundException(username + " not found"));
        };
    }

    /**
     * Custom AuthenticationSuccessHandler to redirect users based on their roles
     * after login.
     */
    @Bean
    public AuthenticationSuccessHandler successHandler() {
        return (request, response, authentication) -> {
            User user = (User) authentication.getPrincipal(); // Assuming User implements UserDetails
            String redirectUrl = switch (user.getRole()) {
                case ADMIN -> "/admin/welcome";
                case STAFF -> "/staff/welcome";
                case STUDENT -> "/student/welcome";
            };
            response.sendRedirect(redirectUrl);
        };
    }

    /**
     * Security filter chain to define access rules and login/logout behavior.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin())) // Allow H2 Console
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/auth/**", "/h2-console/**").permitAll() // Allow login page and H2 console
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
