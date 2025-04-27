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

/**
 * DataInitializer is responsible for initializing the application with default
 * user and project data
 * when the application is first run. This class implements the
 * CommandLineRunner interface and runs
 * the data initialization code during application startup.
 */
@Component
public class DataInitializer implements CommandLineRunner {

        private final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder;
        private final ProjectRepository projectRepository;

        /**
         * Constructs the DataInitializer with the required repositories for user and
         * project data.
         * 
         * @param userRepository    the repository for user data
         * @param passwordEncoder   the encoder for password encryption
         * @param projectRepository the repository for project data
         */
        @Autowired
        public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder,
                        ProjectRepository projectRepository) {
                this.userRepository = userRepository;
                this.passwordEncoder = passwordEncoder;
                this.projectRepository = projectRepository;
        }

        /**
         * Initializes the user and project data if the database is empty.
         * It creates default users (Admin, Staff, and Students) and default projects
         * with relevant details.
         * This method is invoked during application startup by the Spring Boot
         * framework.
         * 
         * @param args command line arguments (not used)
         * @throws Exception if an error occurs during initialization
         */
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
                        userRepository.save(new Staff("STF00003", "Hank", "Schrader", "staff3@ada.edu.az",
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

                        Staff supervisor2 = (Staff) userRepository.findByEmail("staff2@ada.edu.az")
                                        .orElseThrow(() -> new IllegalArgumentException("Supervisor not found"));

                        Staff supervisor3 = (Staff) userRepository.findByEmail("staff3@ada.edu.az")
                                        .orElseThrow(() -> new IllegalArgumentException("Supervisor not found"));

                        Project project1 = new Project();
                        project1.setCategory(List.of("Artificial Intelligence"));
                        project1.setSubcategories(List.of("Machine Learning", "Natural Language Processing"));
                        project1.setTitle("AI Research Project");
                        project1.setDescription(
                                        "A project focused on artificial intelligence research, exploring new algorithms.");
                        project1.setObjectives(List.of("Develop new AI algorithms",
                                        "Apply AI solutions to real-world problems",
                                        "Collaborate with industry partners"));
                        project1.setStatus(Project.Status.OPEN);
                        project1.setSupervisor(supervisor);
                        project1.setResearchFocus(List.of("Research Project"));
                        project1.setCategory(List.of("Artificial Intelligence"));
                        project1.setDegreeRestriction(List.of("Undergraduate"));
                        project1.setMajorRestriction(List.of("Computer Science"));
                        project1.setStudyYearRestriction(List.of("Sophomore"));
                        project1.setType(ProjectType.GROUP);
                        project1.setMaxStudents(3);

                        projectRepository.save(project1);

                        Project project2 = new Project();
                        project2.setCategory(List.of("Web Development"));
                        project2.setSubcategories(List.of("Frontend Development", "Full Stack"));
                        project2.setTitle("Web Development Research Project");
                        project2.setDescription(
                                        "A project focused on Web Development research, exploring new algorithms.");
                        project2.setObjectives(List.of("Develop new WD algorithms",
                                        "Apply WD solutions to real-world problems",
                                        "Collaborate with industry partners"));
                        project2.setStatus(Project.Status.OPEN);
                        project2.setResearchFocus(List.of("Senior Design Project"));
                        project2.setCategory(List.of("Web Development"));
                        project2.setDegreeRestriction(List.of("Undergraduate"));
                        project2.setMajorRestriction(List.of("Computer Science"));
                        project2.setStudyYearRestriction(List.of("Freshman"));
                        project2.setSupervisor(supervisor);
                        project2.setType(ProjectType.INDIVIDUAL);
                        project2.setMaxStudents(1);

                        projectRepository.save(project2);

                        Project project3 = new Project();
                        project3.setCategory(List.of("Data Science"));
                        project3.setSubcategories(List.of("Big Data"));
                        project3.setTitle("Big Data Analytics Project");
                        project3.setDescription(
                                        "A project aimed at analyzing large datasets to extract meaningful insights and patterns.");
                        project3.setObjectives(List.of("Collect and preprocess large datasets",
                                        "Apply data mining techniques", "Visualize data insights"));
                        project3.setStatus(Project.Status.OPEN);
                        project3.setSupervisor(supervisor);
                        project3.setResearchFocus(List.of("Senior Design Project"));
                        project3.setDegreeRestriction(List.of("Undergraduate", "Graduate"));
                        project3.setMajorRestriction(List.of("Information Technology", "Computer Science"));
                        project3.setStudyYearRestriction(List.of("Sophomore", "Senior"));
                        project3.setType(ProjectType.GROUP);
                        project3.setMaxStudents(4);

                        projectRepository.save(project3);

                        Project project4 = new Project();
                        project4.setCategory(List.of("Cybersecurity"));
                        project4.setSubcategories(List.of("Network Security"));
                        project4.setTitle("Network Security Assessment");
                        project4.setDescription(
                                        "A project focused on evaluating and improving the security of network infrastructures.");
                        project4.setObjectives(List.of("Conduct vulnerability assessments",
                                        "Implement security protocols", "Develop incident response strategies."));
                        project4.setStatus(Project.Status.OPEN);
                        project4.setSupervisor(supervisor);
                        project4.setResearchFocus(List.of("Senior Design Project"));
                        project4.setDegreeRestriction(List.of("Graduate"));
                        project4.setMajorRestriction(List.of("Computer Science", "Information Technology"));
                        project4.setStudyYearRestriction(List.of("Sophomore"));
                        project4.setType(ProjectType.INDIVIDUAL);
                        project4.setMaxStudents(1);

                        projectRepository.save(project4);

                        Project project5 = new Project();
                        project5.setCategory(List.of("Software Engineering"));
                        project5.setSubcategories(List.of("DevOps"));
                        project5.setTitle("Agile Development of Web Application");
                        project5.setDescription(
                                        "A project to develop a web application using agile methodologies and best practices.");
                        project5.setObjectives(
                                        List.of("Gather user requirements", "Design and implement application features",
                                                        "Conduct user testing and feedback"));
                        project5.setStatus(Project.Status.OPEN);
                        project5.setSupervisor(supervisor);
                        project5.setResearchFocus(List.of("Senior Design Project"));
                        project5.setDegreeRestriction(List.of("Undergraduate"));
                        project5.setMajorRestriction(List.of("Computer Science"));
                        project5.setStudyYearRestriction(List.of("Sophomore"));
                        project5.setType(ProjectType.GROUP);
                        project5.setMaxStudents(5);

                        projectRepository.save(project5);

                        Project project6 = new Project();
                        project6.setCategory(List.of("Artificial Intelligence"));
                        project6.setSubcategories(List.of("Machine Learning"));
                        project6.setTitle("AI (Machine Learning) Project");
                        project6.setDescription(
                                        "A project using machine learning methodologies and best practices.");
                        project6.setObjectives(
                                        List.of("Gather user requirements", "Design and implement necessary features",
                                                        "Conduct user testing and feedback"));
                        project6.setStatus(Project.Status.OPEN);
                        project6.setSupervisor(supervisor2);
                        project6.setResearchFocus(List.of("Research Project"));
                        project6.setDegreeRestriction(List.of("Undergraduate"));
                        project6.setMajorRestriction(List.of("Computer Science"));
                        project6.setStudyYearRestriction(List.of("Sophomore"));
                        project6.setType(ProjectType.GROUP);
                        project6.setMaxStudents(3);

                        projectRepository.save(project6);

                        Project project7 = new Project();
                        project7.setCategory(List.of("Cybersecurity"));
                        project7.setSubcategories(List.of("Ethical Hacking"));
                        project7.setTitle("Cybersecurity (Ethical Hacking) Project");
                        project7.setDescription(
                                        "A project focused on ethical hacking practices to identify and secure vulnerabilities in systems.");
                        project7.setObjectives(
                                        List.of(
                                                        "Identify common security vulnerabilities",
                                                        "Simulate penetration testing scenarios",
                                                        "Develop a report with mitigation strategies"));
                        project7.setStatus(Project.Status.OPEN);
                        project7.setSupervisor(supervisor3); // make sure supervisor3 is already defined
                        project7.setResearchFocus(List.of("Research Project"));
                        project7.setDegreeRestriction(List.of("Undergraduate"));
                        project7.setMajorRestriction(List.of("Information Security", "Computer Science"));
                        project7.setStudyYearRestriction(List.of("Junior", "Senior"));
                        project7.setType(ProjectType.INDIVIDUAL);
                        project7.setMaxStudents(1);

                        projectRepository.save(project7);

                        Project project8 = new Project();
                        project8.setCategory(List.of("Software Engineering"));
                        project8.setSubcategories(List.of("Agile Development"));
                        project8.setTitle("Agile Software Engineering Project");
                        project8.setDescription(
                                        "A team-based project focused on applying Agile methodologies such as Scrum to develop a functional software product.");
                        project8.setObjectives(
                                        List.of(
                                                        "Plan and manage sprints",
                                                        "Implement iterative development practices",
                                                        "Conduct regular reviews and retrospectives"));
                        project8.setStatus(Project.Status.OPEN);
                        project8.setSupervisor(supervisor3);
                        project8.setResearchFocus(List.of("Capstone Project"));
                        project8.setDegreeRestriction(List.of("Undergraduate"));
                        project8.setMajorRestriction(List.of("Computer Engineering", "Information Technologies"));
                        project8.setStudyYearRestriction(List.of("Junior", "Senior"));
                        project8.setType(ProjectType.GROUP);
                        project8.setMaxStudents(4);

                        projectRepository.save(project8);
                }
        }
}
