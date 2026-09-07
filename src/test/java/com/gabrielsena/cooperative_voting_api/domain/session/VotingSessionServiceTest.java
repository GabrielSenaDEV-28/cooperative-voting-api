package com.gabrielsena.cooperative_voting_api.domain.session;

import com.gabrielsena.cooperative_voting_api.domain.exception.VotingSessionAlreadyExistsException;
import com.gabrielsena.cooperative_voting_api.domain.exception.VotingTopicNotFoundException;
import com.gabrielsena.cooperative_voting_api.domain.session.dto.OpenVotingSessionRequest;
import com.gabrielsena.cooperative_voting_api.domain.session.dto.OpenVotingSessionResponse;
import com.gabrielsena.cooperative_voting_api.domain.topic.VotingTopic;
import com.gabrielsena.cooperative_voting_api.domain.topic.VotingTopicRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VotingSessionServiceTest {

    @Mock
    private VotingSessionRepository votingSessionRepository;

    @Mock
    private VotingTopicRepository votingTopicRepository;

    @Test
    void shouldOpenVotingSessionWithSpecifiedDuration() {

        UUID topicId = UUID.randomUUID();
        VotingTopic topic = new VotingTopic("Budget approval");
        Instant fixedInstant = Instant.parse("2026-09-06T20:00:00Z");

        Clock clock = Clock.fixed(fixedInstant, ZoneOffset.UTC);

        VotingSessionService service = new VotingSessionService(
            votingSessionRepository,
            votingTopicRepository,
            clock
        );

        OpenVotingSessionRequest request = new OpenVotingSessionRequest(5);

        when(votingTopicRepository.findById(topicId))
            .thenReturn(Optional.of(topic));

        when(votingSessionRepository.existsByVotingTopic_Id(topicId))
            .thenReturn(false);

        when(votingSessionRepository.saveAndFlush(any(VotingSession.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        OpenVotingSessionResponse response = service.openSession(topicId, request);

        assertEquals(fixedInstant, response.openedAt());
        assertEquals(fixedInstant.plusSeconds(300), response.closesAt());

        verify(votingTopicRepository).findById(topicId);
        verify(votingSessionRepository).existsByVotingTopic_Id(topicId);
        verify(votingSessionRepository).saveAndFlush(any(VotingSession.class));
    }

    @Test
    void shouldOpenVotingSessionWithDefaultDurationWhenDurationIsNull() {

        UUID topicId = UUID.randomUUID();
        VotingTopic topic = new VotingTopic("Budget approval");
        Instant fixedInstant = Instant.parse("2026-09-06T20:00:00Z");

        Clock clock = Clock.fixed(fixedInstant, ZoneOffset.UTC);

        VotingSessionService service = new VotingSessionService(
                votingSessionRepository,
                votingTopicRepository,
                clock
        );

        OpenVotingSessionRequest request = new OpenVotingSessionRequest(null);

        when(votingTopicRepository.findById(topicId))
                .thenReturn(Optional.of(topic));

        when(votingSessionRepository.existsByVotingTopic_Id(topicId))
                .thenReturn(false);

        when(votingSessionRepository.saveAndFlush(any(VotingSession.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        OpenVotingSessionResponse response = service.openSession(topicId, request);

        assertEquals(fixedInstant, response.openedAt());
        assertEquals(fixedInstant.plusSeconds(60), response.closesAt());

        verify(votingTopicRepository).findById(topicId);
        verify(votingSessionRepository).existsByVotingTopic_Id(topicId);
        verify(votingSessionRepository).saveAndFlush(any(VotingSession.class));
    }

    @Test
    void shouldThrowExceptionWhenVotingTopicDoesNotExist() {

        UUID topicId = UUID.randomUUID();
        Instant fixedInstant = Instant.parse("2026-09-06T20:00:00Z");

        Clock clock = Clock.fixed(fixedInstant, ZoneOffset.UTC);

        VotingSessionService service = new VotingSessionService(
                votingSessionRepository,
                votingTopicRepository,
                clock
        );

        OpenVotingSessionRequest request = new OpenVotingSessionRequest(5);

        when(votingTopicRepository.findById(topicId))
                .thenReturn(Optional.empty());

        assertThrows(
                VotingTopicNotFoundException.class,
                () -> service.openSession(topicId, request)
        );

        verify(votingTopicRepository).findById(topicId);
        verify(votingSessionRepository, never()).existsByVotingTopic_Id(any());
        verify(votingSessionRepository, never()).saveAndFlush(any(VotingSession.class));
    }

    @Test
    void shouldThrowExceptionWhenVotingSessionAlreadyExists() {

        UUID topicId = UUID.randomUUID();
        VotingTopic topic = new VotingTopic("Budget approval");
        Instant fixedInstant = Instant.parse("2026-09-06T20:00:00Z");

        Clock clock = Clock.fixed(fixedInstant, ZoneOffset.UTC);

        VotingSessionService service = new VotingSessionService(
                votingSessionRepository,
                votingTopicRepository,
                clock
        );

        OpenVotingSessionRequest request = new OpenVotingSessionRequest(5);

        when(votingTopicRepository.findById(topicId))
                .thenReturn(Optional.of(topic));

        when(votingSessionRepository.existsByVotingTopic_Id(topicId))
                .thenReturn(true);

        assertThrows(
                VotingSessionAlreadyExistsException.class,
                () -> service.openSession(topicId, request)
        );

        verify(votingTopicRepository).findById(topicId);
        verify(votingSessionRepository).existsByVotingTopic_Id(topicId);
        verify(votingSessionRepository, never())
                .saveAndFlush(any(VotingSession.class));
    }

    @Test
    void shouldThrowExceptionWhenDatabaseRejectsConcurrentSessionCreation() {

        UUID topicId = UUID.randomUUID();
        VotingTopic topic = new VotingTopic("Budget approval");
        Instant fixedInstant = Instant.parse("2026-09-06T20:00:00Z");

        Clock clock = Clock.fixed(fixedInstant, ZoneOffset.UTC);

        VotingSessionService service = new VotingSessionService(
                votingSessionRepository,
                votingTopicRepository,
                clock
        );

        OpenVotingSessionRequest request = new OpenVotingSessionRequest(5);

        when(votingTopicRepository.findById(topicId))
                .thenReturn(Optional.of(topic));

        when(votingSessionRepository.existsByVotingTopic_Id(topicId))
                .thenReturn(false);

        when(votingSessionRepository.saveAndFlush(any(VotingSession.class)))
                .thenThrow(new DataIntegrityViolationException(
                        "Unique constraint violation"
                ));

        assertThrows(
                VotingSessionAlreadyExistsException.class,
                () -> service.openSession(topicId, request)
        );

        verify(votingTopicRepository).findById(topicId);
        verify(votingSessionRepository).existsByVotingTopic_Id(topicId);
        verify(votingSessionRepository).saveAndFlush(any(VotingSession.class));
    }
}
