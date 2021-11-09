package akatsuki.restaurantsysteminformation.restauranttable.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class RestaurantTableNotAvailableException extends RuntimeException {
    public RestaurantTableNotAvailableException(String message) {
        super(message);
    }
}
