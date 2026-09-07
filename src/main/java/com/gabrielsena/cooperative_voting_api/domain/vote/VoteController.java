package com.gabrielsena.cooperative_voting_api.domain.vote;

import com.gabrielsena.cooperative_voting_api.domain.vote.dto.CastVoteRequest;
import com.gabrielsena.cooperative_voting_api.domain.vote.dto.CastVoteResponse;
import com.gabrielsena.cooperative_voting_api.domain.vote.dto.VotingResultResponse;
import com.gabrielsena.cooperative_voting_api.presentation.mobile.VoteSelectionService;
import com.gabrielsena.cooperative_voting_api.presentation.mobile.dto.MobileSelectionResponse;
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
    private final VoteSelectionService voteSelectionService;

    @PostMapping
    public ResponseEntity<CastVoteResponse> castVote(
            @PathVariable UUID topicId,
            @Valid @RequestBody CastVoteRequest request) {

        CastVoteResponse response = voteService.castVote(topicId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/form")
    public ResponseEntity<MobileSelectionResponse> getVoteForm(
            @PathVariable UUID topicId,
            @RequestParam UUID associateId
    ) {
        MobileSelectionResponse response =
                voteSelectionService.buildVoteSelection(topicId, associateId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/result")
    public ResponseEntity<VotingResultResponse> getVotingResult(
            @PathVariable UUID topicId
    ) {
        VotingResultResponse response = voteService.getVotingResult(topicId);

        return ResponseEntity.ok(response);
    }

}
