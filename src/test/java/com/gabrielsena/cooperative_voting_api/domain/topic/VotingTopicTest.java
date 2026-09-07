package com.gabrielsena.cooperative_voting_api.domain.topic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class VotingTopicTest {

    @Test
    void shouldCreateVotingTopicWithValidTitle() {
        VotingTopic votingTopic = new VotingTopic("Annual budget approval");

        assertEquals("Annual budget approval", votingTopic.getTitle());
    }

    @Test
    void shouldTrimTitleWhenCreatingVotingTopic() {
        VotingTopic votingTopic = new VotingTopic("    Annual budget approval    ");

        assertEquals("Annual budget approval", votingTopic.getTitle());
    }

    @Test
    void shouldThrowExceptionWhenTitleIsBlank() {
        assertThrows(IllegalArgumentException.class, () -> new VotingTopic("    "));
    }

    @Test
    void shouldThrowExceptionWhenTitleIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new VotingTopic(null));
    }
}
