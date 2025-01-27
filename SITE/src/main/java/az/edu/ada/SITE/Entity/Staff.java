package az.edu.ada.SITE.Entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
public class Staff extends User {
    private String department;

    @ManyToMany
    private List<Project> projects;
}