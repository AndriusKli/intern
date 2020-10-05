package uk.co.zenitech.intern.errorhandling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import uk.co.zenitech.intern.errorhandling.exceptions.ParsingException;

import javax.validation.ConstraintViolationException;
import java.net.URISyntaxException;
import java.util.NoSuchElementException;

@ControllerAdvice
@Component
public class ErrorHandler {
    private static final Logger logger = LoggerFactory.getLogger(ErrorHandler.class);

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleEntryNotFound(NoSuchElementException e, ServletWebRequest request) {
        logger.info("Error encountered: {} Reason: {}", e.getClass(), e.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(request.getRequest().getRequestURI(), e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleEntryNotFound(ConstraintViolationException e, ServletWebRequest request) {
        logger.info("Error encountered: {} Reason: {}", e.getClass(), e.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(request.getRequest().getRequestURI(), e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ParsingException.class)
    public ResponseEntity<ErrorResponse> handleParsingException(ParsingException e, ServletWebRequest request) {
        logger.info("Error encountered: {} Reason: {}", e.getClass(), e.getMessage(), e);
        ErrorResponse errorResponse = new ErrorResponse(request.getRequest().getRequestURI(), e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(URISyntaxException.class)
    public ResponseEntity<ErrorResponse> handleURIException(URISyntaxException e, ServletWebRequest request) {
        logger.info("Error encountered: {} Reason: {}", e.getClass(), e.getMessage(), e);
        ErrorResponse errorResponse = new ErrorResponse(request.getRequest().getRequestURI(), e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e, ServletWebRequest request) {
        logger.info("Error encountered: {} Reason: {}", e.getClass(), e.getMessage(), e);
        ErrorResponse errorResponse = new ErrorResponse(request.getRequest().getRequestURI(), e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
