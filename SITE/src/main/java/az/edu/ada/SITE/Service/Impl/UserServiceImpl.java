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

    /**
     * Authenticates a user by matching the provided email and password.
     * 
     * @param email    The email of the user to authenticate.
     * @param password The password of the user to authenticate.
     * @return The User entity if the credentials are correct, or null if no match
     *         is found.
     */
    @Override
    public User authenticate(String email, String password) {
        return userRepository.findByEmailAndPassword(email, password)
                .orElse(null);
    }
}
