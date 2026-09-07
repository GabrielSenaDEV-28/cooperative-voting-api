package com.gabrielsena.cooperative_voting_api.domain.vote;

import com.gabrielsena.cooperative_voting_api.domain.vote.dto.CastVoteRequest;
import com.gabrielsena.cooperative_voting_api.domain.vote.dto.CastVoteResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/topics/{topicId}/votes")
@RequiredArgsConstructor
public class VoteController {

    private final VoteService voteService;

    @PostMapping
    public ResponseEntity<CastVoteResponse> castVote(
            @PathVariable UUID topicId,
            @Valid @RequestBody CastVoteRequest request) {

        CastVoteResponse response = voteService.castVote(topicId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
