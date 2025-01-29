package az.edu.ada.SITE.Service.Impl;

import az.edu.ada.SITE.Entity.User;
import az.edu.ada.SITE.Repository.UserRepository;
import az.edu.ada.SITE.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User authenticate(String username, String password) {
        return userRepository.findByUsernameAndPassword(username, password)
                .orElse(null); // Returns null if no matching user is found
    }
}
