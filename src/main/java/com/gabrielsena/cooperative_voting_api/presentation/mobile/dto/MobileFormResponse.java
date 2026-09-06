package com.gabrielsena.cooperative_voting_api.presentation.mobile.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record MobileFormResponse(
        @JsonProperty("tipo")
        String type,
        @JsonProperty("titulo")
        String title,
        @JsonProperty("itens")
        List<MobileFormItem> items,
        @JsonProperty("botaoOk")
        MobileAction okButton,
        @JsonProperty("botaoCancelar")
        MobileAction cancelButton
) {
}
