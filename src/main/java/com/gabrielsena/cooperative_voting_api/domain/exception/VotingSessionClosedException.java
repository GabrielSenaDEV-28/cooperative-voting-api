package com.gabrielsena.cooperative_voting_api.domain.exception;

public class VotingSessionClosedException extends RuntimeException {
    public VotingSessionClosedException(String message) {
        super(message);
    }
}
