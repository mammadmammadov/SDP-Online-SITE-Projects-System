package az.edu.ada.SITE.Repository;

import az.edu.ada.SITE.Entity.Project;
import az.edu.ada.SITE.Entity.Staff;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
  List<Project> findBySupervisor(Staff supervisor);
}
