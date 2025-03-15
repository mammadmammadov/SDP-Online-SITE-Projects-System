package az.edu.ada.SITE.Controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import az.edu.ada.SITE.Entity.User;

@Controller
@RequestMapping({ "", "/", "/auth" })
public class AuthController {

    @GetMapping({ "", "/" })
    public String handleRootRequest(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            User user = (User) authentication.getPrincipal();
            return switch (user.getRole()) {
                case ADMIN -> "redirect:/admin/welcome";
                case STAFF -> "redirect:/staff/projects";
                case STUDENT -> "redirect:/student/projects";
            };
        }
        return "redirect:/auth/login";
    }

    @GetMapping("/login")
    public String showLoginPage(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            User user = (User) authentication.getPrincipal();
            return switch (user.getRole()) {
                case ADMIN -> "redirect:/admin/welcome";
                case STAFF -> "redirect:/staff/projects";
                case STUDENT -> "redirect:/student/projects";
            };
        }
        return "login";
    }
}