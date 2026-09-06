package com.gabrielsena.cooperative_voting_api.domain.topic;

import com.gabrielsena.cooperative_voting_api.domain.topic.dto.CreateVotingTopicRequest;
import com.gabrielsena.cooperative_voting_api.domain.topic.dto.CreateVotingTopicResponse;

import com.gabrielsena.cooperative_voting_api.presentation.mobile.VotingTopicFormService;
import com.gabrielsena.cooperative_voting_api.presentation.mobile.dto.MobileAction;
import com.gabrielsena.cooperative_voting_api.presentation.mobile.dto.MobileFormItem;
import com.gabrielsena.cooperative_voting_api.presentation.mobile.dto.MobileFormResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(VotingTopicController.class)
class VotingTopicControllerTest {

    @MockitoBean
    private VotingTopicService votingTopicService;

    @MockitoBean
    private VotingTopicFormService votingTopicFormService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnCreatedWhenRequestIsValid() throws Exception {
        UUID topicId = UUID.randomUUID();

        CreateVotingTopicResponse response =
                new CreateVotingTopicResponse(
                        topicId,
                        "Annual budget approval"
                );

        when(votingTopicService.create(any(CreateVotingTopicRequest.class)))
                .thenReturn(response);

        mockMvc.perform(
                post("/api/v1/topics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                "title": "Annual budget approval"
                                }
                                """)
        )
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(topicId.toString()))
        .andExpect(jsonPath("$.title").value("Annual budget approval"));
    }

    @Test
    void shouldReturnBadRequestWhenTitleIsBlank() throws Exception {
        mockMvc.perform(
                post("/api/v1/topics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "title": "   "
                                }
                                """)
        )
        .andExpect(status().isBadRequest());

        verifyNoInteractions(votingTopicService);
    }

    @Test
    void shouldReturnVotingTopicCreationForm() throws Exception {

        MobileFormResponse response = new MobileFormResponse(
                "FORMULARIO",
                "CRIAÇÃO DE PAUTA",
                List.of(
                        new MobileFormItem(
                                "TEXTO",
                                null,
                                null,
                                "Preencha as informações abaixo para registrar uma nova pauta para votação.",
                                null
                        ),
                        new MobileFormItem(
                                "INPUT_TEXTO",
                                "title",
                                "Nome da pauta",
                                null,
                                ""
                        )
                ),
                new MobileAction(
                        "Confirmar",
                        "http://localhost:8080/api/v1/topics",
                        Map.of()
                ),
                new MobileAction(
                        "Cancelar",
                        "http://localhost:8080",
                        null
                )
        );

        when(votingTopicFormService.buildCreationForm()).thenReturn(response);

        mockMvc.perform(get("/api/v1/topics/form"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipo").value(response.type()))
                .andExpect(jsonPath("$.titulo").value(response.title()))
                .andExpect(jsonPath("$.itens[1].id").value("title"))
                .andExpect(jsonPath("$.botaoOk.url").value("http://localhost:8080/api/v1/topics"))
                .andExpect(jsonPath("$.botaoCancelar.url").value("http://localhost:8080"))
                .andExpect(jsonPath("$.botaoCancelar.body").doesNotExist());
    }

}
