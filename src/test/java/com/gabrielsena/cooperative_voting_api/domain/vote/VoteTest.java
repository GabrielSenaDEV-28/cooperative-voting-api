package com.gabrielsena.cooperative_voting_api.domain.vote;

import com.gabrielsena.cooperative_voting_api.domain.topic.VotingTopic;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class VoteTest {

    @Test
    void shouldCreateVoteWhenDataIsValid() {
        VotingTopic topic = new VotingTopic("Pauta teste");
        UUID associateId = UUID.randomUUID();
        Instant votedAt = Instant.now();

        Vote vote = new Vote(
                topic,
                associateId,
                VoteChoice.YES,
                votedAt
        );

        assertEquals(topic, vote.getVotingTopic());
        assertEquals(associateId, vote.getAssociateId());
        assertEquals(VoteChoice.YES, vote.getChoice());
        assertEquals(votedAt, vote.getVotedAt());
    }

    @Test
    void shouldThrowExceptionWhenVotingTopicIsNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Vote(
                        null,
                        UUID.randomUUID(),
                        VoteChoice.YES,
                        Instant.now()
                )
        );

        assertEquals(
                "Voting topic must not be null",
                exception.getMessage()
        );
    }

    @Test
    void shouldThrowExceptionWhenAssociateIdIsNull() {
        VotingTopic topic = new VotingTopic("Pauta teste");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Vote(
                        topic,
                        null,
                        VoteChoice.YES,
                        Instant.now()
                )
        );

        assertEquals(
                "Associate id must not be null",
                exception.getMessage()
        );
    }

    @Test
    void shouldThrowExceptionWhenVoteChoiceIsNull() {
        VotingTopic topic = new VotingTopic("Pauta teste");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Vote(
                        topic,
                        UUID.randomUUID(),
                        null,
                        Instant.now()
                )
        );

        assertEquals(
                "Vote choice must not be null",
                exception.getMessage()
        );
    }

    @Test
    void shouldThrowExceptionWhenVotedAtIsNull() {
        VotingTopic topic = new VotingTopic("Pauta teste");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Vote(
                        topic,
                        UUID.randomUUID(),
                        VoteChoice.YES,
                        null
                )
        );

        assertEquals(
                "Vote timestamp must not be null",
                exception.getMessage()
        );
    }
}