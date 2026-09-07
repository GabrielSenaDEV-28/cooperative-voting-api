package com.gabrielsena.cooperative_voting_api.presentation.mobile;

import com.gabrielsena.cooperative_voting_api.presentation.mobile.dto.MobileSelectionItem;
import com.gabrielsena.cooperative_voting_api.presentation.mobile.dto.MobileSelectionResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class VoteSelectionService {

    private final String callbackBaseUrl;

    public VoteSelectionService(
            @Value("${app.callback-base-url}") String callbackBaseUrl
    ) {
        this.callbackBaseUrl = callbackBaseUrl;
    }

    public MobileSelectionResponse buildVoteSelection(
            UUID topicId,
            UUID associateId
    ) {
        String voteUrl = callbackBaseUrl
                + "/api/v1/topics/"
                + topicId
                + "/votes";

        MobileSelectionItem yesOption = new MobileSelectionItem(
                "Sim",
                voteUrl,
                Map.of(
                        "associateId", associateId,
                        "choice", "YES"
                )
        );

        MobileSelectionItem noOption = new MobileSelectionItem(
                "Não",
                voteUrl,
                Map.of(
                        "associateId", associateId,
                        "choice", "NO"
                )
        );

        return new MobileSelectionResponse(
                "SELECAO",
                "Vote na pauta",
                List.of(yesOption, noOption)
        );
    }
}