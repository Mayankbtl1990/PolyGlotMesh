package com.infotact.polyglotmesh.api;

import java.util.concurrent.RejectedExecutionException;
import java.util.stream.Collectors;

import com.infotact.polyglotmesh.runtime.ScriptExecutionException;
import org.graalvm.polyglot.PolyglotException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebInputException;

@RestControllerAdvice
public class ApiExceptionHandler {

    private static final Logger log =
            LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<ApiError> handleValidation(
            WebExchangeBindException exception
    ) {
        String message = exception.getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .sorted()
                .collect(Collectors.joining("; "));

        return response(
                HttpStatus.BAD_REQUEST,
                "INVALID_REQUEST",
                message
        );
    }

    @ExceptionHandler(ServerWebInputException.class)
    public ResponseEntity<ApiError> handleUnreadableRequest(
            ServerWebInputException exception
    ) {
        return response(
                HttpStatus.BAD_REQUEST,
                "INVALID_REQUEST",
                "Request body must be valid JSON with language and code fields."
        );
    }

    @ExceptionHandler(PolyglotException.class)
    public ResponseEntity<ApiError> handleGuestFailure(
            PolyglotException exception
    ) {
        if (exception.isGuestException() || exception.isSyntaxError()) {
            return response(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    "SCRIPT_ERROR",
                    shorten(exception.getMessage())
            );
        }

        log.error("Polyglot runtime infrastructure failure", exception);

        return response(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "RUNTIME_ERROR",
                "The execution runtime failed. Check backend logs."
        );
    }

    @ExceptionHandler(RejectedExecutionException.class)
    public ResponseEntity<ApiError> handleBusyRuntime(
            RejectedExecutionException exception
    ) {
        return response(
                HttpStatus.SERVICE_UNAVAILABLE,
                "RUNTIME_BUSY",
                "Execution capacity is busy. Try again later."
        );
    }

    @ExceptionHandler(ScriptExecutionException.class)
    public ResponseEntity<ApiError> handleScriptExecution(
            ScriptExecutionException exception
    ) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(new ApiError(
                        exception.code(),
                        exception.getMessage(),
                        exception.execution(),
                        exception.guestStack()
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpectedFailure(
            Exception exception
    ) {
        log.error("Unexpected execution API failure", exception);

        return response(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "INTERNAL_ERROR",
                "An unexpected server error occurred."
        );
    }

    private ResponseEntity<ApiError> response(
            HttpStatus status,
            String code,
            String message
    ) {
        return ResponseEntity.status(status)
                .body(new ApiError(code, message));
    }

    private String shorten(String message) {
        if (message == null || message.isBlank()) {
            return "Script execution failed.";
        }

        return message.length() > 2000
                ? message.substring(0, 2000) + "..."
                : message;
    }
}