package com.gabrielsena.cooperative_voting_api.domain.vote;

public interface VoteCountProjection {

    VoteChoice getChoice();
    long getTotal();
}