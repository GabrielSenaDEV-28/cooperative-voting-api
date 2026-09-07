CREATE TABLE votes (
    id UUID PRIMARY KEY,
    voting_topic_id UUID NOT NULL REFERENCES voting_topics(id),
    associate_id UUID NOT NULL,
    choice VARCHAR(3) NOT NULL,
    voted_at TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT uk_vote_topic_associate
                   UNIQUE (voting_topic_id, associate_id),

    CONSTRAINT ck_votes_choice
                   CHECK (choice IN ('YES', 'NO'))
);