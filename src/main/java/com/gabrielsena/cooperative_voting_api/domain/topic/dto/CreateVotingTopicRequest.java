package com.gabrielsena.cooperative_voting_api.domain.topic.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateVotingTopicRequest(
        @NotBlank
        @Size(max = 255)
        String title
) {
}
