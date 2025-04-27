package az.edu.ada.SITE.Service;

import az.edu.ada.SITE.Entity.User;

/**
 * Service interface for handling user authentication.
 */
public interface UserService {

    /**
     * Authenticates a user based on their email and password.
     *
     * @param email    the email address of the user to authenticate
     * @param password the password of the user to authenticate
     * @return the authenticated user if the credentials are valid, or null if
     *         authentication fails
     */
    User authenticate(String email, String password);
}
