package com.gabrielsena.cooperative_voting_api.presentation.mobile;

import com.gabrielsena.cooperative_voting_api.presentation.mobile.dto.MobileAction;
import com.gabrielsena.cooperative_voting_api.presentation.mobile.dto.MobileFormItem;
import com.gabrielsena.cooperative_voting_api.presentation.mobile.dto.MobileFormResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class VotingTopicFormService {

    private final String callbackBaseUrl;

    public VotingTopicFormService(@Value("${app.callback-base-url}") String callbackBaseUrl) {
        this.callbackBaseUrl = callbackBaseUrl;
    }

    public MobileFormResponse buildCreationForm() {

        MobileFormItem description = new MobileFormItem(
                "TEXTO",
                null,
                null,
                "Preencha as informações abaixo para registrar uma nova pauta para votação.",
                null
        );

        MobileFormItem titleInput = new MobileFormItem(
                "INPUT_TEXTO",
                "title",
                "Nome da pauta",
                null,
                ""
        );

        MobileAction okButton = new MobileAction(
                "Confirmar",
                callbackBaseUrl + "/api/v1/topics",
                Map.of()
        );

        MobileAction cancelButton = new MobileAction(
                "Cancelar",
                 callbackBaseUrl,
                null
        );

        return new MobileFormResponse(
                "FORMULARIO",
                "CRIAÇÃO DE PAUTA",
                List.of(description, titleInput),
                okButton,
                cancelButton
        );
    }
}
