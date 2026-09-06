package com.gabrielsena.cooperative_voting_api.presentation.mobile.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record MobileAction(
        @JsonProperty("texto")
        String text,
        String url,
        Map<String, Object> body
) {
}
