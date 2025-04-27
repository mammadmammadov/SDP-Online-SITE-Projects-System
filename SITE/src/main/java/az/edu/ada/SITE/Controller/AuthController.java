package az.edu.ada.SITE.Controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import az.edu.ada.SITE.Entity.User;

/**
 * Controller for handling authentication-related requests such as
 * root access and login page navigation.
 */
@Controller
@RequestMapping({ "", "/", "/auth" })
public class AuthController {

    /**
     * Handles requests to the root ("/", "", or "/auth").
     * Redirects authenticated users based on their role to their respective home
     * pages.
     * Unauthenticated users are redirected to the login page.
     *
     * @param authentication the current authentication object.
     * @return redirect URL to the appropriate page.
     */
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

    /**
     * Displays the login page.
     * If the user is already authenticated, redirects them to their role-specific
     * home page.
     *
     * @param authentication the current authentication object.
     * @return the name of the login view or redirect URL for authenticated users.
     */
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
