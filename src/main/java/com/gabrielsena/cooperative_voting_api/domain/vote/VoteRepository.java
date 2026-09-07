package com.gabrielsena.cooperative_voting_api.domain.vote;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface VoteRepository extends JpaRepository<Vote, UUID> {

    boolean existsByVotingTopic_IdAndAssociateId(UUID id, UUID associateID);

    @Query("""
        select v.choice as choice, count(v) as total
        from Vote v
        where v.votingTopic.id = :topicId
        group by v.choice
        """)
    List<VoteCountProjection> countVotesByTopicId(
            @Param("topicId") UUID topicId
    );
}
