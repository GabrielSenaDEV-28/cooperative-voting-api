package com.gabrielsena.cooperative_voting_api.domain.vote;

import com.gabrielsena.cooperative_voting_api.domain.exception.AssociateAlreadyVotedException;
import com.gabrielsena.cooperative_voting_api.domain.exception.VotingSessionClosedException;
import com.gabrielsena.cooperative_voting_api.domain.exception.VotingSessionNotFoundException;
import com.gabrielsena.cooperative_voting_api.domain.session.VotingSession;
import com.gabrielsena.cooperative_voting_api.domain.session.VotingSessionRepository;
import com.gabrielsena.cooperative_voting_api.domain.topic.VotingTopic;
import com.gabrielsena.cooperative_voting_api.domain.vote.dto.CastVoteRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class VoteServiceTest {

    @Mock
    private VoteRepository voteRepository;

    @Mock
    private VotingSessionRepository votingSessionRepository;

    private Clock clock;
    private VoteService voteService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        clock = Clock.fixed(
                Instant.parse("2026-09-07T03:00:00Z"),
                ZoneOffset.UTC
        );

        voteService = new VoteService(
                voteRepository,
                votingSessionRepository,
                clock
        );
    }

    @Test
    void shouldCastVoteWhenSessionIsOpenAndAssociateHasNotVoted() {
        UUID topicId = UUID.randomUUID();
        UUID associateId = UUID.randomUUID();

        VotingTopic topic = new VotingTopic("Pauta teste");

        VotingSession session = new VotingSession(
                topic,
                Instant.parse("2026-09-07T02:50:00Z"),
                Instant.parse("2026-09-07T03:10:00Z")
        );

        CastVoteRequest request = new CastVoteRequest(
                associateId,
                VoteChoice.YES
        );

        when(votingSessionRepository.findByVotingTopic_Id(topicId))
                .thenReturn(Optional.of(session));

        when(voteRepository.existsByVotingTopic_IdAndAssociateId(topicId, associateId))
                .thenReturn(false);

        when(voteRepository.saveAndFlush(any(Vote.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var response = voteService.castVote(topicId, request);

        assertEquals(associateId, response.associateId());
        assertEquals(VoteChoice.YES, response.choice());
        assertEquals(Instant.parse("2026-09-07T03:00:00Z"), response.votedAt());

        verify(voteRepository).saveAndFlush(any(Vote.class));
    }

    @Test
    void shouldThrowExceptionWhenVotingSessionDoesNotExist() {
        UUID topicId = UUID.randomUUID();

        when(votingSessionRepository.findByVotingTopic_Id(topicId))
                .thenReturn(Optional.empty());

        assertThrows(
                VotingSessionNotFoundException.class,
                () -> voteService.castVote(
                        topicId,
                        new CastVoteRequest(UUID.randomUUID(), VoteChoice.YES)
                )
        );

        verify(voteRepository, never())
                .saveAndFlush(any(Vote.class));
    }

    @Test
    void shouldThrowExceptionWhenVotingSessionIsClosed() {
        UUID topicId = UUID.randomUUID();

        VotingTopic topic = new VotingTopic("Pauta teste");

        VotingSession session = new VotingSession(
                topic,
                Instant.parse("2026-09-07T02:00:00Z"),
                Instant.parse("2026-09-07T03:00:00Z")
        );

        when(votingSessionRepository.findByVotingTopic_Id(topicId))
                .thenReturn(Optional.of(session));

        assertThrows(
                VotingSessionClosedException.class,
                () -> voteService.castVote(
                        topicId,
                        new CastVoteRequest(UUID.randomUUID(), VoteChoice.YES)
                )
        );

        verify(voteRepository, never())
                .saveAndFlush(any(Vote.class));
    }

    @Test
    void shouldThrowExceptionWhenAssociateHasAlreadyVoted() {
        UUID topicId = UUID.randomUUID();
        UUID associateId = UUID.randomUUID();

        VotingTopic topic = new VotingTopic("Pauta teste");

        VotingSession session = new VotingSession(
                topic,
                Instant.parse("2026-09-07T02:50:00Z"),
                Instant.parse("2026-09-07T03:10:00Z")
        );

        when(votingSessionRepository.findByVotingTopic_Id(topicId))
                .thenReturn(Optional.of(session));

        when(voteRepository.existsByVotingTopic_IdAndAssociateId(topicId, associateId))
                .thenReturn(true);

        assertThrows(
                AssociateAlreadyVotedException.class,
                () -> voteService.castVote(
                        topicId,
                        new CastVoteRequest(associateId, VoteChoice.YES)
                )
        );

        verify(voteRepository, never())
                .saveAndFlush(any(Vote.class));
    }

    @Test
    void shouldTranslateDataIntegrityViolationIntoAssociateAlreadyVotedException() {
        UUID topicId = UUID.randomUUID();
        UUID associateId = UUID.randomUUID();

        VotingTopic topic = new VotingTopic("Pauta teste");

        VotingSession session = new VotingSession(
                topic,
                Instant.parse("2026-09-07T02:50:00Z"),
                Instant.parse("2026-09-07T03:10:00Z")
        );

        when(votingSessionRepository.findByVotingTopic_Id(topicId))
                .thenReturn(Optional.of(session));

        when(voteRepository.existsByVotingTopic_IdAndAssociateId(topicId, associateId))
                .thenReturn(false);

        when(voteRepository.saveAndFlush(any(Vote.class)))
                .thenThrow(new DataIntegrityViolationException("constraint violation"));

        assertThrows(
                AssociateAlreadyVotedException.class,
                () -> voteService.castVote(
                        topicId,
                        new CastVoteRequest(associateId, VoteChoice.YES)
                )
        );
    }
}