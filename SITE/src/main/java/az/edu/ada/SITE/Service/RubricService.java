package az.edu.ada.SITE.Service;

import az.edu.ada.SITE.Entity.Rubric;
import az.edu.ada.SITE.Repository.RubricRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RubricService {
    private final RubricRepository rubricRepository;

    public RubricService(RubricRepository rubricRepository) {
        this.rubricRepository = rubricRepository;
    }

    public List<Rubric> getRubricsByProjectId(Long projectId) {
        return rubricRepository.findByProjectId(projectId);
    }

    public Rubric saveRubric(Rubric rubric) {
        return rubricRepository.save(rubric);
    }

    public void deleteRubric(Long id) {
        rubricRepository.deleteById(id);
    }

    public Optional<Rubric> getRubricById(Long id) {
        return rubricRepository.findById(id);
    }

}