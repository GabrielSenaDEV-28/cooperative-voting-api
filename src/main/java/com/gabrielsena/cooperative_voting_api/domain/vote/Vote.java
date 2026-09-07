package com.gabrielsena.cooperative_voting_api.domain.vote;

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
@Table(
        name = "votes",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_vote_topic_associate",
                        columnNames = {"voting_topic_id", "associate_id"}
                )
        }
)
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "voting_topic_id", nullable = false)
    private VotingTopic votingTopic;

    @Column(name = "associate_id", nullable = false)
    private UUID associateId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 3)
    private VoteChoice choice;

    @Column(name = "voted_at", nullable = false)
    private Instant votedAt;

    public Vote(
            VotingTopic votingTopic,
            UUID associateId,
            VoteChoice choice,
            Instant votedAt
    ) {
        if (votingTopic == null) {
            throw new IllegalArgumentException("Voting topic must not be null");
        }

        if (associateId == null) {
            throw new IllegalArgumentException("Associate id must not be null");
        }

        if (choice == null) {
            throw new IllegalArgumentException("Vote choice must not be null");
        }

        if (votedAt == null) {
            throw new IllegalArgumentException("Vote timestamp must not be null");
        }

        this.votingTopic = votingTopic;
        this.associateId = associateId;
        this.choice = choice;
        this.votedAt = votedAt;
    }
}
