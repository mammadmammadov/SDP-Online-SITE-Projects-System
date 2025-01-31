package az.edu.ada.SITE.Init;

import az.edu.ada.SITE.Entity.Admin;
import az.edu.ada.SITE.Entity.Project;
import az.edu.ada.SITE.Entity.Staff;
import az.edu.ada.SITE.Entity.Student;
import az.edu.ada.SITE.Repository.ProjectRepository;
import az.edu.ada.SITE.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProjectRepository projectRepository;

    @Autowired
    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder,
            ProjectRepository projectRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.projectRepository = projectRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            userRepository
                    .save(new Admin("ADM00001", "John", "Wick", "admin@ada.edu.az",
                            passwordEncoder.encode("admin123")));
            userRepository.save(new Staff("STF00001", "Jason", "White", "staff@ada.edu.az",
                    passwordEncoder.encode("staff123"), "Professor",
                    "Computer Science"));
            userRepository.save(new Staff("STF00002", "Jack", "Sparrow", "staff2@ada.edu.az",
                    passwordEncoder.encode("staff123"), "Assistant Professor",
                    "Computer Science"));
            userRepository.save(new Student("STD00001", "Walter", "White", "student@ada.edu.az",
                    passwordEncoder.encode("student123"),
                    "Undergraduate", "Computer Science", 2025));
            userRepository.save(new Student("STD00002", "Jesse", "Pinkman", "student2@ada.edu.az",
                    passwordEncoder.encode("student123"),
                    "Undergraduate", "Computer Science", 2026));
        }
        if (projectRepository.count() == 0) {
            Staff supervisor = (Staff) userRepository.findByEmail("staff@ada.edu.az")
                    .orElseThrow(() -> new IllegalArgumentException("Supervisor not found"));

            Project project = new Project();
            project.setTitle("AI Research Project");
            project.setDescription("A project focused on artificial intelligence research, exploring new algorithms.");
            project.setObjectives(
                    "1. Develop new AI algorithms\n2. Apply AI solutions to real-world problems\n3. Collaborate with industry partners.");
            project.setStatus(Project.Status.OPEN);
            project.setSupervisor(supervisor);

            projectRepository.save(project);
        }
    }
}
