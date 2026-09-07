package com.gabrielsena.cooperative_voting_api.domain.vote;

import com.gabrielsena.cooperative_voting_api.domain.exception.AssociateAlreadyVotedException;
import com.gabrielsena.cooperative_voting_api.domain.exception.VotingSessionClosedException;
import com.gabrielsena.cooperative_voting_api.domain.exception.VotingSessionNotFoundException;
import com.gabrielsena.cooperative_voting_api.domain.exception.VotingTopicNotFoundException;
import com.gabrielsena.cooperative_voting_api.domain.session.VotingSession;
import com.gabrielsena.cooperative_voting_api.domain.session.VotingSessionRepository;
import com.gabrielsena.cooperative_voting_api.domain.topic.VotingTopic;
import com.gabrielsena.cooperative_voting_api.domain.topic.VotingTopicRepository;
import com.gabrielsena.cooperative_voting_api.domain.vote.dto.CastVoteRequest;
import com.gabrielsena.cooperative_voting_api.domain.vote.dto.VotingResultResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
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

    @Mock
    private VotingTopicRepository votingTopicRepository;

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
                clock,
                votingTopicRepository
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

    @Test
    void shouldReturnVotingResultWithYesAndNoVotes() {
        UUID topicId = UUID.randomUUID();

        VoteCountProjection yesProjection = mock(VoteCountProjection.class);
        VoteCountProjection noProjection = mock(VoteCountProjection.class);

        when(yesProjection.getChoice()).thenReturn(VoteChoice.YES);
        when(yesProjection.getTotal()).thenReturn(3L);

        when(noProjection.getChoice()).thenReturn(VoteChoice.NO);
        when(noProjection.getTotal()).thenReturn(2L);

        when(voteRepository.countVotesByTopicId(topicId))
                .thenReturn(List.of(yesProjection, noProjection));

        VotingResultResponse result = voteService.getVotingResult(topicId);

        assertEquals(topicId, result.topicId());
        assertEquals(3L, result.yesVotes());
        assertEquals(2L, result.noVotes());
        assertEquals(5L, result.totalVotes());

        verify(votingTopicRepository, never()).existsById(any());
    }

    @Test
    void shouldReturnZeroVotesWhenTopicExistsButHasNoVotes() {
        UUID topicId = UUID.randomUUID();

        when(voteRepository.countVotesByTopicId(topicId))
                .thenReturn(List.of());

        when(votingTopicRepository.existsById(topicId))
                .thenReturn(true);

        VotingResultResponse result = voteService.getVotingResult(topicId);

        assertEquals(topicId, result.topicId());
        assertEquals(0L, result.yesVotes());
        assertEquals(0L, result.noVotes());
        assertEquals(0L, result.totalVotes());
    }

    @Test
    void shouldThrowExceptionWhenVotingTopicDoesNotExist() {
        UUID topicId = UUID.randomUUID();

        when(voteRepository.countVotesByTopicId(topicId))
                .thenReturn(List.of());

        when(votingTopicRepository.existsById(topicId))
                .thenReturn(false);

        assertThrows(
                VotingTopicNotFoundException.class,
                () -> voteService.getVotingResult(topicId)
        );
    }

    @Test
    void shouldCalculateVotingResultRegardlessOfProjectionOrder() {
        UUID topicId = UUID.randomUUID();

        VoteCountProjection noProjection = mock(VoteCountProjection.class);
        VoteCountProjection yesProjection = mock(VoteCountProjection.class);

        when(noProjection.getChoice()).thenReturn(VoteChoice.NO);
        when(noProjection.getTotal()).thenReturn(4L);

        when(yesProjection.getChoice()).thenReturn(VoteChoice.YES);
        when(yesProjection.getTotal()).thenReturn(6L);

        when(voteRepository.countVotesByTopicId(topicId))
                .thenReturn(List.of(noProjection, yesProjection));

        VotingResultResponse result = voteService.getVotingResult(topicId);

        assertEquals(6L, result.yesVotes());
        assertEquals(4L, result.noVotes());
        assertEquals(10L, result.totalVotes());
    }
}