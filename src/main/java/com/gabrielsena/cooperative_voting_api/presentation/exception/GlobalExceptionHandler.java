package com.gabrielsena.cooperative_voting_api.presentation.exception;

import com.gabrielsena.cooperative_voting_api.domain.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(VotingTopicNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleVotingNotFound(
            VotingTopicNotFoundException exception
    ) {
        return buildErrorResponse(
                HttpStatus.NOT_FOUND,
                exception.getMessage()
        );
    }

    @ExceptionHandler(VotingSessionAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleVotingSessionAlreadyExists(
            VotingSessionAlreadyExistsException exception
    ) {
        return buildErrorResponse(
                HttpStatus.CONFLICT,
                exception.getMessage()
        );
    }

    @ExceptionHandler(VotingSessionNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleVotingSessionNotFound(
            VotingSessionNotFoundException exception
    ) {
        return buildErrorResponse(
                HttpStatus.CONFLICT,
                exception.getMessage()
        );
    }

    @ExceptionHandler(VotingSessionClosedException.class)
    public ResponseEntity<ApiErrorResponse> handleVotingSessionClosed(
            VotingSessionClosedException exception
    ) {
        return buildErrorResponse(
                HttpStatus.CONFLICT,
                exception.getMessage()
        );
    }

    @ExceptionHandler(AssociateAlreadyVotedException.class)
    public ResponseEntity<ApiErrorResponse> handleAssociateAlreadyVoted(
            AssociateAlreadyVotedException exception
    ) {
        return buildErrorResponse(
                HttpStatus.CONFLICT,
                exception.getMessage()
        );
    }

    private ResponseEntity<ApiErrorResponse> buildErrorResponse(
            HttpStatus status,
            String message
    ) {
        ApiErrorResponse response = new ApiErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                message
        );

        return ResponseEntity
                .status(status)
                .body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationException(
            MethodArgumentNotValidException exception
    ) {
        String message = exception
                .getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(fieldError ->
                        fieldError.getField() + ": " + fieldError.getDefaultMessage()
                )
                .orElse("Invalid request");

        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                message
        );
    }
}