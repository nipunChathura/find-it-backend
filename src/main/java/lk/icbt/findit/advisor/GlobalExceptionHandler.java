package lk.icbt.findit.advisor;

import com.google.firebase.messaging.FirebaseMessagingException;
import jakarta.servlet.http.HttpServletRequest;
import lk.icbt.findit.common.ResponseCodes;
import lk.icbt.findit.common.ResponseStatus;
import lk.icbt.findit.exception.InvalidRequestException;
import lk.icbt.findit.response.FieldErrorItem;
import lk.icbt.findit.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private static final List<String> SENSITIVE_FIELDS = List.of("password", "currentPassword", "newPassword", "token");

    /**
     * Builds a standard API failure response with status, responseCode, responseMessage and fieldErrors.
     * All API failures use this format so clients always receive the same structure and the failure reason in responseMessage.
     */
    private static Response buildFailureResponse(String responseCode, String responseMessage, List<FieldErrorItem> fieldErrors) {
        Response response = new Response();
        response.setStatus(ResponseStatus.FAILURE.getStatus());
        response.setResponseCode(responseCode);
        response.setResponseMessage(responseMessage);
        response.setFieldErrors(fieldErrors != null ? fieldErrors : Collections.singletonList(
                new FieldErrorItem("error", responseMessage, null)
        ));
        return response;
    }

    @ExceptionHandler({ClassNotFoundException.class})
    @ResponseBody
    public Response classNotFoundHandler(ClassNotFoundException e, HttpServletRequest request) {
        log.error("API error [{} {}]: ClassNotFoundException - {}", request.getMethod(), request.getRequestURI(), e.getMessage());
        String message = "An internal error occurred. Please try again later.";
        return buildFailureResponse(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()), message, null);
    }

    @ExceptionHandler(InvalidRequestException.class)
    @ResponseBody
    public Response handleInvalidRequestException(InvalidRequestException ex, HttpServletRequest request) {
        log.warn("API error [{} {}]: InvalidRequestException code={} message={}", request.getMethod(), request.getRequestURI(), ex.getCode(), ex.getMessage());
        return buildFailureResponse(ex.getCode(), ex.getMessage(), null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Response handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        List<FieldErrorItem> items = fieldErrors.stream()
                .map(err -> new FieldErrorItem(
                        err.getField(),
                        err.getDefaultMessage(),
                        maskSensitive(err.getField(), err.getRejectedValue())
                ))
                .collect(Collectors.toList());
        String summary = fieldErrors.stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining("; "));
        String message = "Validation failed. " + summary;

        log.warn("API validation error [{} {}]: {} - fieldErrors={}", request.getMethod(), request.getRequestURI(), summary, items.stream().map(FieldErrorItem::getField).toList());

        return buildFailureResponse(ResponseCodes.VALIDATION_ERROR_CODE, message, items);
    }

    private static String maskSensitive(String field, Object rejectedValue) {
        if (rejectedValue == null) return null;
        String s = rejectedValue.toString();
        if (SENSITIVE_FIELDS.stream().anyMatch(f -> field.equalsIgnoreCase(f))) return "[REDACTED]";
        return s.length() > 100 ? s.substring(0, 100) + "..." : s;
    }

    @ExceptionHandler(AccessDeniedException.class)
    @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    public Response handleAccessDeniedException(AccessDeniedException ex, HttpServletRequest request) {
        log.warn("API error [{} {}]: AccessDenied - {}", request.getMethod(), request.getRequestURI(), ex.getMessage());
        String message = "Access denied. Only SYSADMIN can perform this operation.";
        return buildFailureResponse(ResponseCodes.FORBIDDEN_NOT_SYSADMIN_CODE, message, null);
    }

    @ExceptionHandler(FirebaseMessagingException.class)
    @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.BAD_GATEWAY)
    @ResponseBody
    public Response handleFirebaseMessagingException(FirebaseMessagingException ex, HttpServletRequest request) {
        log.error("API error [{} {}]: FirebaseMessagingException - {}", request.getMethod(), request.getRequestURI(), ex.getMessage());
        String message = "Failed to send push notification: " + ex.getMessage();
        return buildFailureResponse(ResponseCodes.FIREBASE_MESSAGING_ERROR_CODE, message, null);
    }

    @ExceptionHandler(Exception.class)
    @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public Response handleUnexpectedException(Exception ex, HttpServletRequest request) {
        log.error("API unexpected error [{} {}]: {} - {}", request.getMethod(), request.getRequestURI(), ex.getClass().getSimpleName(), ex.getMessage(), ex);
        String message = "An unexpected error occurred. Please try again later.";
        return buildFailureResponse(ResponseCodes.FAILED_CODE, message, null);
    }
}
