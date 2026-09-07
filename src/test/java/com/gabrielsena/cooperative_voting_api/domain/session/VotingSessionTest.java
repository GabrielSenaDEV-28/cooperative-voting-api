package com.gabrielsena.cooperative_voting_api.domain.session;

import com.gabrielsena.cooperative_voting_api.domain.topic.VotingTopic;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class VotingSessionTest {

    @Test
    void shouldCreateVotingSession() {

        VotingTopic topic = new VotingTopic("Budget approval");
        Instant openedAt = Instant.parse("2026-09-06T20:00:00Z");
        Instant closesAt = Instant.parse("2026-09-06T20:05:00Z");

        VotingSession session = new VotingSession(topic, openedAt, closesAt);

        assertEquals(topic, session.getVotingTopic());
        assertEquals(openedAt, session.getOpenedAt());
        assertEquals(closesAt, session.getClosesAt());
    }

    @Test
    void shouldThrowExceptionWhenVotingTopicIsNull() {

        Instant openedAt = Instant.parse("2026-09-06T20:00:00Z");
        Instant closesAt = Instant.parse("2026-09-06T20:05:00Z");

        assertThrows(IllegalArgumentException.class, () -> new VotingSession(null, openedAt, closesAt));
    }

    @Test
    void shouldThrowExceptionWhenOpenedAtOrClosesAtIsNull() {

        VotingTopic topic = new VotingTopic("Budget approval");
        Instant openedAt = Instant.parse("2026-09-06T20:00:00Z");
        Instant closesAt = Instant.parse("2026-09-06T20:05:00Z");

        assertThrows(IllegalArgumentException.class, () -> new VotingSession(topic, null, openedAt));
        assertThrows(IllegalArgumentException.class, () -> new VotingSession(topic, openedAt, null));
    }

    @Test
    void shouldThrowExceptionWhenClosingTimeIsNotAfterOpeningTime() {

        VotingTopic topic = new VotingTopic("Budget approval");
        Instant openedAt = Instant.parse("2026-09-06T20:05:00Z");
        Instant closesAt = Instant.parse("2026-09-06T20:00:00Z");

        assertThrows(IllegalArgumentException.class, () -> new VotingSession(topic, openedAt, closesAt));
    }

    @Test
    void shouldThrowExceptionWhenClosingTimeEqualsOpeningTime() {

        VotingTopic topic = new VotingTopic("Budget approval");
        Instant openedAt = Instant.parse("2026-09-06T20:00:00Z");
        Instant closesAt = Instant.parse("2026-09-06T20:00:00Z");

        assertThrows(IllegalArgumentException.class, () -> new VotingSession(topic, openedAt, closesAt));
    }
}
