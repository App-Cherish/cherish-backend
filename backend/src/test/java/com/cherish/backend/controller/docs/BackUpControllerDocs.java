package com.cherish.backend.controller.docs;

import com.cherish.backend.controller.ConstValue;
import com.cherish.backend.domain.BackUp;
import com.cherish.backend.repositroy.AvatarRepository;
import com.cherish.backend.service.BackUpService;
import com.cherish.backend.service.DiaryService;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(RestDocumentationExtension.class)
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class BackUpControllerDocs {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    WebApplicationContext context;

    ObjectMapper objectMapper = new ObjectMapper();

    MockHttpSession session;

    @MockBean
    BackUpService backUpService;

    @Autowired
    AvatarRepository avatarRepository;

    @BeforeEach
    void init(RestDocumentationContextProvider restDocumentationContextProvider) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(documentationConfiguration(restDocumentationContextProvider))
                .build();
        session = new MockHttpSession();

        session.setAttribute(ConstValue.sessionName, 1L);

    }

    @Test
    @DisplayName("[DOCS]가장 최근 백업 불러오기")
    public void getRecentBackUpTest() throws Exception {
        //given
        BackUp backUp = BackUp.of("newBackUpId1", "os1", "deviceType1", 3, null);
        given(backUpService.getRecentBackUp(eq(1L))).willReturn(backUp);

        //when
        //then
        mockMvc.perform(get("/api/backup")
                        .session(session)
                )
                .andExpect(status().isOk())
                .andDo(document("getRecentBackUp",
                        preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .tag("BACK UP API(Header에 로그인시 받은 SESSION 쿠키값이 항상 입력이 되있어야 합니다!)")
                                .summary("가장 최근 백업 불러오기")
                                .build())));
    }


}
