package com.certimeter.tickets.exception;

import com.certimeter.tickets.dto.HttpResponse;
import com.certimeter.tickets.enumeration.HttpResponseEnum;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class TicketExceptionController {
    @ExceptionHandler(FailureException.class)
    public ResponseEntity<HttpResponse> handleFailureException(FailureException exception) {
        HttpResponseEnum responseEnum = exception.getHttpResponseEnum();

        String message = exception.getCustomMessage() != null ? exception.getCustomMessage() : responseEnum.getDescription();

        if (responseEnum == HttpResponseEnum.AUTHORIZATION_FAILED) {
            HttpResponse failureResponse = new HttpResponse(
                    responseEnum.getId(),
                    responseEnum.getDescription()
            );
            return ResponseEntity.status(responseEnum.getHttpStatus()).body(failureResponse);
        }
        HttpStatus httpStatusOfFailure = responseEnum.getHttpStatus();
        return ResponseEntity.status(httpStatusOfFailure).body(new HttpResponse(responseEnum.getId(), message));
    }

    // This method is called when a request body is invalid. It extracts the error messages from the exception and returns them as a string.

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getAllErrors().stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }
}

