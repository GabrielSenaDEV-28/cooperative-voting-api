package com.gabrielsena.cooperative_voting_api.domain.vote.dto;

import com.gabrielsena.cooperative_voting_api.domain.vote.VoteChoice;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CastVoteRequest(
        @NotNull UUID associateId,
        @NotNull VoteChoice choice
) {
}
