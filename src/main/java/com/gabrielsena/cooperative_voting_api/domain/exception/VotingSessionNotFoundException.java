package com.gabrielsena.cooperative_voting_api.domain.exception;

public class VotingSessionNotFoundException extends RuntimeException {
    public VotingSessionNotFoundException(String message) {
        super(message);
    }
}
