package com.gabrielsena.cooperative_voting_api.domain.vote.dto;

import java.util.UUID;

public record VotingResultResponse(
        UUID topicId,
        long yesVotes,
        long noVotes,
        long totalVotes
) {}