package lk.icbt.findit.advisor;

import com.google.firebase.messaging.FirebaseMessagingException;
import lk.icbt.findit.common.ResponseCodes;
import lk.icbt.findit.common.ResponseStatus;
import lk.icbt.findit.exception.InvalidRequestException;
import lk.icbt.findit.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({ClassNotFoundException.class})
    @ResponseBody
    public Response classNotFoundHandler(ClassNotFoundException e){
        Response response = new Response();
        response.setStatus(ResponseStatus.FAILURE.getStatus());
        response.setResponseMessage(e.getMessage());
        response.setResponseCode(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        return response;
    }

    @ExceptionHandler(InvalidRequestException.class)
    @ResponseBody
    public Response handleInvalidRequestException(InvalidRequestException ex) {
        Response response = new Response();
        response.setStatus(ResponseStatus.FAILURE.getStatus());
        response.setResponseMessage(ex.getMessage());
        response.setResponseCode(ex.getCode());
        return response;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Response handleValidationException(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        Response response = new Response();
        response.setStatus(ResponseStatus.FAILURE.getStatus());
        response.setResponseCode(ResponseCodes.FAILED_CODE);
        response.setResponseMessage(message);
        return response;
    }

    @ExceptionHandler(AccessDeniedException.class)
    @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    public Response handleAccessDeniedException(AccessDeniedException ex) {
        Response response = new Response();
        response.setStatus(ResponseStatus.FAILURE.getStatus());
        response.setResponseCode(ResponseCodes.FORBIDDEN_NOT_SYSADMIN_CODE);
        response.setResponseMessage("Access denied. Only SYSADMIN can perform this operation.");
        return response;
    }

    @ExceptionHandler(FirebaseMessagingException.class)
    @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.BAD_GATEWAY)
    @ResponseBody
    public Response handleFirebaseMessagingException(FirebaseMessagingException ex) {
        Response response = new Response();
        response.setStatus(ResponseStatus.FAILURE.getStatus());
        response.setResponseCode(ResponseCodes.FIREBASE_MESSAGING_ERROR_CODE);
        response.setResponseMessage("Failed to send push notification: " + ex.getMessage());
        return response;
    }
}
