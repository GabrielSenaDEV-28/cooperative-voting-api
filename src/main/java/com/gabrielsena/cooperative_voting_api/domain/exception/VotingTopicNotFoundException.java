package com.gabrielsena.cooperative_voting_api.domain.exception;

public class VotingTopicNotFoundException extends RuntimeException {

    public VotingTopicNotFoundException(String message) {
        super(message);
    }
}
