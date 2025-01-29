package az.edu.ada.SITE.Service;

import az.edu.ada.SITE.Entity.User;

public interface UserService {
    User authenticate(String username, String password);
}
