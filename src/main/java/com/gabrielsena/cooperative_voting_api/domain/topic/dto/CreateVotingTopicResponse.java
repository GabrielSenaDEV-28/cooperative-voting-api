package com.gabrielsena.cooperative_voting_api.domain.topic.dto;

import java.util.UUID;

public record CreateVotingTopicResponse(
        UUID id,
        String title
) {
}
