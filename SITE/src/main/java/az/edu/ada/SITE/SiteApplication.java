package az.edu.ada.SITE;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The main entry point for the Spring Boot application.
 * This class starts the application and initializes the Spring context.
 */
@SpringBootApplication
public class SiteApplication {

    /**
     * The main method that launches the Spring Boot application.
     *
     * @param args command-line arguments passed to the application
     */
    public static void main(String[] args) {
        SpringApplication.run(SiteApplication.class, args);
    }
}
