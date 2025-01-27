package az.edu.ada.SITE.Service;

import az.edu.ada.SITE.Entity.User;
import az.edu.ada.SITE.Repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }
}

