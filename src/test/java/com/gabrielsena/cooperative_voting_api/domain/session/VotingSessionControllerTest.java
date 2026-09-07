package com.gabrielsena.cooperative_voting_api.domain.session;

import com.gabrielsena.cooperative_voting_api.domain.exception.VotingSessionAlreadyExistsException;
import com.gabrielsena.cooperative_voting_api.domain.exception.VotingTopicNotFoundException;
import com.gabrielsena.cooperative_voting_api.domain.session.dto.OpenVotingSessionRequest;
import com.gabrielsena.cooperative_voting_api.domain.session.dto.OpenVotingSessionResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VotingSessionController.class)
class VotingSessionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VotingSessionService votingSessionService;

    @Test
    void shouldOpenVotingSession()  throws Exception {

        UUID topicId = UUID.randomUUID();
        UUID sessionId = UUID.randomUUID();

        OpenVotingSessionResponse response = new OpenVotingSessionResponse(
                sessionId,
                topicId,
                Instant.parse("2026-09-06T20:00:00Z"),
                Instant.parse("2026-09-06T20:05:00Z")
        );

        when(votingSessionService.openSession(topicId, new OpenVotingSessionRequest(5))).thenReturn(response);

        mockMvc.perform(
                post("/api/v1/topics/{topicId}/sessions", topicId)
                    .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "durationMinutes": 5
                                }
                                """)
        )
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(sessionId.toString()))
        .andExpect(jsonPath("$.topicId").value(topicId.toString()))
        .andExpect(jsonPath("$.openedAt").value("2026-09-06T20:00:00Z"))
        .andExpect(jsonPath("$.closesAt").value("2026-09-06T20:05:00Z"));
    }

    @Test
    void shouldReturnBadRequestWhenDurationIsNotPositive() throws Exception {
        UUID topicId = UUID.randomUUID();

        mockMvc.perform(
                post("/api/v1/topics/{topicId}/sessions", topicId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "durationMinutes": 0
                            }
                            """)
        )
        .andExpect(status().isBadRequest());

        verify(votingSessionService, never())
            .openSession(any(), any());

    }

    @Test
    void shouldReturnNotFoundWhenVotingTopicDoesNotExist() throws Exception {

        UUID topicId = UUID.randomUUID();

        when(votingSessionService.openSession(topicId, new OpenVotingSessionRequest(5)))
                .thenThrow(new VotingTopicNotFoundException("Voting topic not found"));

        mockMvc.perform(
                post("/api/v1/topics/{topicId}/sessions", topicId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "durationMinutes": 5
                        }
                        """)
        )
        .andExpect(status().isNotFound());

        verify(votingSessionService)
                .openSession(eq(topicId), any(OpenVotingSessionRequest.class));
    }

    @Test
    void shouldReturnConflictWhenVotingSessionAlreadyExists() throws Exception {

        UUID topicId = UUID.randomUUID();

        when(votingSessionService.openSession(topicId, new OpenVotingSessionRequest(5)))
                .thenThrow(new VotingSessionAlreadyExistsException("Voting session already exists for this topic"));

        mockMvc.perform(
                post("/api/v1/topics/{topicId}/sessions", topicId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "durationMinutes": 5
                        }
                        """)
        )
        .andExpect(status().isConflict());

        verify(votingSessionService).openSession(eq(topicId), any(OpenVotingSessionRequest.class));
    }
}
