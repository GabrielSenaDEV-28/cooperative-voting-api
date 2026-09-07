package com.gabrielsena.cooperative_voting_api.domain.session;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface VotingSessionRepository extends JpaRepository<VotingSession, UUID> {

    boolean existsByVotingTopic_Id(UUID votingTopicId);
    Optional<VotingSession> findByVotingTopic_Id(UUID votingTopicId);
}
