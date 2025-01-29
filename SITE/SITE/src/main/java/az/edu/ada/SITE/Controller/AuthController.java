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
        return "login"; // Corresponds to login.html in the templates folder
    }

    @PostMapping("/login")
    public String loginUser(@RequestParam String username, @RequestParam String password, Model model) {
        System.out.println("Received login request with username: " + username);

        try {
            User user = userService.authenticate(username, password);
            if (user != null) {
                System.out.println("User authenticated: " + username);
                return "redirect:" + user.getRole().getWelcomePage();
            } else {
                System.out.println("Authentication failed for username: " + username);
                model.addAttribute("errorMessage", "Invalid username or password. Please try again.");
                return "login";
            }
        } catch (Exception e) {
            System.err.println("Error during authentication: " + e.getMessage());
            model.addAttribute("errorMessage", "An unexpected error occurred.");
            return "login";
        }
    }



}
