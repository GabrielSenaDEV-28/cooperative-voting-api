package com.gabrielsena.cooperative_voting_api.presentation.exception;

public record ApiErrorResponse(
        int status,
        String error,
        String message
) {
}