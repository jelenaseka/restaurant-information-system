package akatsuki.restaurantsysteminformation.room.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class RoomExistsException extends RuntimeException {
    public RoomExistsException() {
        super();
    }

    public RoomExistsException(String message) {
        super(message);
    }

    public RoomExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
