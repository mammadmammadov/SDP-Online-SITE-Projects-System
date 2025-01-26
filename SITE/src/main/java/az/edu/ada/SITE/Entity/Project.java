package az.edu.ada.SITE.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String project_title;

    private String project_description;

    private String member_size;

    @OneToMany(mappedBy = "project")
    private List<Student> students;
}
