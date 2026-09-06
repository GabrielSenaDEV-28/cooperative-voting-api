package com.gabrielsena.cooperative_voting_api.presentation.mobile.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record MobileFormItem(
        @JsonProperty("tipo")
        String type,
        String id,
        @JsonProperty("titulo")
        String title,
        @JsonProperty("texto")
        String text,
        @JsonProperty("valor")
        String value
) {
}
