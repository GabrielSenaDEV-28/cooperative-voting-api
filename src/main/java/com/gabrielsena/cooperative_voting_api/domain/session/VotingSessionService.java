package com.gabrielsena.cooperative_voting_api.domain.session;

import com.gabrielsena.cooperative_voting_api.domain.exception.VotingSessionAlreadyExistsException;
import com.gabrielsena.cooperative_voting_api.domain.exception.VotingTopicNotFoundException;
import com.gabrielsena.cooperative_voting_api.domain.session.dto.OpenVotingSessionRequest;
import com.gabrielsena.cooperative_voting_api.domain.session.dto.OpenVotingSessionResponse;
import com.gabrielsena.cooperative_voting_api.domain.topic.VotingTopic;
import com.gabrielsena.cooperative_voting_api.domain.topic.VotingTopicRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class VotingSessionService {

    private final VotingSessionRepository votingSessionRepository;
    private final VotingTopicRepository votingTopicRepository;
    private final Clock clock;

    @Transactional
    public OpenVotingSessionResponse openSession(
            UUID topicId,
            OpenVotingSessionRequest request
    ) {

        VotingTopic topic = votingTopicRepository.findById(topicId)
                .orElseThrow(() ->
                        new VotingTopicNotFoundException("Voting topic not found")
                );

        if (votingSessionRepository.existsByVotingTopic_Id(topicId)) {

            log.warn(
                    "Voting session creation rejected because session already exists: topicId={}",
                    topicId
            );

            throw new VotingSessionAlreadyExistsException(
                    "Voting session already exists for this topic"
            );
        }

        int durationMinutes =
                request.durationMinutes() == null
                        ? 1
                        : request.durationMinutes();

        Instant openedAt = Instant.now(clock);
        Instant closesAt = openedAt.plus(durationMinutes, ChronoUnit.MINUTES);

        VotingSession session =
                new VotingSession(topic, openedAt, closesAt);

        try {

            VotingSession savedSession =
                    votingSessionRepository.saveAndFlush(session);

            log.info(
                    "Voting session opened: topicId={}, closesAt={}",
                    topicId,
                    savedSession.getClosesAt()
            );

            return new OpenVotingSessionResponse(
                    savedSession.getId(),
                    savedSession.getVotingTopic().getId(),
                    savedSession.getOpenedAt(),
                    savedSession.getClosesAt()
            );

        } catch (DataIntegrityViolationException e) {

            log.warn(
                    "Voting session creation rejected by database constraint: topicId={}",
                    topicId
            );

            throw new VotingSessionAlreadyExistsException(
                    "Voting session already exists for this topic"
            );
        }
    }
}
