package az.edu.ada.SITE.DTO;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import az.edu.ada.SITE.Entity.Assignment;
import az.edu.ada.SITE.Entity.Staff;
import az.edu.ada.SITE.Entity.Project.ApplicationStatus;
import az.edu.ada.SITE.Entity.Project.ProjectType;
import az.edu.ada.SITE.Entity.Project.Status;

/**
 * Data Transfer Object for Project entity.
 * Contains all the relevant project information including the supervisor,
 * students, assignments, and deliverables.
 */
@Data
public class ProjectDTO {
        /**
         * The unique identifier of the project.
         */
        private Long id;

        /**
         * The title of the project.
         */
        private String title;

        /**
         * A description of the project.
         */
        private String description;

        /**
         * A list of objectives related to the project.
         */
        private List<String> objectives;

        /**
         * The type of the project (e.g., group, individual).
         */
        private ProjectType type;

        /**
         * Keywords associated with the project.
         */
        private String keywords;

        /**
         * A list of research focus areas for the project.
         */
        private List<String> researchFocus = new ArrayList<>();

        /**
         * A list of categories the project falls under.
         */
        private List<String> category = new ArrayList<>();

        /**
         * A list of study year restrictions for the project.
         */
        private List<String> studyYearRestriction = new ArrayList<>();

        /**
         * A list of degree restrictions for the project (e.g., Bachelor's, Master's).
         */
        private List<String> degreeRestriction = new ArrayList<>();

        /**
         * A list of major restrictions for the project.
         */
        private List<String> majorRestriction = new ArrayList<>();

        /**
         * The current status of the project (e.g., ongoing, completed).
         */
        private Status status;

        /**
         * The maximum number of students that can be assigned to the project.
         */
        private Integer maxStudents;

        /**
         * The staff member supervising the project.
         */
        private Staff supervisor;

        /**
         * A list of co-supervisors assigned to the project.
         */
        private List<Staff> coSupervisors;

        /**
         * A list of students currently assigned to the project.
         */
        private List<StudentDTO> students = new ArrayList<>();

        /**
         * A list of students who have requested to join the project.
         */
        private List<StudentDTO> requestedStudents = new ArrayList<>();

        /**
         * A list of subcategories that further define the project's focus.
         */
        private List<String> subcategories = new ArrayList<>();

        /**
         * The application status for the project (e.g., open, closed).
         */
        private ApplicationStatus appStatus;

        /**
         * A list of deliverables associated with the project.
         */
        private List<DeliverableDTO> deliverables = new ArrayList<>();

        /**
         * A list of assignments associated with the project.
         */
        private List<Assignment> assignments = new ArrayList<>();

        /**
         * Default constructor for ProjectDTO.
         */
        public ProjectDTO() {
        }

        /**
         * Parameterized constructor to initialize ProjectDTO with all project details.
         *
         * @param id                   The project ID.
         * @param title                The title of the project.
         * @param description          A description of the project.
         * @param objectives           A list of objectives for the project.
         * @param type                 The type of the project.
         * @param keywords             Keywords related to the project.
         * @param researchFocus        A list of research focus areas for the project.
         * @param category             A list of categories the project falls under.
         * @param studyYearRestriction A list of study year restrictions.
         * @param degreeRestriction    A list of degree restrictions.
         * @param majorRestriction     A list of major restrictions.
         * @param status               The status of the project.
         * @param maxStudents          The maximum number of students for the project.
         * @param supervisor           The supervisor of the project.
         * @param coSupervisors        A list of co-supervisors for the project.
         * @param students             A list of students assigned to the project.
         * @param requestedStudents    A list of students who have requested to join the
         *                             project.
         * @param subcategories        A list of subcategories for the project.
         * @param appStatus            The application status of the project.
         * @param deliverables         A list of deliverables for the project.
         * @param assignments          A list of assignments for the project.
         */
        public ProjectDTO(Long id, String title, String description, List<String> objectives, ProjectType type,
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
