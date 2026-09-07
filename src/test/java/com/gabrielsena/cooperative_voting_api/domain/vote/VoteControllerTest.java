package com.gabrielsena.cooperative_voting_api.domain.vote;

import com.gabrielsena.cooperative_voting_api.domain.exception.AssociateAlreadyVotedException;
import com.gabrielsena.cooperative_voting_api.domain.exception.VotingSessionClosedException;
import com.gabrielsena.cooperative_voting_api.domain.exception.VotingSessionNotFoundException;
import com.gabrielsena.cooperative_voting_api.domain.vote.dto.CastVoteResponse;
import com.gabrielsena.cooperative_voting_api.presentation.mobile.VoteSelectionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VoteController.class)
class VoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VoteService voteService;

    @MockitoBean
    private VoteSelectionService voteSelectionService;

    @Test
    void shouldReturnCreatedWhenVoteIsCastSuccessfully() throws Exception {
        UUID topicId = UUID.randomUUID();
        UUID voteId = UUID.randomUUID();
        UUID associateId = UUID.randomUUID();
        Instant votedAt = Instant.parse("2026-09-07T03:00:00Z");

        CastVoteResponse response = new CastVoteResponse(
                voteId,
                topicId,
                associateId,
                VoteChoice.YES,
                votedAt
        );

        when(voteService.castVote(eq(topicId), any()))
                .thenReturn(response);

        mockMvc.perform(
                        post("/api/v1/topics/{topicId}/votes", topicId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                    {
                                      "associateId": "%s",
                                      "choice": "YES"
                                    }
                                    """.formatted(associateId))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(voteId.toString()))
                .andExpect(jsonPath("$.topicId").value(topicId.toString()))
                .andExpect(jsonPath("$.associateId").value(associateId.toString()))
                .andExpect(jsonPath("$.choice").value("YES"))
                .andExpect(jsonPath("$.votedAt").value("2026-09-07T03:00:00Z"));
    }

    @Test
    void shouldReturnBadRequestWhenRequestIsInvalid() throws Exception {
        UUID topicId = UUID.randomUUID();

        mockMvc.perform(
                        post("/api/v1/topics/{topicId}/votes", topicId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                    {
                                      "associateId": null,
                                      "choice": null
                                    }
                                    """)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnConflictWhenVotingSessionDoesNotExist() throws Exception {
        UUID topicId = UUID.randomUUID();
        UUID associateId = UUID.randomUUID();

        when(voteService.castVote(eq(topicId), any()))
                .thenThrow(new VotingSessionNotFoundException(
                        "Voting session not found for this topic"
                ));

        mockMvc.perform(
                        post("/api/v1/topics/{topicId}/votes", topicId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                    {
                                      "associateId": "%s",
                                      "choice": "YES"
                                    }
                                    """.formatted(associateId))
                )
                .andExpect(status().isConflict());
    }

    @Test
    void shouldReturnConflictWhenVotingSessionIsClosed() throws Exception {
        UUID topicId = UUID.randomUUID();
        UUID associateId = UUID.randomUUID();

        when(voteService.castVote(eq(topicId), any()))
                .thenThrow(new VotingSessionClosedException(
                        "Voting session is closed"
                ));

        mockMvc.perform(
                        post("/api/v1/topics/{topicId}/votes", topicId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                    {
                                      "associateId": "%s",
                                      "choice": "YES"
                                    }
                                    """.formatted(associateId))
                )
                .andExpect(status().isConflict());
    }

    @Test
    void shouldReturnConflictWhenAssociateHasAlreadyVoted() throws Exception {
        UUID topicId = UUID.randomUUID();
        UUID associateId = UUID.randomUUID();

        when(voteService.castVote(eq(topicId), any()))
                .thenThrow(new AssociateAlreadyVotedException(
                        "Associate has already voted on this topic"
                ));

        mockMvc.perform(
                        post("/api/v1/topics/{topicId}/votes", topicId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                    {
                                      "associateId": "%s",
                                      "choice": "YES"
                                    }
                                    """.formatted(associateId))
                )
                .andExpect(status().isConflict());
    }
}