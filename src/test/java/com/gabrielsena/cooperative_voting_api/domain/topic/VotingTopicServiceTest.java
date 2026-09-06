package com.gabrielsena.cooperative_voting_api.domain.topic;

import com.gabrielsena.cooperative_voting_api.domain.topic.dto.CreateVotingTopicRequest;
import com.gabrielsena.cooperative_voting_api.domain.topic.dto.CreateVotingTopicResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VotingTopicServiceTest {
    @Mock
    private VotingTopicRepository votingTopicRepository;

    @InjectMocks
    private VotingTopicService votingTopicService;

    @Test
    void shouldCreateVotingTopicAndReturnResponseWhenRequestIsValid() {

        CreateVotingTopicRequest request = new CreateVotingTopicRequest("Annual budget approval");
        VotingTopic savedTopic = new VotingTopic("Annual budget approval");
        UUID topicId = UUID.randomUUID();

        ArgumentCaptor<VotingTopic> topicCaptor = ArgumentCaptor.forClass(VotingTopic.class);

        ReflectionTestUtils.setField(savedTopic, "id", topicId);

        when(votingTopicRepository.save(any(VotingTopic.class))).thenReturn(savedTopic);

        CreateVotingTopicResponse response = votingTopicService.create(request);

        assertEquals("Annual budget approval", response.title());
        assertEquals(topicId, response.id());

        verify(votingTopicRepository).save(topicCaptor.capture());

        VotingTopic capturedTopic = topicCaptor.getValue();

        assertEquals("Annual budget approval", capturedTopic.getTitle());
    }
}
