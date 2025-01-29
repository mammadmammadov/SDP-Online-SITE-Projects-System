package az.edu.ada.SITE.Init;

import az.edu.ada.SITE.Entity.Admin;
import az.edu.ada.SITE.Entity.Staff;
import az.edu.ada.SITE.Entity.Student;
import az.edu.ada.SITE.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            userRepository
                    .save(new Admin("ADM00001", "John", "Wick", "jwick00001", "admin@gmail.com",
                            passwordEncoder.encode("admin123")));
            userRepository.save(new Staff("STF00001", "Jason", "White", "jwhite00001", "staff@gmail.com",
                    passwordEncoder.encode("staff123"), "Professor",
                    "Computer Science"));
            userRepository.save(new Student("STD00001", "Walter", "Black", "wblack00001", "student@gmail.com",
                    passwordEncoder.encode("student123"),
                    "Undergraduate", "Computer Science", 2025));
        }
    }

}
