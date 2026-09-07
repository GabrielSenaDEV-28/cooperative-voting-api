package com.gabrielsena.cooperative_voting_api.domain.session;

import com.gabrielsena.cooperative_voting_api.domain.session.dto.OpenVotingSessionRequest;
import com.gabrielsena.cooperative_voting_api.domain.session.dto.OpenVotingSessionResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/topics/{topicId}/sessions")
@RequiredArgsConstructor
public class VotingSessionController {

    private final VotingSessionService votingSessionService;

    @PostMapping
    public ResponseEntity<OpenVotingSessionResponse> createVotingSession(@PathVariable UUID topicId, @Valid @RequestBody OpenVotingSessionRequest request) {
        OpenVotingSessionResponse response = votingSessionService.openSession(topicId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
