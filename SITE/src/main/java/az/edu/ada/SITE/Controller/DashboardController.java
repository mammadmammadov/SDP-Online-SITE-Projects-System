package az.edu.ada.SITE.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping({ "", "/" })
public class DashboardController {

    @GetMapping("/admin/welcome")
    public String showAdminDashboard() {
        return "admin_welcome";
    }

    @GetMapping("/staff/welcome")
    public String showStaffDashboard() {
        return "staff_welcome";
    }

    @GetMapping("/student/welcome")
    public String showStudentDashboard() {
        return "student_welcome";
    }
}
