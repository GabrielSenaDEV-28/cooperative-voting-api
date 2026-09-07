package com.gabrielsena.cooperative_voting_api.presentation.exception;

import com.gabrielsena.cooperative_voting_api.domain.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(VotingTopicNotFoundException.class)
    public ResponseEntity<Void> handleVotingNotFound(
            VotingTopicNotFoundException exception
    ) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @ExceptionHandler(VotingSessionAlreadyExistsException.class)
    public ResponseEntity<Void> handleVotingSessionAlreadyExists(
            VotingSessionAlreadyExistsException exception
    ) {
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @ExceptionHandler(VotingSessionNotFoundException.class)
    public ResponseEntity<Void> handleVotingSessionNotFound(
            VotingSessionNotFoundException exception
    ) {
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @ExceptionHandler(VotingSessionClosedException.class)
    public ResponseEntity<Void> handleVotingSessionClosed(
            VotingSessionClosedException exception
    ) {
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @ExceptionHandler(AssociateAlreadyVotedException.class)
    public ResponseEntity<Void> handleAssociateAlreadyVoted(
            AssociateAlreadyVotedException exception
    ) {
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }
}
