package com.gabrielsena.cooperative_voting_api.domain.session;

import com.gabrielsena.cooperative_voting_api.domain.topic.VotingTopic;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "voting_sessions")
public class VotingSession {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "voting_topic_id", nullable = false)
    private VotingTopic votingTopic;

    @Column(name = "opened_at", nullable = false)
    private Instant openedAt;

    @Column(name = "closes_at", nullable = false)
    private Instant closesAt;

    public VotingSession(VotingTopic topic, Instant openedAt, Instant closesAt) {
        if (topic == null) {
            throw new IllegalArgumentException("Voting topic must not be null");
        }

        if (openedAt == null || closesAt == null) {
            throw new IllegalArgumentException("Session timestamps must not be null");
        }

        if(!closesAt.isAfter(openedAt)) {
            throw new IllegalArgumentException("Closing time must be after opening time");
        }

        this.votingTopic = topic;
        this.openedAt = openedAt;
        this.closesAt = closesAt;
    }
}
