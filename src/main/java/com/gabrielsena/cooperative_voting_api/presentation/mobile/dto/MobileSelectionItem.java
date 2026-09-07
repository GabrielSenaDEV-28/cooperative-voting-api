package com.gabrielsena.cooperative_voting_api.presentation.mobile.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public record MobileSelectionItem(
        @JsonProperty("texto")
        String text,
        String url,
        Map<String, Object> body
) {
}
