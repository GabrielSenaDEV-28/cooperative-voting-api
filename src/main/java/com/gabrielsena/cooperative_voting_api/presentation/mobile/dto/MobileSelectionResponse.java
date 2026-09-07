package com.gabrielsena.cooperative_voting_api.presentation.mobile.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record MobileSelectionResponse(
        @JsonProperty("tipo")
        String type,
        @JsonProperty("titulo")
        String titulo,
        List<MobileSelectionItem> itens
) {
}