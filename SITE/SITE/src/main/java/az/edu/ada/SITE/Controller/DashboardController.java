package az.edu.ada.SITE.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping({ "", "/" })
public class DashboardController {

    @GetMapping("/admin/welcome")
    public String showAdminDashboard() {
        return "admin_welcome";  // Corresponds to admin_welcome.html in the templates folder
    }

    @GetMapping("/staff/welcome")
    public String showStaffDashboard() {
        return "staff_welcome";  // Corresponds to staff_welcome.html in the templates folder
    }

    @GetMapping("/student/welcome")
    public String showStudentDashboard() {
        return "student_welcome";  // Corresponds to student_welcome.html in the templates folder
    }
}
