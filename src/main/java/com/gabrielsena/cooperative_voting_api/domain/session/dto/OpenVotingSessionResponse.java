package com.gabrielsena.cooperative_voting_api.domain.session.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.Instant;
import java.util.UUID;

public record OpenVotingSessionResponse(
        UUID id,
        UUID topicId,
        Instant openedAt,
        Instant closesAt
) {
}
