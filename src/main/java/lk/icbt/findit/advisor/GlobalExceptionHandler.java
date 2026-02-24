package lk.icbt.findit.advisor;

import lk.icbt.findit.common.ResponseStatus;
import lk.icbt.findit.exception.InvalidRequestException;
import lk.icbt.findit.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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

}
