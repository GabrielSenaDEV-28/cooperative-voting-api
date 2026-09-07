package com.gabrielsena.cooperative_voting_api.domain.vote;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VoteRepository extends JpaRepository<Vote, UUID> {

    boolean existsByVotingTopic_IdAndAssociateId(UUID id, UUID associateID);
}
