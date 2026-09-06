package com.gabrielsena.cooperative_voting_api.domain.topic;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface VotingTopicRepository extends JpaRepository<VotingTopic, UUID> {

}
