package az.edu.ada.SITE.DTO;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import az.edu.ada.SITE.Entity.Assignment;
import az.edu.ada.SITE.Entity.Staff;
import az.edu.ada.SITE.Entity.Project.ApplicationStatus;
import az.edu.ada.SITE.Entity.Project.ProjectType;
import az.edu.ada.SITE.Entity.Project.Status;

@Data
public class ProjectDTO {
        private Long id;
        private String title;
        private String description;
        private String objectives;
        private ProjectType type;
        private String keywords;
        private List<String> researchFocus = new ArrayList<>();
        private List<String> category = new ArrayList<>();
        private List<String> studyYearRestriction = new ArrayList<>();
        private List<String> degreeRestriction = new ArrayList<>();
        private List<String> majorRestriction = new ArrayList<>();
        private Status status;
        private Integer maxStudents;
        private Staff supervisor;

        private List<Staff> coSupervisors;
        private List<StudentDTO> students = new ArrayList<>();
        private List<StudentDTO> requestedStudents = new ArrayList<>();
        private List<String> subcategories = new ArrayList<>();
        private ApplicationStatus appStatus;
        private List<DeliverableDTO> deliverables = new ArrayList<>();
        private List<Assignment> assignments = new ArrayList<>();

        public ProjectDTO() {
        }

        public ProjectDTO(Long id, String title, String description, String objectives, ProjectType type,
                        String keywords,
                        List<String> researchFocus, List<String> category, List<String> studyYearRestriction,
                        List<String> degreeRestriction, List<String> majorRestriction,
                        Status status, Integer maxStudents, Staff supervisor, List<Staff> coSupervisors,
                        List<StudentDTO> students,
                        List<StudentDTO> requestedStudents,
                        List<String> subcategories, ApplicationStatus appStatus,
                        List<DeliverableDTO> deliverables, List<Assignment> assignments) {
                this.id = id;
                this.title = title;
                this.description = description;
                this.objectives = objectives;
                this.type = type;
                this.keywords = keywords;
                this.researchFocus = researchFocus;
                this.category = category;
                this.studyYearRestriction = studyYearRestriction;
                this.degreeRestriction = degreeRestriction;
                this.majorRestriction = majorRestriction;
                this.status = status;
                this.maxStudents = maxStudents;
                this.supervisor = supervisor;
                this.coSupervisors = coSupervisors;
                this.students = students;
                this.requestedStudents = requestedStudents;
                this.subcategories = subcategories;
                this.appStatus = appStatus;
                this.deliverables = deliverables;
                this.assignments = assignments;
        }
}
