package com.gabrielsena.cooperative_voting_api.domain.topic;

import com.gabrielsena.cooperative_voting_api.domain.topic.dto.CreateVotingTopicRequest;
import com.gabrielsena.cooperative_voting_api.domain.topic.dto.CreateVotingTopicResponse;
import com.gabrielsena.cooperative_voting_api.presentation.mobile.VotingTopicFormService;
import com.gabrielsena.cooperative_voting_api.presentation.mobile.dto.MobileFormResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/topics")
@RequiredArgsConstructor
public class VotingTopicController {

    private final VotingTopicService votingTopicService;
    private final VotingTopicFormService votingTopicFormService;

    @PostMapping
    public ResponseEntity<CreateVotingTopicResponse> create(@Valid @RequestBody CreateVotingTopicRequest request) {
        CreateVotingTopicResponse response = votingTopicService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/form")
    public ResponseEntity<MobileFormResponse> getCreationForm() {
        MobileFormResponse response = votingTopicFormService.buildCreationForm();

        return ResponseEntity.ok(response);
    }
}
