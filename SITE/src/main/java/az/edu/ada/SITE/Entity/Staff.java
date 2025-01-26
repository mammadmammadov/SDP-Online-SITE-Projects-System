package az.edu.ada.SITE.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class Staff extends User {
    private String department;

    @ManyToMany
    private List<Project> projects;
}