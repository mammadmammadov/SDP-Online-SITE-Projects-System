package az.edu.ada.SITE.Controller;

import az.edu.ada.SITE.Enum.Role;
import az.edu.ada.SITE.Entity.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public class RegistrationController {
    @GetMapping("/register")
    public String showRegistrationPage(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(
            @ModelAttribute("user") User user,
            @RequestParam String name,
            @RequestParam String surname,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam Role role,
            Model model) {

        model.addAttribute("message", "User registered successfully!");

        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLoginPage(Model model) {
        if (model.containsAttribute("message")) {
            model.addAttribute("message", model.getAttribute("message"));
        }
        return "login";
    }
}
