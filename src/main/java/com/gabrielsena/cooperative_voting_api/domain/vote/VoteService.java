package com.gabrielsena.cooperative_voting_api.domain.vote;

import com.gabrielsena.cooperative_voting_api.domain.exception.AssociateAlreadyVotedException;
import com.gabrielsena.cooperative_voting_api.domain.exception.VotingSessionClosedException;
import com.gabrielsena.cooperative_voting_api.domain.exception.VotingSessionNotFoundException;
import com.gabrielsena.cooperative_voting_api.domain.exception.VotingTopicNotFoundException;
import com.gabrielsena.cooperative_voting_api.domain.session.VotingSession;
import com.gabrielsena.cooperative_voting_api.domain.session.VotingSessionRepository;
import com.gabrielsena.cooperative_voting_api.domain.topic.VotingTopicRepository;
import com.gabrielsena.cooperative_voting_api.domain.vote.dto.CastVoteRequest;
import com.gabrielsena.cooperative_voting_api.domain.vote.dto.CastVoteResponse;
import com.gabrielsena.cooperative_voting_api.domain.vote.dto.VotingResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;
    private final VotingSessionRepository votingSessionRepository;
    private final Clock clock;
    private final VotingTopicRepository votingTopicRepository;

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

    @Transactional(readOnly = true)
    public VotingResultResponse getVotingResult(UUID topicId) {

        List<VoteCountProjection> voteCounts = voteRepository.countVotesByTopicId(topicId);

        if(voteCounts.isEmpty() && !votingTopicRepository.existsById(topicId)) {
            throw new VotingTopicNotFoundException("Voting topic not found");
        }

        long yesVotes = 0;
        long noVotes = 0;

        for(VoteCountProjection voteCount : voteCounts) {

            switch (voteCount.getChoice()) {
                case YES -> yesVotes = voteCount.getTotal();
                case NO -> noVotes = voteCount.getTotal();
            }
        }

        long totalVotes = yesVotes + noVotes;

        return new VotingResultResponse(
                topicId,
                yesVotes,
                noVotes,
                totalVotes
        );
    }

}
