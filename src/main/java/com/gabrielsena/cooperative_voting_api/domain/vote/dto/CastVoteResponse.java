package com.gabrielsena.cooperative_voting_api.domain.vote.dto;

import com.gabrielsena.cooperative_voting_api.domain.vote.VoteChoice;

import java.time.Instant;
import java.util.UUID;

public record CastVoteResponse(
        UUID id,
        UUID topicId,
        UUID associateId,
        VoteChoice choice,
        Instant votedAt
) {
}
