package az.edu.ada.SITE.Controller;

import az.edu.ada.SITE.Entity.User;
import az.edu.ada.SITE.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String loginUser(@RequestParam String email, @RequestParam String password, Model model) {
        try {
            User user = userService.authenticate(email, password);
            if (user != null) {
                System.out.println("User authenticated: " + email);
                return "redirect:" + user.getRole().getWelcomePage();
            } else {
                return "redirect:/auth/login?error=true";
            }
        } catch (Exception e) {
            return "redirect:/auth/login?error=true";
        }
    }
}
