package az.edu.ada.SITE.CustomException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Custom exception thrown when a requested resource is not found.
 * Automatically returns a 404 Not Found HTTP status.
 */
@Getter
@Setter
@ToString
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFound extends RuntimeException {

    /** The name of the resource that was not found. */
    private String resourceName;

    /** The field name used to search for the resource. */
    private String fieldName;

    /** The field value used to search for the resource. */
    private Object fieldValue;

    /**
     * Constructs a new {@code ResourceNotFound} exception with details about the
     * missing resource.
     *
     * @param resourceName the name of the resource (e.g., "User", "Project").
     * @param fieldName    the name of the field that was searched (e.g., "id",
     *                     "email").
     * @param fieldValue   the value of the field that was used in the search.
     */
    public ResourceNotFound(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s : '%s'", resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }
}
