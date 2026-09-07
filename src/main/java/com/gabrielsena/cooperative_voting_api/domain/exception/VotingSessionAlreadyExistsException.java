package com.gabrielsena.cooperative_voting_api.domain.exception;

public class VotingSessionAlreadyExistsException extends RuntimeException {

    public VotingSessionAlreadyExistsException(String message) {
        super(message);
    }
}
