package az.edu.ada.SITE.Init;

import az.edu.ada.SITE.Entity.Admin;
import az.edu.ada.SITE.Entity.Project;
import az.edu.ada.SITE.Entity.Project.ProjectType;
import az.edu.ada.SITE.Entity.Staff;
import az.edu.ada.SITE.Entity.Student;
import az.edu.ada.SITE.Repository.ProjectRepository;
import az.edu.ada.SITE.Repository.UserRepository;

import java.util.List;

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
                                        "Undergraduate", "Computer Science", "Sophomore"));
                        userRepository.save(new Student("STD00002", "Jesse", "Pinkman", "student2@ada.edu.az",
                                        passwordEncoder.encode("student123"),
                                        "Undergraduate", "Computer Science", "Freshman"));
                        userRepository.save(new Student("STD00003", "Gustavo", "Fring", "student3@ada.edu.az",
                                        passwordEncoder.encode("student123"),
                                        "Undergraduate", "Computer Science", "Sophomore"));
                        userRepository.save(new Student("STD00004", "Mike", "Ehrmantraut", "student4@ada.edu.az",
                                        passwordEncoder.encode("student123"),
                                        "Undergraduate", "Information Technologies", "Freshman"));
                        userRepository.save(new Student("STD00005", "Hank", "Schrader", "student5@ada.edu.az",
                                        passwordEncoder.encode("student123"),
                                        "Undergraduate", "Computer Science", "Freshman"));
                }
                if (projectRepository.count() == 0) {
                        Staff supervisor = (Staff) userRepository.findByEmail("staff@ada.edu.az")
                                        .orElseThrow(() -> new IllegalArgumentException("Supervisor not found"));

                        Project project1 = new Project();
                        project1.setCategory(List.of("Artificial Intelligence"));
                        project1.setTitle("AI Research Project");
                        project1.setDescription(
                                        "A project focused on artificial intelligence research, exploring new algorithms.");
                        project1.setObjectives(
                                        "1. Develop new AI algorithms\n2. Apply AI solutions to real-world problems\n3. Collaborate with industry partners.");
                        project1.setStatus(Project.Status.OPEN);
                        project1.setSupervisor(supervisor);
                        project1.setCategory(List.of("Artificial Intelligence"));
                        project1.setDegreeRestriction(List.of("Undergraduate"));
                        project1.setMajorRestriction(List.of("Computer Science"));
                        project1.setStudyYearRestriction(List.of("Sophomore"));
                        project1.setType(ProjectType.GROUP);
                        project1.setMaxStudents(3);

                        projectRepository.save(project1);

                        Project project2 = new Project();
                        project2.setCategory(List.of("Web Development"));
                        project2.setTitle("Web Development Research Project");
                        project2.setDescription(
                                        "A project focused on Web Development research, exploring new algorithms.");
                        project2.setObjectives(
                                        "1. Develop new WD algorithms\n2. Apply WD solutions to real-world problems\n3. Collaborate with industry partners.");
                        project2.setStatus(Project.Status.OPEN);
                        project2.setCategory(List.of("Web Development"));
                        project2.setDegreeRestriction(List.of("Undergraduate"));
                        project2.setMajorRestriction(List.of("Computer Science"));
                        project2.setStudyYearRestriction(List.of("Freshman"));
                        project2.setSupervisor(supervisor);
                        project2.setType(ProjectType.INDIVIDUAL);
                        project2.setMaxStudents(1);

                        projectRepository.save(project2);
                }
        }
}
