CREATE TABLE voting_sessions (
    id UUID PRIMARY KEY,
    voting_topic_id UUID NOT NULL UNIQUE REFERENCES voting_topics(id),
    opened_at TIMESTAMP WITH TIME ZONE NOT NULL,
    closes_at TIMESTAMP WITH TIME ZONE NOT NULL
);