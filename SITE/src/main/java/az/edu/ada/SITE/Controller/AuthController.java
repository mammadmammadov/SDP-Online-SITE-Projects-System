package az.edu.ada.SITE.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping({"", "/", "/auth"})
public class AuthController {

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }
}
