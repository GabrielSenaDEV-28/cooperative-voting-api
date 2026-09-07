package com.gabrielsena.cooperative_voting_api.domain.vote;

import com.gabrielsena.cooperative_voting_api.domain.exception.AssociateAlreadyVotedException;
import com.gabrielsena.cooperative_voting_api.domain.exception.VotingSessionClosedException;
import com.gabrielsena.cooperative_voting_api.domain.exception.VotingSessionNotFoundException;
import com.gabrielsena.cooperative_voting_api.domain.session.VotingSession;
import com.gabrielsena.cooperative_voting_api.domain.session.VotingSessionRepository;
import com.gabrielsena.cooperative_voting_api.domain.vote.dto.CastVoteRequest;
import com.gabrielsena.cooperative_voting_api.domain.vote.dto.CastVoteResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;
    private final VotingSessionRepository votingSessionRepository;
    private final Clock clock;

    @Transactional
    public CastVoteResponse castVote(UUID topicId, CastVoteRequest request) {

        VotingSession session = votingSessionRepository
                .findByVotingTopic_Id(topicId)
                .orElseThrow(() -> new VotingSessionNotFoundException("Voting session not found for this topic"));

        Instant now = Instant.now(clock);

        if(!now.isBefore(session.getClosesAt())) {
            throw new VotingSessionClosedException("Voting session is closed");
        }

        if(voteRepository.existsByVotingTopic_IdAndAssociateId(topicId, request.associateId())) {
            throw new AssociateAlreadyVotedException("Associate has already voted on this topic");
        }

        Vote vote = new Vote(
            session.getVotingTopic(),
            request.associateId(),
            request.choice(),
            now
        );

        try {
            Vote savedVote = voteRepository.saveAndFlush(vote);

            return new CastVoteResponse(
                    savedVote.getId(),
                    savedVote.getVotingTopic().getId(),
                    savedVote.getAssociateId(),
                    savedVote.getChoice(),
                    savedVote.getVotedAt()
            );
        } catch (DataIntegrityViolationException e) {
            throw new AssociateAlreadyVotedException(
                    "Associate has already voted on this topic"
            );
        }
    }

}
