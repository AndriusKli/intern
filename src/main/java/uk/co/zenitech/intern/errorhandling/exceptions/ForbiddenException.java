package uk.co.zenitech.intern.errorhandling.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN, reason = "Failed to authenticate")
public class ForbiddenException  extends RuntimeException{
    public ForbiddenException(String message) {
        super(message);
    }
}
