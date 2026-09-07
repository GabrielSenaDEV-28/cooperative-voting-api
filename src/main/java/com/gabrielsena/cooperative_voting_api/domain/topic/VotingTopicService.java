package com.gabrielsena.cooperative_voting_api.domain.topic;

import com.gabrielsena.cooperative_voting_api.domain.topic.dto.CreateVotingTopicRequest;
import com.gabrielsena.cooperative_voting_api.domain.topic.dto.CreateVotingTopicResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class VotingTopicService {
    private final VotingTopicRepository votingTopicRepository;

    @Transactional
    public CreateVotingTopicResponse create(CreateVotingTopicRequest request) {
        VotingTopic topic = new VotingTopic(request.title());
        VotingTopic savedTopic = votingTopicRepository.save(topic);

        log.info(
                "Voting topic created: topicId={}",
                savedTopic.getId()
        );

        return new CreateVotingTopicResponse(
                savedTopic.getId(),
                savedTopic.getTitle()
        );

    }
}
