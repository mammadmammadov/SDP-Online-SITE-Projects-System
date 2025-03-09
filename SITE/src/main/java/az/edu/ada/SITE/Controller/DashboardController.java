package az.edu.ada.SITE.Controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import az.edu.ada.SITE.Entity.User;
import az.edu.ada.SITE.Repository.UserRepository;

@Controller
@RequestMapping({ "", "/" })
public class DashboardController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/admin/welcome")
    public String showAdminDashboard(Model model, Principal principal) {
        try {
            String email = principal.getName();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            String fullName = user.getName() + " " + user.getSurname();
            model.addAttribute("adminName", fullName);

            return "admin_welcome";
        } catch (UsernameNotFoundException e) {
            return "redirect:/auth/login?error=user_not_found";
        }
    }

    @GetMapping("/staff/welcome")
    public String showStaffDashboard(Model model, Principal principal) {
        try {
            String email = principal.getName();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            String fullName = user.getName() + " " + user.getSurname();
            model.addAttribute("staffName", fullName);

            return "staff_welcome";
        } catch (UsernameNotFoundException e) {
            return "redirect:/auth/login?error=user_not_found";
        }
    }

    @GetMapping("/student/welcome")
    public String showStudentDashboard(Model model, Principal principal) {
        try {
            String email = principal.getName();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            String fullName = user.getName() + " " + user.getSurname();
            model.addAttribute("studentName", fullName);

            return "student_welcome";
        } catch (UsernameNotFoundException e) {
            return "redirect:/auth/login?error=user_not_found";
        }
    }
}
