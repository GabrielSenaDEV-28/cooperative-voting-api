package com.gabrielsena.cooperative_voting_api.domain.exception;

public class AssociateAlreadyVotedException extends RuntimeException {
    public AssociateAlreadyVotedException(String message) {
        super(message);
    }
}
