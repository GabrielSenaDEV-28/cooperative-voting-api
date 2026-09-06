package com.gabrielsena.cooperative_voting_api.domain.topic;

import com.gabrielsena.cooperative_voting_api.domain.topic.dto.CreateVotingTopicRequest;
import com.gabrielsena.cooperative_voting_api.domain.topic.dto.CreateVotingTopicResponse;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(VotingTopicController.class)
class VotingTopicControllerTest {

    @MockitoBean
    private VotingTopicService votingTopicService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnCreatedWhenRequestIsValid() throws Exception {
        UUID topicId = UUID.randomUUID();

        CreateVotingTopicResponse response =
                new CreateVotingTopicResponse(
                        topicId,
                        "Annual budget approval"
                );

        when(votingTopicService.create(any(CreateVotingTopicRequest.class)))
                .thenReturn(response);

        mockMvc.perform(
                post("/api/v1/topics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                "title": "Annual budget approval"
                                }
                                """)
        )
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(topicId.toString()))
        .andExpect(jsonPath("$.title").value("Annual budget approval"));
    }

    @Test
    void shouldReturnBadRequestWhenTitleIsBlank() throws Exception {
        mockMvc.perform(
                post("/api/v1/topics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "title": "   "
                                }
                                """)
        )
        .andExpect(status().isBadRequest());

        verifyNoInteractions(votingTopicService);
    }

}
