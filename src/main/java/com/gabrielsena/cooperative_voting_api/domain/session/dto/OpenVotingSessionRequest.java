package com.gabrielsena.cooperative_voting_api.domain.session.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.time.Instant;
import java.util.UUID;

public record OpenVotingSessionRequest(
        @Positive
        Integer durationMinutes
) {
}
