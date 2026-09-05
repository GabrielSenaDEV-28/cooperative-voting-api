package com.gabrielsena.cooperative_voting_api.domain.topic;

import com.gabrielsena.cooperative_voting_api.domain.topic.dto.CreateVotingTopicRequest;
import com.gabrielsena.cooperative_voting_api.domain.topic.dto.CreateVotingTopicResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/topics")
@RequiredArgsConstructor
public class VotingTopicController {
    private final VotingTopicService votingTopicService;

    @PostMapping
    public ResponseEntity<CreateVotingTopicResponse> create(@Valid @RequestBody CreateVotingTopicRequest request) {
        CreateVotingTopicResponse response = votingTopicService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
