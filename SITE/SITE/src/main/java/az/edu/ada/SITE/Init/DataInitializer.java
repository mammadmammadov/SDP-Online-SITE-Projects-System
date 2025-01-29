package az.edu.ada.SITE.Init;

import az.edu.ada.SITE.Entity.User;
import az.edu.ada.SITE.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            userRepository.save(new User(null, "admin", passwordEncoder.encode("admin123"), User.Role.ADMIN));
            userRepository.save(new User(null, "staff", passwordEncoder.encode("staff123"), User.Role.STAFF));
            userRepository.save(new User(null, "student", passwordEncoder.encode("student123"), User.Role.STUDENT));
        }
    }

}
