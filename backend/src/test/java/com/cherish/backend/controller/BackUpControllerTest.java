package com.cherish.backend.controller;

import com.cherish.backend.domain.Avatar;
import com.cherish.backend.domain.BackUp;
import com.cherish.backend.domain.Gender;
import com.cherish.backend.repositroy.AccountRepository;
import com.cherish.backend.repositroy.AvatarRepository;
import com.cherish.backend.repositroy.BackUpRepository;
import com.cherish.backend.repositroy.SessionTokenRepository;
import com.cherish.backend.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Profile("dev")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Transactional
@AutoConfigureMockMvc
class BackUpControllerTest {

    MockMvc mockMvc;

    @Autowired
    WebApplicationContext context;

    ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    AccountService accountService;

    @Autowired
    AccountRepository accountRepository;
    @Autowired
    AvatarRepository avatarRepository;
    @Autowired
    SessionTokenRepository tokenRepository;

    @Autowired
    BackUpRepository backUpRepository;

    MockHttpSession httpSession;

    Avatar avatar;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        httpSession = new MockHttpSession();

        avatar = Avatar.of("name1", LocalDate.now(), Gender.MALE);
        avatarRepository.save(avatar);

        httpSession.setAttribute(ConstValue.sessionName,avatar.getId());
    }

    @AfterEach
    void clean() {
        httpSession.clearAttributes();
    }

    @Test
    @DisplayName("정상적인 최신 백업 기록을 요청한 경우 백업한 기록이 존재하면 백업 기록을 출력해준다.")
    public void getRecentBackUpAPITest() throws Exception {
        //given
        BackUp backUp = BackUp.of("asdasd","os1","device1",3,avatar);
        backUpRepository.save(backUp);
        //when
        //then
        mockMvc.perform(get("/api/backup")
                .session(httpSession)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.backUpID").exists())
                .andExpect(jsonPath("$.osVersion").exists())
                .andExpect(jsonPath("$.deviceType").exists())
                .andExpect(jsonPath("$.diaryCount").exists())
                .andExpect(jsonPath("$.createdDate").exists());
    }

    @Test
    @DisplayName("정상적인 최신 백업 기록을 요청한 경우 백업기록이 존재하지 않는 경우 에러메세지를 출력해준다.")
    public void getRecentBackUpNullAPITest() throws Exception {
        //given
        //when
        //then
        mockMvc.perform(get("/api/backup")
                        .session(httpSession)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").exists())
                .andExpect(jsonPath("$.message").exists());
    }



}